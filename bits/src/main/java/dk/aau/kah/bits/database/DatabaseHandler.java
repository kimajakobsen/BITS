package dk.aau.kah.bits.database;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;

import da.aau.kah.bits.exceptions.InvalidDatabaseConfig;
import da.aau.kah.bits.physical_storage.QB4OLAPFactPropertyTable;

public class DatabaseHandler {

	public DatabaseConfig databaseConfig;
	private Dataset dataset;
	//String assemblerFile = "src/main/resources/assembler.ttl" ;
	
	public DatabaseHandler(DatabaseConfig databaseConfig) throws IOException, InvalidDatabaseConfig {
		
		databaseConfig.validate();
		this.databaseConfig = databaseConfig;
		
		createTDBDirectoryIfNotExist();
		
		if (this.databaseConfig.isFreshLoad()) {
			clearTDBDatabase();
		}
		
		if (this.databaseConfig.getExperimentDataset().equals("TPC-H")) {
			loadTPCHDataset();
		}
		else if (this.databaseConfig.getExperimentDataset().equals("TPC-DS")) {
			//TODO
		}
		else {
			throw new InvalidDatabaseConfig("The Experiment Dataset "+this.databaseConfig.getExperimentDataset()+" is not known, implementation is missing.");
		}
	}
	
	private void createTDBDirectoryIfNotExist() {
		File theDir = new File(databaseConfig.getTDBDatasetPath());

		// if the directory does not exist, create it
		if (!theDir.exists()) {
		    try{
		        theDir.mkdir();
		    } 
		    catch(SecurityException se){
		    	se.printStackTrace();
		    }        
		}
	}

	private void loadTPCHDataset() throws IOException {
		String directory = databaseConfig.getTDBDatasetPath() ;
		this.dataset = TDBFactory.createDataset(directory);
		
		this.dataset.begin(ReadWrite.WRITE) ;
		try {
			
			Model ontology = ModelFactory.createDefaultModel();
			Model dimensions = ModelFactory.createDefaultModel();
			Model facts = ModelFactory.createDefaultModel();
			
			ontology.add(RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getExperimentDataset()+"/onto/tpc-h-qb4o-delivered-version.ttl"));
	
			dimensions.add(RDFDataMgr.loadModel(getTPCHPath()+"/customer.ttl"));
			dimensions.add(RDFDataMgr.loadModel(getTPCHPath()+"/nation.ttl"));
			dimensions.add(RDFDataMgr.loadModel(getTPCHPath()+"/orders.ttl"));
			dimensions.add(RDFDataMgr.loadModel(getTPCHPath()+"/part.ttl"));
			dimensions.add(RDFDataMgr.loadModel(getTPCHPath()+"/partsupp.ttl"));
			dimensions.add(RDFDataMgr.loadModel(getTPCHPath()+"/region.ttl"));
			dimensions.add(RDFDataMgr.loadModel(getTPCHPath()+"/supplier.ttl"));
			if (databaseConfig.getDimensionModelName().equals("pt")) {
				//PropertyTable customerPropertyTable = initilizePropertyTable(ontology,getTPCHPath()+"/customer.ttl");
				//TODO
			}

			
			facts.add(RDFDataMgr.loadModel(getTPCHPath()+"/lineitem.ttl"));
			if (databaseConfig.getFactStorageModel().equals("pt")) {
				QB4OLAPFactPropertyTable factTable = new QB4OLAPFactPropertyTable(ontology,facts); 
				facts = factTable.getModel();
			}
			
			
			//Concatenate the model with the existing model and add it to the named graph. 
			dataset.addNamedModel(databaseConfig.getOntologyModelURL(), ontology.add(dataset.getNamedModel(databaseConfig.getOntologyModelURL())));
			dataset.addNamedModel(databaseConfig.getDimensionModelURL(), dimensions.add(dataset.getNamedModel(databaseConfig.getOntologyModelURL())));
			dataset.addNamedModel(databaseConfig.getFactModelURL(), facts.add(dataset.getNamedModel(databaseConfig.getOntologyModelURL())));

			dataset.commit();
		} finally {
			dataset.end();
		}
	}
	

	private String getTPCHPath(){
		return "src/main/resources/"+databaseConfig.getExperimentDataset()+"/"+databaseConfig.getScaleFactorString();
	}

	public Model getOntologyModel() {
		return getDataset(databaseConfig.getOntologyModelURL());
	}
	
	public Model getFactModel() {
		return getDataset(databaseConfig.getFactModelURL());
	}
	
	public Model getDimensionModel() {
		return getDataset(databaseConfig.getDimensionModelURL());
	}
	
	private Model getDataset(String modelType) {
		Model model;
		
		if (modelType == "default") {
			this.dataset.begin(ReadWrite.READ) ; 
			model = this.dataset.getDefaultModel();
			this.dataset.end() ;
		} else {
			this.dataset.begin(ReadWrite.READ) ; 
			model = this.dataset.getNamedModel(modelType);
			this.dataset.end() ;
		}
		return model;
	}
		
	public void clearTDBDatabase() throws IOException {
		FileUtils.cleanDirectory(new File(databaseConfig.getTDBDatasetPath()));
	}
}

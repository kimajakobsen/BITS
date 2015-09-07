package dk.aau.kah.bits.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
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
			Model customer = ModelFactory.createDefaultModel();
			Model nation = ModelFactory.createDefaultModel();
			Model orders = ModelFactory.createDefaultModel();
			Model part = ModelFactory.createDefaultModel();
			Model partsupp = ModelFactory.createDefaultModel();
			Model region = ModelFactory.createDefaultModel();
			Model supplier = ModelFactory.createDefaultModel();
			Model facts = ModelFactory.createDefaultModel();
			
			ontology.add(RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getExperimentDataset()+"/onto/tpc-h-qb4o-delivered-version.ttl"));
	
			customer.add(RDFDataMgr.loadModel((getTPCHPath()+"/customer.ttl")));
			nation.add(RDFDataMgr.loadModel((getTPCHPath()+"/nation.ttl")));
			orders.add(RDFDataMgr.loadModel((getTPCHPath()+"/orders.ttl")));
			part.add(RDFDataMgr.loadModel((getTPCHPath()+"/part.ttl")));
			partsupp.add(RDFDataMgr.loadModel((getTPCHPath()+"/partsupp.ttl")));
			region.add(RDFDataMgr.loadModel((getTPCHPath()+"/region.ttl")));
			supplier.add(RDFDataMgr.loadModel((getTPCHPath()+"/supplier.ttl")));
			if (databaseConfig.getDimensionModelName().equals("pt")) {
				//PropertyTable customerPropertyTable = initilizePropertyTable(ontology,getTPCHPath()+"/customer.ttl");
				//TODO
			}

			
			facts.add(RDFDataMgr.loadModel((getTPCHPath()+"/lineitem.ttl")));
			if (databaseConfig.getFactStorageModel().equals("pt")) {
				QB4OLAPFactPropertyTable factTable = new QB4OLAPFactPropertyTable(ontology,facts); 
				facts = factTable.getModel();
			}
			
			
			//Concatenate the model with the existing model and add it to the named graph. 
			dataset.addNamedModel(databaseConfig.getOntologyModelURL(), ontology.add(dataset.getNamedModel(databaseConfig.getOntologyModelURL())));
			if (databaseConfig.getDimensionModelName()=="#") {
				//This might overwrite existing named models, this need to be tested further.
				dataset.addNamedModel(databaseConfig.getPrefix()+"customer", customer);
				dataset.addNamedModel(databaseConfig.getPrefix()+"nation", nation);
				dataset.addNamedModel(databaseConfig.getPrefix()+"orders", orders);
				dataset.addNamedModel(databaseConfig.getPrefix()+"part", part);
				dataset.addNamedModel(databaseConfig.getPrefix()+"partsupp", partsupp);
				dataset.addNamedModel(databaseConfig.getPrefix()+"region", region);
				dataset.addNamedModel(databaseConfig.getPrefix()+"supplier", supplier);
				
			} else {
				dataset.addNamedModel(databaseConfig.getDimensionModelURL(), dimensions.add(dataset.getNamedModel(databaseConfig.getDimensionModelURL())));
			}
			dataset.addNamedModel(databaseConfig.getFactModelURL(), facts.add(dataset.getNamedModel(databaseConfig.getFactModelURL())));

			dataset.commit();
		} finally {
			dataset.end();
		}
	}
	
	private String getTPCHPath(){
		return "src/main/resources/"+databaseConfig.getExperimentDataset()+"/"+databaseConfig.getScaleFactorString();
	}

	public Model getDefaultModel() {
		this.dataset.begin(ReadWrite.READ) ; 
		Model model = this.dataset.getDefaultModel();
		this.dataset.end() ;
		return model;
	}
	
	public Model getModel(String modelName) {
		this.dataset.begin(ReadWrite.READ) ; 
		Model model = this.dataset.getNamedModel(modelName);
		this.dataset.end() ;
		return model;
	}
	
	public List<String> getAllModelNames() {
		dataset.begin(ReadWrite.READ) ;
		String qiry = "select distinct ?g { graph ?g { ?s ?p ?o } }";

	    Query query = QueryFactory.create(qiry);
	    QueryExecution qexec = QueryExecutionFactory.create(query, this.dataset);
	    /*Execute the Query*/
	    ResultSet results = qexec.execSelect();
	    
	    List<String> models = new ArrayList<String>();
	    while (results.hasNext()) {
		    QuerySolution row=results.nextSolution();
		    models.add(row.get("g").toString());
		  }
	    qexec.close();
	    dataset.close();
		return models;
		
	}
	
	public void clearTDBDatabase() throws IOException {
		FileUtils.cleanDirectory(new File(databaseConfig.getTDBDatasetPath()));
	}
}

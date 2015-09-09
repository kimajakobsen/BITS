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

	private DatabaseConfig databaseConfig;
	private Dataset dataset;
	//String assemblerFile = "src/main/resources/assembler.ttl" ;
	
	public DatabaseHandler(DatabaseConfig databaseConfig) throws IOException, InvalidDatabaseConfig {
		
		databaseConfig.validate();
		this.databaseConfig = databaseConfig;
		
		createTDBDirectoryIfNotExist();
		
		if (this.databaseConfig.isFreshLoad()) {
			clearTDBDatabase();
		}
		
		if (this.databaseConfig.getDatasetType().equals("TPC-H")) {
			loadTPCHDataset();
		}
		else if (this.databaseConfig.getDatasetType().equals("TPC-DS")) {
			//TODO
		}
		else {
			throw new InvalidDatabaseConfig("The Experiment Dataset "+this.databaseConfig.getDatasetType()+" is not known, implementation is missing.");
		}
	}
	
	private void createTDBDirectoryIfNotExist() {
		File theDir = new File(databaseConfig.getDatasetPath());

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
		String directory = databaseConfig.getTDBPath() ;
		this.dataset = TDBFactory.createDataset(directory);
		
		this.dataset.begin(ReadWrite.WRITE) ;
		try {
			//TODO
			//loop through the models, get model from onto, instead of all this repeat stuff.
			Model ontology = ModelFactory.createDefaultModel();
			Model dimensions = ModelFactory.createDefaultModel();
			Model facts = ModelFactory.createDefaultModel();
			
			Model customer = ModelFactory.createDefaultModel();
			Model nation = ModelFactory.createDefaultModel();
			Model orders = ModelFactory.createDefaultModel();
			Model part = ModelFactory.createDefaultModel();
			Model partsupp = ModelFactory.createDefaultModel();
			Model region = ModelFactory.createDefaultModel();
			Model supplier = ModelFactory.createDefaultModel();
			
			
			
			ontology.add(RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getDatasetType()+"/onto/tpc-h-qb4o-delivered-version.ttl"));
	
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
			dataset.addNamedModel(databaseConfig.getFactModelURL(), facts.add(dataset.getNamedModel(databaseConfig.getFactModelURL())));
		
			if (databaseConfig.getDimensionModelName().equals("#")) {
				//This might overwrite existing named models, this need to be tested further.
				dataset.addNamedModel(databaseConfig.getPrefix()+"customer", customer);
				dataset.addNamedModel(databaseConfig.getPrefix()+"nation", nation);
				dataset.addNamedModel(databaseConfig.getPrefix()+"orders", orders);
				dataset.addNamedModel(databaseConfig.getPrefix()+"part", part);
				dataset.addNamedModel(databaseConfig.getPrefix()+"partsupp", partsupp);
				dataset.addNamedModel(databaseConfig.getPrefix()+"region", region);
				dataset.addNamedModel(databaseConfig.getPrefix()+"supplier", supplier);
			} else {
				dataset.addNamedModel(databaseConfig.getDimensionModelURL(), customer.add(dataset.getNamedModel(databaseConfig.getDimensionModelURL())));
				dataset.addNamedModel(databaseConfig.getDimensionModelURL(), nation.add(dataset.getNamedModel(databaseConfig.getDimensionModelURL())));
				dataset.addNamedModel(databaseConfig.getDimensionModelURL(), orders.add(dataset.getNamedModel(databaseConfig.getDimensionModelURL())));
				dataset.addNamedModel(databaseConfig.getDimensionModelURL(), part.add(dataset.getNamedModel(databaseConfig.getDimensionModelURL())));
				dataset.addNamedModel(databaseConfig.getDimensionModelURL(), partsupp.add(dataset.getNamedModel(databaseConfig.getDimensionModelURL())));
				dataset.addNamedModel(databaseConfig.getDimensionModelURL(), region.add(dataset.getNamedModel(databaseConfig.getDimensionModelURL())));
				dataset.addNamedModel(databaseConfig.getDimensionModelURL(), supplier.add(dataset.getNamedModel(databaseConfig.getDimensionModelURL())));
			}
			dataset.commit();
		} finally {
			dataset.end();
		}
	}
	
	private String getTPCHPath(){
		return databaseConfig.getDatasetPath();
	}

	public Model getDefaultModel() {
		this.dataset.begin(ReadWrite.READ) ; 
		Model model = this.dataset.getDefaultModel();
		this.dataset.commit() ;
		return model;
	}
	
	public Model getOntologyModel()
	{
		return getModel(databaseConfig.getOntologyModelURL());
	}
	
	public String getOntologyModelURI()
	{
		return databaseConfig.getOntologyModelURL();
	}
	
	public Model getFactModel()
	{
		return getModel(databaseConfig.getFactModelURL());
	}
	
	public String getFactModelURI() {
		return databaseConfig.getFactModelURL();
	}
	
	public List<String> getDimensionModelURI() {
		List<String> modelURIs = getAllModelNames();
		modelURIs.remove(getFactModelURI());
		modelURIs.remove(getOntologyModelURI());
		return modelURIs;
	}
	
	public Model getModel(String modelName) {
		this.dataset.begin(ReadWrite.READ) ; 
		Model model = this.dataset.getNamedModel(modelName);
		this.dataset.commit() ;
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
	    dataset.commit();
		return models;
		
	}
	
	public void clearTDBDatabase() throws IOException {
		FileUtils.cleanDirectory(new File(databaseConfig.getTDBPath()));
	}

	public ResultSet executeQuery(Query query) {
		dataset.begin(ReadWrite.READ);
	    QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
	    ResultSet resultSet = qexec.execSelect();
	    dataset.commit();
		return resultSet;
	}
}

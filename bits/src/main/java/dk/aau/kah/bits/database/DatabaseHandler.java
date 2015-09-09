package dk.aau.kah.bits.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

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
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
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
		
		
		try {

			Model ontology = ModelFactory.createDefaultModel();
			ontology.add(RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getDatasetType()+"/onto/tpc-h-qb4o-delivered-version.ttl"));
			dataset.begin(ReadWrite.WRITE) ;
			dataset.addNamedModel(databaseConfig.getOntologyModelURL(), ontology.add(dataset.getNamedModel(databaseConfig.getOntologyModelURL())));
			dataset.commit();
			
			
			Model facts = ModelFactory.createDefaultModel();
			facts.add(RDFDataMgr.loadModel((getTPCHPath()+"/lineitem.ttl")));
			dataset.begin(ReadWrite.WRITE) ;
			dataset.addNamedModel(databaseConfig.getFactModelURL(), facts.add(dataset.getNamedModel(databaseConfig.getFactModelURL())));
			dataset.commit();
			
			HashMap<String,Model> levelModels = new HashMap<String,Model>();
			for (Resource resource : getDistinctLevelProperties()) {
				levelModels.put(resource.toString(), ModelFactory.createDefaultModel());
			}
			
			for (Entry<String, Model> resource : levelModels.entrySet()) {
				String filePath = pairLevelsWithFilesTPCH(resource.getKey()); 
				if (databaseConfig.getDimensionModelName().equals("#")) {
					dataset.begin(ReadWrite.WRITE) ;
					dataset.addNamedModel(resource.getKey(), RDFDataMgr.loadModel(filePath));
					dataset.commit();
				} else {
					//This might overwrite existing named models, this need to be tested further.
					dataset.begin(ReadWrite.WRITE) ;
					dataset.addNamedModel(databaseConfig.getDimensionModelURL(), RDFDataMgr.loadModel(filePath));
					dataset.commit();
				}
			}
			
//			if (databaseConfig.getDimensionModelName().equals("pt")) {
//				//PropertyTable customerPropertyTable = initilizePropertyTable(ontology,getTPCHPath()+"/customer.ttl");
//				//TODO
//			}

//			if (databaseConfig.getFactStorageModel().equals("pt")) {
//				QB4OLAPFactPropertyTable factTable = new QB4OLAPFactPropertyTable(ontology,facts); 
//				facts = factTable.getModel();
//			}
		} finally {
			dataset.end();
		}
	}
	
	private String pairLevelsWithFilesTPCH(String resource) {
		String[] split = resource.split("_");
		
		return getTPCHPath()+"/"+split[split.length-1]+".ttl";
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
	
	public List<String> getAllDimensionModelURI() {
		List<String> modelURIs = getAllModelNames();
		modelURIs.remove(getFactModelURI());
		modelURIs.remove(getOntologyModelURI());
		return modelURIs;
	}
	
	public String getDimensionModelURI()
	{
		if (databaseConfig.getDimensionModelName().equals("#")) {
			return null;
		}
		return databaseConfig.getDimensionModelURL();
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
	
	public HashSet<Resource> getDistinctLevelProperties()
	{
		HashSet<Resource> levelProperties = new HashSet<Resource>();
		Model ontologyModel = getOntologyModel();
		Property a = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		RDFNode levelProperty = ResourceFactory.createResource("http://publishing-multidimensional-data.googlecode.com/git/index.html#ref_qbplus_LevelProperty");
		levelProperties.addAll(ontologyModel.listSubjectsWithProperty(a, levelProperty).toList());
		return levelProperties;
	}
}

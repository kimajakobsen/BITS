package dk.aau.kah.bits.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.tdb.TDBFactory;

import da.aau.kah.bits.config.DatabaseConfig;
import da.aau.kah.bits.config.GeneralConfig;
import da.aau.kah.bits.exceptions.InvalidDatabaseConfig;

public class DatabaseHandler {

	private DatabaseConfig databaseConfig;
	private Dataset dataset;
	private GeneralConfig config;
	
	public DatabaseHandler(DatabaseConfig databaseConfig) throws IOException, InvalidDatabaseConfig {
		
		this.config = GeneralConfig.getInstance();
		
		databaseConfig.validate();
		this.databaseConfig = databaseConfig;
		
		createTDBDirectoryIfNotExist();
		
		if (this.databaseConfig.isFreshLoad()) {
			clearTDBDatabase();
		}
		
		if (this.databaseConfig.getDatasetType().equals("TPC-H")) {
			if (!doesTDBExist(databaseConfig.getTDBPath())) {
				dataset = TDBFactory.createDataset(databaseConfig.getTDBPath());
				TDBFactory.release(dataset);
				loadTPCHDataset();
			}
		}
		else if (this.databaseConfig.getDatasetType().equals("TPC-DS")) {
			//TODO
		}
		else {
			throw new InvalidDatabaseConfig("The Experiment Dataset "+this.databaseConfig.getDatasetType()+" is not known, implementation is missing.");
		}
		dataset = TDBFactory.createDataset(databaseConfig.getTDBPath());
			
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
		String directory = databaseConfig.getTDBPath();
		String tdbloader = config.getTdbLoaderPath();
		String ontologyPath = "src/main/resources/"+databaseConfig.getDatasetType()+"/onto/tpc-h-qb4o-delivered-version.ttl";
		
		executeBashCommand(tdbloader+" --loc="+directory+" --graph="+databaseConfig.getOntologyModelURL()+" "+ontologyPath , config.isVerbose());
		executeBashCommand(tdbloader+" --loc="+directory+" --graph="+databaseConfig.getFactModelURL()+" "+getTPCHPath()+"/lineitem.ttl" , config.isVerbose());
		if (databaseConfig.getDimensionModelName().equals("#")) {
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#l_has_customer "+getTPCHPath()+"/customer.ttl" , config.isVerbose());
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#l_has_nation "+getTPCHPath()+"/nation.ttl" , config.isVerbose());
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#l_has_orders "+getTPCHPath()+"/order.ttl" , config.isVerbose());
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#l_has_part "+getTPCHPath()+"/part.ttl" , config.isVerbose());
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#l_has_partsupp "+getTPCHPath()+"/partsupplier.ttl" , config.isVerbose());
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#l_has_region "+getTPCHPath()+"/region.ttl" , config.isVerbose());
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#l_has_supplier "+getTPCHPath()+"/supplier.ttl" , config.isVerbose());
		} else {
	        executeBashCommand(tdbloader+" --loc="+directory+" --graph="+databaseConfig.getDimensionModelURL()+" "+getTPCHPath()+"/customer.ttl" , config.isVerbose());
	        executeBashCommand(tdbloader+" --loc="+directory+" --graph="+databaseConfig.getDimensionModelURL()+" "+getTPCHPath()+"/nation.ttl" , config.isVerbose());
	        executeBashCommand(tdbloader+" --loc="+directory+" --graph="+databaseConfig.getDimensionModelURL()+" "+getTPCHPath()+"/order.ttl" , config.isVerbose());
	        executeBashCommand(tdbloader+" --loc="+directory+" --graph="+databaseConfig.getDimensionModelURL()+" "+getTPCHPath()+"/part.ttl" , config.isVerbose());
	        executeBashCommand(tdbloader+" --loc="+directory+" --graph="+databaseConfig.getDimensionModelURL()+" "+getTPCHPath()+"/partsupplier.ttl" , config.isVerbose());
	        executeBashCommand(tdbloader+" --loc="+directory+" --graph="+databaseConfig.getDimensionModelURL()+" "+getTPCHPath()+"/region.ttl" , config.isVerbose());
	        executeBashCommand(tdbloader+" --loc="+directory+" --graph="+databaseConfig.getDimensionModelURL()+" "+getTPCHPath()+"/supplier.ttl" , config.isVerbose());
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
	
	private void executeBashCommand(String command, Boolean verbose) {

		String s = null;
		 
        try {
             
            // using the Runtime exec method:
        	//"/usr/local/apache-jena-2.12.1/bin/tdbloader --help"
            Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
 
            // read the output from the command
            if (verbose) {
            	 BufferedReader stdInput = new BufferedReader(new
                         InputStreamReader(p.getInputStream()));
            	 
            	System.out.println("Here is the standard output of the command:\n");
                while ((s = stdInput.readLine()) != null) {
                    System.out.println(s);
                }
			}
            
            if (verbose) {
            	BufferedReader stdError = new BufferedReader(new
                        InputStreamReader(p.getErrorStream()));
            	
            	// read any errors from the attempted command
                System.out.println("Here is the standard error of the command (if any):\n");
                while ((s = stdError.readLine()) != null) {
                    System.out.println(s);
                }
			}
            
             
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
        }
	}
	
	public void closeConnection() {
		dataset.close();
	}
	
	private int countNumberOfFilesInFolder(String folder){
		return new File(folder).list().length;
	}

	private boolean doesTDBExist(String folder) {
		//The number five is selected by random. I know that there can be some random files remaining in the folders.
		if (countNumberOfFilesInFolder(folder) > 5) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean doesLockExist(String folder) {
		File f = new File(folder+"/tdb.lock");
		if(f.exists() && !f.isDirectory()) { 
			return true;
			}
		return false;
	}
}

package dk.aau.kah.bits.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.jena.propertytable.graph.GraphCSV;
import org.apache.jena.propertytable.lang.CSV2RDF;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.tdb.TDBFactory;

import da.aau.kah.bits.config.DatasetConfig;
import da.aau.kah.bits.config.PhysicalStorageConfig;
import da.aau.kah.bits.config.GeneralConfig;
import da.aau.kah.bits.exceptions.InvalidDatabaseConfig;

public class DatabaseHandler {

	private PhysicalStorageConfig physicalStorageConfig;
	private DatasetConfig datasetConfig;
	private Dataset dataset;
	private GeneralConfig config;
	

	public DatabaseHandler(PhysicalStorageConfig physicalStorageConfig, DatasetConfig datasetConfig) throws IOException, InvalidDatabaseConfig {
		
		this.config = GeneralConfig.getInstance();
		
		physicalStorageConfig.validate();
		datasetConfig.validate();
		
		this.datasetConfig = datasetConfig;
		this.physicalStorageConfig = physicalStorageConfig;
		
		createTDBDirectoryIfNotExist();
		
		if (this.datasetConfig.isFreshLoad()) {
			clearTDBDatabase();
		}
		
		if (this.datasetConfig.getDatasetType().equals("TPC-H")) {
			if (!doesTDBExist(getTDBPath())) {
				dataset = TDBFactory.createDataset(getTDBPath());
				TDBFactory.release(dataset);
				loadTPCHDataset();
			}
		}
		else if (this.datasetConfig.getDatasetType().equals("TPC-DS")) {
			if (!doesTDBExist(getTDBPath())) {
				dataset = TDBFactory.createDataset(getTDBPath());
				TDBFactory.release(dataset);
				loadTPCDSDataset();
			}
		}
		else {
			throw new InvalidDatabaseConfig("The Experiment Dataset "+this.datasetConfig.getDatasetType()+" is not known, implementation is missing.");
		}
		dataset = TDBFactory.createDataset(getTDBPath());
			
	}
	
	private void createTDBDirectoryIfNotExist() {
		File theDir = new File(getTDBPath());

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
	
	private void loadTPCDSDataset() {
		CSV2RDF.init() ;
		String directory = getTDBPath();
		Model model_csv_array_impl = ModelFactory.createModelForGraph(new GraphCSV(getDatasetPath()+"/item.dat")); // PropertyTableArrayImpl
		Dataset dataset = TDBFactory.createDataset(directory) ;
		dataset.begin(ReadWrite.WRITE);
		dataset.addNamedModel("http://example/table1", model_csv_array_impl) ;
		dataset.commit();
		
	}

	private void loadTPCHDataset() throws IOException {
		String directory = getTDBPath();
		String tdbloader = config.getTdbLoaderPath();
		String ontologyPath = "src/main/resources/"+datasetConfig.getDatasetType()+"/onto/tpc-h-qb4o-delivered-version.ttl";
		
		executeBashCommand(tdbloader+" --loc="+directory+" --graph="+physicalStorageConfig.getOntologyModelURL()+" "+ontologyPath , config.isVerbose());
		executeBashCommand(tdbloader+" --loc="+directory+" --graph="+physicalStorageConfig.getFactModelURL()+" "+getDatasetPath()+"/lineitem.ttl" , config.isVerbose());
		if (physicalStorageConfig.getDimensionModelName().equals("level")) {
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#has_customer "+getDatasetPath()+"/customer.ttl" , config.isVerbose());
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#has_nation "+getDatasetPath()+"/nation.ttl" , config.isVerbose());
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#has_order "+getDatasetPath()+"/order.ttl" , config.isVerbose());
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#has_part "+getDatasetPath()+"/part.ttl" , config.isVerbose());
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#has_partsupp "+getDatasetPath()+"/partsupplier.ttl" , config.isVerbose());
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#has_region "+getDatasetPath()+"/region.ttl" , config.isVerbose());
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#has_supplier "+getDatasetPath()+"/supplier.ttl" , config.isVerbose());
		} else if(physicalStorageConfig.getDimensionModelName().equals("dimension")) {
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#orderDim "+getDatasetPath()+"/customer.ttl" , config.isVerbose());
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#orderDim "+getDatasetPath()+"/nation.ttl" , config.isVerbose());
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#orderDim "+getDatasetPath()+"/order.ttl" , config.isVerbose());
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#partDim "+getDatasetPath()+"/part.ttl" , config.isVerbose());
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#supplierDim "+getDatasetPath()+"/partsupplier.ttl" , config.isVerbose());
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#orderDim "+getDatasetPath()+"/region.ttl" , config.isVerbose());
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#supplierDim "+getDatasetPath()+"/supplier.ttl" , config.isVerbose());
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#supplierDim "+getDatasetPath()+"/nation.ttl" , config.isVerbose()); //duplicate data
			executeBashCommand(tdbloader+" --loc="+directory+" --graph=http://lod2.eu/schemas/rdfh#supplierDim "+getDatasetPath()+"/region.ttl" , config.isVerbose()); //duplicate data
		} else {
	        executeBashCommand(tdbloader+" --loc="+directory+" --graph="+physicalStorageConfig.getDimensionModelURL()+" "+getDatasetPath()+"/customer.ttl" , config.isVerbose());
	        executeBashCommand(tdbloader+" --loc="+directory+" --graph="+physicalStorageConfig.getDimensionModelURL()+" "+getDatasetPath()+"/nation.ttl" , config.isVerbose());
	        executeBashCommand(tdbloader+" --loc="+directory+" --graph="+physicalStorageConfig.getDimensionModelURL()+" "+getDatasetPath()+"/order.ttl" , config.isVerbose());
	        executeBashCommand(tdbloader+" --loc="+directory+" --graph="+physicalStorageConfig.getDimensionModelURL()+" "+getDatasetPath()+"/part.ttl" , config.isVerbose());
	        executeBashCommand(tdbloader+" --loc="+directory+" --graph="+physicalStorageConfig.getDimensionModelURL()+" "+getDatasetPath()+"/partsupplier.ttl" , config.isVerbose());
	        executeBashCommand(tdbloader+" --loc="+directory+" --graph="+physicalStorageConfig.getDimensionModelURL()+" "+getDatasetPath()+"/region.ttl" , config.isVerbose());
	        executeBashCommand(tdbloader+" --loc="+directory+" --graph="+physicalStorageConfig.getDimensionModelURL()+" "+getDatasetPath()+"/supplier.ttl" , config.isVerbose());
		}
	}

	private String getDatasetPath(){
		return datasetConfig.getDatasetPath();
	}

	public Model getDefaultModel() {
		this.dataset.begin(ReadWrite.READ) ; 
		Model model = this.dataset.getDefaultModel();
		this.dataset.commit() ;
		return model;
	}
	
	public Model getOntologyModel()
	{
		return getModel(physicalStorageConfig.getOntologyModelURL());
	}
	
	public String getOntologyModelURI()
	{
		return physicalStorageConfig.getOntologyModelURL();
	}
	
	public Model getFactModel()
	{
		return getModel(physicalStorageConfig.getFactModelURL());
	}
	
	public String getFactModelURI() {
		return physicalStorageConfig.getFactModelURL();
	}
	
	public List<String> getAllDimensionModelURI() {
		List<String> modelURIs = getAllModelNames();
		modelURIs.remove(getFactModelURI());
		modelURIs.remove(getOntologyModelURI());
		return modelURIs;
	}
	
	public String getDimensionModelURI()
	{
		if (physicalStorageConfig.getDimensionModelName().equals("#")) {
			return null;
		}
		return physicalStorageConfig.getDimensionModelURL();
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
	
	private String getTDBPath() {
		String part1 = physicalStorageConfig.getConfigFileName().substring(0, physicalStorageConfig.getConfigFileName().length()-1);
		String part2 = datasetConfig.getConfigFileName();
		
		return "src/main/resources/tdb/"+ part1 + "_" + part2;
	}
	
	public void clearTDBDatabase() throws IOException {
		FileUtils.cleanDirectory(new File(getTDBPath()));
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

	public List<File> getQueries() {
		return datasetConfig.getQueries();
	}
	
	public String getDatasetType() {
		return datasetConfig.getDatasetType();
	}
}

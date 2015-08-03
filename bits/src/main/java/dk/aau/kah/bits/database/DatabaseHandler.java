package dk.aau.kah.bits.database;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;

public class DatabaseHandler {

	public DatabaseConfig databaseConfig;
	//String assemblerFile = "src/main/resources/assembler.ttl" ;
	
	public DatabaseHandler(DatabaseConfig databaseConfig) throws IOException {
		try {
			databaseConfig.validate();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		// Create a local reference to the config object
		this.databaseConfig = databaseConfig;
		
		if (doesDatabaseExist() == false) {
			clearTDBDatabase();
			InitilizeAndLoadTDBDatabase();
		}
	}
	
	private Dataset InitilizeAndLoadTDBDatabase() {
		Dataset dataset = TDBFactory.createDataset(databaseConfig.getTDBPath());
		dataset.begin(ReadWrite.WRITE) ;
		
		try {
			Model facts = RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getScaleFactorString()+"/lineitem.ttl") ;
			Model customer = RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getScaleFactorString()+"/customer.ttl") ;
			Model nation = RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getScaleFactorString()+"/nation.ttl") ;
			Model orders = RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getScaleFactorString()+"/orders.ttl") ;
			Model part = RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getScaleFactorString()+"/part.ttl") ;
			Model partsupp = RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getScaleFactorString()+"/partsupp.ttl") ;
			Model region = RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getScaleFactorString()+"/region.ttl") ;
			Model supplier = RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getScaleFactorString()+"/supplier.ttl") ;
			Model ontology = RDFDataMgr.loadModel("src/main/resources/onto/tpc-h-qb4o-delivered-version.ttl") ;
		

			// add logic for the other configs
			// TODO
			
			Model model = ModelFactory.createDefaultModel();
			model.add(facts);
			model.add(customer);
			model.add(nation);
			model.add(orders);
			model.add(part);
			model.add(partsupp);
			model.add(region);
			model.add(supplier);
			model.add(ontology);
			
			
			dataset.addNamedModel(databaseConfig.getDimensionModelName(), model);
			
			dataset.commit();
		} finally {
			dataset.end();
		}
		
		
		
		return dataset;
	}

	public Model getDefaultModel() {
		return getDataSet("default");
	}
	
	public Model getOntologyModel() {
		return getDataSet("ontology");
	}
	
	public Model getFactModel() {
		return getDataSet("fact");
	}
	
	public Model getDimensionModel() {
		return getDataSet("dimension");
	}
	
	
	
	
	private Model getDataSet(String modelType) {
		String path = databaseConfig.getTDBPath();
		System.out.println(path);
		Dataset dataset = TDBFactory.createDataset(path);
		
		Model model;
		
		if (modelType == "default") {
			dataset.begin(ReadWrite.READ) ; 
			model = dataset.getDefaultModel();
			dataset.end() ;
		} else {
			dataset.begin(ReadWrite.READ) ; 
			model = dataset.getNamedModel(modelType);
			dataset.end() ;
		}
			  
		return model;
		
	}
	
	private boolean doesDatabaseExist() {
		// TODO
		return false;
		
	}
	

	
	public void clearTDBDatabase() throws IOException {

			FileUtils.cleanDirectory(databaseConfig.getTDBPathFile());

	}
}

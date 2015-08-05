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
import org.apache.jena.tdb.TDB;
import org.apache.jena.tdb.TDBFactory;

import dk.aau.kah.bits.helpers.PrintHelper;

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
			//clearTDBDatabase();
			InitilizeAndLoadTDBDatabase();
		}
	}
	
	private Dataset InitilizeAndLoadTDBDatabase() {
		String directory = databaseConfig.getTDBPath() ;
		Dataset dataset = TDBFactory.createDataset(directory);
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
			TDB.sync(dataset);
			model.close();
			dataset.end();
			
		} finally {
			dataset.close();
		}
		
		
		
		return dataset;
	}

	//public Model getDefaultModel() {
	//	return getDataSet(databaseConfig.);
	//}
	
	public Model getOntologyModel() {
		return getDataset(databaseConfig.getOntologyModelName());
	}
	
	public Model getFactModel() {
		return getDataset(databaseConfig.getFactModelName());
	}
	
	public Model getDimensionModel() {
		return getDataset(databaseConfig.getDimensionModelName());
	}
	
	
	
	
	private Model getDataset(String modelType) {
		String path = databaseConfig.getTDBPath();

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

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

import da.aau.kah.bits.exceptions.InvalidDatabaseConfig;
import dk.aau.kah.bits.helpers.PrintHelper;

public class DatabaseHandler {

	public DatabaseConfig databaseConfig;
	private Dataset dataset;
	//String assemblerFile = "src/main/resources/assembler.ttl" ;
	
	public DatabaseHandler(DatabaseConfig databaseConfig) throws IOException, InvalidDatabaseConfig {
		
		databaseConfig.validate();
		this.databaseConfig = databaseConfig;
		
		if (this.databaseConfig.doFreshLoad()) {
			clearTDBDatabase();
		}
		
		if (this.databaseConfig.getExperimentDataset().equals("TPCH-H")) {
			loadTPCHExperimentalDataset();
		}
		else if (this.databaseConfig.getExperimentDataset().equals("TPCH-DS")) {
			//TODO
		}
			
		
	}
	
	private void loadTPCHExperimentalDataset() {
		String directory = databaseConfig.getTDBPath() ;
		this.dataset = TDBFactory.createDataset(directory);
		
		loadTDBIntoSingleModel();

	}
	
	
	private void loadTDBIntoSingleModel() {
		this.dataset.begin(ReadWrite.WRITE) ;
		
		try {
			Model model = ModelFactory.createDefaultModel();
			model.add(RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getScaleFactorString()+"/lineitem.ttl")) ;
			model.add(RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getScaleFactorString()+"/customer.ttl")) ;
			model.add(RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getScaleFactorString()+"/nation.ttl")) ;
			model.add(RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getScaleFactorString()+"/orders.ttl")) ;
			model.add(RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getScaleFactorString()+"/part.ttl")) ;
			model.add(RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getScaleFactorString()+"/partsupp.ttl")) ;
			model.add(RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getScaleFactorString()+"/region.ttl")) ;
			model.add(RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getScaleFactorString()+"/supplier.ttl")) ;
			model.add(RDFDataMgr.loadModel("src/main/resources/onto/tpc-h-qb4o-delivered-version.ttl")) ;
			
			dataset.addNamedModel(databaseConfig.getDimensionModelName(), model);
			dataset.commit();
			TDB.sync(dataset);
			dataset.end();
			
		} finally {
			dataset.close();
		}
	}

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

			FileUtils.cleanDirectory(databaseConfig.getTDBPathFile());

	}
}

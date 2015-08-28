package dk.aau.kah.bits.database;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.jena.propertytable.PropertyTable;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;

import da.aau.kah.bits.exceptions.InvalidDatabaseConfig;

public class DatabaseHandler {

	public DatabaseConfig databaseConfig;
	private Dataset dataset;
	//String assemblerFile = "src/main/resources/assembler.ttl" ;
	
	public DatabaseHandler(DatabaseConfig databaseConfig) throws IOException, InvalidDatabaseConfig {
		
		databaseConfig.validate();
		this.databaseConfig = databaseConfig;
		
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
	
	private void loadTPCHDataset() {
		String directory = databaseConfig.getTDBFilePath() ;
		this.dataset = TDBFactory.createDataset(directory);
		
		this.dataset.begin(ReadWrite.WRITE) ;
		try {
			
			Model ontology = ModelFactory.createDefaultModel();
			Model dimensions = ModelFactory.createDefaultModel();
			Model facts = ModelFactory.createDefaultModel();
			
			
			ontology.add(RDFDataMgr.loadModel("src/main/resources/"+databaseConfig.getExperimentDataset()+"/onto/tpc-h-qb4o-delivered-version.ttl"));
	
			if (databaseConfig.getDimensionModelName().equals("st")) {
				dimensions.add(RDFDataMgr.loadModel(getTPCHPath()+"/customer.ttl"));
				dimensions.add(RDFDataMgr.loadModel(getTPCHPath()+"/nation.ttl"));
				dimensions.add(RDFDataMgr.loadModel(getTPCHPath()+"/orders.ttl"));
				dimensions.add(RDFDataMgr.loadModel(getTPCHPath()+"/part.ttl"));
				dimensions.add(RDFDataMgr.loadModel(getTPCHPath()+"/partsupp.ttl"));
				dimensions.add(RDFDataMgr.loadModel(getTPCHPath()+"/region.ttl"));
				dimensions.add(RDFDataMgr.loadModel(getTPCHPath()+"/supplier.ttl"));
			} else if (databaseConfig.getDimensionModelName().equals("pt")) {
				//PropertyTable customerPropertyTable = initilizePropertyTable(ontology,getTPCHPath()+"/customer.ttl");
				//TODO
			}

			if (databaseConfig.getFactModelName().equals("st")) {
				facts.add(RDFDataMgr.loadModel(getTPCHPath()+"/lineitem.ttl"));
			} else if (databaseConfig.getFactModelName().equals("pt")) {
				PropertyTable factPropertyTable = initilizePropertyTable(ontology,getTPCHPath()+"/lineitem.ttl");
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
	

	
	private PropertyTable initilizePropertyTable(Model ontology, String path) {
		List<String> destinctProperties;
		String sparql = "select distinct ?b where {?a ?b ?c}";

		Query qry = QueryFactory.create(sparql);
		QueryExecution qe = QueryExecutionFactory.create(qry, ontology);
		ResultSet rs = qe.execSelect();
		
		ResultSetFormatter.out(System.out, rs, qry) ;
		
		
		return null;
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

			FileUtils.cleanDirectory(new File(databaseConfig.getTDBFilePath()));

	}
}

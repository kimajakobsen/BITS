package dk.aau.kah.bits.main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.management.openmbean.InvalidKeyException;

import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.InvalidListException;

import da.aau.kah.bits.config.DatasetConfig;
import da.aau.kah.bits.config.GeneralConfig;
import da.aau.kah.bits.config.PhysicalStorageConfig;
import da.aau.kah.bits.exceptions.InvalidDatabaseConfig;
import dk.aau.kah.bits.database.DatabaseHandler;
import dk.aau.kah.bits.experiment.AbstractExperimentHandler;
import dk.aau.kah.bits.experiment.ExperimentFactory;
import dk.aau.kah.bits.helpers.ConfigurationLoader;

public class Run {
	DatabaseHandler databaseHandler;
	PhysicalStorageConfig physicalStorageConfig;
	DatasetConfig datasetConfig;
	AbstractExperimentHandler experimentHandler;
	
	public Run(String physicalStorageConfigName, String datasetConfigName) {
		try {
			physicalStorageConfig = ConfigurationLoader.loadPhysicalStorageConfig(physicalStorageConfigName);
			datasetConfig = ConfigurationLoader.loadDatasetConfig(datasetConfigName);
			
			//ConfigurationLoader.loadSystemConfig("kah-ThinkPad-X220.json");
			//Problem with this is properbly due to toString function
			GeneralConfig generalConfig = GeneralConfig.getInstance();
			generalConfig.setTdbLoaderPath("/usr/local/apache-jena-2.12.1/bin/tdbloader");
			generalConfig.setVerbose(true);
		
			databaseHandler = new DatabaseHandler(physicalStorageConfig,datasetConfig);
			
			ExperimentFactory experimentFactory = new ExperimentFactory();
			experimentHandler = experimentFactory.makeEvaluation(databaseHandler);
			experimentHandler.run();
//			
			//experimentHandler.printResults();
			
			databaseHandler.closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidDatabaseConfig e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		return physicalStorageConfig.getConfigFileName().substring(0,physicalStorageConfig.getConfigFileName().length()-1 ) +"-"+ databaseHandler.getDatasetType() ;
	}

	public HashMap<String, String> getResults() {
		try {
			return experimentHandler.getResults();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean equals(Run otherRun) {
		HashMap<String, String> localResults = this.getResults();
		HashMap<String, String> otherResults = otherRun.getResults();
		
		compareHashMap(localResults, otherResults);
		for (Entry<String, String> iterable_element : localResults.entrySet()) {
			if (!iterable_element.getValue().equals(otherResults.get(iterable_element.getKey()))) {
				return false; 
			}
		}
		return true;
	}
	
	public long getResultTimeTotal() {
		Long totalMilliseconds = (long) 0;
		try {
			for (Entry<String, Long> iterable_element : experimentHandler.getResultTimes().entrySet()) {
				totalMilliseconds += iterable_element.getValue();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return totalMilliseconds;
	}
	
	public HashMap<String, Query> getResultTimes() {
		try {
			return experimentHandler.getQueries();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public long getResultTime(String query) {
		try {
			return experimentHandler.getResultTime(query);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public void compareQueryTimes(Run otherRun) {
		compareHashMap(this.getResults(),otherRun.getResults());
		
		System.out.println("\t\t"+this.toString()+"\t"+otherRun.toString());
		for (Entry<String, String> iterator : this.getResults().entrySet()) {
			System.out.println(iterator.getKey() + ": "+iterator.getValue()+"\t"+otherRun.getResultTime(iterator.getKey()));
		}
	}
	
	private boolean compareHashMap(HashMap<String,String> map1, HashMap<String,String> map2) {
		if (map1.size() != map2.size()) {
			throw new InvalidListException("The HashMaps are not equal in size: "+map1.size() + " !=" + map2.size());
		}
		
		for (Entry<String, String> iterator : map1.entrySet()) {
			if (!map2.containsKey(iterator.getKey())) {
				throw new InvalidKeyException("The key: "+ iterator.getKey() + " is not contained in " + map2.toString());
			} 
		}
		
		for (Entry<String, String> iterator : map2.entrySet()) {
			if (!map1.containsKey(iterator.getKey())) {
				throw new InvalidKeyException("The key: "+ iterator.getKey() + " is not contained in " + map1.toString());
			} 
		}
		return true;
	}
}

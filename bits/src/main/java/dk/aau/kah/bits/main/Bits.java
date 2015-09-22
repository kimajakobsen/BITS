package dk.aau.kah.bits.main;

import java.io.IOException;

import da.aau.kah.bits.config.DatabaseConfig;
import da.aau.kah.bits.config.GeneralConfig;
import da.aau.kah.bits.exceptions.InvalidDatabaseConfig;
import dk.aau.kah.bits.database.DatabaseHandler;
import dk.aau.kah.bits.experiment.AbstractExperimentHandler;
import dk.aau.kah.bits.experiment.ExperimentFactory;
import dk.aau.kah.bits.helpers.ConfigurationLoader;

public class Bits {

	public static void main(String[] args) {

		DatabaseHandler databaseHandler;
		DatabaseConfig databaseConfig;

		try {
			databaseConfig = ConfigurationLoader.loadDatabaseConfig("onto0st-fact1st-dim#st.json");
			
			//ConfigurationLoader.loadSystemConfig("kah-ThinkPad-X220.json");
			GeneralConfig generalConfig = GeneralConfig.getInstance();
			generalConfig.setTdbLoaderPath("/usr/local/apache-jena-2.12.1/bin/tdbloader");
			generalConfig.setVerbose(true);
		
			databaseHandler = new DatabaseHandler(databaseConfig);
			
			//System.out.println("all models: "+databaseHandler.getAllModelNames());
			
			ExperimentFactory experimentFactory = new ExperimentFactory();
			AbstractExperimentHandler experimentHandler = experimentFactory.makeEvaluation(databaseHandler, databaseConfig);
			experimentHandler.run();
			experimentHandler.getResults();
			
			databaseHandler.closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidDatabaseConfig e) {
			e.printStackTrace();
		}

		
		
		// Query database

		// display/save results

	}

}

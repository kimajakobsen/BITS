package dk.aau.kah.bits.main;

import java.io.IOException;

import org.apache.jena.rdf.model.Model;

import dk.aau.kah.bits.database.DatabaseConfig;
import dk.aau.kah.bits.database.DatabaseHandler;
import dk.aau.kah.bits.helpers.PrintHelper;


public class Bits {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Parse input parameters
		
		//get or create database
		DatabaseConfig databaseConfig = new DatabaseConfig();
		databaseConfig.setDimensionModelName("all");
		databaseConfig.setDimensionStorageModel("st");
		databaseConfig.setFactModelName("all");
		databaseConfig.setFactStorageModel("st");
		databaseConfig.setOntologyModelName("all");
		databaseConfig.setOntologyStorageModel("st");
		databaseConfig.setScaleFactor("0.0001");
		
		
		DatabaseHandler DBHandler;
		try {
			DBHandler = new DatabaseHandler(databaseConfig);
			Model defaultModel = DBHandler.getFactModel();
			PrintHelper.printModel(defaultModel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		

		//Query database
		
		//display/save results

	}
	


}

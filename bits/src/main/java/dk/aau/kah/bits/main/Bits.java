package dk.aau.kah.bits.main;

import java.io.IOException;

import org.apache.jena.rdf.model.Model;

import da.aau.kah.bits.exceptions.InvalidDatabaseConfig;
import dk.aau.kah.bits.database.DatabaseConfig;
import dk.aau.kah.bits.database.DatabaseHandler;
import dk.aau.kah.bits.helpers.ConfigurationLoader;
import dk.aau.kah.bits.helpers.PrintHelper;


public class Bits {

	public static void main(String[] args) {	
		DatabaseHandler databaseHandler;
		DatabaseConfig databaseConfig;
	
		try {
			databaseConfig = ConfigurationLoader.load("onto0st-fact0st-dim0st.json");
			databaseHandler = new DatabaseHandler(databaseConfig);
			Model defaultModel = databaseHandler.getFactModel();
			PrintHelper.printModel(defaultModel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidDatabaseConfig e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		

		//Query database
		
		//display/save results

	}
	


}

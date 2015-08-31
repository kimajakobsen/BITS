package dk.aau.kah.bits.main;

import java.io.IOException;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.propertytable.Column;
import org.apache.jena.propertytable.PropertyTable;
import org.apache.jena.propertytable.Row;
import org.apache.jena.propertytable.graph.GraphPropertyTable;
import org.apache.jena.propertytable.impl.PropertyTableArrayImpl;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import da.aau.kah.bits.exceptions.InvalidDatabaseConfig;
import dk.aau.kah.bits.database.DatabaseConfig;
import dk.aau.kah.bits.database.DatabaseHandler;
import dk.aau.kah.bits.helpers.ConfigurationLoader;


public class Bits {

	public static void main(String[] args) {	
		
		DatabaseHandler databaseHandler;
		DatabaseConfig databaseConfig;
	
		try {
			//databaseConfig = ConfigurationLoader.load("onto0st-fact0st-dim0st.json");
			databaseConfig = ConfigurationLoader.load("onto0st-fact1pt-dim#pt.json");
			databaseHandler = new DatabaseHandler(databaseConfig);
			Model factModel = databaseHandler.getFactModel();
			//PrintHelper.printModel(factModel);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidDatabaseConfig e) {
			e.printStackTrace();
		} 
		

		
		//Query database
		
		//display/save results

	}
	


}

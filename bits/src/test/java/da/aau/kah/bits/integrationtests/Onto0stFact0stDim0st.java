package da.aau.kah.bits.integrationtests;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.collections15.list.SetUniqueList;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import da.aau.kah.bits.exceptions.InvalidDatabaseConfig;
import dk.aau.kah.bits.database.DatabaseConfig;
import dk.aau.kah.bits.database.DatabaseHandler;
import dk.aau.kah.bits.helpers.ConfigurationLoader;

public class Onto0stFact0stDim0st extends AbstractIntegrationTest {
	private DatabaseConfig databaseConfig;
	private DatabaseHandler databaseHandler;
	private String fileName = "onto0st-fact0st-dim0st.json";
	
	@Before
	public void setup(){
		try {
			databaseConfig = ConfigurationLoader.load(fileName);
			databaseHandler = new DatabaseHandler(databaseConfig);
		} catch (FileNotFoundException e) {
			fail("File not found");
			e.printStackTrace();
		} catch (InvalidDatabaseConfig e) {
			fail("Config is invalid");
			e.printStackTrace();
		} catch (IOException e) {
			fail("File not readable");
			e.printStackTrace();
		}
	}
	
	@After
	public void teardown(){
	}
	
	@Test
	public void numberOfModels() {
		//Operate
		int numberOfModels = countNumberOfModels(databaseHandler); 
		
		//Check
		assertEquals("There should only be one model in the config: " + fileName, 1, numberOfModels);
	}
	
	@Test
	public void nameOfModels() {
		HashSet<String> modelNamesFromConfig = new HashSet<String>();
		
		modelNamesFromConfig.add(databaseConfig.getDimensionModelURL());
		modelNamesFromConfig.add(databaseConfig.getFactModelURL());
		modelNamesFromConfig.add(databaseConfig.getOntologyModelURL());
			
		HashSet<String> modelNamesFromHandler = new HashSet<String>();
		for (String modelName : databaseHandler.getAllModelNames()) {
			modelNamesFromHandler.add(modelName);
		}
				
		//Check
		assertThat(modelNamesFromHandler, IsIterableContainingInOrder.contains(modelNamesFromConfig.toArray()));
	}

}

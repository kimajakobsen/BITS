package da.aau.kah.bits.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import da.aau.kah.bits.config.DatasetConfig;
import da.aau.kah.bits.config.PhysicalStorageConfig;
import da.aau.kah.bits.exceptions.InvalidDatabaseConfig;
import dk.aau.kah.bits.database.DatabaseHandler;
import dk.aau.kah.bits.helpers.ConfigurationLoader;

public class Onto0stFact0stDim0stTest extends AbstractIntegrationTest {
	private PhysicalStorageConfig physicalStorageConfig;
	private DatasetConfig datasetConfig;
	private DatabaseHandler databaseHandler;
	private String physicalStorageConfigFileName = "base.json";
	private String datasetConfigFileName = "TPCH-sf0.1.json";
	
	
	@Before
	public void setup(){
		try {
			physicalStorageConfig = ConfigurationLoader.loadPhysicalStorageConfig(physicalStorageConfigFileName);
			datasetConfig = ConfigurationLoader.loadDatasetConfig(datasetConfigFileName);
			databaseHandler = new DatabaseHandler(physicalStorageConfig, datasetConfig);
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
		assertEquals("There should only be one model in the config: " + physicalStorageConfigFileName, 1, numberOfModels);
	}
	
	@Test
	public void nameOfModels() {
		HashSet<String> modelNamesFromConfig = new HashSet<String>();
		
		modelNamesFromConfig.add(physicalStorageConfig.getDimensionModelURL());
		modelNamesFromConfig.add(physicalStorageConfig.getFactModelURL());
		modelNamesFromConfig.add(physicalStorageConfig.getOntologyModelURL());
			
		HashSet<String> modelNamesFromHandler = new HashSet<String>();
		for (String modelName : databaseHandler.getAllModelNames()) {
			modelNamesFromHandler.add(modelName);
		}
				
		//Check
		assertThat(modelNamesFromHandler, IsIterableContainingInOrder.contains(modelNamesFromConfig.toArray()));
	}

}

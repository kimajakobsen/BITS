package dk.aau.kah.bits.helpers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;

import da.aau.kah.bits.config.PhysicalStorageConfig;
import da.aau.kah.bits.config.GeneralConfig;

public class ConfigurationLoader {

	public static PhysicalStorageConfig loadDatabaseConfig(String JSONFile) throws FileNotFoundException {
		String path = "src/main/resources/databaseConfigurations/";
		BufferedReader configuration = new BufferedReader(new FileReader(path+JSONFile));
		Gson gson = new Gson();	
		PhysicalStorageConfig config = gson.fromJson(configuration, PhysicalStorageConfig.class);
		config.setConfigFileName(JSONFile);
		config.setTDBPath("src/main/resources/tdb/"+JSONFile.substring(0, JSONFile.length()-5)+"/");
		return config;
		
	}
	
	public static void loadSystemConfig(String JSONFile) throws FileNotFoundException {
		Gson gson = new Gson();	
		String path = "src/main/resources/systemConfigurations/";
		BufferedReader configuration = new BufferedReader(new FileReader(path+JSONFile));
		GeneralConfig config = GeneralConfig.getInstance();
		config = gson.fromJson(configuration, GeneralConfig.class);
	}
}

package dk.aau.kah.bits.helpers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;

import da.aau.kah.bits.config.DatabaseConfig;
import da.aau.kah.bits.config.GeneralConfig;

public class ConfigurationLoader {

	public static DatabaseConfig loadDatabaseConfig(String JSONFile) throws FileNotFoundException {
		String path = "src/main/resources/databaseConfigurations/";
		BufferedReader configuration = new BufferedReader(new FileReader(path+JSONFile));
		Gson gson = new Gson();	
		DatabaseConfig config = gson.fromJson(configuration, DatabaseConfig.class);
		config.setConfigFileName(JSONFile);
		config.setTDBPath("src/main/resources/tdb/"+JSONFile.substring(0, JSONFile.length()-5)+"/");
		return config;
		
	}
	
	public static GeneralConfig loadSystemConfig(String JSONFile) throws FileNotFoundException {
		String path = "src/main/resources/systemConfigurations/";
		BufferedReader configuration = new BufferedReader(new FileReader(path+JSONFile));
		Gson gson = new Gson();	
		GeneralConfig config = gson.fromJson(configuration, GeneralConfig.class);
		return config;
		
	}
}

package dk.aau.kah.bits.helpers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;

import dk.aau.kah.bits.database.DatabaseConfig;

public class ConfigurationLoader {

	public static DatabaseConfig load(String JSONFile) throws FileNotFoundException {
		String path = "src/main/resources/databaseConfigurations/";
		BufferedReader configuration = new BufferedReader(new FileReader(path+JSONFile));
		Gson gson = new Gson();	
		DatabaseConfig config = gson.fromJson(configuration, DatabaseConfig.class);
		config.setConfigFileName(JSONFile);
		config.setTDBDatasetPath("src/main/resources/tdb/"+JSONFile.substring(0, JSONFile.length()-5)+"/");
		return config;
		
	}
}

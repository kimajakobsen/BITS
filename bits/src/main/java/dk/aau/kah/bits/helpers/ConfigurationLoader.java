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
		config.setTDBFileName(JSONFile);
		config.setTDBFilePath(path+JSONFile);
		return config;
		
	}
}

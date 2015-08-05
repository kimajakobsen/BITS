package dk.aau.kah.bits.helpers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;

import dk.aau.kah.bits.database.DatabaseConfig;

public class ConfigurationLoader {

	public static DatabaseConfig load(String JSONFile) throws FileNotFoundException {
		BufferedReader configuration = new BufferedReader(new FileReader("src/main/resources/databaseConfigurations/"+JSONFile));
		Gson gson = new Gson();	
		return gson.fromJson(configuration, DatabaseConfig.class);
		
	}
}

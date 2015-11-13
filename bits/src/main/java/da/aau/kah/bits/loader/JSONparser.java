package da.aau.kah.bits.loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;

public class JSONparser {

	public ConfigTPCDS load () {
		
		Gson gson = new Gson();

		try {
			BufferedReader br = new BufferedReader(
				new FileReader("src/main/resources/TPC-DS/json/tpcds_schema.json"));

			//convert the json string back to object
			ConfigTPCDS obj = gson.fromJson(br, ConfigTPCDS.class);
			return obj;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

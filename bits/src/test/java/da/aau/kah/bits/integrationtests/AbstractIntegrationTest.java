package da.aau.kah.bits.abstracttests;

import java.util.List;

import dk.aau.kah.bits.database.DatabaseHandler;

public abstract class AbstractIntegrationTest {

	
	public int countNumberOfModels(DatabaseHandler dh) {
		List<String> models = dh.getAllModelNames();
		
		return models.size();
		
	}

}

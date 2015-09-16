package dk.aau.kah.bits.evaluation;

import da.aau.kah.bits.config.DatabaseConfig;
import da.aau.kah.bits.exceptions.InvalidDatabaseConfig;
import dk.aau.kah.bits.database.DatabaseHandler;

public class ExperimentFactory {

	public ExperimentFactory() {
		
	}

	public AbstractExperimentHandler makeEvaluation(DatabaseHandler databaseHandle, DatabaseConfig databaseConfig) throws InvalidDatabaseConfig {
		String datasetType = databaseConfig.getDatasetType();
		AbstractExperimentHandler evaluationhandler;
		
		if (datasetType.equals("TPC-H")) {
			evaluationhandler = new ExperimentHandlerTPCH(databaseHandle);
		} else {
			throw new InvalidDatabaseConfig("The Dataset Type "+databaseConfig.getDatasetType()+" is not known, implementation is missing.");
		}
		return evaluationhandler;
	}
	 
}

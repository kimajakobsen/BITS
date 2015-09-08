package dk.aau.kah.bits.evaluation;

import da.aau.kah.bits.exceptions.InvalidDatabaseConfig;
import dk.aau.kah.bits.database.DatabaseConfig;
import dk.aau.kah.bits.database.DatabaseHandler;

public class EvaluationFactory {

	public EvaluationFactory() {
		
	}

	public AbstractEvaluationHandler makeEvaluation(DatabaseHandler databaseHandle, DatabaseConfig databaseConfig) throws InvalidDatabaseConfig {
		String datasetType = databaseConfig.getDatasetType();
		AbstractEvaluationHandler evaluationhandler;
		
		if (datasetType.equals("TPC-H")) {
			evaluationhandler = new EvaluationHandlerTPCH(databaseHandle);
		} else {
			throw new InvalidDatabaseConfig("The Dataset Type "+databaseConfig.getDatasetType()+" is not known, implementation is missing.");
		}
		return evaluationhandler;
	}
	 
}

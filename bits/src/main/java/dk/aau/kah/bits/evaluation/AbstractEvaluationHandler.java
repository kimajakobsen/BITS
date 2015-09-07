package dk.aau.kah.bits.evaluation;

import java.io.IOException;

import dk.aau.kah.bits.database.DatabaseHandler;

public abstract class AbstractEvaluationHandler {
	protected DatabaseHandler databaseHandler;
	
	AbstractEvaluationHandler(DatabaseHandler databaseHandler){
		this.databaseHandler = databaseHandler;
	}

	public abstract void run() throws IOException;
	
	public abstract void getResults();
	
	public abstract void getResultTimes();
}

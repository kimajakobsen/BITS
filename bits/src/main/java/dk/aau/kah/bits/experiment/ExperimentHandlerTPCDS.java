package dk.aau.kah.bits.experiment;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

import dk.aau.kah.bits.database.DatabaseHandler;

public class ExperimentHandlerTPCDS extends AbstractExperimentHandler {

	ExperimentHandlerTPCDS(DatabaseHandler databaseHandler) {
		super(databaseHandler);
	}

	@Override
	public void run() throws IOException {
		List<File> queries = databaseHandler.getQueries();
			
			String rawQuery = "select * from <http://example/table1> where {?a ?b ?c} limit 10";
			Query query = QueryFactory.create(rawQuery) ;
			String queryName = "item";
			
//			IF (GENERALCONFIG.ISVERBOSE()) {
//				SYSTEM.OUT.PRINTLN(QUERYNAME+"\N" + QUERY);
//			}
			
			
			long startTime = System.nanoTime();
			ResultSet resultSet = databaseHandler.executeQuery(query);
			long endTime = System.nanoTime();
			long timeInMilliseconds = (endTime-startTime)/1000000; //convert to ms
			
			if (generalConfig.isVerbose()) {
				System.out.println(queryName+": "+timeInMilliseconds+"ms");
			}
			
			resultSetMap.put(queryName, resultSet);
			timeMap.put(queryName,timeInMilliseconds) ; 
			queryMap.put(queryName, query);
		    
			
		runHasBeenExecuted = true;
	}

	@Override
	public HashMap<String, String> getResults() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResult(String queryName) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Long> getResultTimes() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getResultTime(String queryName) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Query> getQueries() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQuery(String queryName) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void printResults() throws IOException {
	checkIfRunHasBeenExecuted();
		
		for(Entry<String, ResultSet> entry : resultSetMap.entrySet()) {
			String key = entry.getKey();
			ResultSet value = entry.getValue();
			System.out.println();
			ResultSetFormatter.out(System.out, value, queryMap.get(key)) ;
		}

	}

	@Override
	public String getResultHumanReadable(String queryName) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}

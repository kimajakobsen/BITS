package dk.aau.kah.bits.experiment;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

import dk.aau.kah.bits.database.DatabaseHandler;

public class ExperimentHandlerTPCH extends AbstractExperimentHandler {
	private HashMap <String, String> resultsMap = new HashMap<String,String>();
	private HashMap <String,ResultSet> resultSetMap = new HashMap<String,ResultSet>();
	private HashMap <String,Long> timeMap = new HashMap<String, Long>();
	private HashMap <String,Query> queryMap = new HashMap<String, Query>();
	private Boolean runHasBeenExecuted = false;
	
	ExperimentHandlerTPCH(DatabaseHandler dh) {
		super(dh);
	}

	@Override
	public void run() throws IOException {
		List<File> queries = databaseHandler.getQueries();
			
		for (final File fileEntry : queries) {
			String rawQuery = FileUtils.readFileToString(fileEntry);
			Query query = QueryFactory.create(rawQuery) ;
			
			addNamedGraphsToQuery(query);
			
			String queryName = getQueryName(fileEntry);
			
			long startTime = System.nanoTime();
			ResultSet resultSet = databaseHandler.executeQuery(query);
			long endTime = System.nanoTime();
			long timeInMilliseconds = (endTime-startTime)/1000000;
			
			if (generalConfig.isVerbose()) {
				System.out.println(queryName+": "+timeInMilliseconds);
			}
			
			resultSetMap.put(queryName, resultSet);
			timeMap.put(queryName,timeInMilliseconds) ; //convert to ms
			queryMap.put(queryName, query);
		    
			
	    }
		runHasBeenExecuted = true;
	}

	private String getQueryName(final File fileEntry) {
		String[] queryPathSplit = fileEntry.toString().split("/");
		String queryNameWithExtension = queryPathSplit[queryPathSplit.length-1];
		String queryName = queryNameWithExtension.substring(0,queryNameWithExtension.length()-7);
		return queryName;
	}

	private void addNamedGraphsToQuery(Query query) {
		for (String modelURI : getNamedGraphs(query)) {
			query.addGraphURI(modelURI);
		}
	}

	@Override
	public HashMap<String, String> getResults() throws IOException {
		checkIfRunHasBeenExecuted();
		
		for(Entry<String, ResultSet> entry : resultSetMap.entrySet()) {
			String key = entry.getKey();
			ResultSet value = entry.getValue();
			//TODO check if the value is already know, if then do not recompute. (will return null)
			resultsMap.put(key, getQuerySolution(value));
		}
		return resultsMap;
	}
	
	private String getQuerySolution(ResultSet value) {
		String results = "";
		while (value.hasNext()) { 
			QuerySolution row=value.nextSolution();
			results += row.toString();
	    }
		return results;
	}


	

	@Override
	public HashMap<String, Long> getResultTimes() throws IOException {
		checkIfRunHasBeenExecuted();
		return timeMap;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printResults() throws IOException {
		checkIfRunHasBeenExecuted();
		
		for(Entry<String, ResultSet> entry : resultSetMap.entrySet()) {
			String key = entry.getKey();
			ResultSet value = entry.getValue();
			ResultSetFormatter.out(System.out, value, queryMap.get(key)) ;
		}
		 
	}

	@Override
	public HashMap<String, Query> getQueries() throws IOException {
		checkIfRunHasBeenExecuted();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResult(String queryName) throws IOException {
		checkIfRunHasBeenExecuted();
		// TODO Auto-generated method stub
		return null;
	}

	private void checkIfRunHasBeenExecuted() throws IOException {
		if (!runHasBeenExecuted) {
			run();
		}
	}

	@Override
	public Long getResultTime(String queryName) throws IOException {
		checkIfRunHasBeenExecuted();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQuery(String queryName) throws IOException {
		checkIfRunHasBeenExecuted();
		// TODO Auto-generated method stub
		return null;
	}

}

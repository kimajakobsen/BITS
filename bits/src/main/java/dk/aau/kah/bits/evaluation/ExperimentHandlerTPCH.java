package dk.aau.kah.bits.evaluation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

import dk.aau.kah.bits.database.DatabaseHandler;

public class ExperimentHandlerTPCH extends AbstractExperimentHandler {
	private HashMap <String, String> results = new HashMap<String,String>();
	ExperimentHandlerTPCH(DatabaseHandler dh) {
		super(dh);
	}

	private String queriesPath = "src/test/resources/TPC-H/QueriesSeed100/";
	
	@Override
	public void run() throws IOException {
		
		for (final File fileEntry : new File(queriesPath).listFiles()) {
			String rawQuery = FileUtils.readFileToString(fileEntry);
			System.out.println(fileEntry);
			Query query = QueryFactory.create(rawQuery) ;
			
			for (String modelURI : getNamedGraphs(query)) {
				query.addGraphURI(modelURI);
			}

			ResultSet resultSet = databaseHandler.executeQuery(query);
		    
		    /*Execute the Query*/
			
		    System.out.println(query);
		    ResultSetFormatter.out(System.out, resultSet, query) ;
//		    while (resultSet.hasNext()) {
//			    QuerySolution row=resultSet.nextSolution();
//			    results.put(fileEntry.toString(), row.toString());
//			  }
	        }
	    }

	@Override
	public HashMap<String, String> getResults() {
		return results;
	}


	@Override
	public void getResultTimes() {
		// TODO Auto-generated method stub
		
	}

}

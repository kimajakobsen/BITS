package dk.aau.kah.bits.evaluation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

import dk.aau.kah.bits.database.DatabaseHandler;

public class EvaluationHandlerTPCH extends AbstractEvaluationHandler {
	private HashMap <String, String> results = new HashMap<String,String>();
	EvaluationHandlerTPCH(DatabaseHandler dh) {
		super(dh);
	}

	private String queriesPath = "src/test/resources/TPC-H/QueriesSeed100/";
	
	@Override
	public void run() throws IOException {
		Dataset dataset = databaseHandler.getDataset();;
		
		for (final File fileEntry : new File(queriesPath).listFiles()) {
			String rawQuery = FileUtils.readFileToString(fileEntry);
			Query query = QueryFactory.create(rawQuery) ;
			
			for (String modelURI : getNamedGraphs(query)) {
				query.addNamedGraphURI(modelURI);
			}

			dataset.begin(ReadWrite.READ) ;
		    QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
		    
		    /*Execute the Query*/
		    System.out.println(fileEntry);
		    ResultSet resultSet = qexec.execSelect();
		    //ResultSetFormatter.out(System.out, resultSet, query) ;
		    while (resultSet.hasNext()) {
			    QuerySolution row=resultSet.nextSolution();
			    results.put(fileEntry.toString(), row.toString());
			  }
		    qexec.close();
		    dataset.commit();
	        }
		dataset.close();
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

package dk.aau.kah.bits.experiment;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Resource;

import dk.aau.kah.bits.database.DatabaseHandler;

public abstract class AbstractExperimentHandler {
	protected DatabaseHandler databaseHandler;
	
	AbstractExperimentHandler(DatabaseHandler databaseHandler){
		this.databaseHandler = databaseHandler;
	}

	public abstract void run() throws IOException;
	
	public abstract HashMap<String, String> getResults();
	
	public abstract void getResultTimes();
	
	protected HashSet<String> getNamedGraphs(Query query) {
		HashSet<String> namedGraphs = new HashSet<String>(); 
		
		namedGraphs.add(databaseHandler.getOntologyModelURI());
		namedGraphs.add(databaseHandler.getFactModelURI());
		
		if (databaseHandler.getDimensionModelURI() == null) {
			HashSet<Resource> levelProperties = databaseHandler.getDistinctLevelProperties();
			for (Resource resource : levelProperties) {
				Pattern p = Pattern.compile("(.)*"+resource.toString()+"(.)*", Pattern.DOTALL);
				Matcher m = p.matcher(query.getQueryPattern().toString());
				if (m.find()) { //if property is in query
					namedGraphs.add(resource.toString());
				}
			}
		} else {
			namedGraphs.add(databaseHandler.getDimensionModelURI());
		}
		return namedGraphs;
	}
}

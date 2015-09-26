package dk.aau.kah.bits.experiment;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Resource;

import da.aau.kah.bits.config.GeneralConfig;
import dk.aau.kah.bits.database.DatabaseHandler;

public abstract class AbstractExperimentHandler {
	protected DatabaseHandler databaseHandler;
	protected GeneralConfig generalConfig;
	
	AbstractExperimentHandler(DatabaseHandler databaseHandler){
		this.databaseHandler = databaseHandler;
		this.generalConfig = GeneralConfig.getInstance();
	}

	public abstract void run() throws IOException;
	
	public abstract HashMap<String, String> getResults() throws IOException;
	
	public abstract String getResult(String queryName) throws IOException;
	
	public abstract HashMap<String, Long> getResultTimes() throws IOException;
	
	public abstract Long getResultTime(String queryName) throws IOException;
	
	public abstract HashMap<String,Query> getQueries() throws IOException;
	
	public abstract String getQuery(String queryName) throws IOException;
	
	public abstract void printResults() throws IOException;
	
	protected HashSet<String> getNamedGraphs(Query query) {
		HashSet<String> namedGraphs = new HashSet<String>(); 
		
		namedGraphs.add(databaseHandler.getOntologyModelURI());
		namedGraphs.add(databaseHandler.getFactModelURI());
		
		if (databaseHandler.getDimensionModelURI() == null) { // in case of #
			HashSet<Resource> levelProperties = databaseHandler.getDistinctLevelProperties();
			for (Resource resource : levelProperties) {
				Pattern p = Pattern.compile("(.)*"+resource.toString()+"(.)*", Pattern.DOTALL);
				Matcher m = p.matcher(query.getQueryPattern().toString());
				if (m.find()) { //if property is in query
					namedGraphs.add(resource.toString());
				}
			}
			
			Pattern lineitemPattern = Pattern.compile("(.)*http://lod2.eu/schemas/rdfh#lineitem(.)*", Pattern.DOTALL);
			Matcher lineitemMatcher = lineitemPattern.matcher(query.getQueryPattern().toString());
			Boolean lineitemMatch = lineitemMatcher.find();
			
			Pattern partsuppPattern = Pattern.compile("(.)*http://lod2.eu/schemas/rdfh#partsupp(.)*", Pattern.DOTALL);
			Matcher partsuppMatcher = partsuppPattern.matcher(query.getQueryPattern().toString());
			Boolean partsuppMatch = partsuppMatcher.find();
			
			Pattern orderPattern = Pattern.compile("(.)*http://lod2.eu/schemas/rdfh#orders(.)*", Pattern.DOTALL);
			Matcher orderMatcher = orderPattern.matcher(query.getQueryPattern().toString());
			Boolean orderMatch = orderMatcher.find();
			
			Pattern customerPattern = Pattern.compile("(.)*http://lod2.eu/schemas/rdfh#orders(.)*", Pattern.DOTALL);
			Matcher customerMatcher = customerPattern.matcher(query.getQueryPattern().toString());
			Boolean customerMatch = customerMatcher.find();
			
			if (lineitemMatch == false && partsuppMatch == true) {
				//Special case where cube is based on partsupp and not lineitem
				namedGraphs.add("http://lod2.eu/schemas/rdfh#has_partsupp");
				namedGraphs.remove(databaseHandler.getFactModelURI());
			} else if (lineitemMatch == false && orderMatch == true) {
				namedGraphs.add("http://lod2.eu/schemas/rdfh#has_order");
				namedGraphs.remove(databaseHandler.getFactModelURI());
			} else if (lineitemMatch == true && partsuppMatch == true) {
				namedGraphs.add("http://lod2.eu/schemas/rdfh#has_partsupp");
			} else if (lineitemMatch == false && customerMatch == true) {
				namedGraphs.add("http://lod2.eu/schemas/rdfh#has_customer");
				namedGraphs.remove(databaseHandler.getFactModelURI());
			}
		} else {
			namedGraphs.add(databaseHandler.getDimensionModelURI());
		}
		return namedGraphs;
	}
}

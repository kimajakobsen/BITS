package da.aau.kah.bits.physical_storage;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.propertytable.PropertyTable;
import org.apache.jena.propertytable.graph.GraphPropertyTable;
import org.apache.jena.propertytable.impl.PropertyTableArrayImpl;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class QB4OLAPFactPropertyTable extends QB4OLAPPropertyTable {
	private Model ontology;
	private Model facts;
	private PropertyTable propertytable;
	
	public QB4OLAPFactPropertyTable(Model ontology, Model facts) {
		this.ontology = ontology;
		this.facts = facts;
		this.propertytable = new PropertyTableArrayImpl(countNumberOfSubjects(facts), countAttributeAndComponentPropertyAndLevel(ontology));
		
		StmtIterator iter = facts.listStatements();
		try { 
			while (iter.hasNext()) {
			    Statement stmt      = iter.nextStatement();  // get next statement
			    Resource  subject   = stmt.getSubject();     // get the subject
			    Property  predicate = stmt.getPredicate();   // get the predicate
			    RDFNode   object    = stmt.getObject();      // get the object

			    propertytable.createRow(getRowNode(subject)).setValue(propertytable.createColumn(getColumnNode(predicate)), object.asNode());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Node getColumnNode(Property predicate) {
		return NodeFactory.createURI(predicate.getURI()) ;
	}
	
	private Node getRowNode(Resource subject) {
		return NodeFactory.createURI(subject.getURI()) ;
	}

	private int countNumberOfSubjects(Model facts){
		String sparql = ""
				+ "prefix rdfh:           <http://lod2.eu/schemas/rdfh#>  "
				+ "prefix qb:             <http://purl.org/linked-data/cube#>  "
				+ "select (count(*) as ?count) "
				+ "where { ?a ?b ?c"
				+ "}" 
				+ "group by ?a";

		Query qry = QueryFactory.create(sparql);

		QueryExecution qe = QueryExecutionFactory.create(qry, ontology);
		ResultSet rs = qe.execSelect();
		
		//ResultSetFormatter.out(System.out, rs, qry) ;
		int propertyCount = 0;
		while (rs.hasNext()) {
		    QuerySolution row=rs.nextSolution();
		    propertyCount = Integer.parseInt(row.getLiteral("count").getString());
		  }
		qe.close();
		return propertyCount;
	}
	

	
	private int countAttributeAndComponentPropertyAndLevel(Model ontology) {
		String sparql = ""
				+ "prefix rdfh:           <http://lod2.eu/schemas/rdfh#>  "
				+ "prefix qb:             <http://purl.org/linked-data/cube#>  "
				+ "prefix qb4o:           <http://publishing-multidimensional-data.googlecode.com/git/index.html#ref_qbplus_> "
				+ "select (count(*) as ?count) "
				+ "where {"
				+ "rdfh:lineitemStructure qb:component _:comp . "
				+ "_:comp ?componentProperty ?Property . "
				+ "filter (?componentProperty = qb:attribute || ?componentProperty = qb:measure || ?componentProperty = qb4o:level)}";

		Query qry = QueryFactory.create(sparql);

		QueryExecution qe = QueryExecutionFactory.create(qry, ontology);
		ResultSet rs = qe.execSelect();
		
		//ResultSetFormatter.out(System.out, rs, qry) ;
		int propertyCount = 0;
		while (rs.hasNext()) {
		    QuerySolution row=rs.nextSolution();
		    propertyCount = Integer.parseInt(row.getLiteral("count").getString());
		  }
		qe.close();
		return propertyCount;
		
	}
	
	public Model getModel() {
		GraphPropertyTable graph = new GraphPropertyTable(propertytable);
		return ModelFactory.createModelForGraph(graph);
	}

}

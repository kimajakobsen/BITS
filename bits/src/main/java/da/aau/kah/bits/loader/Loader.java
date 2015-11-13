package da.aau.kah.bits.loader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;

import da.aau.kah.bits.exceptions.InvalidFieldDatatype;
import da.aau.kah.bits.exceptions.InvalidFieldName;

public class Loader {

	private List<String> csvFiles = new ArrayList<String>();
	private String namespace = "http://example.org/";
	
	public Loader(List<String> filePaths) {
		this.csvFiles = filePaths;
		csvFiles.add("src/main/resources/TPC-DS/sf-1/inventory.dat");
	}

	
	public void run () {
		JSONparser parser = new JSONparser();
		ConfigTPCDS configTPCDS = parser.load();
		
		
		
		
		BufferedReader br = null;
		String rawLine = "";
		String cvsSplitBy = "\\|";
		Model model = ModelFactory.createDefaultModel();

		for (String csvFile : csvFiles) {
			try {
				Table table = configTPCDS.getTableWithName(getFileNameWithoutExtension(csvFile));
				
				br = new BufferedReader(new FileReader(csvFile));
				
				
				while ((rawLine = br.readLine()) != null) {
					RDFNode object;
					String[] line = rawLine.split(cvsSplitBy);
					
					String subjectName = getSubject(table, line);
					Resource subject = ResourceFactory.createResource(subjectName);
					
					int index = 0;
					for (String value : line) {
						Property predicate = getProperty(table.getFields().get(index));
						if (table.getFields().get(index).hasForeignKeys()) { //if Object property
							object = getObject(table.getFields().get(index));
						} else { // datatype property
							object = getLiteral(table.getFields().get(index), value);
						}
						Statement s = ResourceFactory.createStatement(subject, predicate, object);
						model.add(s);
						index++;
					}
						
						
						//model.add(s, p, o)
						//Statement s = ResourceFactory.createStatement(subject, predicate, object);
						//model.add(s); // add the statement (triple) to the model
				        // use comma as separator
					
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidFieldName e) {
				e.printStackTrace();
			} catch (InvalidFieldDatatype e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		

		System.out.println("Done");
		
	}


	private Property getProperty(Field field) {
		// TODO, what happens if property already exist.
		return ResourceFactory.createProperty(namespace, field.getName());
	}
	
	private RDFNode getObject(Field field) {
		List<String> foreignKeys = field.getForeignKeys();
		String URI = namespace;
		for (String key : foreignKeys) {
			URI += key;
			if(!foreignKeys.get(foreignKeys.size()-1).equals(key)) { //If not last element
				URI += "_";
			}
		}
		return ResourceFactory.createResource(URI);
	}
	
	private Literal getLiteral(Field field, String input) throws InvalidFieldDatatype {
		Literal literal;
		switch (field.getType()) {
		case "int":
			literal = ResourceFactory.createTypedLiteral(new Integer(input));
			break;
		case "double":
			literal = ResourceFactory.createTypedLiteral(new Double(input));
			break;
		case "str":
			literal = ResourceFactory.createLangLiteral(input, "en");
			break;
		case "date":
			literal = ResourceFactory.createTypedLiteral(new Date(input));
			break;
		default:
			throw new InvalidFieldDatatype("The type: "+field.getType()+ "is not a valid type, please use int,double,str, or date");
		}
		return literal;
	}
	
	private String getSubject(Table table, String[] line) throws InvalidFieldName {
		List<String> keys = table.getKeys();
		String subjectName = namespace;
		for (String key : keys) {
			int indexnumber = table.getFieldIndex(key);
			String keyPart = line[indexnumber];
			subjectName += keyPart;
			if(!keys.get(keys.size()-1).equals(key)) { //If not last element
				subjectName += "_";
			}
		}
		return subjectName;
	}


	private String getFileNameWithoutExtension(String csvFile2) {
		
		String[] folderSplit = csvFile2.split("/");
		String filename = folderSplit[folderSplit.length-1];
		String[] extensionSplit = filename.split("\\.");
		
		return extensionSplit[0];
	}

}

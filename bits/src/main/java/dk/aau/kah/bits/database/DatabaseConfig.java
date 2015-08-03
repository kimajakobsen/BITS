package dk.aau.kah.bits.database;


import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import da.aau.kah.bits.exceptions.InvalidDatabaseConfig;

public class DatabaseConfig {
	
	File TDBFile;
	
	private String scaleFactor;
	private String ontologyModelName;
	private String ontologyStorageModel;
	private String dimensionModelName;
	private String dimensionStorageModel;
	private String factModelName;
	private String factStorageModel;

	public String getScaleFactor() {
		return scaleFactor;
	}
	public String getScaleFactorString() {
		return "sf-"+getScaleFactor();
	}
	public String getOntologyModelName() {
		return ontologyModelName;
	}
	public String getOntologyStorageModel() {
		return ontologyStorageModel;
	}
	public String getDimensionModelName() {
		return dimensionModelName;
	}
	public String getDimensionStorageModel() {
		return dimensionStorageModel;
	}
	public String getFactModelName() {
		return factModelName;
	}
	public String getFactStorageModel() {
		return factStorageModel;
	}
	public void setScaleFactor(String d) {
		this.scaleFactor = d;
	}
	public void setOntologyModelName(String ontologyModelName) {
		this.ontologyModelName = ontologyModelName;
	}
	public void setOntologyStorageModel(String ontologyStorageModel) {
		this.ontologyStorageModel = ontologyStorageModel;
	}
	public void setDimensionModelName(String dimensionModelName) {
		this.dimensionModelName = dimensionModelName;
	}
	public void setDimensionStorageModel(String dimensionStorageModel) {
		this.dimensionStorageModel = dimensionStorageModel;
	}
	public void setFactModelName(String factModelName) {
		this.factModelName = factModelName;
	}
	public void setFactStorageModel(String factStorageModel) {
		this.factStorageModel = factStorageModel;
	}

	public boolean validate() throws InvalidDatabaseConfig {
		// TODO
		// Der er et minimum af hvad der skal sætter, fact, dim, onto. Dette skal tjekkes.
		if (false) {
			
			throw new InvalidDatabaseConfig("Some message TODO");
		}
		return true;
	}
	
	public String getTDBPath()
	{
		String ontoModelNumber = "0";
		String dimModelNumber;
		String factModelNumber;
		
		factModelNumber = (ontologyModelName == factModelName ?  "0" : "1" ); 
		
		// reasoning about what number the dimension model corresponds to in the TDB naming system
		if (ontologyModelName == dimensionModelName) {
			dimModelNumber = ontoModelNumber;
		}
		else if (dimensionModelName == factModelName ) {
			dimModelNumber = ontoModelNumber; 
		}
		else {
			dimModelNumber = "2";
		}
		
		return "src/main/resources/tdb/"+"onto"+ontoModelNumber+ontologyStorageModel+"-"+"fact"+
			factModelNumber+factStorageModel+"-"+"dim"+dimModelNumber+dimensionStorageModel+"/";
	}
	
	public File getTDBPathFile()
	{
		if (TDBFile == null) {
			return new File(getTDBPath());
		}
		return TDBFile;
	}
	


}

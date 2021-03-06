package da.aau.kah.bits.config;

import da.aau.kah.bits.exceptions.InvalidDatabaseConfig;

public class PhysicalStorageConfig {
	private String ontologyModelName;
	private String ontologyStorageModel;
	private String dimensionModelName;
	private String dimensionStorageModel;
	private String factModelName;
	private String factStorageModel;
	private String prefix = "http://example.org/";
	private String configFileName;

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
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	private String addURLToModelName(String modelName)
	{
		return getPrefix()+modelName;
	}
	public String getOntologyModelURL() {
		return addURLToModelName(ontologyModelName);
	}
	public String getDimensionModelURL() {
		return addURLToModelName(dimensionModelName);
	}
	public String getFactModelURL() {
		return addURLToModelName(factModelName);
	}
	public String getConfigFileName() {
		return configFileName;
	}
	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}
	public void validate() throws InvalidDatabaseConfig {
		if (ontologyModelName == null){
			throw new InvalidDatabaseConfig("ontologyModelName is not set, config is not valid");
		}
		if (ontologyStorageModel == null){
			throw new InvalidDatabaseConfig("ontologyStorageModel is not set, config is not valid");
		}
		if (dimensionModelName == null){
			throw new InvalidDatabaseConfig("dimensionModelName is not set, config is not valid");
		}
		if (dimensionStorageModel == null){
			throw new InvalidDatabaseConfig("dimensionStorageModel is not set, config is not valid");
		}
		if (factModelName == null){
			throw new InvalidDatabaseConfig("factModelName is not set, config is not valid");
		}
		if (factStorageModel == null){
			throw new InvalidDatabaseConfig("factStorageModel is not set, config is not valid");
		}
	}


}

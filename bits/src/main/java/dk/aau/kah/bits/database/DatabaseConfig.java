package dk.aau.kah.bits.database;


import da.aau.kah.bits.exceptions.InvalidDatabaseConfig;

public class DatabaseConfig {
	private String TDBDatasetPath;
	private String configFileName;
	private String experimentDataset;
	private String scaleFactor;
	private String ontologyModelName;
	private String ontologyStorageModel;
	private String dimensionModelName;
	private String dimensionStorageModel;
	private String factModelName;
	private String factStorageModel;
	private boolean freshLoad = false;
	private boolean trashDimension = false;
	private String prefix = "http://example.org/";

	public String getTDBDatasetPath() {
		return TDBDatasetPath;
	}
	public void setTDBDatasetPath(String TDBDatasetFilePath) {
		this.TDBDatasetPath = TDBDatasetFilePath;
	}
	public String getConfigFileName() {
		return configFileName;
	}
	public void setConfigFileName(String ConfigFileName) {
		this.configFileName = ConfigFileName;
	}
	public boolean isFreshLoad() {
		return freshLoad;
	}
	public boolean isTrashDimension() {
		return trashDimension;
	}
	public void setTrashDimension(boolean trashDimension) {
		this.trashDimension = trashDimension;
	}
	public String getScaleFactor() {
		return scaleFactor;
	}
	public String getScaleFactorString() {
		return "sf-"+getScaleFactor();
	}
	public String getOntologyModelName() {
		return ontologyModelName;
	}
	public String getOntologyModelURL() {
		return addURLToModelName(ontologyModelName);
	}
	public String getOntologyStorageModel() {
		return ontologyStorageModel;
	}
	public String getDimensionModelName() {
		return dimensionModelName;
	}
	public String getDimensionModelURL() {
		return addURLToModelName(dimensionModelName);
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
	public String getFactModelURL() {
		return addURLToModelName(factModelName);
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
	public String getExperimentDataset() {
		return experimentDataset;
	}
	public void setExperimentDataset(String experimentset) {
		this.experimentDataset = experimentset;
	}

	public void validate() throws InvalidDatabaseConfig {
		
		if (experimentDataset == null){
			throw new InvalidDatabaseConfig("experimentDataset is not set, config is not valid");
		}
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
		if (experimentDataset.equals("TPC-H")){
			if (scaleFactor == null) {
				throw new InvalidDatabaseConfig("The scalefactor is not set, it needs to be specified when using TPCH");
			}
		}
		// TODO
		// Der er et minimum af hvad der skal sætter, fact, dim, onto. Dette skal tjekkes.
		if (false) {
			
			throw new InvalidDatabaseConfig("Some message TODO");
		}

	}
	
	@Override
	public String toString () {
		return this.TDBDatasetPath;
		
	}
	public void setFreshLoad(boolean freshLoad) {
		this.freshLoad = freshLoad;
	}
	
	private String addURLToModelName(String modelName)
	{
		return getPrefix()+modelName;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	


}

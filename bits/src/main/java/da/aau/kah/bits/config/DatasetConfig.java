package da.aau.kah.bits.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import da.aau.kah.bits.exceptions.InvalidDatabaseConfig;

public class DatasetConfig {
	private String datasetType;
	private String scaleFactor;
	private String queries;
	private String configFileName;
	private boolean freshLoad = false;
	
	public String getDatasetPath() {
		return "src/main/resources/"+getDatasetType()+"/"+getScaleFactorString();
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
	public String getScaleFactor() {
		return scaleFactor;
	}
	public String getScaleFactorString() {
		return "sf-"+getScaleFactor();
	}
	public void setScaleFactor(String d) {
		this.scaleFactor = d;
	}
	public void validate() throws InvalidDatabaseConfig {
		
		if (datasetType == null){
			throw new InvalidDatabaseConfig("experimentDataset is not set, config is not valid");
		}
		if (datasetType.equals("TPC-H")){
			if (scaleFactor == null) {
				throw new InvalidDatabaseConfig("The scalefactor is not set, it needs to be specified when using TPCH");
			}
		}
	}
	
	@Override
	public String toString () {
		return this.getDatasetPath();
	}
	public void setFreshLoad(boolean freshLoad) {
		this.freshLoad = freshLoad;
	}
	public String getDatasetType() {
		return datasetType;
	}
	public void setDatasetType(String datasetType) {
		this.datasetType = datasetType;
	}
	public List<File> getQueries() {
		String queriesPath = "src/test/resources/TPC-H/QueriesSeed100/";
		List<File> queryFiles = new ArrayList<File>();
		String[] split = this.queries.split(",");
		for (String pathname : split) {
			queryFiles.add(new File(queriesPath+pathname+".sparql"));
		}
		return queryFiles;
	}
	public void setQueries(String queries) {
		this.queries = queries;
	}
}

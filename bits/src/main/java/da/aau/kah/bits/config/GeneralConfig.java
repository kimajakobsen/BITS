package da.aau.kah.bits.config;

public class GeneralConfig {
	
	private static GeneralConfig instance = null;
	protected GeneralConfig() {}
	private String tdbLoaderPath;
	private Boolean Verbose = false;
	
	public static GeneralConfig getInstance() {
		if(instance == null) {
			instance = new GeneralConfig();
		}
		return instance;
	}
	public String getTdbLoaderPath() {
		return instance.tdbLoaderPath;
	}
	public void setTdbLoaderPath(String tdbLoader) {
		instance.tdbLoaderPath = tdbLoader;
	}
	public Boolean isVerbose() {
		return instance.Verbose;
	}
	public void setVerbose(Boolean verbose) {
		instance.Verbose = verbose;
	}
}

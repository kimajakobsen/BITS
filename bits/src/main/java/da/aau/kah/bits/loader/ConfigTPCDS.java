package da.aau.kah.bits.loader;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ConfigTPCDS {

	private List<Table> tables = new ArrayList<Table>();
	
	/**
	* 
	* @param tables
	*/
	public ConfigTPCDS(List<Table> tables) {
		this.tables = tables;
	}
	
	/**
	* 
	* @return
	* The tables
	*/
	public List<Table> getTables() {
		return tables;
	}
	
	/**
	* 
	* @param tables
	* The tables
	*/
	public void setTables(List<Table> tables) {
		this.tables = tables;
	}
	
	public Table getTableWithName(String name) {
		for (Table table : tables) {
			System.out.println(table.getName());
			if (table.getName().equals(name)) {
				return table;
			}
		}
		return null;
	}
	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}

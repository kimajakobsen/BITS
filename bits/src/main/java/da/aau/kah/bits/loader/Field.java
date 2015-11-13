package da.aau.kah.bits.loader;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Field {

	private String name;
	private String type;
	private List<String> foreignKey = new ArrayList<String>();

	/**
	* 
	* @param name
	* @param type
	*/
	public Field(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public Field(String name, String type, List<String> foreignKey) {
		this.foreignKey = foreignKey;
		this.name = name;
		this.type = type;
	}


	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Field withName(String name) {
		this.name = name;
	return this;
	}
	
	/**
	* 
	* @return
	* The type
	*/
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public Field withType(String type) {
		this.type = type;
		return this;
	}
	
	public boolean hasForeignKeys(){
		if (foreignKey.size()==0) {
			return false;
		} else {
			return true;
		}
	}
	
	public List<String> getForeignKeys() {
		return foreignKey;
	}
	
	public void setForeignKeys(List<String> keys) {
		this.foreignKey = keys;
	}
	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
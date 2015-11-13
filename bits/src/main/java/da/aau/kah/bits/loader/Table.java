package da.aau.kah.bits.loader;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import da.aau.kah.bits.exceptions.InvalidFieldName;

public class Table {

	private String name;
	private List<Field> fields = new ArrayList<Field>();
	private List<String> keys = new ArrayList<String>();
	
	/**
	* 
	* @param keys
	* @param name
	* @param fields
	*/
	public Table(String name, List<Field> fields, List<String> keys) {
		this.name = name;
		this.fields = fields;
		this.keys = keys;
	}
	
	/**
	* 
	* @return
	* The name
	*/
	public String getName() {
		return name;
	}
	
	/**
	* 
	* @param name
	* The name
	*/
	public void setName(String name) {
		this.name = name;
	}
	
	public Table withName(String name) {
		this.name = name;
		return this;
	}
	
	/**
	* 
	* @return
	* The fields
	*/
	public List<Field> getFields() {
		return fields;
	}
	
	/**
	* 
	* @param fields
	* The fields
	*/
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
	public Table withFields(List<Field> fields) {
		this.fields = fields;
		return this;
	}
	
	/**
	* 
	* @return
	* The keys
	*/
	public List<String> getKeys() {
		return keys;
	}
	
	/**
	* 
	* @param keys
	* The keys
	*/
	public void setKeys(List<String> keys) {
		this.keys = keys;
	}
	
	public Table withKeys(List<String> keys) {
		this.keys = keys;
		return this;
	}
	

	
	public int getFieldIndex(String fieldName) throws InvalidFieldName {
		int index = 0;
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				return index;
			}
			index++;
		}
		throw new InvalidFieldName("the field: "+ fieldName + " does not exist in table with fields: " + fields.toString());
	}
	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}

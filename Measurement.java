
public class Measurement {
	/*
	 * classe che definisce gli attributi delle misurazioni delle sonde
	 */
	private int attribute_index;
	private int resource_index;
	private String short_name;
	private String java_type;
	private String value;
	

	
	protected Measurement(){
		
	}
	
	
	protected Measurement(int attribute,int index,String name,String type,String value){
		this.attribute_index=attribute;
		this.resource_index=index;
		this.short_name=name;
		this.java_type=type;
		this.value=value;
		
		
	}
	/*
	 * Metodi di get e set
	 */
	
	protected void setAttribute_index(int attribute){
		this.attribute_index=attribute;
	}
	protected void setResource_index(int index){
		this.resource_index=index;
	}
	protected void setShort_name(String name){
		this.short_name=name;
	}
	protected void setJava_type(String type){
		this.java_type=type;
	}
	protected void setValue(String value){
		this.value=value;
	}
	
	
	
	protected int getAttribute_index(){
	    return this.attribute_index;
	}
	protected int getResource_index(){
		return this.resource_index;
	}
	protected String getShort_name(){
		return this.short_name;
	}
	protected String getJava_type(){
		return this.java_type;
	}
	protected String getValue(){
		return this.value;
	}
	
	public String toString(){
		
		return "index: "+this.attribute_index+" resource index: "+this.resource_index+" name: "+this.short_name+" type: "+this.java_type+" value: "+this.value;
		
	}
	
}


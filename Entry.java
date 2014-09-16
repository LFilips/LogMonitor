import java.util.ArrayList;
import java.util.Iterator;


public class Entry {
/* 
 * Object for represente an entry of the table
 */
	  private long probeID;
	  private String identification;
	  private String ip;
	  private int group_ID;
	  private int provider_ID;
	  private long    seq;
	  private long timestamp;
	  private String type;
	  private ArrayList<Measurement> lista_attributi;
	
	  
	  
	  protected Entry(){
		
		this.identification=null;
		this.ip=null;
		this.type=null;
		this.lista_attributi=new ArrayList<Measurement>();
		
		
	}
	
	
	//set method
	protected void setIdentification(String identification){
		this.identification=identification;
	}
	protected void setIp(String ip){
		this.ip=ip;
	}
	protected void setGroupID(int id){
		this.group_ID=id;
	}
	protected void setProviderID(int id){
		this.provider_ID=id;
	}
	protected void setProbeID(long probeID){
		this.probeID=probeID;
	}
	protected void setSeq(long seq){
		this.seq=seq;
	}
	protected void setTimestamp(long timestamp){
		this.timestamp=timestamp;
	}
	protected void setType(String type){
		this.type=type;
	}
	protected void insertMeasurement(Measurement misura){
		this.lista_attributi.add(misura);
	}
	
	
	//get method
	protected String getIdentification(){
		return this.identification;
	}
	protected String getIp(){
		return this.ip;
	}
	protected int getGroupID(){
		return this.group_ID;
	}
	protected int getProviderID(){
		return this.provider_ID;
	}
	protected long getProbeID(){
		return this.probeID;
	}
	protected long getSeq(){
		return this.seq;
	}
	protected long getTimestamp(){
		return this.timestamp;
	}
	protected String getType(){
		return this.type;
	}
	protected ArrayList<Measurement> getAttributes(){
		return this.lista_attributi;
	}
	protected void deleteAttributes(){
		this.lista_attributi=null;
		this.lista_attributi=new ArrayList<Measurement>();
	}
	
	
	public String toString(){
		/*
		 * Print all the measurement list
		 */
		String lista="";
		Iterator<Measurement> it=lista_attributi.listIterator();
		while(it.hasNext()){
			Measurement m=it.next();
			lista+=m.toString()+" ";
		}
		
		return "identification: "+this.identification+" ip: "+this.ip+" group_ID: "+this.group_ID+" providerID: "+this.provider_ID+" probeID: "+this.probeID+" seq: "+this.seq+" timestamp: "
		+this.timestamp+" type: "+this.type+" attributes: "+lista;
		
	}
	
	
}

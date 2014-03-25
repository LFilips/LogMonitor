
public class SpaceSpec<SpaceHierarchy,Object> {
	private SpaceHierarchy livello;
	private Object valore;
	
	public SpaceSpec(SpaceHierarchy livello,Object valore){
		this.livello=livello;
		this.valore=valore;
		
		
	}
	public SpaceHierarchy getHierarchy(){
		return this.livello;
	}
	public Object getObject(){
		return this.valore;
	}

}

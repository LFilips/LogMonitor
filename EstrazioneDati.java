import java.util.ArrayList;
import java.util.Scanner;


public class EstrazioneDati {
	
	public static void main(String args[]){
		while(true){
			
			EstrazioneDati.inserimentoParametriQuery();
			System.out.println("");
			System.out.println("Starting new Insert");
			System.out.println("");
			
			
		}
		
		
		
		
	}
	
	public static void inserimentoParametriQuery(){
		
		System.out.println("Insert Type :Jmx,Memory,Disk,Network,Cpu,null per tutti");
		Scanner console=new Scanner(System.in);
		String str=console.nextLine();
	
		SpaceHierarchy spaceLevel=null;	
		ResourceType type=null;
		if(str.equalsIgnoreCase("JMX"))
				type=ResourceType.JMX;
		else if(str.equalsIgnoreCase("MEMORY"))
			type=ResourceType.MEMORY;
		else if(str.equalsIgnoreCase("CPU"))
			type=ResourceType.CPU;
		else if(str.equalsIgnoreCase("DISK"))
			type=ResourceType.DISK;
		else if(str.equalsIgnoreCase("NETWORK"))
			type=ResourceType.NETWORK;
		else
			System.out.println("Type value not valid , all value will be returned");
					
		System.out.println("Insert measurement name,null for all");
			str=console.nextLine();
		if(!str.equalsIgnoreCase("null"))
			str=null;
			
		String parola="";	
		
		System.out.println("Insert spatial hierarchy level : Provider, Group, Machine");
		parola=console.nextLine();
		if(parola.equalsIgnoreCase("PROVIDER"))
				spaceLevel=SpaceHierarchy.PROVIDER;
		else if(parola.equalsIgnoreCase("GROUP"))
			spaceLevel=SpaceHierarchy.GROUP;
		else if(str.equalsIgnoreCase("MACHINE"))
			spaceLevel=SpaceHierarchy.MACHINE;
		else{	
			System.out.println("Hierarchy not valid");
			return;
		}
		
		System.out.println("Insert aggregation specification");
		
		ArrayList<SpaceSpec> list=new ArrayList<SpaceSpec>();
		int value;
		String parolaToParse;
		while(!parola.equalsIgnoreCase("quit")){
		 System.out.println("Insert hierarchy level");
		 parola=console.nextLine();
		System.out.println("Insert Value");
		 parolaToParse=console.nextLine();

		 
		 value=Integer.parseInt(parolaToParse);
		 
		 if(parola.equalsIgnoreCase("PROVIDER"))
				spaceLevel=SpaceHierarchy.PROVIDER;
		else if(parola.equalsIgnoreCase("GROUP"))
			spaceLevel=SpaceHierarchy.GROUP;
		else if(str.equalsIgnoreCase("MACHINE"))
			spaceLevel=SpaceHierarchy.MACHINE;
		else{
			System.out.println("Inserted spatial hierarchy not valid");
			return;
		}
		SpaceSpec spaceSpec=new SpaceSpec(spaceLevel,value);
		list.add(spaceSpec);
		System.out.println(Insert quit for stop, otherwise return");
		parola=console.nextLine();
		}
		String dateTo,dateFrom;
		System.out.println("Insert starting date day/month/years hour:minute:second:millisecond");
		dateFrom=console.nextLine();
		
		System.out.println("Insert starting date day/month/years hour:minute:second:millisecond");
		dateTo=console.nextLine();
		
		System.out.println("Insert aggregation time in millisecond");
		int div=Integer.parseInt(console.nextLine());
		
		Store.doProcess(type,str,spaceLevel,list,dateFrom,dateTo,div);
		
	}

}

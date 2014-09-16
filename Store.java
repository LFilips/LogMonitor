import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;




public class Store implements Runnable {
/*
 * Class that insert measurement inside DB
 */
	
	  /*
	   * Connection with DB, loaded from external configuration file
	   *
	   */
	  private static String dbClassName ;
	  private static String CONNECTION ;
	  
	  
	  private static long timeout;
	  private static String directory;
	  private static Connection c;
	  private final static String user="user";
	  private final static String pass="password";
	  private static String userName;
	  private static String password;
	  private static String dbHw;
	  private static String dbMeasurement;

	  
	   
	  
	  public Store(){
		  
		  /*
		   * Thread run
		   */
	
	    Thread thread = new Thread(this,"Store");
	    thread.start();
		 
	    
	    }
	    
	    
	    public void run(){
	    
	    	/*
	    	 * Load parameters from config file
	    	 *
	    	 */
	    	
	    	loadParametersFromRegistry();
				
			
		    System.out.println("dbclassName: "+dbClassName);
		    System.out.println("db: "+CONNECTION);
		   
		    try {
				Class.forName(dbClassName);
			} catch (ClassNotFoundException e) {
				
				e.printStackTrace();
			}

		   



			Properties p = new Properties();
		    p.put(user,userName);
		    p.put(pass,password);

		    
		    c=null;
		    
		    
			try {
				c = DriverManager.getConnection(CONNECTION,p);
			} catch (SQLException e) {
			
				e.printStackTrace();
			}
		   
		    System.out.println("Connecting to DB, startin measurement:");
		    
		 
	    while(true){
	    
	    	try {
					Thread.sleep(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				System.out.println("Running Store");
				File active_folder = new File(directory);
				if(active_folder.isDirectory()){
					for(File activeFile : active_folder.listFiles()){
						System.out.println("Selezionato il file: "+activeFile.getName());
						if(!activeFile.getName().endsWith(".log"))//i file sono tutti .log
							continue;
						try {
							/*
							 * creazione dell'oggetto entry che uso mer mantenere le informazioni
							 * sulla singola misurazione
							 * 
							 */
							
							System.out.println("creazione oggetto Entry");
							Entry entry=new Entry();
							
							
							
							FileInputStream is = new FileInputStream(activeFile);
							DataInputStream in = new DataInputStream(is);
						    BufferedReader br = new BufferedReader(new InputStreamReader(in));
						    String strLine="";
						    String app="";
						    int i=0;
						    while ((strLine = br.readLine()) != null){
						    	/*
						    	 * readLine() mi separa ogni misurazione del file
						    	 * in questo modo posso gestire separatamente ogni misurazione
						    	*/
						    	//elimino la lista precedente
						    	
						    	entry.getAttributes().clear();
						    	
							    i++;
							    System.out.println("");
						    	System.out.println("Analizzo la misurazione numero "+i+" del File :"+activeFile.getName());
						    	StringTokenizer lineParser = new StringTokenizer(strLine,":");
						    	lineParser.nextToken();//seq
						    	strLine=lineParser.nextToken();
						    	//adesso azzero il contenuto di strLine
						    	
						    	/*
						    	 * dato che il secondo token della misurazione è di questo tipo
						    	 * 470 probename
						    	 * e a me serve solo il valore 470 lo separo tramite un substring
						    	 * e dato che c'era uno spazio prima del 470 faccio substring con 1
						    	 * ho dovuto farlo pure con timestamp e probeID
						    	 */
						    	System.out.println("Inserimento attributi nell'oggetto entry . . .");
						    	System.out.println("Inserimento seq: "+strLine.substring(1,strLine.lastIndexOf(" ")));
						    	entry.setSeq(Long.parseLong(strLine.substring(1,strLine.lastIndexOf(" "))));
						    	
						    	//la parola probename è stata tolta con il token precedente
						    	strLine="";
						    	
						    	strLine=lineParser.nextToken();//nomeEffettivo
						    	System.out.println("inserimento identification: "+strLine);
						    	entry.setIdentification(strLine.substring(1));
						    	strLine=lineParser.nextToken();//ip
						    	System.out.println("inserimento ip: "+strLine);
						    	entry.setIp(strLine);						    	
						    	strLine=lineParser.nextToken();//group_id
						    	System.out.println("inserimento group_ID: "+strLine);
						    	entry.setGroupID(Integer.parseInt(strLine));
						    	app=lineParser.nextToken();
						    	app=app.substring(0,app.lastIndexOf(" "));
						    	strLine=app;//provider_id , l'ultimo token era composto cosi PROVIDER_IP probeid: e a me serve solo
						    	//provider_ID
						    	System.out.println("inserimento provider_ID: "+strLine);
						    	entry.setProviderID(Integer.parseInt(strLine));
						    	
						    	strLine=lineParser.nextToken();
						    	System.out.println("Inserimento probeID: "+strLine.substring(1,strLine.lastIndexOf(" ")));
						    	entry.setProbeID(Long.parseLong(strLine.substring(1,strLine.lastIndexOf(" "))));
						    	strLine=lineParser.nextToken();
						    	System.out.println("Inserimento timestamp: "+strLine.substring(1,strLine.lastIndexOf(" ")));
						    	entry.setTimestamp(Long.parseLong(strLine.substring(1,strLine.lastIndexOf(" "))));
						    	strLine=lineParser.nextToken();
						    	System.out.println("Inserimento type: "+strLine.substring(1,strLine.lastIndexOf(" ")));
						    	entry.setType(strLine.substring(1,strLine.lastIndexOf(" ")));
						    	/*i type dovrebbero essere solo di 5 tipi
						    	 * cpu,memory,network,disk,jmx e se type è jmx metto nella tabella software
						    	 */
						    	//il rimanente sono gli attributi
						    	System.out.println("Inserimento attributi oggetti Measurement . . .");
						    	/*
						    	 *  [0: mem-free LONG 1699528704, 1: mem-used LONG 1734864896]
						    	 *  io sono arrivato fino al token ad esempio    DISK attributes
						    	 *  il prossimo token è [0 poi mem-free long 2234152152, 1 poi .....
						    	 *  potrei prima prendere tutti i token,togliere tutti i : ed otterrei
						    	 *  0 mem-free LONG 1699528704, 1 mem-used LONG 1734864896, 2 . . . . .
						    	 *  poi rifare tokenizier con la "," ed ottendere la separazione
						    	 *  rifacendo tokenizer con gli spazi avrei ogni elemento
						    	 * 
						    	 * 
						    	 */
						    	String attributes="";
						    	while(lineParser.hasMoreTokens()){
						    		attributes += lineParser.nextToken();
						    	}
						    	//a questo punto ho tutta la stringa senza i :
						    	attributes=attributes.substring(2,attributes.lastIndexOf("]"));//taglio le parentesi quadre
						    	//System.out.println("Valore stringa attributes: "+attributes);
						    	StringTokenizer virgolaParser=new StringTokenizer(attributes,",");
						    	//in questo modo ho sepato ogni miusurazione
						    	// 0 mem-free LONG 1699528704, 1 mem-used LONG 1734864896,
						    	
						    	while(virgolaParser.hasMoreTokens()){
						    	attributes=virgolaParser.nextToken();
						    	//il token è  0 mem-free LONG 1699528704
						    	StringTokenizer spaceParser=new StringTokenizer(attributes," ");
						    	
						    	Measurement misura=new Measurement();
						    	
						    	misura.setAttribute_index(Integer.parseInt(spaceParser.nextToken()));
						    	System.out.println("AttributeIndex ----------------------- "+misura.getAttribute_index());
						       	misura.setResource_index(Integer.parseInt(spaceParser.nextToken()));
						    	System.out.println("RESOURCE INDEX ----------------------- "+misura.getResource_index());
						    	misura.setShort_name(spaceParser.nextToken());
						    	misura.setJava_type(spaceParser.nextToken());
						    	misura.setValue(spaceParser.nextToken());
						    	//aggiungo l'oggetto misura all'arrayList di entry
						    	System.out.println(misura);
						    	entry.insertMeasurement(misura);
						    	
						    	}
						    	
						    	
						    	/*
						    	 * adesso ho assegnato a tutti gli attributi del db i valori appropriati
						    	 * ed ho creato un oggetto entry dal quale posso accedere a tutte le informazioni
						    	 */
						    	System.out.println("Valori della entry: "+entry);
						    	
						    	//se nella creazione dell'oggetto c ci sono stati dei problemi potrebbe succede che c punti a null
						    		
						    		insertEntry(entry,c);
						    		
						    	
						    	/*metodo delegato dell'inserimento delle entry nel database nel posto corretto
						    	 * la classe store termina e periodicamente inserisce le entry dei nuovi file all'interno del database
						    	 * 
						    	 */
						    		
						       	
						    	
						    }
						    
							
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
	    	
			}catch(Exception e){
				e.printStackTrace();
				}
			
			
	     }
	    
	    
	    }
	    //fine di run
	    
	    
	    
	    private static void loadParametersFromRegistry(){
	    //carico le infromazioni da un file di configurazione
	    	String propsFile = "config/database.config";
	    	Properties props = new Properties();
			try {
				props.load(new FileInputStream(propsFile));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			dbClassName = props.getProperty("dbClassName");
			CONNECTION = props.getProperty("CONNECTION");
			timeout = Long.parseLong(props.getProperty("ThreadTimeout"));
			directory=props.getProperty("directory");
			userName=props.getProperty("user");
			password=props.getProperty("password");
			dbHw=props.getProperty("databaseHardWare");
			dbMeasurement=props.getProperty("databaseMeasurement");
	    }
	    
	    private static void insertEntry(Entry entry,Connection c){
	    	//metodo per l'inserimento delle entry nel database
	    	
	    	try{
	    		Statement stmt = c.createStatement();
	    		
	    	String insertHW_table;
	    	String insertMeasurement;
		    /*
		     * l'inserimento è sulla tabella indicata da dbHw
		     * 
		     */
	    	
	    		insertHW_table = ("INSERT into "+dbHw+" " +
		                "(identification,ip,groupID,providerID,probeID,seq,timestamp,type) "+
		                "values ('"+entry.getIdentification()+"','"+entry.getIp()+"','"+entry.getGroupID()+"','"+entry.getProviderID()+"', '"+entry.getProbeID()+"', '"+entry.getSeq()+"'," +
		                		"'"+entry.getTimestamp()+"','"+entry.getType()+"')") ;
	    		//update della tabella
	    		stmt.executeUpdate(insertHW_table);
	    		
	    		stmt=c.createStatement();
	    		String SELECT_SQL_RICHIESTA_ID = "SELECT id FROM "+dbHw+" WHERE id = LAST_INSERT_ID()";
                ResultSet rs = stmt.executeQuery(SELECT_SQL_RICHIESTA_ID);
                int ris;
                if(rs.next()){
                        ris = rs.getInt("id");
                
                System.out.println("Inserimento in dbHW avvenuto");
	    		
	    		
	    		Iterator<Measurement> iter=entry.getAttributes().iterator();
	    		while(iter.hasNext()){
	    			
	    			Measurement m=iter.next();
		    		//adesso inserisco tutti i dati di ogni misura nel database delle misurazioni
		    		insertMeasurement= ("INSERT into "+dbMeasurement+" " +
			                "(id_riferimento,attribute_index,resource_index,short_name,java_type,value) "+
			                "values ('"+ris+"','"+m.getAttribute_index()+"','"+m.getResource_index()+"','"+m.getShort_name()+"','"+m.getJava_type()+"','"+m.getValue()+"')") ;
		    		stmt.executeUpdate(insertMeasurement);
			    	System.out.println("Inserimento misurazione n: "+m.getAttribute_index());
		    		}
                }else{
                	System.out.println("impossibilie accedere all'ultimo id inserito");
                }
	    	
	    	
	    		
		    System.out.println("inserimenti avvenuti con successo nel db");
	    	
	    	
	    	}catch(SQLException e){
	    		
	    		e.printStackTrace();
	    		System.out.println("Errore nella creazione dello statement o nell'inserimento di un entry nel db");
	    		
	    	}
	    }
	    	
	    	public static ResultSet doProcess(ResourceType type,String misType,SpaceHierarchy spaceGrouping,
	    			List<SpaceSpec> spaceSpec,String dateFrom,String dateTo,long timeGrouping){
	    		
	    		loadParametersFromRegistry();
		    	
		    	Properties p = new Properties();
			    p.put(user,userName);
			    p.put(pass,password);
		    	
			    c=null;//dichiarata a null poichè essendo in seguito in un blocco try catch dopo non era inizializzata
			    
	    		System.out.println("");
	    		System.out.println("");
	    		System.out.println("");
	    		System.out.println("INIZIO doProcess le richieste sono:");
	    		System.out.println("tiporisorsa :"+type);
	    		System.out.println("tipo misurazione: "+misType);
	    		System.out.println("livello di raggruppamento: "+spaceGrouping);
	    		System.out.println("Dati del raggruppamento:");
	    		Iterator<SpaceSpec> it=spaceSpec.iterator();
	    		while(it.hasNext()){
	    			SpaceSpec sp=it.next();
	    			System.out.println(""+sp.getHierarchy()+" "+sp.getObject());
	    		}
	    		System.out.println("Intervallo temporale: "+dateFrom+" "+dateTo);
	    		System.out.println("Raggruppamento temporale in millisecondi: "+timeGrouping);
	    		
	    		/* 
	    		 * a questo punto in base ai dati di ingresso devo construire la query adeguata
	    		 * rispettivamente Resourcetype rappresenta la risorsa che un enum CPU MEMORY DISK NETWORK JMX
	    		 * misType indica il valore dello shortName richiesto 
	    		 * SpaceGrouping indica il livello di raggruppamento richiesto e la lista
	    		 * successica indica i valori necessari per filtrare al liverllo richiesto
	    		 * le date indicano il filtraggio in base al tempo e con il long raggruppo
	    		 */
	    		
	    		/*
	    		 * trasformo subito le date in timestamp
	    		 */
	    		ResultSet resSet=null;
	    		
	    		long timestampFrom=Time.getTimestampFromDate(dateFrom);
	    		long timestampTo=Time.getTimestampFromDate(dateTo);
	    		System.out.println("TimeStampFrom: "+timestampFrom+" TimestampTo: "+timestampTo);
	    		
	    		/*
	    		 * La query è composta da due parti , gli attributi di selezione le condizioni e il ragguppamento
	    		 * la faccio quindi in tre parti separate preparo da prima le stringhe fisse
	    		 */
	    		String finalWhere="";
	    		String finalSelect="";
	    		String finalGroupBy="";
	    		String query="";
	    		String finalFrom="from "+dbHw+","+dbMeasurement+" ";
	    		String fixedWhere="where "+dbHw+".id= "+dbMeasurement+".id_riferimento && timestamp>="+timestampFrom+" && timestamp<="+timestampTo+" && ";
	    		//a questa stringa poi ci appendo la qhere creata dinamicamente
	    		//aggiungo lo shortName solo se è diverso da null
	    		if(type!=null){
	    			fixedWhere+="type='"+type+"' && ";
	    		}
	    		if(misType!=null){
	    			fixedWhere+="short_name='"+misType+"' && ";
	    		}
	    		String fixedSelect=",type,short_name,avg(value),Max(Value),Min(value)";

	    		/*
	    		 * creo dinamicamento la stringa che prende i campi sia della group by che della select
	    		 */
	    		
	    		String fixedGroupBy=",short_name,timestamp DIV "+timeGrouping;
	    		
	    		String dinamicGroupBy="";
	    		String dinamicWhere="";
	    		
	    		
	    		switch(spaceGrouping){
	    		
	    		case PROVIDER:
	    			//se ho il raggruppamento per proviuder significa che ho solo 'informazione sul provider nella lista
	    			dinamicGroupBy="providerID";
	    			break;
	    		case GROUP:
	    			dinamicGroupBy="providerID,groupID";
	    			break;
	    		case MACHINE:
	    			dinamicGroupBy="providerID,groupID,identification";
	    			
		    		break;
	    		default:
	    			System.out.println("Inserito un valore di enumerazione non valido");
	    			break;
	    		
	    		
	    		
	    		
	    		}
	    		
	    		/*
	    		 * preparo le stringe per la where con la lista a prescindere dal livello di aggregazione richiesto
	    		 */
	    		Iterator<SpaceSpec> iter=spaceSpec.iterator();
	    		String providerConstraint="";
    			String groupConstraint="";
    			String machineConstraint="";
    			int i=0;
    			int j=0;
    			int k=0;
	    		while(iter.hasNext()){
	    			
	    			SpaceSpec s=iter.next();
	    			//a questo punto assegno i due campi in modo da formare una stringa da appendere
	    			String identification="";
	    			Integer number;
	    			
	    			SpaceHierarchy hie=(SpaceHierarchy) s.getHierarchy();
	    			
	    			//se ci sono 1 o piu valori devo fare in modo che venga cosi (providerID=0 || providerID=1)
	    			switch(hie){
	    			
	    			case PROVIDER:
	    				if(j==0){
	    				number=(Integer)s.getObject();//prenso l'intero che identifica
	    				providerConstraint+="( providerID"+"="+number;
	    				j++;
	    				
	    				}
	    				else{
	    					number=(Integer)s.getObject();
	    					providerConstraint=providerConstraint+" || "+"providerID"+"="+number;
	    					//devo fare in modo che se non ci sono piu richieste di tipo machine nella lista appendo la )
	    				}
	    				break;
	    			case GROUP:
	    				if(k==0){
	    				number=(Integer)s.getObject();//prenso l'intero che identifica
	    				groupConstraint+="( groupID"+"="+number;
	    				k++;
	    				
	    				}
	    				else{
	    					
	    					number=(Integer)s.getObject();
	    					groupConstraint=groupConstraint+" || "+"groupID"+"="+number;
	    					//devo fare in modo che se non ci sono piu richieste di tipo machine nella lista appendo la )
	    				}
	    				break;
	    			case MACHINE:
	    				if(i==0){
	    				identification=(String)s.getObject();
	    				machineConstraint+="( identification"+"='"+identification+"'";
	    				i++;
	    				
	    				}
	    				else{
	    					
	    					identification=(String)s.getObject();
	    					machineConstraint=machineConstraint+" || "+"identification"+"='"+identification+"'";
	    					//devo fare in modo che se non ci sono piu richieste di tipo machine nella lista appendo la )
	    				}
	    				break;
	    		
	    		
	    			default :
	    				System.out.println("nella lista sono presenti dei valori sbagliati");
	    				break;
	    			}
	    			
	    				
	    		}
	    		
	    		if(j!=0){ //il provider deve essere sempre presente
    			providerConstraint+=" )";
    			dinamicWhere+=providerConstraint;
	    		}
    			//quando è presente il gruppo significa che è presente anche il provider
    			if(k!=0){
    			groupConstraint+=" )";
    			dinamicWhere+=" && "+groupConstraint;
    			//se c'è la macchina significa che provider e group sono gia presenti
    			//quindi suppondo che l'if precedente sia stato fatto
    			}
	    		if(i!=0){
	    		machineConstraint+=" )";
	    		dinamicWhere+=" && "+machineConstraint;
	    		}
    		
	    		
	    	
	    		System.out.println("Query :");
	    		finalSelect="select "+dinamicGroupBy+fixedGroupBy+fixedSelect;
	    		finalWhere=fixedWhere+dinamicWhere;
	    		finalGroupBy="group by "+dinamicGroupBy+fixedGroupBy;
	    		System.out.println(finalSelect);
	    		System.out.println(finalFrom);
	    		System.out.println(finalWhere);
	    		System.out.println(finalGroupBy);
	    		//composizione della query finale
	    		query=finalSelect+" "+finalFrom+finalWhere+" "+finalGroupBy+";";
	    		//query al database

		    	
			    
				try {
					c = DriverManager.getConnection(CONNECTION,p);
				} catch (SQLException e) {
				
					e.printStackTrace();
					System.out.println("errore nella connessione con il database");
				}
	    		try{
			    	Statement stmt = c.createStatement();
			    	resSet = stmt.executeQuery(query);

			    	}catch(SQLException e){
			    		e.printStackTrace();
			    		System.out.println("errore nell'esecuzione della query di selezione");
			    	}
			    	
			    	return resSet;
	    	}
	    	
	    
	    
	    
}
	    	
	    	

public class Main {
	public static void main(String[] args) {
		if(args.length != 1){
			System.out.println("Bad Parameter, use only [Inserimento][Estrazione]");
			System.exit(0);
		}
		String parameter = args[0];
		Component component_to_run = null;
		if(parameter.equalsIgnoreCase("Inserimento"))
			component_to_run = Component.INSERIMENTO;
		else if (parameter.equalsIgnoreCase("estrazione"))
			component_to_run = Component.ESTRAZIONE;
		else{
			System.out.println("Bad Parameter, use only [Inserimento][Estrazione]");
			System.exit(0);
		}
		switch (component_to_run) {
			case INSERIMENTO : InserimentoDati.main(null); break;
			case ESTRAZIONE : EstrazioneDati.main(null); break;
			default:{System.out.println("Bad Parameter, use only [Inserimento][Estrazione]");System.exit(0);}
		}
	}


enum Component{
	INSERIMENTO,
	ESTRAZIONE
}
}

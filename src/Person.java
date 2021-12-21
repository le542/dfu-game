
import java.util.HashMap;

class Person {
	private Display display;
	private String name;
	private String[] sentences;
	private String rejection;
	private HashMap<String, String[]> response; 

	//constructor
	public Person (String name, String[] sentences) {
		this.name=name;
		this.sentences=sentences;
		//rejection: when given an object they don't want
		rejection = "\"No thanks\", " + name + " replies.";
		//response: when given an object they want
		response = new HashMap<String, String[]>();
		display = new Display();  //used for formatting
	}

	//sets the response. 
	//parameters: item they desire (string), and their response (String[])
	public void setResponse(String desire, String[] r){
		response.put(desire, r);
	}
  
	//when player gives an item they dont want
	public String getRejection() {
		return rejection;
	}

	//getter for name
	public String getName() {
		return name;
	}

	//display sentences when the player talks to them
	public void displaySentences() {
		for (int i = 0; i < sentences.length; i++) {
			display.standard(sentences[i]);
			//adds space between paragraphs if theres more than 1
			if (i < sentences.length - 1) display.blankLine();
		}
	}

	// returns whether or not the person want the item
	//true - yes, false - no
	public boolean desires(String item) {
		return (response.get(item) != null);
	}

	//displays response when given an item 
	public void displayResponse(String item) {
		for (int i = 0; i < response.get(item).length; i++) {
			display.standard(response.get(item)[i]);
			//adds space between paragraphs if theres more than 1
			if (i < response.get(item).length - 1) display.blankLine();
		}
	}

}
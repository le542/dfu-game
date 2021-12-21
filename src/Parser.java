/*
* 
 * This class is part of Zork. Zork is a simple, text based adventure game.
 *
 * This parser reads user input and tries to interpret it as a "Zork"
 * command. Every time it is called it reads a line from the terminal and
 * tries to interpret the line as a four word command. It returns the command
 * as an object of class Command.
 *
 * The parser has a set of known command words. It checks user input against
 * the known commands, and if the input is not one of the known commands, it
 * returns a command object that is marked as an unknown command.
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

class Parser {
		private String[] validCommands;  // holds valid commands

   	public Parser() {
			 validCommands = new String[] {"go", "take", "back", "help", "quit", "look", "inventory", "weigh", "trash", "talk", "give", "jump", "fight", "dev"};
   	}

	 public String getPlayerName() {
		 String inputLine = "";
		 System.out.print("> ");

		 BufferedReader reader =
           new BufferedReader(new InputStreamReader(System.in));
       try {
           inputLine = reader.readLine();
       }
       catch(java.io.IOException exc) {
           System.out.println ("There was an error during reading: "
                               + exc.getMessage());
      }

			return inputLine;
	 }

   public Command getCommand()     {
       String inputLine = "";   // will hold the full input line
       String word1;
       String word2;
			 String word3;
			 String word4;
	

       System.out.print("> "); 

       BufferedReader reader =
           new BufferedReader(new InputStreamReader(System.in)); 
					 // takes user input
       try {
           inputLine = reader.readLine().toLowerCase();
					 // turns it into lowercase letters
       }
       catch(java.io.IOException exc) {
           System.out.println ("There was an error during reading: "
                               + exc.getMessage());
       }

       StringTokenizer tokenizer = new StringTokenizer(inputLine);

       if(tokenizer.hasMoreTokens())
           word1 = tokenizer.nextToken();      // get first word
       else
           word1 = "look";
					 //enter key automatically defaults to 'look'
       if(tokenizer.hasMoreTokens())
           word2 = tokenizer.nextToken();      // get second word
       else
           word2 = null;
		   if(tokenizer.hasMoreTokens())
           word3 = tokenizer.nextToken();      // get third word
       else
           word3 = null;
			 if(tokenizer.hasMoreTokens())
           word4 = tokenizer.nextToken();      // get fourth word
       else
           word4 = null;

       // note: we just ignore the rest of the input line.

       // Now check whether this word is known. If so, create a command
       // with it. If not, create a "nil" command (for unknown command).

       if(isCommand(word1))
           return new Command(word1, word2, word3, word4);
       else
           return new Command(null, word2, word3, word4);
   }


	/**
	* Check whether a given String is a valid command word. Return true if it is,
  * false if it isn't.	 **/
	public boolean isCommand(String aString) {
		for (String command: validCommands) {
			if (command.equals(aString))
				return true;
		}
		// if we get here, the string was not found in the commands
		return false;
	}

}


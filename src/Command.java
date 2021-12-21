/*
 * Class Command - a command in an adventure game.
 *
 * Author:  Michael Kolling 
 * Modified by Kevin Le and Arfah Khan Dec 2019
 * 
 * The Command object is an object that contains 4 strings
 * Commands are created by user input
 * 
 */

class Command
{
   private String commandWord;
   private String secondWord;
   private String thirdWord;
	 private String fourthWord;

   /* CONSTRUCTOR
    * Create a command object. All four word must be supplied, but
    * any of them can be null. The command word should be null to
    * indicate that this was a command that is not recognised by this game.*/
   public Command(String firstWord, String secondWord, String thirdWord, String fourthWord) {
		commandWord = firstWord;
		this.secondWord = secondWord;
		this.thirdWord = thirdWord;
		this.fourthWord = fourthWord;
   }

   /**
    * Return the command word (the first word) of this command. If the
    * command was not understood, the result is null.
    */
   public String getCommandWord()
   {
       return commandWord;
   }


   /** GETTERS
    * Returns the second, third and fourth words of this command. 
		* Returns null if there was none.
    */
		
   public String getSecondWord()
   {
       return secondWord;
   }
   public String getThirdWord()
   {
       return thirdWord;
	 }
	 public String getFourthWord() {
		 return fourthWord;
	 }

	 
   /**
    * Return true if this command was not understood.
    */
   public boolean isUnknown()
   {
       return (commandWord == null);
   }

   /**
    * Return true if the command has a second/third/fourth word.
    */
   public boolean hasSecondWord()
   {
    	return (secondWord != null);
   }
   public boolean hasThirdWord()
   {
			return (thirdWord != null);
   }
	 public boolean hasFourthWord() {
		 return (fourthWord != null);
	 }

}


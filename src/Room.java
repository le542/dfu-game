/*
 * Class Room - a room in an adventure game.
 *
 * Author:  Michael Kolling 
 * Modified by Kevin Le and Arfah Khan Dec 2019
 * 
 * "Room" represents one location in the scenery of the game.  It is 
 * connected to other rooms via exits.  For each exit, the room stores a reference
 * to the neighbouring room.
 */

import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

class Room
{
   private Display display;
	 private String name;
   private String[] description;
   private HashMap<String, Room> exits;    // stores exits of this room.
	 private List<Item> items;
	 private Person person;

   /**
    * Create a room with a description(String array). 
		* Initially, it has no exits or items
    */
   public Room(String name, String[] description) {
    this.name = name; //name of room
		this.description = description;
		exits = new HashMap<String, Room>();
		items = new ArrayList<Item>(); 
		person = null; // no one in the room
		display = new Display(); //used for formatting
   }
	
	//sets the description of the room
	 public void setDescription(String[] d) {
		 description = d;
	 }

   /**
    * Define the exits of this room. 
    */
   public void setExits(Room[] e) { //takes in array of rooms
		for (Room r: e) {
			exits.put(r.getName(), r);
		}
   }
	 public void addExit(Room e) { //takes in a single room
		 exits.put(e.getName(), e);
	 }

	 /**
    * adds Item object to room .
    */
		public void addItem(Item thing)
   {
			items.add(thing);
   }


	// remove item by string
	 	public void removeItem(String s)
   {
				items.remove(getItemByName(s));
   }

	// set person
	 public void setPerson(Person p) {
		 person = p;
   }
   
	 // removes person
	 public void removePerson() {
		 person = null;
	 }

	 	 /**
    * Return the name of the room (the one that was defined in the
    * constructor).
    */
   public String getName() {
       return name;
   }

   /**
    * Return a description of this room
		* as well as the person, any items, and the exits
    */
   public void getDescription() {
		 	 display.paragraphs(description);
			 if (person != null) display.standard(personString()); 
			 if (items.size() > 0) display.standard(itemString());
			 if (exits.size() > 0) display.standard(exitString());
   }

   /**
    * Return a string listing the room's exits
    */
   private String exitString()
   {
       String returnString = "Exits:";
			 Set<String> keys = exits.keySet();
       for(Iterator<String> iter = keys.iterator(); iter.hasNext(); ) {
				 returnString += " " + iter.next() + ",";
			 }
			 //removes the last comma
       return returnString.substring(0,returnString.length()-1);
   }


		  /**
    * Return a string listing the room's items
    */
   private String itemString() {
       String returnString = "Items:";
       for(Item i: items) {
				 returnString += " " + i.getName();
				 //if it has a price it displays it.
				 if (i.getCost() > 0) returnString += " ($" + i.getCost() + ")";
				 returnString += ",";
			 }
			 //removes the last comma.
       return returnString.substring(0,returnString.length()-1);
   }

	 	   /**
    * Return a string showing the person in the room
    */
   private String personString() {
       return "People: " + person.getName();
   }


   /**
    * Return the room that is reached if we go from this room in direction
    * "direction". If there is no room in that direction, return null.
    */
   public Room nextRoom(String roomName){
       return (Room)exits.get(roomName);
   }

	//returns an Item object in the room when given a String
	 public Item getItemByName(String name){
		 for (Item i: items) {
			 if (i.getName().equals(name)) {
				 return i;
			 }
		 }
		 return null; //if item not found
	 }

	//getter for Person object
	 public Person getPerson() {
		 return person;
	 }


}

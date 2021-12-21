/**
 * Class Game - the main class of the "Zork" game.
 *
 * Author: Michael Kolling Version: 1.1 Date: March 2000
 * Modified by: Kevin Good Date: October 2019
 * Modified again by: Kevin Le and Arfah Khan Date: Dec 2019
 *
 * To play this game, create an instance of this class and call the "play"
 * routine
 * 
 * This main class creates and initializes all the others: it creates all rooms,
 * creates the parser and starts the game. It also evaluates the commands that
 * the parser returns.
 *
 * Bugs: the timer is not the most accurate :(
 *       you get +60 minutes when it's a new hour on the clock
 *
 */
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.time.LocalTime;

class Game {
	private Parser parser; 
	private Display display;
	private String playerName;
	private Room currentRoom;
	private Room[] allRooms;
	private List<Room> undoList = new ArrayList<Room>();
	private List<Item> inventory = new ArrayList<Item>();
	private int cash = 0;
	private int inventorySpace = 1; //The amount of space availible in inventory
	private int inventoryMax = 1; //The max amount of space in inventory
	private boolean timerOn = false;
	private int startTime = 0;
	private int timePassed = 0;
	private int duration = 600; //600 is 10 minutes, 300 is 5 minutes
	private LocalTime localTime; 
	private boolean finished = false; //keeps loop in play() running
	private int chanceOfWinning; //scale of 1-10 (see fight() method)

	//constructor
	public Game() {
		/** Create the parser (takes in user input) and displayer (makes it look aesthetically pleasing) */
		parser = new Parser();
		display = new Display();
	}

	/* ------------- METHODS : ---------------*/

	//play (main loop)
	public void play() {

		System.out.println();
		System.out.println();

		// builds the game objects
		create();
		//opening screen
		printWelcome();

		// Enter the main command loop. Here we repeatedly read commands and
		// execute them until the game is over. 
		while (!finished) {
			
			//User input 
			Command command = parser.getCommand();
			display.top();

			//when the timer is turned on (happens mid-game)
			if (timerOn) {
				localTime = LocalTime.now(); //gets the current time
				int timeRemaining = duration - timePassed; 
				//duration is either 600 (10 min) or 300 (5 min)
				int minutes = timeRemaining/60;
				int seconds = timeRemaining%60;
				if (seconds < 10) {
					//adds a zero before single digits
					display.centered("Time remaining: " + minutes + ":0" + seconds);
				} else {
					display.centered("Time remaining: " + minutes + ":" + seconds);
				}
				display.blankLine();
			}

			//what the player sees
			processCommand(command);
			display.bottom();

			// check to see if timer has ran out
			if (timerOn) {
				localTime = LocalTime.now();
				timePassed = (localTime.getMinute() * 60 + localTime.getSecond()) - startTime; //amount of seconds passed after the timer went off
				if (timePassed > duration) {
					//duration is either 600 (10 min) or 300 (5 min)
					display.top();
					display.centered("TIMES UP! The cops have located you and you have been arrested for your crimes.");
					display.centered("GAME OVER.");
					display.bottom();
					finished = true; // stop loop
				}
			}


		}
	}

	//creates rooms, items, scripts, etc.
	private void create() {

		// gets name of player 
		display.top();
		display.centered("Please enter your name.");
		display.bottom();
		playerName = parser.getPlayerName();

		// create people and their opening lines (when you talk to them)
		// we only have one person because the game is already complicated enough
		Person dood = new Person("Mr. Dood", new String[] { "'Hi " + playerName + ",' Mr. Dood says. 'How’s your project going?' You tell him you need more time on your project. ‘Sorry, I can’t help you there,’ he says. ", "You realize what this means…", "There’s no way you're going to get this project done without bribing him." });

		// response (what happens when you give items to the person)
		// parameters: Name of the item (String) and the paragraphs of their response (String[])
		// as you can see, their responses usually unlock something new in the game
		// see give() function to see the code to the unlocks
		dood.setResponse("cookies", new String[] {
			 "You give him the cookies. ‘Wow, what a surprise! Thanks, " + playerName + ".’", "You ask about the deadline. He rejects the suggestion despite accepting your gift. It looks like you’re going to have to source elsewhere... maybe he'll want Wendy's?", "[New exit unlocked: outside]" });
		dood.setResponse("nuggets", new String[] { 
			"You give him the nuggets. ‘How lovely’ he says. He gladly accepts the nuggets.", "He takes a bite and then starts choking. ‘Need….. Drink…’ pointing to the mug across the room", "[New item in room: mug]"});
		dood.setResponse("mug", new String[] {
			"Holding the mug by the handle, you smash it against the desk, creating a sharp jagged weapon. You use it to mug Mr. Dood.", "'EMPTY YOUR POCKETS' you say. He complies, tossing his keys on the floor."});

		// rooms and their descriptions
		// room construction parameters: name of room (string), paragraphs of description of room (String[])
		Room room109 = new Room("room 109", new String[] { 
			"You're in Room 109, where you attend your APCSA class.", "Type 'talk' to talk to Mr. Dood." });
		Room commons = new Room("commons", new String[] { 
			"You're in commons. The snack line is open, so you walk over there." });
		Room outside = new Room("outside", new String[] { 
			"You're outside in a parking lot." });
		Room pickerington = new Room("pickerington", new String[] { 
			"You're outside in a parking lot." }); //same as outside
		Room kroger = new Room("kroger", new String[] { 
			"You're at Kroger. You wander towards the home goods section, and you see they have a sale on travel items." });
		Room bank = new Room("bank", new String[] { 
			"You're at the Chase Bank ATM. Please enter your card and type your pin to withdraw cash." });
		Room wendys = new Room("wendys", new String[] {
			"You are at Wendy’s, looking at the dollar menu (because 4 for 4s are still too expensive)"});
		Room freeway = new Room("freeway",
				new String[] { "You're on the freeway with Mr. Dood's car. Where would you like to go?" });
		Room airport = new Room("airport", new String[] { "You are now at the airport. The next flight out of here is to Barcelona, Spain." });
		Room plane = new Room("plane", new String[] { "You're on a plane to Spain... but you notice the flight attendants whispering to eachother and looking at you... do you think they know what you've done?", "Hint: type 'help' to take a look at availible commands... specifically the last one." });
		Room spain = new Room("spain", new String[] { "You're in Spain. You walk out the plane and you get stopped by the authorities. Game over." });
		Room ocean = new Room("ocean", new String[] { "You're in the ocean. You see an island in the distance and something underwater... is it a shark?" });
		Room underwater = new Room("underwater", new String[] { "You swim down underwater, and drown. The shark laughs at you. Game over." });
		Room death = new Room("death", new String[] { "You jumped off a plane without a parachute. Your body crushed on impact. Game over." });
		Room island = new Room("island", new String[] { "You swim to the island. You are now on the beach.","You see a masked figure in a boat approaching the island. They don't look like the authorities, but they could be dangerous.", "You better find a way to arm yourself. You see a shack." });
		Room shack = new Room("shack", new String[] {
				"The shack is empty. Any sort of object that could've been used as a weapon fell into the portal on the floor.", "The portal will send you back to Pickerington. Type 'jump' to go through portal.", "Try to find the best weapon possible to arm yourself. Remember, you are still wanted by the police, so you must return to the portal in less than 5 minutes. Good luck." });
		Room portal = new Room("portal", new String[] {
				"You exit the shack. You are now on the beach outside. The sun is setting, and you see a masked person running towards you with a sword.", "Type 'fight' to commence the final stage of the game." });



		//adds all rooms to the array allRooms for easy, universal access 
		allRooms = new Room[] {room109, airport, kroger, wendys, bank, freeway, outside, commons, plane, spain, ocean, death, underwater, island, shack, portal};

		// set initial exits for each room
		// parameters: an array of Rooms that can be exits.
		room109.setExits(new Room[] { commons });
		commons.setExits(new Room[] { room109 });
		outside.setExits(new Room[] { room109, kroger, bank, wendys});
		pickerington.setExits(new Room[] { room109, kroger, bank, wendys, freeway});
		kroger.setExits(new Room[] { outside });
		wendys.setExits(new Room[] { outside });
		bank.setExits(new Room[] { outside });
		freeway.setExits(new Room[] { airport, pickerington });
		airport.setExits(new Room[] { freeway, plane });
		plane.setExits(new Room[] { spain });
		ocean.setExits(new Room[] { underwater, island });
		island.setExits(new Room[] { shack, ocean });
		shack.setExits(new Room[] { island });


		// assigns person to room. param: Person object.
		room109.setPerson(dood);

		// add objects and their weight and cost
		// param: name (string), weight (int), and cost (int)
		// things with zero weight are special items
		room109.addItem(new Item("bookbag", 0, 0));
		outside.addItem(new Item("car", 0, 0));
		commons.addItem(new Item("cookies", 1, 1));
		wendys.addItem(new Item("nuggets", 2, 1));
		kroger.addItem(new Item("parachute", 5, 140));
		kroger.addItem(new Item("luggage", 0, 100));
		airport.addItem(new Item("ticket", 1, 700));


		// start game in room 109
		currentRoom = room109;

	}

	//opening message
	private void printWelcome() {
		System.out.println();

		display.top();
		display.centered("Hi " + playerName + ". Welcome to");
		display.blankLine();
		display.standard("       ██████╗  ██████╗ ███╗   ██╗████████╗");
		display.standard("       ██╔══██╗██╔═══██╗████╗  ██║╚══██╔══╝");
		display.standard("       ██║  ██║██║   ██║██╔██╗ ██║   ██║");
		display.standard("       ██║  ██║██║   ██║██║╚██╗██║   ██║");
		display.standard("       ██████╔╝╚██████╔╝██║ ╚████║   ██║");
		display.standard("       ╚═════╝  ╚═════╝ ╚═╝  ╚═══╝   ╚═╝");
		display.standard("       ███████╗ ██████╗ ██████╗ ██╗  ██╗");
		display.standard("       ██╔════╝██╔═══██╗██╔══██╗██║ ██╔╝");
		display.standard("       █████╗  ██║   ██║██████╔╝█████╔╝ ");
		display.standard("       ██╔══╝  ██║   ██║██╔══██╗██╔═██╗ ");
		display.standard("       ██║     ╚██████╔╝██║  ██║██║  ██╗");
		display.standard("       ╚═╝      ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝");
		display.standard("                ██╗   ██╗██████╗ ");
		display.standard("                ██║   ██║██╔══██╗");
		display.standard("                ██║   ██║██████╔╝");
		display.standard("                ██║   ██║██╔═══╝ ");
		display.standard("                ╚██████╔╝██║     ");
		display.standard("                 ╚═════╝ ╚═╝    ");

		display.blankLine();
		display.centered("The Zorkian-Based Game");
		display.centered("By Kevin Le and Arfah Khan");
		display.blankLine();
		display.centered("Press 'enter' to continue.");
		display.bottom();
		parser.getCommand(); //keystroke
		display.top();
		display.centered("You are sitting in your APCSA class, pretending to be working on your project. Your project is due tomorrow, and you barely even started it! You better talk to your teacher Mr. Dood about giving you an extension.");
		display.blankLine();
		display.centered("Press 'enter' to see instructions.");
		display.bottom();
		parser.getCommand(); //keystroke
		display.top();
		printHelp();
		display.blankLine();
		display.blankLine();
		display.centered("Hint: Don't forget to 'TAKE BOOKBAG' before leaving the room");
		display.blankLine();
		display.centered("Press 'enter' to start game.");
		display.bottom();
		System.out.println();
	}

	//processes command
	private void processCommand(Command command) {
		// implemented in play() loop
		// takes in a Command and redirects it to another method

		if (command.isUnknown()) { 
			if (currentRoom.getName().equals("bank")) { 
					display.centered("Sorry, that is not the correct pin. Please try again."); 
					// if they're at a bank, this message is shown
					// (red herring, there is no pin)
			} else {
				//if they're not at the bank:
				display.centered("Invalid or unavailible command");
			}
			return;
		}

		String commandWord = command.getCommandWord();
		if (commandWord.equals("help"))
			printHelp();
		else if (commandWord.equals("look")) {
			look();
		} else if (commandWord.equals("go")) {
			goRoom(command);
		} else if (commandWord.equals("take")) {
			take(command);
		} else if (commandWord.equals("inventory")) {
			printInventory();
		} else if (commandWord.equals("back")) {
			goBack();
		} else if (commandWord.equals("weigh")) {
			weigh(command);
		} else if (commandWord.equals("trash")) {
			trash(command);
		} else if (commandWord.equals("talk")) {
			talk(command);
		} else if (commandWord.equals("jump")) {
			jump();
		} else if (commandWord.equals("give")) {
			give(command);
		} else if (commandWord.equals("fight")) {
			fight();
		} else if (commandWord.equals("dev")) {
			dev();
		} else if (commandWord.equals("quit")) {
			if (command.hasSecondWord()) {
				display.centered("Quit what?");
				display.centered("Type 'help' if you need help.");
			} else {
				display.centered("ok byeeee.");
				finished = true; // signal that we want to quit
			}
		}
	}

	/* ----------- USER COMMANDS: ---------------*/

	//displays room descriptions 
	private void look() {
		currentRoom.getDescription(); //prints the room description

		//game overs
		String room = currentRoom.getName(); //gets the name of the room
		if (room.equals("spain") || room.equals("underwater") || room.equals("death")) {
			finished = true; //if the name matches, the game ends
		}

	}

	//jump command
	private void jump() {
		// this command only works on a plane or at the shack with the portal
		if (currentRoom.getName().equals("plane")) { 
			if (scanInventory("parachute")!=null) {
				display.standard("You jump off the plane. The parachute opens and you land safely. You've successfully escaped the authorities. Timer has been stopped. ");
				inventorySpace += scanInventory("parachute").getWeight();
				inventory.remove(scanInventory("parachute"));
				timerOn = false;
				display.blankLine();
				undoList.add(currentRoom);
				currentRoom = allRooms[10]; // goes to ocean!
				look();
			} else {
				currentRoom = allRooms[11]; // goes to death!
				look();
			}
		} else if(currentRoom.getName().equals("shack")){

			int random = (int)(Math.random() * 6); //random between 0 and 5
			undoList.add(currentRoom); // to go back
			currentRoom = allRooms[random]; //goes to random room
			currentRoom.addExit(allRooms[15]); //adds portal exit to random room
			startTimer(300); // 300 seconds is 5 minutes

			random = (int)(Math.random() * 6); //generates another random number between 0 and 5
			allRooms[random].addItem(new Item("fork", 3, 0)); //puts fork in random room.
			display.standard("The portal randomly teleported you to this location. Find the best weapon you can find and return here before the timer runs out. You have 5 minutes.");
			display.blankLine();
			look();
		} else {
			//if they're located anywhere other than the plane or shack
			display.centered("You jump in place. Nothing happens, but you looked really dumb doing it.");
		}
	}

	//displays commands and their descriptions
	private void printHelp() {
		//runs when player types 'help'
		display.centered("--- IMPORTANT Commands ---");
		display.blankLine();
		display.standard("> go [exit] / go to [exit]");
		display.standard("     - Lets you move around");
		display.standard("> take [item] / trash [item]");
		display.standard("     - Adds/removes an item from inventory");
		display.standard("> give [item]");
		display.standard("     - Gives item (in inventory) to person");
		display.standard("> [ENTER KEY] / look");
		display.standard("     - Get info about room");
		display.blankLine();
		display.blankLine();
		display.centered("--- Other Commands ---");
		display.standard("> inventory");
		display.standard("     - Displays inventory and amount of money");
		display.standard("> weigh [item]");
		display.standard("     - Shows the weight of item");
		display.standard("> back");
		display.standard("     - Goes back one move");
		display.standard("> help");
		display.standard("     - View these instructions again");
		display.standard("> jump");
		display.standard("     - Jump up and down");
	}

	//enter rooms
	private void goRoom(Command command) {
		String roomName;

		if (!command.hasSecondWord()) {
			// if the command is just "go", we don't know where to go...
			display.centered("Go where?");
			return;
		} else if (command.getSecondWord().equals("back")) {
			// in case they accidentally say "go back"
			goBack();
			return;
		} else if (command.getSecondWord().equals("to")||command.getSecondWord().equals("on")) {
			if (command.hasThirdWord()) {
				// if the command was "go to/on [room]" roomName will be the third word
				roomName = command.getThirdWord();
				if (command.hasFourthWord()) {
					// for two word roomnames (ex. 'room 109')
					roomName += " " + command.getFourthWord();
				}
			} else {
				// if the command is just "go to"
				display.centered("Go to where?");
				return;
			}
		} else {
			// if the command was "go [room]" roomName will be second word
			roomName = command.getSecondWord();
			if (command.hasThirdWord()) {
				// for two word roomnames (ex. 'room 109')
				roomName += " " + command.getThirdWord();
			}
		// if they try to go to the room they're already in
		} 
		
		if (roomName.equals(currentRoom.getName())) {
			display.centered("You're already here.");
			return;
		}

		// Try to leave current room.
		Room nextRoom = currentRoom.nextRoom(roomName);
		if (nextRoom == null) { //If can't find room
			display.centered("Room not found."); 
			return;
		}

		// RESTRICTION: they can't go on the plane without a ticket
		if (nextRoom.getName().equals("plane")) {
			if (scanInventory("ticket")==null) {
				//no ticket
				display.centered("You need a ticket to get on a plane");
				return;
			} else {
				//removes ticket from inventory
				inventorySpace += scanInventory("ticket").getWeight(); 
				inventory.remove(scanInventory("ticket"));
			}
		}

		// after returning to the island through portal (last stage) 
		if (nextRoom.getName().equals("portal")) {
			timerOn = false; //turn off 5-minute timer
			undoList.clear(); //prohibits going back
			showBestWeapon(); //reveals weapon for the final fight
		}
		
		undoList.add(currentRoom); // for the undolist
		currentRoom = nextRoom; // moves to the next room
		look(); //shows description

	}

	//weighs item
	private void weigh(Command command) {
		String itemName; 
		Item itemObject;
		if (!command.hasSecondWord()) {
			// if the command is just "weigh", we don't know what to weigh...
			display.centered("Weight what?");
			display.centered("Type 'help' if you need help");
			return;
		} else {
			itemName = command.getSecondWord();
			// two word items
			if (command.hasThirdWord()) {
				itemName += " " + command.getThirdWord();
			}
		}

		// Try to find the item object with the string input
		if (currentRoom.getItemByName(itemName) == null) { 
			//object not found
			display.centered("We cannot find that item in the room");
			return;
		} else {
			//displays weight
			itemObject = currentRoom.getItemByName(itemName); 
			display.centered("Weight: " + itemObject.getWeight());
		}

	}

	//take item
	private void take(Command command) {
		String itemName;
		Item itemObject;
		if (!command.hasSecondWord()) {
			// if the command is just "take", we don't know what to take...
			display.centered("Take what?");
			display.centered("Type 'help' if you need help.");
			return;
		} else {
			itemName = command.getSecondWord();
			// two word item
			if (command.hasThirdWord()) {
				itemName += " " + command.getThirdWord();
			}
		}

		// Try to find item
		if (currentRoom.getItemByName(itemName) == null) {
			display
					.centered("We cannot find that item in the room.");
			return;
		} else {
			itemObject = currentRoom.getItemByName(itemName);
		}

		//Check if there's enough money
		if (itemObject.getCost() > cash) {
			display.centered("Not enough money to take item.");
			display.blankLine();
			printInventory();
			return;
		} 

		// Check if there's enough space
		if (itemObject.getWeight() > inventorySpace) {
			display.centered("Not enough space in inventory.");
			display.blankLine();
			printInventory();
			return;
		} 

		//Special items 
		//these objects are technically not added to the inventory

		if (itemObject.getWeight() == 0) { 

			//bookbag
			if (itemName.equals("bookbag")) {
					inventoryMax += 3; //increases inventory capacity
					inventorySpace += 3;
					cash += 2; //adds 2 bucks
					currentRoom.removeItem("bookbag"); //removes bookbag from room
					display.centered("Bookbag taken. Inventory capacity has been upgraded by 3");
					display.blankLine();
					display.centered("Also, you found $2 in the front pocket.");
					return;
			} 

			// car
			if (itemName.equals("car")) {
				if (scanInventory("keys")!=null) {
					display.centered("Car has been taken.");
					display.centered("[New exit unlocked: freeway]");
					currentRoom.removeItem("car"); //removes car from room
					inventorySpace += scanInventory("keys").getWeight();
					inventory.remove(scanInventory("keys")); //removes keys from inventory
					currentRoom.addExit(allRooms[5]); //adds freeway exit to outside
				} else {
					display.centered("You need keys");
				}
				return;
			} 
			
			//luggage (same as bookbag basically)
			if (itemName.equals("luggage")) {
				inventoryMax += 6;
				inventorySpace += 6;
				cash -= itemObject.getCost(); //luggage costed money
				currentRoom.removeItem("luggage");
				display.centered("Luggage bought. Inventory capacity has been upgraded by 6");
				display.centered("Cash remaining: $" + cash);
			} 

			//money
			if (itemName.equals("money")) {
				cash += 1000; 
				currentRoom.removeItem("money");
				display.centered("You've collected the money. You now have $" + cash);
			} 

		// weapons (this is for the final stage of the game)
		// new weapons appear after one is picked up
		} else if (itemName.equals("fork")||itemName.equals("pitchfork")||itemName.equals("trident")) {
				inventory.add(itemObject); //adds to inventory
				currentRoom.removeItem(itemName); //removes from room
				inventorySpace -= itemObject.getWeight(); //take up space
				display.centered("Weapon \""+itemObject.getName()+"\" added");
				if (itemObject.getCost()>0) display.centered("Cash remaining: $" + cash);
				display.blankLine();
				 
				switch(itemName) {
					case "fork":
						//puts pitchfork at kroger every time
						allRooms[2].addItem(new Item("pitchfork", 5, 60));
						display.centered("[New weapon added somewhere in the area]");
						break;
					case "pitchfork":
						//generates a random number between 0 and 5
						int random = (int)(Math.random() * 6);
						//adds the trident to a random room
						cash -= itemObject.getCost(); //cost money
						allRooms[random].addItem(new Item("trident", 7, 0));
						display.centered("[New weapon added somewhere in the area]");
						break;
					case "trident":
						display.centered("This the best weapon availible. Return to the portal ASAP!");
						break;
					default:
						break;
				}

		// standard procedure (for normal objects)		
		} else {
			inventory.add(itemObject); //add to inventory
			currentRoom.removeItem(itemName); //remove from room
			inventorySpace -= itemObject.getWeight(); //takes up space
			cash -= itemObject.getCost(); //cost money
			display.centered("Item \""+itemObject.getName()+"\" added"); 
			if (itemObject.getCost()>0) display.centered("Cash remaining: $" + cash);
		}

		if (itemName.equals("brick")) {

			display.bottom();
			parser.getCommand(); // requires keystroke

			display.top();
			display.standard("‘Sorry, our machine did not regi-’");
			display.blankLine();
			display.standard("You get fed up and smash the ATM machine with a brick. It works, and cash spills onto the ground beneath you.");
			display.blankLine();
			display.standard("[New item in room: money]");

			currentRoom.addItem(new Item("money", 0, 0)); //add item to room
			inventorySpace += scanInventory("brick").getWeight();
			inventory.remove(scanInventory("brick")); //remove brick from inventory
			currentRoom.setDescription(new String[] {"You're at the Chase Bank ATM. The ATM is currently out of order due to a recent robbery."}); //updates bank description
		}

	}

	//trash item
	private void trash(Command command) {
		String itemName;
		Item itemObject = null;
		if (!command.hasSecondWord()) {
			// if the command is just "trash", we don't know what to trash...
			display.centered("Trash what?");
			display.centered("Type 'help' if you need help.");
			return;
		} else {
			itemName = command.getSecondWord();
			// two word item
			if (command.hasThirdWord()) {
				itemName += " " + command.getThirdWord();
			}
		}

		// Try to find item using a for each loop
		itemObject = scanInventory(itemName);

		if (itemObject == null) { //is null by default
			display.centered("Item not in inventory.");
			return;
		} else {
			//removes object from inventory
			inventorySpace += itemObject.getWeight();
			inventory.remove(itemObject);
			display.centered("Item \"" + itemObject.getName() + "\" removed");
		}

	}

	// back button
	private void goBack() {
		//undolist is basically like search history
		if (undoList.isEmpty()) { //if there's no history
			display.centered("You have reached the beginning of the game.");
			display.centered("All you can do now is move forward.");
		} else {
			//goes to previous room and deletes the room from the history
			currentRoom = undoList.get(undoList.size() - 1);
			undoList.remove(undoList.size() - 1);
			look();
		}
	}

	// prints the inventory and how much is left
	private void printInventory() {
		String capacity = "" + (inventoryMax - inventorySpace) + "/" + inventoryMax;
		display.centered("Inventory: [" + capacity + "]"); //displays capacities
		if (inventorySpace == 0) display.centered("!! FULL !!"); //if full
		display.blankLine();
		display.standard("cash [$" + cash + "]"); //shows cash
		for (Item i : inventory) { //lists every item and weight
			display.standard(i.getName() + " [weight: " + i.getWeight() + "]");
		}
	}

	//talks to person
	private void talk(Command command) {
		if (currentRoom.getPerson() == null) {
			display.centered("There's no one in the room to talk to.");
		} else {
			currentRoom.getPerson().displaySentences();
		}
	}

	//gives items to person
	private void give(Command command) {
		String itemName;
		Person personObject = null;
		Item itemObject = null;

		if (command.hasThirdWord()) {
			itemName = command.getSecondWord() + " " + command.getThirdWord();
		} else if (command.hasSecondWord()) {
			itemName = command.getSecondWord();
		} else {
			display.centered("What?");
			return;
		}

		personObject = currentRoom.getPerson(); 

		for (Item i : inventory) {
			if (i.getName().equals(itemName)) {
				itemObject = i;
			}
		}

		if (personObject == null) {
			display.centered("There is no one in the room to give " + itemName + " to. Type 'help' if you need help.");
			return;
		} else if (itemObject == null) {
			display.centered("That item is not in your inventory. Type 'help' if you need help.");
			return;
		} else if (!personObject.desires(itemName)) {
			talk(command);
			display.standard(personObject.getRejection());
			return;
		} else {
			personObject.displayResponse(itemName);
			inventorySpace += itemObject.getWeight();
			inventory.remove(itemObject);
		}

		//every time an item is given, new things unlock

		//when you give dood cookies cookies 
		if (itemName.equals("cookies") && currentRoom.getName().equals("room 109")) {
			 //adds outside (index 6) exit for room 109 (currentRoom)
			currentRoom.addExit(allRooms[6]);
			//adds outside (index 6) exit for commons (index 7) too
			allRooms[7].addExit(allRooms[6]); 
		}

		//when you give dood nuggets
		if (itemName.equals("nuggets") && currentRoom.getName().equals("room 109")) {
			currentRoom.addItem(new Item("mug", 2, 0)); //adds mug to room
		}

		//the mugging
		if (itemName.equals("mug") && currentRoom.getName().equals("room 109")) {

			display.blankLine();
			display.centered("Press 'enter' to continue");
			display.bottom();
			parser.getCommand(); //require a keystroke
			display.top();
			display.standard("You suddenly realize what just happened. You tried to give Mr. Dood a mug but you ended up mugging him instead!");
			display.blankLine();
			display.standard("Mr. Dood escapes. He must've called the authorities. You have 10 minutes to escape the premises. Get as far away as you can!");
			display.blankLine();
			display.centered("[New item in room: keys]");

			//updates bank description
			currentRoom.setDescription(new String[] {"You're in Room 109, where you attend APCSA class", "Mr. Dood is no where to be found."}); 
			//yeet mr.dood out the room
			currentRoom.removePerson();
			//adds keys to room (need for taking car)
			currentRoom.addItem(new Item("keys", 1, 0)); 
			//adds brick to bank (need for robbing atm)
			allRooms[4].addItem(new Item("brick", 3, 0)); 
			//10 min timer
			startTimer(600); 
		}

	}

	//final stage of game
	public void fight() {
		if (currentRoom.getName().equals("portal")) {
			display.centered("You pull out your weapon of choice as the masked figure runs towards you. You close your eyes and breathe in. This is it. ");
			display.blankLine();
			display.centered("Press 'enter' to take your first strike");
			display.bottom();
			parser.getCommand(); // press enter
			display.top();
			int winningNumber = (int) (Math.random() * 10);
			if (winningNumber < chanceOfWinning) {
				display.standard("You succesfully strike the masked figure. The figure lies wounded on the sands, still alive, but unable to move. The island stands silent; all you can hear are the waves crashing in the background. ");
				display.blankLine();
				display.centered("Press 'enter' to approach the body");
				display.bottom();
				parser.getCommand(); // press enter
				display.top();
				display.standard("You approach the body and unmask them. It's Mr. Dood! You fall onto your knees. Tears fill your eyes. 'I-I'm sorry...' you tell him. He uses his last breath to say 'It's okay, " + playerName + ", before I die, I will make sure to give you an A on your project.'");
				display.blankLine();
				display.standard("He pulls out his phone and goes on Infinite Campus and gives you a 150/150. He submits the grade, and then dies in your arms. The end.");
				finished = true;
			} else {
				display.standard("You unsuccesfully strike the masked figure. You lie wounded on the sands, still alive, but unable to move. The island stands silent; all you can hear are the waves are crashing in the background. ");
				display.blankLine();
				display.centered("Press 'enter' to continue");
				display.bottom();
				parser.getCommand(); // press enter
				
				display.top();
				display.standard("The masked figure approaches you and unmasks themselves. It's Mr. Dood!  Tears fill your eyes. 'I-I'm sorry...' you tell him. 'I'm sorry for mugging you.' He tells you 'It's okay, " + playerName + ". I forgive you. Before you die, I will make sure to give you an A on your project.'");
				display.blankLine();
				display.standard("He pulls out his phone and goes on Infinite Campus and gives you a 150/150. He submits the grade, and then you die. The end.");
				finished = true;
			}
		} else {
			display.centered("Invalid or unavailible command");
		}
	}


	/* --------- MORE METHODS : ------------*/
	
	//starts timer, parameter: duration in seconds (int)
	public void startTimer(int d) {
		duration = d;
		timerOn = true; //starts timer
		localTime = LocalTime.now(); //gets the current time
		startTime = localTime.getMinute() * 60 + localTime.getSecond();
		//converts that time into seconds
	}

	//shows weapon of choice for final fight
	private void showBestWeapon() {
		//this happens after the player returns from portal
		display.centered("You've returned from the portal and made it back to the island. Timer has been stopped.");
		display.blankLine();
		display.centered("The most powerful weapon you've collected is:");
		display.blankLine();

		// prints the best weapon they have
		if (scanInventory("trident")!=null) {
			display.centered("Trident");
			display.centered("90% chance of winning.");
			chanceOfWinning = 9; // out of 10.
		} else if (scanInventory("pitchfork")!=null) {
			display.centered("Pitchfork");
			display.centered("70% chance of winning.");
			chanceOfWinning = 7;
		} else if (scanInventory("fork")!=null) {
			display.centered("Fork");
			display.centered("50% chance of winning.");
			chanceOfWinning = 5;
		} else {
			display.centered("These hands");
			display.centered("20% chance of winning.");
			chanceOfWinning = 2;
		}

		display.blankLine();
	}

	// scans inventory, returns item
	private Item scanInventory(String n) {
		//takes in a string and searches for an item inside inventory
		// with the name that matches. 
		for (Item i : inventory) {
			if (i.getName().equals(n)) {
				return i; //returns Item if it contains the item.
			}
		}
		return null; // returns if item not found
	}

	//shortcut for the developers
	public void dev() {
		//shortcut for the developers bc we're lazy
			undoList.add(currentRoom);
			currentRoom = allRooms[15]; //final fight
			look();
	}

}

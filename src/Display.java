/*
	Makes the game look cute (and the code less ugly)
*/

import java.lang.Math;

class Display {
	private String line;

	public Display() {
		line = "";
	}

	//top border
	public void top() {
		System.out.println("╔══════════════════════════════════════════════════╗");
		blankLine();
	}

  //side borders
	public void blankLine() {
		System.out.println("║                                                  ║");
	}
  
	//bottom border
	public void bottom() {
		blankLine();
		System.out.println("╚══════════════════════════════════════════════════╝");

	}

	//prints a string within the borders
	public void standard(String s) {
		line = ""; // final string to print
		String append = ""; // placeholder variable

		/*
		 * the program will take text from String s and add it to String line until the
		 * length of s is 0
		 */
		while (s.length() > 0) {
			/*
			 * if the string is greater than 48 characters, it will need more than one line
			 * of text
			 */
			if (s.length() > 48) {
				// left border edge
				line += "║ ";
				/*
				 * if the 48th or 49th character is a space, the first 48 characters of String s
				 * will be assigned to variable append (to be appended to line)
				 */
				if (s.substring(47, 48).equals(" ") || s.substring(48, 49).equals(" ")) {
					append = s.substring(0, 48);
				} else {
					/*
					 * this for loop finds where the last space in the first 48 chars are. All text
					 * before than space will be assigned to variable append
					 */
					for (int i = 48; !s.substring(i - 1, i).equals(" "); i--) {
						append = s.substring(0, i - 2);
					}
				}
				// deletes the text in append from s
				s = s.substring(append.length());
				// append is appended to line
				line += append;
				// adds spaces to end of the line to fit reach 48 characters
				for (int i = 48 - append.length(); i > 0; i--) {
					line += " ";
				}
				// right border edge + new line (since s still has text)
				line += " ║\n";
			} else {
				// adds a left border and String s (which has 48 or fewer characters) to line
				line = line + "║ " + s;
				// adds spaces to end of the line to fit reach 48 characters
				if (s.length() != 48) {
					for (int i = 48 - s.length(); i > 0; i--) {
						line += " ";
					}
				}
				// right border
				line += " ║";
				// ends the while loop;
				s = "";
			}
			s = s.trim(); // gets rid of dangling spaces around the text
		}
		System.out.println(line); // prints the result
	}

	//prints centered text
	public void centered(String s) {
		// SAME AS 'void standard()' except with a few changes commented below

		line = "";
		String append = "";
		double padding; // padding = number of spaces needed before and after text

		while (s.length() > 0) {
			if (s.length() > 48) {
				line += "║ ";
				if (s.substring(47, 48).equals(" ") || s.substring(48, 49).equals(" ")) {
					append = s.substring(0, 48);
				} else {
					for (int i = 48; !s.substring(i - 1, i).equals(" "); i--) {
						append = s.substring(0, i - 2);
					}
				}
				s = s.substring(append.length());
				// padding = number of spaces needed on each side to reach 48 chars
				padding = (double) (48 - append.length()) / 2;
				// adds spaces before text
				// casting rounds DOWN if padding is a decimal
				for (int i = (int) padding; i > 0; i--) {
					line += " ";
				}
				// text is appended
				line += append;
				// adds spaces after text
				// Math.round rounds UP if padding is a decimal
				for (int i = (int) Math.round(padding); i > 0; i--) {
					line += " ";
				}
				line += " ║\n";
			} else {
				line += "║ ";
				// padding = number of spaces needed before and after to reach 48 chars
				padding = (double) (48 - s.length()) / 2;
				// adds spaces before text
				// casting rounds DOWN if padding is a decimal
				for (int i = (int) padding; i > 0; i--) {
					line += " ";
				}
				// text is appended
				line += s;
				// adds spaces after text
				// Math.round rounds UP if padding is a decimal
				for (int i = (int) Math.round(padding); i > 0; i--) {
					line += " ";
				}
				line += " ║";
				s = "";
			}
			s = s.trim();
		}
		System.out.println(line);
	}
 
 // displays array of strings into paragraphs
	public void paragraphs(String[] essay) {
		for (String paragraph: essay) {
			standard(paragraph);
			blankLine();
		}
	}
}
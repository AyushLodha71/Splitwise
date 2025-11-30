package splitwiseapplication;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;

/**
 * CheckBalances - Display Who Owes Money to Whom in a Group
 * 
 * PURPOSE:
 * Provides a comprehensive view of all debt relationships within a group.
 * Shows which members owe money to other members and vice versa.
 * Interactive interface where clicking a member's button reveals their detailed balance.
 * 
 * FUNCTIONALITY:
 * - Fetches all group members from db4
 * - Fetches all payment records from db6 (Member1, Amount, Member2)
 * - Calculates bidirectional debt relationships
 * - Creates clickable button for each member
 * - Clicking button toggles display of member's balance details
 * - Shows both debts owed and debts receivable for each member
 * 
 * USER FLOW:
 * 1. User clicks "Check Balances" in MainPage
 * 2. CheckBalances screen shows list of all group members as buttons
 * 3. User clicks a member's button
 * 4. Screen shows detailed balance for that member:
 *    - "You owe $X to [Name]" (for debts)
 *    - "[Name] owes $X" (for receivables)
 * 5. Clicking same button again hides the details (toggle)
 * 6. User can click Back to return to MainPage
 * 
 * BALANCE CALCULATION LOGIC:
 * For each payment record in db6:
 * - If Amount > 0:
 *   - Member2 owes Money to Member1
 *   - Add "[Member2] owes [Amount]" to Member1's info
 *   - Add "You owe [Amount] to [Member1]" to Member2's info
 * - If Amount < 0:
 *   - Member1 owes Money to Member2 (negative indicates reverse debt)
 *   - Convert to positive amount for display
 *   - Add "[Member1] owes [Amount]" to Member2's info
 *   - Add "You owe [Amount] to [Member2]" to Member1's info
 * - If Amount = 0: No debt between members (skip)
 * 
 * DATA STRUCTURE:
 * Uses Member_Info objects to store each member's balance information:
 * - name: Member's username
 * - members: ArrayList of balance strings (debts/receivables)
 * 
 * DATABASE QUERIES:
 * - db4.[GroupCode]: Fetch all member names
 * - db6.[GroupCode]: Fetch all payment records (Member1, Amount, Member2)
 * 
 * UI BEHAVIOR:
 * - Each member has button + label (initially empty)
 * - Click button: Label shows balance details (HTML formatted with bullets)
 * - Click again: Label clears (toggle behavior)
 * - Back button: Return to MainPage
 * 
 * NAVIGATION:
 * - Entry Point: MainPage.java (when user clicks "Check Balances")
 * - Exit Point: MainPage.java (when clicking Back)
 * 
 * UI COMPONENTS:
 * - JButton for each member (dynamic based on group size)
 * - JLabel for each member's balance details (empty initially)
 * - JButton for Back navigation
 * - GridLayout with 1 column (vertical list)
 * 
 * DESIGN PATTERN:
 * - Implements ActionListener for event handling
 * - Static frame pattern (single shared JFrame instance)
 * - Constructor builds UI and calculates balances, runGUI() displays it
 * - Uses Member_Info data class to organize balance information
 * 
 * @author Original Development Team
 */
public class CheckBalances implements ActionListener{
	
	static JFrame frame;
	JPanel contentPane;
	String uname,gcode;
	ArrayList<Member_Info> info = new ArrayList<Member_Info>();
	Member_Info addMember;
	ArrayList<String[]> PACombinations;
	ArrayList<String> members;
	ArrayList<String> memberValues = new ArrayList<String>();
	JButton mmbrb, back;
	ArrayList<JLabel> memberLabels = new ArrayList<JLabel>();
	JLabel mmbrl;
	
	/**
	 * Constructor - Initialize CheckBalances GUI and Calculate Debt Relationships
	 * 
	 * PURPOSE:
	 * Creates the balance checking interface and computes all debt relationships
	 * between group members based on payment records.
	 * 
	 * BEHAVIOR:
	 * - Stores username and group code
	 * - Creates JFrame with title "Splitwise - Check Balances Page"
	 * - Sets frame size to 300x500 pixels
	 * - Fetches all group members from db4
	 * - Creates Member_Info object for each member
	 * - Creates button + label pair for each member
	 * - Fetches all payment records from db6
	 * - Calculates debt relationships from payment records
	 * - Populates Member_Info objects with balance strings
	 * - Configures frame but keeps invisible (runGUI() displays it)
	 * 
	 * DEBT CALCULATION ALGORITHM:
	 * For each row in db6 payment records (skip row 0 = headers):
	 * 1. Extract: Member1 (column 1), Amount (column 2), Member2 (column 3)
	 * 2. Parse Amount as double
	 * 3. If Amount > 0:
	 *    - Member2 owes Member1
	 *    - Add "[Member2] owes [Amount]" to Member1's list
	 *    - Add "You owe [Amount] to [Member1]" to Member2's list
	 * 4. If Amount < 0:
	 *    - Member1 owes Member2 (reverse direction)
	 *    - Convert to positive: positiveAmount = -Amount
	 *    - Add "[Member1] owes [positiveAmount]" to Member2's list
	 *    - Add "You owe [positiveAmount] to [Member2]" to Member1's list
	 * 5. If Amount = 0: Skip (no debt)
	 * 
	 * STRING TRIMMING:
	 * All member names are trimmed to handle potential whitespace from database.
	 * Critical for indexOf() lookups to work correctly.
	 * 
	 * UI CONSTRUCTION:
	 * For each member:
	 * - Create JButton with member's name
	 * - Create JLabel (empty initially)
	 * - Add button and label to content pane
	 * - Store in memberValues and memberLabels lists
	 * 
	 * DEBUG OUTPUT:
	 * Prints members list and each Member2 value to System.out for debugging.
	 * 
	 * DATABASE QUERIES:
	 * - db4.[GroupCode]: SELECT name - Get all group members
	 * - db6.[GroupCode]: SELECT * - Get all payment records
	 * 
	 * @param usrname The logged-in user viewing balances
	 * @param grpcode The group code for which to check balances
	 */
	public CheckBalances(String usrname, String grpcode) {
		
		uname = usrname;
		gcode = grpcode;
		
		frame = new JFrame("Splitwise - Check Balances Page");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 500);
		/* Create a content pane */
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(0, 1, 10, 5));
		contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		
		members = new ArrayList<>(Arrays.asList(ApiCaller.ApiCaller3("https://splitwise.up.railway.app/db4/GetSpecificData?val=name&table="+ grpcode)));
		
		System.out.println(members);
		
		for (String i: members) {
			addMember = new Member_Info(i);
			info.add(addMember);
			mmbrb = new JButton(i);
			mmbrb.addActionListener(this);
			mmbrb.setActionCommand(i);
			memberValues.add(i);
			mmbrl = new JLabel("");
			memberLabels.add(mmbrl);
			contentPane.add(mmbrb);
			contentPane.add(mmbrl);
		}
		
		back = new JButton("Back");
		back.addActionListener(this);
		back.setActionCommand("Back");
		contentPane.add(back);
		
		PACombinations = new ArrayList<>(Arrays.asList(ApiCaller.ApiCaller1("https://splitwise.up.railway.app/db6/GetRowData?table="+ grpcode)));
		
		for (int i = 1; i < PACombinations.size(); i++) {
			String[] t = PACombinations.get(i);
			System.out.println(t[3]);
			double amount = Double.parseDouble(t[2]);
			if (amount > 0) {
				info.get(members.indexOf(t[1])).addMembers(t[3] + " owes " + amount + " to " + t[1]);
				info.get(members.indexOf(t[3])).addMembers(t[3] + " owes " + amount + " to " + t[1]);
			} else if (amount < 0) {
				double positiveAmount = -amount;
				info.get(members.indexOf(t[3])).addMembers(t[1] + " owes " + positiveAmount + " to " + t[3]);
				info.get(members.indexOf(t[1])).addMembers(t[1] + " owes " + positiveAmount + " to " + t[3]);
			} else{
				info.get(members.indexOf(t[3])).addMembers("Settled with " + t[1]);
				info.get(members.indexOf(t[1])).addMembers("Settled with " + t[3]);
			}
		}
		
		frame.setContentPane(contentPane);
		frame.setVisible(false);
		
	}
	
	/**
	 * actionPerformed - Handle User Actions (Member Button Clicks, Back Button)
	 * 
	 * PURPOSE:
	 * Event handler for all user interactions in the CheckBalances screen.
	 * Manages toggling balance display for members and navigation.
	 * 
	 * EVENT TYPES:
	 * 
	 * 1. "Back" - Navigate back to MainPage
	 *    - Creates MainPage instance with username and group code
	 *    - Shows MainPage screen
	 *    - Disposes current frame
	 * 
	 * 2. Member Button Click - Toggle balance display for specific member
	 *    - Action command = member's name
	 *    - Finds member's index in memberValues list
	 *    - Gets corresponding label from memberLabels list
	 *    - Checks if label is currently empty:
	 *      - If empty: Populates label with member's balance details
	 *        - Calls getString() to format balance info as HTML
	 *      - If not empty: Clears label (hides balance)
	 *    - Creates toggle behavior (show/hide on each click)
	 * 
	 * TOGGLE LOGIC:
	 * - First click: Label empty ("") → Show balance
	 * - Second click: Label has content → Hide balance (set to "")
	 * - Third click: Label empty again → Show balance again
	 * 
	 * MEMBER LOOKUP:
	 * Uses indexOf() on memberValues to find position.
	 * Position corresponds to both memberLabels and info lists.
	 * 
	 * @param event The ActionEvent containing the action command
	 */
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		if (eventName.equals("Back")) {
			MainPage mpage = new MainPage(uname,gcode);
			mpage.runGUI();
			frame.dispose();
		} else {
			int t = memberValues.indexOf(eventName);
			String lText = memberLabels.get(t).getText();
			
			if (lText.equals("")) {
				memberLabels.get(t).setText(getString(info.get(t)));
			} else {
				memberLabels.get(memberValues.indexOf(eventName)).setText("");
			}
		}
		
	}
	
	/**
	 * getString - Format Member Balance Details as HTML
	 * 
	 * PURPOSE:
	 * Converts Member_Info object into formatted HTML string for display in JLabel.
	 * Generates a visual summary of balances for a specific member.
	 * 
	 * HTML FORMATTING:
	 * - Wraps entire content in <html> tags for JLabel rendering
	 * - Each balance entry on separate line using <br>
	 * - Format: "• Member Name = Amount"
	 * - Bullet point (•) used as visual indicator
	 * 
	 * POSITIVE vs NEGATIVE AMOUNTS:
	 * - Positive amount: Member owes others money
	 *   Example: "• John = 50.0" means this member owes John $50
	 * 
	 * - Negative amount: Member is owed money by others
	 *   Example: "• Jane = -30.0" means Jane owes this member $30
	 * 
	 * ZERO BALANCES:
	 * - Zero amounts included in display
	 * - Indicates settled/no interaction between members
	 * 
	 * DATA SOURCE:
	 * - Member_Info object contains:
	 *   - info: List of member names
	 *   - value: List of balance amounts
	 *   - Both lists are parallel (same index = same member)
	 * 
	 * OUTPUT EXAMPLE:
	 * <html>• Alice = 100.0<br>• Bob = -50.0<br>• Charlie = 0.0</html>
	 * 
	 * ITERATION:
	 * - Loops through getMembers() which returns formatted strings
	 * - Each string already includes member name and amount
	 * - Adds bullet and line break for each entry
	 * 
	 * @param pinformation The Member_Info object containing member names and balances
	 * @return HTML-formatted string displaying all balance relationships
	 */
	public String getString(Member_Info pinformation) {
		
		String returnString = "<html>";
		
		for (String i: pinformation.getMembers()) {
			returnString += "• " + i + "<br>";
		}

		returnString += "</html>";
		
		return returnString;
		
	}
	
	/**
	 * runGUI - Display CheckBalances Screen
	 * 
	 * PURPOSE:
	 * Instance method to make the CheckBalances frame visible to the user.
	 * Shows the balance summary screen with interactive member buttons.
	 * 
	 * FUNCTIONALITY:
	 * - Enables decorated look and feel for JFrame
	 * - Makes the instance 'frame' visible
	 * 
	 * INSTANCE PATTERN:
	 * - Uses instance 'frame' member variable
	 * - Frame is initialized by constructor before calling this method
	 * - Must be called on CheckBalances instance
	 * 
	 * CALL SITES:
	 * - Called from MainPage after CheckBalances constructor
	 * - Pattern: CheckBalances cb = new CheckBalances(...); cb.runGUI();
	 * 
	 * @throws None (assumes frame is already initialized by constructor)
	 */
	public void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}

}

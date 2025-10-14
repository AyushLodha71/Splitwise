package splitwiseapplication;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;

/**
 * CheckBalances - Individual Balance Details Interface
 * 
 * This class provides a member-by-member breakdown of all debts in the group.
 * Unlike SettlePayment which shows only the current user's debts, this interface
 * shows the complete balance state for ALL members.
 * 
 * User Experience:
 * - Displays a button for each group member
 * - Click a member's button to toggle display of their balance details
 * - Details show all debts that member is involved in
 * 
 * Balance Display Format:
 * For each member, shows all their debts in both directions:
 * - "Member owes $X to OtherMember"
 * - "OtherMember owes $X to Member"
 * 
 * Technical Details:
 * - Uses Member_Info objects to store balance data per member
 * - Loads debt pairs from PendingAmount file
 * - Each debt is recorded TWICE (once for each member involved)
 * - Clicking a member button toggles their balance details on/off
 * 
 * File Used: PendingAmount/{groupCode}
 * Format: "creditor>amount>debtor"
 * - Positive amount: debtor owes creditor
 * - Negative amount: creditor owes debtor (bilateral debt)
 * 
 * @version 2.0 - Enhanced with comprehensive documentation and improved UX
 */
public class CheckBalances implements ActionListener{
	
	// Main application frame
	static JFrame frame;
	
	// Main content panel with vertical grid layout
	JPanel contentPane;
	
	// Current user and group identifiers
	String uname,gcode;
	
	// Member_Info objects storing balance details for each member
	ArrayList<Member_Info> info = new ArrayList<>();
	Member_Info addMember;
	
	// Raw debt pair data from PendingAmount file
	ArrayList<String[]> PACombinations;
	
	// List of all group members
	ArrayList<String> members;
	
	// Member names for event handling
	ArrayList<String> memberValues = new ArrayList<>();
	
	// UI components
	JButton mmbrb, back;
	
	// Labels displaying balance details below each member button
	ArrayList<JLabel> memberLabels = new ArrayList<>();
	JLabel mmbrl;
	
	/**
	 * Constructs the CheckBalances interface showing all member balances.
	 * 
	 * This constructor performs several complex operations:
	 * 1. Creates a button for each group member
	 * 2. Creates a label below each button for balance details
	 * 3. Loads all debt pairs from PendingAmount file
	 * 4. Populates Member_Info objects with balance data
	 * 
	 * Layout Structure:
	 * - Vertical grid layout: [Button][Label][Button][Label]...[Back]
	 * - Each member gets: button (to toggle) + label (for details)
	 * 
	 * Balance Data Processing:
	 * For each debt pair "creditor>amount>debtor":
	 * - If amount > 0:
	 *   * Add "debtor owes amount" to creditor's info
	 *   * Add "You owe amount to creditor" to debtor's info
	 * - If amount < 0:
	 *   * Reverse the relationship (bilateral debt)
	 * 
	 * This creates a comprehensive view where each member can see:
	 * - Who owes them money
	 * - Who they owe money to
	 * 
	 * @param usrname The current user (for returning to MainPage)
	 * @param grpcode The group code to load balances for
	 */
	public CheckBalances(String usrname, String grpcode) {
		
		uname = usrname;
		gcode = grpcode;
		
		frame = new JFrame("Splitwise - Check Balances Page");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 500);
		
		// Create main content panel with vertical grid layout
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(0, 1, 10, 5));
		contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		
		// Load all group members
		members = Exists.contents_STR(new File("src/splitwiseapplication/Groups/"+gcode));
		
		// Create button + label pair for each member
		for (String i: members) {
			// Initialize Member_Info object to store balance details
			addMember = new Member_Info(i);
			info.add(addMember);
			
			// Create member button (click to toggle balance details)
			mmbrb = new JButton(i);
			mmbrb.setToolTipText("Click to view " + i + "'s balance details");
			mmbrb.addActionListener(this);
			mmbrb.setActionCommand(i);
			memberValues.add(i);
			
			// Create label for displaying balance details (initially empty)
			mmbrl = new JLabel("");
			memberLabels.add(mmbrl);
			
			contentPane.add(mmbrb);
			contentPane.add(mmbrl);
		}
		
		// Add Back button at bottom
		back = new JButton("Back");
		back.setToolTipText("Return to group dashboard");
		back.addActionListener(this);
		back.setActionCommand("Back");
		contentPane.add(back);
		
		// Load all debt pairs from PendingAmount file
		PACombinations = Exists.contents(new File("src/splitwiseapplication/PendingAmount/"+gcode),">");
		
		// Populate balance data for each member
		// Each debt is recorded TWICE (once for creditor, once for debtor)
		for (int i = 1; i < PACombinations.size(); i++) {
			String[] t = PACombinations.get(i);
			
			if (Double.parseDouble(t[1]) > 0) {
				// Positive amount: debtor owes creditor
				info.get(members.indexOf(t[0])).addMembers(t[2] + " owes " + t[1]);
				info.get(members.indexOf(t[2])).addMembers("You owe " + t[1] + " to " + t[0]);
			} else if (Double.parseDouble(t[1]) < 0) {
				// Negative amount: creditor owes debtor (bilateral debt)
				double newVal = -1.0 * Double.parseDouble(t[1]);
				info.get(members.indexOf(t[2])).addMembers(t[0] + " owes " + newVal);
				info.get(members.indexOf(t[0])).addMembers("You owe " + newVal + " to " + t[2]);
			}
		}
		
		frame.setContentPane(contentPane);
		frame.setVisible(false);
		
	}
	
	/**
	 * Handles button click events for the CheckBalances interface.
	 * 
	 * Action Handling:
	 * - "Back" Button:
	 *   Returns to MainPage (group dashboard)
	 * 
	 * - Member Button:
	 *   Toggles display of that member's balance details
	 *   - If label is empty: show balance details
	 *   - If label has text: hide balance details
	 * 
	 * Toggle Behavior:
	 * This creates an accordion-style interface where clicking a member
	 * button expands/collapses their balance information. Only the clicked
	 * member's details are affected; other members remain unchanged.
	 * 
	 * @param event The button click event containing the action command
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		if (eventName.equals("Back")) {
			// Return to group dashboard
			MainPage mpage = new MainPage(uname,gcode);
			mpage.runGUI();
			frame.dispose();
		} else {
			// Member button clicked - toggle their balance details
			int t = memberValues.indexOf(eventName);
			String lText = memberLabels.get(t).getText();
			
			if (lText.equals("")) {
				// Label is empty - show balance details
				memberLabels.get(t).setText(getString(info.get(t)));
			} else {
				// Label has text - hide balance details
				memberLabels.get(memberValues.indexOf(eventName)).setText("");
			}
		}
		
	}
	
	/**
	 * Formats member balance information as an HTML bulleted list.
	 * 
	 * This method converts a Member_Info object into a displayable string.
	 * Each debt is shown as a bullet point:
	 * • Member owes $25.00
	 * • You owe $15.50 to OtherMember
	 * 
	 * Uses HTML formatting to:
	 * - Enable multi-line display in JLabel
	 * - Create bullet points for readability
	 * - Support line breaks between items
	 * 
	 * @param pinformation The Member_Info object containing balance details
	 * @return HTML-formatted string with all balance entries
	 */
	public String getString(Member_Info pinformation) {
		
		String returnString = "<html>";
		
		// Add each balance entry as a bullet point
		for (String i: pinformation.getMembers()) {
			returnString += "• " + i + "<br>";
		}

		returnString += "</html>";
		
		return returnString;
		
	}
	
	/**
	 * Displays the CheckBalances window.
	 * 
	 * Makes the balance details interface visible to the user.
	 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}

}

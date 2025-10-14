package splitwiseapplication;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

/**
 * SettlePayment - Debt Settlement Interface
 * 
 * This class provides an interface for recording direct payments between members
 * to settle debts. Users can:
 * - View all their pending debts (what they owe and what they're owed)
 * - Select a specific debt from the list
 * - Record a payment to settle or partially settle that debt
 * 
 * The interface displays balances in plain English:
 * - "You need to give $X to Member" (user owes money)
 * - "Member needs to give you $X" (member owes user)
 * 
 * When a payment is recorded:
 * - Updates PendingAmount file (reduces/clears the debt)
 * - Logs transaction in PaymentHistory (type 1 = settlement)
 * 
 * @version 2.0 - Enhanced with comprehensive documentation and improved UX
 */
public class SettlePayment implements ActionListener{

	// Main application frame
	static JFrame frame;
	
	// UI panels for debt list and buttons
	JPanel listPanel, buttonPanel;
	
	// Scrollable list displaying all pending debts
	JList<String> transactionList;
	JScrollPane scrollPane;
	
	// Current user and group identifiers
	String uname,gcode;
	
	// Raw pending amount data
	ArrayList<String[]> info;
	
	// Action buttons
	JButton pay,back;
	
	// Filtered list of debts involving current user
	ArrayList<String[]> options = new ArrayList<String[]>();
	
	/**
	 * Constructs the SettlePayment interface displaying all pending debts.
	 * 
	 * This constructor:
	 * 1. Retrieves all pending amounts from the PendingAmount file
	 * 2. Filters to show only debts involving the current user
	 * 3. Formats each debt in plain English (who owes whom how much)
	 * 4. Creates a scrollable list of all pending debts
	 * 5. Provides "Pay" button to record a settlement
	 * 
	 * Debt Display Format:
	 * - If user is creditor (owed money): "Member needs to give you $X"
	 * - If user is debtor (owes money): "You need to give $X to Member"
	 * 
	 * File Format: PendingAmount/{groupCode}
	 * Line format: "creditor>amount>debtor"
	 * - Positive amount = debtor owes creditor
	 * - Negative amount = creditor owes debtor (handles bilateral debts)
	 * 
	 * @param username The current user viewing their debts
	 * @param code The group code to load debts for
	 */
	public SettlePayment(String username, String code) {
	
		uname = username;
		gcode = code;
		frame = new JFrame("Splitwise - Settle Payment");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Create main panel for debt list
		listPanel = new JPanel();
		listPanel.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		
		// Load all pending amounts for this group, filtered to current user
		info = RetrievePendingAmount(gcode, uname);
		
		// Format each debt in plain English for display
		String[] data;
		if (!options.isEmpty()) {
			data = new String[options.size()];
			for (int i = 0; i < options.size(); i++) {
				// Check if user is first person in the debt pair (creditor in file)
				if (options.get(i)[0].equals(uname)) {
					if (Double.parseDouble(options.get(i)[1]) > 0) {
						// Positive amount: other person owes user
						data[i] = options.get(i)[2] + " needs to give you $" + options.get(i)[1];
					} else if (Double.parseDouble(options.get(i)[1]) < 0){
						// Negative amount: user owes other person
						double val = Double.parseDouble(options.get(i)[1]) * -1.0;
						data[i] = "You need to give $" + val + " to " + options.get(i)[2];
					}
				} else {
					// User is second person in the debt pair (debtor in file)
					if (Double.parseDouble(options.get(i)[1]) > 0) {
						// Positive amount: user owes first person
						data[i] = "You need to give $" + options.get(i)[1] + " to " + options.get(i)[0];
					} else if (Double.parseDouble(options.get(i)[1]) < 0){
						// Negative amount: first person owes user
						double val = Double.parseDouble(options.get(i)[1]) * -1.0;
						data[i] = options.get(i)[0] + " needs to give you $" + val;
					}
				}
			}
		} else {
			// No pending debts - all settled
			data = new String[1];
			data[0]="No transactions Yet";
		}
		
		// Create scrollable list of debts
		transactionList = new JList<>(data);
		transactionList.setToolTipText("Select a debt to settle");
		
		scrollPane = new JScrollPane(transactionList);
		scrollPane.setPreferredSize(new Dimension(500, 200));
		listPanel.add(scrollPane);
		
		// Create action buttons panel
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		// Action buttons: Pay (record settlement) and Back (return to dashboard)
		pay = new JButton("Pay");
		pay.setToolTipText("Record a payment to settle the selected debt");
		back = new JButton("Back");
		back.setToolTipText("Return to group dashboard");
		
		pay.addActionListener(this);
		pay.setActionCommand("pay");
		back.addActionListener(this);
		back.setActionCommand("back");
		
		buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(pay);
        buttonPanel.add(back);
        
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15,0,20,50));
		 
		// Assemble frame layout
		frame.add(listPanel,BorderLayout.WEST);
		frame.add(buttonPanel,BorderLayout.SOUTH);
		 
		frame.pack();
		frame.setVisible(false);
		 
	}
		
	/**
	 * Handles button click events for the SettlePayment interface.
	 * 
	 * Action Handling:
	 * - "Pay" Button:
	 *   1. Gets the selected debt from the list
	 *   2. Opens AmountSettled dialog to record payment amount
	 *   3. Passes debt details and position in file for updating
	 *   4. Closes current window after AmountSettled opens
	 * 
	 * - "Back" Button:
	 *   1. Returns to MainPage (group dashboard)
	 *   2. Closes current settlement window
	 * 
	 * Data Flow for Payment:
	 * Selected debt → AmountSettled → Updates PendingAmount file → Logs in PaymentHistory
	 * 
	 * @param event The button click event containing the action command
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
			
		String eventName = event.getActionCommand();
		
		if (eventName.equals("pay")) {
			// Get selected debt from the list
			int selectedItem = transactionList.getSelectedIndex();
			
			// Open AmountSettled dialog to record the payment
			// Pass: debt details, current user, group code, position in file
			AmountSettled as = new AmountSettled(options.get(selectedItem), uname, gcode, info.indexOf(options.get(selectedItem)));
			as.runGUI();
			frame.dispose();
		} else if (eventName.equals("back")){
			// Return to group dashboard
			MainPage mpage = new MainPage(uname,gcode);
			mpage.runGUI();
			frame.dispose();
		}
	
	}
		
	/**
	 * Retrieves all pending debts from the PendingAmount file for a group.
	 * 
	 * This method:
	 * 1. Reads the PendingAmount/{groupCode} file line by line
	 * 2. Parses each line into [creditor, amount, debtor] array
	 * 3. Adds ALL debts to transactionlst (complete group balance sheet)
	 * 4. Filters debts involving the current user into options list
	 * 
	 * File Format: PendingAmount/{groupCode}
	 * Line Format: "creditor>amount>debtor"
	 * Example: "john>25.50>sarah" means Sarah owes John $25.50
	 * 
	 * Filtering Logic:
	 * - A debt is included in options if current user is either creditor OR debtor
	 * - This shows the user all debts they're involved in (what they owe + what they're owed)
	 * 
	 * Return Values:
	 * - transactionlst: ALL group debts (used for finding position when updating)
	 * - options: User's debts only (populated as side effect via class field)
	 * 
	 * @param c The group code to load debts for
	 * @param usnm The username to filter debts by
	 * @return Complete list of all group debts (not just user's debts)
	 */
	public ArrayList<String[]> RetrievePendingAmount(String c, String usnm) {
			
		String line;
		File textFile;
		ArrayList<String[]> transactionlst = new ArrayList<>();
		
		// Read PendingAmount file for this group
		textFile = new File("src/splitwiseapplication/PendingAmount/"+c);
		
		try (FileReader fileReader = new FileReader(textFile);
		     BufferedReader bufferedReader = new BufferedReader(fileReader)) {
			
			// Parse each debt line and filter for current user
			while ((line = bufferedReader.readLine()) != null ) {
				String[] myArray = line.split(">");
				transactionlst.add(myArray);  // Add to complete list
				
				// Filter: add to options if user is creditor OR debtor
				if(myArray[0].equals(usnm) || myArray[2].equals(usnm)) {
					options.add(myArray);
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException: "
					+ e.getMessage());
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
		return (transactionlst);
		
	}
	
	/**
	 * Displays the SettlePayment window.
	 * 
	 * Makes the debt settlement interface visible to the user.
	 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
	
	}
	
}

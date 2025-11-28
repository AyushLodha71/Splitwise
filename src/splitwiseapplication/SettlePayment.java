package splitwiseapplication;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;

/**
 * SettlePayment - Payment Settlement Screen
 * 
 * PURPOSE:
 * Displays outstanding debts/payments between the current user and other group members.
 * Allows user to view who owes money to whom and select a debt to settle.
 * 
 * FUNCTIONALITY:
 * - Shows list of all pending payment relationships involving current user
 * - Each entry shows: who owes whom, and how much
 * - User can select a payment and click "Pay" to record settlement
 * - "Back" button returns to MainPage
 * 
 * DATA SOURCE:
 * - Queries db6 (payment records) for:
 *   1. Records where current user is Member1
 *   2. Records where current user is Member2
 * - Combines both result sets to show all relationships
 * 
 * AMOUNT INTERPRETATION:
 * - Positive amount: Member2 owes Member1
 * - Negative amount: Member1 owes Member2
 * - Zero amount: Settled/no debt
 * 
 * UI STRUCTURE:
 * - Top: Scrollable list of payment relationships
 * - Bottom: "Pay" and "Back" buttons
 * - Border layout: list on west, buttons on south
 * 
 * NAVIGATION:
 * - From: MainPage (Settle Payment button)
 * - To: AmountSettled (when Pay clicked) or MainPage (when Back clicked)
 * 
 * USAGE:
 * Called from MainPage.java when user clicks "Settle Payment" button.
 * Currently used: 1 call site (MainPage.actionPerformed)
 */
public class SettlePayment implements ActionListener{

	static JFrame frame;
	JPanel listPanel, buttonPanel;
	JList<String> transactionList;
	JScrollPane scrollPane;
	String uname,gcode;
	ArrayList<String[]> info;
	JButton pay,back;
	ArrayList<String[]> options;
	
	/**
	 * SettlePayment Constructor - Initialize Payment Settlement Screen
	 * 
	 * PURPOSE:
	 * Creates the UI for viewing and settling outstanding payments.
	 * Fetches payment data and displays user-friendly payment descriptions.
	 * 
	 * @param username The current user's username
	 * @param code The group code
	 * 
	 * PROCESS:
	 * 
	 * 1. INITIALIZATION:
	 *    - Stores username and group code
	 *    - Creates frame with BorderLayout
	 *    - Sets up list panel and button panel
	 * 
	 * 2. DATA RETRIEVAL:
	 *    - Fetches records where user is Member1 (user owes or is owed)
	 *    - Fetches records where user is Member2 (user owes or is owed)
	 *    - Combines both into 'info' list
	 *    - Also fetches all group payment records into 'options'
	 * 
	 * 3. AMOUNT INTERPRETATION:
	 *    When user is Member1:
	 *    - Positive amount → Member2 owes user
	 *      Display: "Member2 needs to give you $X"
	 *    - Negative amount → User owes Member2
	 *      Display: "You need to give $X to Member2"
	 * 
	 *    When user is Member2:
	 *    - Positive amount → User owes Member1
	 *      Display: "You need to give $X to Member1"
	 *    - Negative amount → Member1 owes user
	 *      Display: "Member1 needs to give you $X"
	 * 
	 * 4. UI CONSTRUCTION:
	 *    - Creates JList with payment descriptions
	 *    - Adds list to scrollable pane (500x200)
	 *    - Creates "Pay" and "Back" buttons
	 *    - Arranges components in BorderLayout
	 * 
	 * 5. EVENT HANDLERS:
	 *    - Pay button → action command "pay"
	 *    - Back button → action command "back"
	 * 
	 * EMPTY STATE:
	 * If no payment records found, displays "No transactions Yet"
	 * 
	 * DATABASE QUERIES:
	 * - db6 table: Payment records (Member1, Amount, Member2)
	 * - Two queries: one for Member1=username, one for Member2=username
	 * 
	 * POST-CONDITIONS:
	 * - Frame created but invisible (call runGUI() to display)
	 * - Payment list populated with user-friendly descriptions
	 * - All buttons configured with action listeners
	 */
	public SettlePayment(String username, String code) {
	
		uname = username;
		gcode = code;
		frame = new JFrame("Splitwise - Settle Payment");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/* Create a content pane */
		listPanel = new JPanel();
		listPanel.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		
		//info = RetrievePendingAmount(gcode, uname);
		info = new ArrayList<>(Arrays.asList(ApiCaller.ApiCaller1("http://localhost:8080/db6/GetRowData?table="+ gcode + "&Member1=" + uname)));
		info.addAll(new ArrayList<>(Arrays.asList(ApiCaller.ApiCaller1("http://localhost:8080/db6/GetRowData?table="+ gcode + "&Member2=" + uname))));

		options = new ArrayList<>(Arrays.asList(ApiCaller.ApiCaller1("http://localhost:8080/db6/GetRowData?table="+ gcode)));

		String[] data;
		if (info.size() != 0) {
			data = new String[info.size()];
			for (int i = 1; i < info.size(); i++) {
				if (info.get(i)[1].equals(uname)) {
					if (Double.parseDouble(info.get(i)[2]) > 0) {
						data[i] = info.get(i)[3] + " needs to give you $" + info.get(i)[2];
					} else if (Double.parseDouble(info.get(i)[2]) < 0){
						double val = Double.parseDouble(info.get(i)[2]) * -1.0;
						data[i] = "You need to give $" + val + " to " + info.get(i)[3];
					}
				} else {
					if (Double.parseDouble(info.get(i)[2]) > 0) {
						double val = Double.parseDouble(info.get(i)[2]);
						data[i] = "You need to give $" + info.get(i)[2] + " to " + info.get(i)[1];
					} else if (Double.parseDouble(info.get(i)[2]) < 0){
						double val = Double.parseDouble(info.get(i)[2]) * -1.0;
						data[i] = info.get(i)[1] + " needs to give you $" + val;
					}
				}
			}
		} else {
			data = new String[1];
			data[0]="No transactions Yet";
		}
		
		transactionList = new JList<>(data);
		
		scrollPane = new JScrollPane(transactionList);
		scrollPane.setPreferredSize(new Dimension(500, 200));
		listPanel.add(scrollPane);
		
		buttonPanel = new JPanel();
		
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        // Add some buttons to the buttonPanel
		pay = new JButton("Pay");
		back = new JButton("Back");
		pay.addActionListener(this);
		pay.setActionCommand("pay");
		back.addActionListener(this);
		back.setActionCommand("back");
		
		buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(pay);
        buttonPanel.add(back);
        
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15,0,20,50));
		 
		 /* Add content pane to frame */
		 frame.add(listPanel,BorderLayout.WEST);
		 frame.add(buttonPanel,BorderLayout.SOUTH);
		 
		 frame.pack();
		 frame.setVisible(false);
		 
	}
	
	/**
	 * actionPerformed - Handle User Actions (Pay Button, Back Button)
	 * 
	 * PURPOSE:
	 * Event handler for all user interactions in the SettlePayment screen.
	 * Manages payment settlement initiation and navigation.
	 * 
	 * EVENT TYPES:
	 * 
	 * 1. "pay" - Initiate Payment Settlement
	 *    - Gets selected item index from transactionList
	 *    - Retrieves corresponding payment record from 'options' list
	 *    - Finds original index in 'info' list
	 *    - Creates AmountSettled screen with:
	 *      * Payment record details
	 *      * Current username
	 *      * Group code
	 *      * Original index in info list
	 *    - Opens AmountSettled screen
	 *    - Closes current frame
	 * 
	 * 2. "back" - Return to Main Page
	 *    - Creates MainPage instance with username and group code
	 *    - Shows MainPage screen
	 *    - Disposes current frame
	 * 
	 * SELECTION HANDLING:
	 * - transactionList.getSelectedIndex() returns selected payment
	 * - options.get(selectedItem) gets full payment record
	 * - info.indexOf() finds original position for tracking
	 * 
	 * NAVIGATION FLOW:
	 * Pay → AmountSettled (record settlement details)
	 * Back → MainPage (return to group dashboard)
	 * 
	 * @param event The ActionEvent containing the action command
	 */
	public void actionPerformed(ActionEvent event) {
			
		String eventName = event.getActionCommand();
		
		if (eventName.equals("pay")) {
			int selectedItem = transactionList.getSelectedIndex();
			AmountSettled as = new AmountSettled(options.get(selectedItem), uname, gcode, info.indexOf(options.get(selectedItem)));
			as.runGUI();
			frame.dispose();
		} else if (eventName.equals("back")){
			MainPage mpage = new MainPage(uname,gcode);
			mpage.runGUI();
			frame.dispose();
		}
	
	}
	
	/**
	 * runGUI - Display the SettlePayment Window
	 * 
	 * PURPOSE:
	 * Makes the SettlePayment frame visible to the user.
	 * Final step in showing the payment settlement interface.
	 * 
	 * BEHAVIOR:
	 * - Enables default window decorations (title bar, close button, etc.)
	 * - Makes the instance frame visible
	 * 
	 * DESIGN PATTERN:
	 * Follows the two-step initialization pattern used throughout the app:
	 * 1. Constructor builds the UI (but keeps frame invisible)
	 * 2. runGUI() displays the frame
	 * 
	 * This separation allows for UI setup before display.
	 * 
	 * INSTANCE METHOD:
	 * Called on SettlePayment instance to display its frame.
	 * 
	 * USAGE:
	 * Called by MainPage.java after creating SettlePayment instance.
	 * Currently used: 1 call site (MainPage.actionPerformed)
	 */
	public void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
	
	}
	
}

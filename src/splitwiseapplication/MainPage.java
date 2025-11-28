package splitwiseapplication;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;

/**
 * MainPage - Primary Group Dashboard and Transaction Hub
 * 
 * PURPOSE:
 * The central hub for all group-related operations. Displays transaction history
 * and provides access to all major features: adding transactions, settling payments,
 * deleting transactions, checking balances, viewing expenses, and exiting groups.
 * 
 * FUNCTIONALITY:
 * - Displays scrollable transaction history list
 * - Shows three types of transactions:
 *   1. Payments added (Type 0): "[User] added a payment of $X for [reason]"
 *   2. Settlements (Type 1): "[User] paid $X to [recipient]"
 *   3. Group exits (Type 2): "[User] left the group"
 * - Provides 7 action buttons for group operations
 * - Validates exit eligibility (must have zero balance)
 * - Performs complete cleanup when user exits group
 * 
 * USER FLOW:
 * 1. User enters group from EnterGroup, JoinGroup, or CreateGroup
 * 2. MainPage displays transaction history
 * 3. User selects an action button
 * 4. Application navigates to corresponding screen
 * 5. User can exit group (if eligible) or go back to Groups menu
 * 
 * TRANSACTION DISPLAY:
 * - Fetches from db5.[GroupCode] table
 * - Formats based on TType column:
 *   - TType=0: Payment/expense added
 *   - TType=1: Settlement/repayment
 *   - TType=2: Member left group
 * - Shows "No transactions Yet" if empty
 * 
 * EXIT GROUP LOGIC:
 * Must satisfy exit eligibility:
 * - User must have $0 balance with ALL other members
 * - Checks db6 for any non-zero amounts where user is Member1 OR Member2
 * - If eligible: Deletes all user records and navigates to Groups
 * - If not eligible: Shows error "Settle Pending Amount before exiting"
 * 
 * DATABASE CLEANUP ON EXIT (DeleteRecords):
 * 1. db6: Delete payment records where user is Member1
 * 2. db6: Delete payment records where user is Member2
 * 3. db1: Update "Total" balance (subtract user's amount)
 * 4. db1: Delete user's balance record
 * 5. db4: Delete user from members table
 * 6. db7: Delete group from user's group list
 * 7. db5: Insert "left the group" transaction (TType=2)
 * 8. db8: Drop user's column from expense tracking table
 * 
 * NAVIGATION PATHS:
 * - Entry Points: EnterGroup, JoinGroup, CreateGroup (9 call sites)
 * - Exit To:
 *   - AddTransaction (add new expense/payment)
 *   - SettlePayment (pay money to another member)
 *   - DeleteTransaction (remove transaction)
 *   - CheckBalances (view who owes what)
 *   - CheckAmountSpent (view expense history)
 *   - Groups menu (back button or after exit)
 * 
 * UI LAYOUT:
 * - BorderLayout with two main sections:
 *   - WEST: Scrollable transaction list (250x300)
 *   - EAST: Vertical button panel with 7 buttons
 * - All buttons: 150x30 pixels, center-aligned, 10px spacing
 * 
 * DESIGN PATTERN:
 * - Implements ActionListener for event handling
 * - Static frame pattern (single shared JFrame instance)
 * - Constructor builds UI, runGUI() displays it
 * - Action command strings differentiate button actions
 * 
 * @author Original Development Team
 */
public class MainPage implements ActionListener{
	
	static JFrame frame;
	JPanel listPanel, buttonPanel;
	JList<String> transactionList;
	JScrollPane scrollPane;
	String uname,gcode;
	String[][] info;
	JButton addTransaction,settlePayment, deleteTransaction,checkBalances,checkAmountSpent,back,exitGroup;
	File fileLoc;
	
	/**
	 * Constructor - Initialize MainPage GUI for Specific Group
	 * 
	 * PURPOSE:
	 * Creates the main group dashboard displaying transaction history and
	 * providing access to all group operations.
	 * 
	 * BEHAVIOR:
	 * - Stores username and group code
	 * - Creates BorderLayout frame
	 * - Fetches transaction history from db5.[GroupCode]
	 * - Formats transactions based on type (payment/settlement/exit)
	 * - Creates scrollable JList for transactions
	 * - Creates 7 action buttons with consistent sizing (150x30)
	 * - Sets up button panel with vertical layout and spacing
	 * - Configures frame but keeps invisible (runGUI() displays it)
	 * 
	 * TRANSACTION FORMATTING:
	 * - TType=0: "[Payee] added a payment of $[Amount] for [Reason]"
	 * - TType=1: "[Payee] paid $[Amount] to [Reason]"
	 * - TType=2: "[Payee] left the group"
	 * - Empty: "No transactions Yet"
	 * 
	 * BUTTON SETUP (7 buttons):
	 * 1. Add Transaction - Navigate to AddTransaction screen
	 * 2. Settle Payment - Navigate to SettlePayment screen
	 * 3. Delete Transaction - Navigate to DeleteTransaction screen
	 * 4. Check Balances - Navigate to CheckBalances screen
	 * 5. Check Amount Spent - Navigate to CheckAmountSpent screen
	 * 6. Back - Return to Groups menu
	 * 7. Exit Group - Leave group (if eligible)
	 * 
	 * UI LAYOUT:
	 * ┌─────────────────────┬──────────────────┐
	 * │ Transaction List    │  Add Transaction │
	 * │ (Scrollable)        │  Settle Payment  │
	 * │ 250x300px           │  Delete Trans... │
	 * │                     │  Check Balances  │
	 * │                     │  Check Amt Spent │
	 * │                     │  Back            │
	 * │                     │  Exit Group      │
	 * └─────────────────────┴──────────────────┘
	 * 
	 * DATABASE QUERY:
	 * db5.[GroupCode]: SELECT * - Gets all transactions
	 * 
	 * @param username The logged-in user viewing the group
	 * @param code The group code for the group being viewed
	 */
	public MainPage(String username, String code) {
	
		uname = username;
		gcode = code;
		frame = new JFrame("Splitwise - Main Page");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/* Create a content pane */
		listPanel = new JPanel();
		listPanel.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		
		info = ApiCaller.ApiCaller1("http://localhost:8080/db5/GetRowData?table="+ code);
		
		String[] data;
		if (info.length!= 0) {
			data = new String[info.length];
			for (int i = 0; i < info.length; i++) {
				if (info[i][4].equals("0")) {
					data[i] = info[i][1] + " added a payment of $"+info[i][2] + " for " + info[i][3];
				} else if (info[i][4].equals("1")){
					data[i] = info[i][1] + " paid $" + info[i][2] + " to " + info[i][3];
				}else if (info[i][4].equals("2")){
					data[i] = info[i][1] + " left the group";
				}
			}
		} else {
			data = new String[1];
			data[0]="No transactions Yet";
		}
		
		transactionList = new JList<>(data);
		
		scrollPane = new JScrollPane(transactionList);
		scrollPane.setPreferredSize(new Dimension(250, 300));
		listPanel.add(scrollPane);
		
		buttonPanel = new JPanel();
		
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        // Add some buttons to the buttonPanel
		addTransaction = new JButton("Add Transaction");
		addTransaction.setMinimumSize(new Dimension(150,30));
		addTransaction.setMaximumSize(new Dimension(150,30));
		addTransaction.setPreferredSize(new Dimension(150,30));
		settlePayment = new JButton("Settle Payment");
		settlePayment.setMinimumSize(new Dimension(150,30));
		settlePayment.setMaximumSize(new Dimension(150,30));
		settlePayment.setPreferredSize(new Dimension(150,30));
		deleteTransaction = new JButton("Delete Transaction");
		deleteTransaction.setMinimumSize(new Dimension(150,30));
		deleteTransaction.setMaximumSize(new Dimension(150,30));
		deleteTransaction.setPreferredSize(new Dimension(150,30));
		checkBalances = new JButton("Check Balances");
		checkBalances.setMinimumSize(new Dimension(150,30));
		checkBalances.setMaximumSize(new Dimension(150,30));
		checkBalances.setPreferredSize(new Dimension(150,30));
		checkAmountSpent = new JButton("Check Amount Spent");
		checkAmountSpent.setMinimumSize(new Dimension(150,30));
		checkAmountSpent.setMaximumSize(new Dimension(150,30));
		checkAmountSpent.setPreferredSize(new Dimension(150,30));
		back = new JButton("Back");
		back.setMinimumSize(new Dimension(150,30));
		back.setMaximumSize(new Dimension(150,30));
		back.setPreferredSize(new Dimension(150,30));
		exitGroup = new JButton("Exit Group");
		exitGroup.setMinimumSize(new Dimension(150,30));
		exitGroup.setMaximumSize(new Dimension(150,30));
		exitGroup.setPreferredSize(new Dimension(150,30));
		addTransaction.setAlignmentX(Component.CENTER_ALIGNMENT);
		settlePayment.setAlignmentX(Component.CENTER_ALIGNMENT);
		deleteTransaction.setAlignmentX(Component.CENTER_ALIGNMENT);
		checkBalances.setAlignmentX(Component.CENTER_ALIGNMENT);
		checkAmountSpent.setAlignmentX(Component.CENTER_ALIGNMENT);
		back.setAlignmentX(Component.CENTER_ALIGNMENT);
		exitGroup.setAlignmentX(Component.CENTER_ALIGNMENT);

		addTransaction.addActionListener(this);
		addTransaction.setActionCommand("addTransaction");
		settlePayment.addActionListener(this);
		settlePayment.setActionCommand("settlePayment");
		deleteTransaction.addActionListener(this);
		deleteTransaction.setActionCommand("deleteTransaction");
		checkBalances.addActionListener(this);
		checkBalances.setActionCommand("checkBalances");
		checkAmountSpent.addActionListener(this);
		checkAmountSpent.setActionCommand("checkAmountSpent");
		back.addActionListener(this);
		back.setActionCommand("back");
		exitGroup.addActionListener(this);
		exitGroup.setActionCommand("exit");
		
		
        // Add some vertical space between buttons
        buttonPanel.add(Box.createVerticalStrut(10)); // Top padding
        buttonPanel.add(addTransaction);
        buttonPanel.add(Box.createVerticalStrut(10)); // Space between buttons
        buttonPanel.add(settlePayment);
        buttonPanel.add(Box.createVerticalStrut(10)); // Space between buttons
        buttonPanel.add(deleteTransaction);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(checkBalances);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(checkAmountSpent);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(back);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(exitGroup);
        
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15,0,20,50));
		 
		 /* Add content pane to frame */
		 frame.add(listPanel,BorderLayout.WEST);
		 frame.add(buttonPanel,BorderLayout.EAST);
		 /* Size and then display the frame. */
		 frame.pack();
		 frame.setVisible(false);
		 
	}
	
	/**
	 * actionPerformed - Handle User Actions (Button Clicks)
	 * 
	 * PURPOSE:
	 * Event handler for all button clicks in the MainPage.
	 * Routes user to appropriate screens or performs group exit.
	 * 
	 * EVENT TYPES:
	 * 
	 * 1. "addTransaction" - Navigate to Add Transaction screen
	 *    - Creates AddTransaction instance
	 *    - Disposes current frame
	 * 
	 * 2. "settlePayment" - Navigate to Settle Payment screen
	 *    - Creates SettlePayment instance
	 *    - Disposes current frame
	 * 
	 * 3. "deleteTransaction" - Navigate to Delete Transaction screen
	 *    - Creates DeleteTransaction instance
	 *    - Disposes current frame
	 * 
	 * 4. "checkBalances" - Navigate to Check Balances screen
	 *    - Creates CheckBalances instance
	 *    - Disposes current frame
	 * 
	 * 5. "checkAmountSpent" - Navigate to Check Amount Spent screen
	 *    - Creates CheckAmountSpent instance
	 *    - Disposes current frame
	 * 
	 * 6. "back" - Navigate back to Groups menu
	 *    - Creates Groups instance
	 *    - Disposes current frame
	 * 
	 * 7. "exit" - Attempt to exit/leave the group
	 *    - Checks exit eligibility (must have zero balance)
	 *    - If eligible:
	 *      - Calls DeleteRecords() to remove user from all group tables
	 *      - Navigates to Groups menu
	 *    - If not eligible:
	 *      - Displays error message: "Settle Pending Amount before exiting"
	 *      - Changes button action command to "Unable to exit"
	 *      - Adds error label to bottom of frame
	 * 
	 * EXIT ELIGIBILITY:
	 * User can only exit if they have $0 balance with all group members.
	 * Uses exitEligibility() method to validate.
	 * 
	 * @param event The ActionEvent containing the action command
	 */
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		if(eventName.equals("addTransaction")) {
			AddTransaction atransaction = new AddTransaction(uname,gcode);
			atransaction.runGUI();
			frame.dispose();
		} else if (eventName.equals("settlePayment")) {
			SettlePayment sPayment = new SettlePayment(uname,gcode);
			sPayment.runGUI();
			frame.dispose();
		} else if(eventName.equals("deleteTransaction")) {
			DeleteTransaction dTransaction = new DeleteTransaction(uname,gcode);
			dTransaction.runGUI();
			frame.dispose();
		} else if (eventName.equals("checkBalances")) {
			CheckBalances cBalances = new CheckBalances(uname,gcode);
			cBalances.runGUI();
			frame.dispose();
		} else if (eventName.equals("checkAmountSpent")) {
			CheckAmountSpent cAmtSpent = new CheckAmountSpent(uname,gcode);
			cAmtSpent.runGUI();
			frame.dispose();
		} else if (eventName.equals("back")) {
			Groups groups = new Groups(uname);
			groups.runGUI();
			frame.dispose();
		} else if (eventName.equals("exit")) {
			if (exitEligibility()) {
				DeleteRecords();
				Groups groups = new Groups(uname);
				groups.runGUI();
				frame.dispose();
			} else {
				JLabel displayError = new JLabel("Settle Pending Amount before exiting");
				exitGroup.setActionCommand("Unable to exit");
				JPanel errorPanelWrapper = new JPanel();
		        errorPanelWrapper.setLayout(new BoxLayout(errorPanelWrapper, BoxLayout.X_AXIS));
		        errorPanelWrapper.setBorder(BorderFactory.createEmptyBorder(0,0,20,30));
		        errorPanelWrapper.add(Box.createHorizontalGlue());		        
		        errorPanelWrapper.add(displayError);
				frame.add(errorPanelWrapper,BorderLayout.SOUTH);
				frame.pack();
			}
		}
		
	}
	
	/**
	 * exitEligibility - Check If User Can Exit Group
	 * 
	 * PURPOSE:
	 * Validates that user has settled all debts before leaving the group.
	 * Users cannot exit if they owe money or are owed money.
	 * 
	 * BEHAVIOR:
	 * - Queries db6 for all payment records involving the user
	 * - Checks records where user is Member1 (user owes others)
	 * - Checks records where user is Member2 (others owe user)
	 * - Returns false if ANY non-zero amount is found
	 * - Returns true only if ALL amounts are exactly 0
	 * 
	 * ALGORITHM:
	 * 1. Fetch records from db6 where user is Member1
	 * 2. Loop through rows (skip row 0 = headers)
	 * 3. Parse Amount column (index 2) as double
	 * 4. If any amount != 0, return false
	 * 5. Fetch records from db6 where user is Member2
	 * 6. Loop through rows (skip row 0 = headers)
	 * 7. Parse Amount column (index 2) as double
	 * 8. If any amount != 0, return false
	 * 9. If all amounts are 0, return true
	 * 
	 * DATABASE QUERIES:
	 * - db6.[GroupCode] WHERE Member1=[username] - Debts user owes
	 * - db6.[GroupCode] WHERE Member2=[username] - Debts owed to user
	 * 
	 * USAGE:
	 * Called by actionPerformed() when user clicks "Exit Group".
	 * Currently used: 1 call site (MainPage.actionPerformed)
	 * 
	 * @return true if user has zero balance with all members, false otherwise
	 */
	public Boolean exitEligibility() {
		
		String[][] records1 = ApiCaller.ApiCaller1("http://localhost:8080/db6/GetRowData?table="+ gcode+"&Member1="+uname);
		String[][] records2 = ApiCaller.ApiCaller1("http://localhost:8080/db6/GetRowData?table="+ gcode+"&Member2="+uname);
		
		for (int i = 1; i < records1.length; i++) {
			String[] row = records1[i];
			if (Double.parseDouble(row[2]) != 0) {
				return false;
			}
		}

		for (int i = 1; i < records2.length; i++) {
			String[] row = records2[i];
			if (Double.parseDouble(row[2]) != 0) {
				return false;
			}
		}
		
		return true;
		
	}
	
	/**
	 * DeleteRecords - Remove User from All Group-Related Database Tables
	 * 
	 * PURPOSE:
	 * Performs complete cleanup when user exits a group.
	 * Removes user's records from all relevant tables and updates group totals.
	 * Also records the exit event in transaction history.
	 * 
	 * BEHAVIOR:
	 * Executes 9 database operations in specific order:
	 * 
	 * 1. db6: DELETE rows where Member1 = username
	 *    - Removes debts user owes to others
	 * 
	 * 2. db6: DELETE rows where Member2 = username
	 *    - Removes debts others owe to user
	 * 
	 * 3. db1: GET user's current balance amount
	 *    - Retrieves Amount WHERE Name = username
	 * 
	 * 4. db1: GET group's total balance
	 *    - Retrieves Amount WHERE Name = 'Total'
	 * 
	 * 5. db1: UPDATE Total balance
	 *    - Sets Total = Total - User's Amount
	 *    - Maintains accurate group total after user leaves
	 * 
	 * 6. db1: DELETE user's balance record
	 *    - Removes WHERE Name = username
	 * 
	 * 7. db4: DELETE user from members table
	 *    - Removes WHERE name = username
	 * 
	 * 8. db7: DELETE group from user's group list
	 *    - Removes WHERE GroupID = group code
	 *    - Updates user's personal group list
	 * 
	 * 9. db5: INSERT exit transaction record
	 *    - INSERT (payee, amount, reason, TType, tid)
	 *    - VALUES (username, 0, 'left', 2, 'NA')
	 *    - TType=2 indicates "left group" event
	 *    - Visible in transaction history as "[User] left the group"
	 * 
	 * 10. db8: DROP user's column from expense tracking
	 *     - ALTER TABLE DROP COLUMN username
	 *     - Removes expense tracking for this user
	 * 
	 * CRITICAL ORDER:
	 * Must fetch balance amounts BEFORE deleting records.
	 * Must update Total BEFORE deleting user's balance.
	 * 
	 * USAGE:
	 * Called by actionPerformed() when exit is confirmed and eligible.
	 * Currently used: 1 call site (MainPage.actionPerformed)
	 * 
	 * NOTE:
	 * The return value 'val' from ApiCaller2() is unused - operations are
	 * fire-and-forget HTTP requests to the backend API.
	 */
	public void DeleteRecords() {
		
		String val = ApiCaller.ApiCaller2("http://localhost:8080/db6/DeleteRowData?table="+ gcode+"&Member1="+uname);
		val = ApiCaller.ApiCaller2("http://localhost:8080/db6/DeleteRowData?table="+ gcode+"&Member2="+uname);
		String[] amountp = ApiCaller.ApiCaller3("http://localhost:8080/db1/GetSpecificData?val=Amount&table="+ gcode+"&Name="+uname);
		String[] amountt = ApiCaller.ApiCaller3("http://localhost:8080/db1/GetSpecificData?val=Amount&table="+ gcode+"&Name=Total");
		val = ApiCaller.ApiCaller2("http://localhost:8080/db1/UpdateData?table=" + gcode + "&where=Name='Total'&Amount="+ (Double.parseDouble(amountt[0]) - Double.parseDouble(amountp[0])));
		val = ApiCaller.ApiCaller2("http://localhost:8080/db1/DeleteRowData?table="+ gcode+"&Name="+uname);
		val = ApiCaller.ApiCaller2("http://localhost:8080/db1/DeleteRowData?table="+ gcode+"&Name="+uname);
		val = ApiCaller.ApiCaller2("http://localhost:8080/db4/DeleteRowData?table="+ gcode+"&name="+uname);
		val = ApiCaller.ApiCaller2("http://localhost:8080/db7/DeleteRowData?table="+ uname+"&GroupID="+gcode);
		val = ApiCaller.ApiCaller2("http://localhost:8080/db5/InsertData?table="+ gcode +"&params=(payee,amount,reason,Ttype,tid)&info=('" + uname + "'," + 0 + ",'left'," + 2 + ",'NA')");
		val = ApiCaller.ApiCaller2("http://localhost:8080/db8/DeleteColumn?table="+ gcode+"&uname="+uname);
		
	}
	
	/**
	 * runGUI - Display the MainPage Window
	 * 
	 * PURPOSE:
	 * Makes the MainPage frame visible to the user.
	 * Final step in showing the group dashboard.
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
	 * Called on MainPage instance to display its frame.
	 * 
	 * USAGE:
	 * Called by multiple screens after creating MainPage instance.
	 * Currently used: 9 call sites (EnterGroup, JoinGroup, CreateGroup,
	 * AddTransaction, SettlePayment, DeleteTransaction, CheckBalances,
	 * CheckAmountSpent, AmountSettled)
	 */
	public void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}

}

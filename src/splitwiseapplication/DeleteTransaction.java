package splitwiseapplication;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * DeleteTransaction - Transaction Deletion Screen
 * 
 * PURPOSE:
 * Displays list of all transactions in the group and allows users to
 * delete transactions, reversing their effects on balances.
 * 
 * FUNCTIONALITY:
 * - Shows all group transactions (expenses and payments)
 * - User can select a transaction to delete
 * - Deletes transaction from history (db5)
 * - Reverses balance changes (db1, db6, db8)
 * - Returns to MainPage after deletion
 * 
 * TRANSACTION TYPES DISPLAYED:
 * 
 * 1. Type 0 (Expense):
 *    Display: "[User] added a payment of $[amount] for [reason]"
 *    Example: "Alice added a payment of $100 for Dinner"
 * 
 * 2. Type 1 (Payment Settlement):
 *    Display: "[User] paid $[amount] to [recipient]"
 *    Example: "Bob paid $50 to Alice"
 * 
 * DELETION PROCESS:
 * 1. User selects transaction from list
 * 2. Click "Delete" button
 * 3. Repay() reverses all balance changes
 * 4. Transaction deleted from db5 (history)
 * 5. Transaction details deleted from db8
 * 6. Returns to MainPage automatically
 * 
 * DATABASE OPERATIONS:
 * - db5: Transaction history (deleted)
 * - db8: Transaction details per member (deleted)
 * - db6: Inter-member balances (reversed)
 * - db1: Individual balances (reversed for Type 0)
 * 
 * NAVIGATION:
 * - From: MainPage (Delete Transaction button)
 * - To: MainPage (after deletion or Back button)
 * 
 * USAGE:
 * Called from MainPage.java when user clicks "Delete Transaction" button.
 * Currently used: 1 call site (MainPage.actionPerformed)
 */
public class DeleteTransaction implements ActionListener{

	static JFrame frame;
	JPanel listPanel, buttonPanel;
	JList<String> transactionList;
	JScrollPane scrollPane;
	String uname,gcode;
	String[][] info;
	JButton delete,back;
	
	/**
	 * DeleteTransaction Constructor - Initialize Transaction Deletion Screen
	 * 
	 * PURPOSE:
	 * Creates the UI for viewing and deleting group transactions.
	 * Fetches transaction history and displays in scrollable list.
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
	 *    - Queries db5 for all transactions in group
	 *    - info array contains: [id, payee, amount, reason, type, tid]
	 * 
	 * 3. TRANSACTION FORMATTING:
	 *    For each transaction, creates user-friendly description:
	 * 
	 *    Type 0 (Expense Transaction):
	 *      Format: "[Payee] added a payment of $[amount] for [reason]"
	 *      Example: "Alice added a payment of $100 for Dinner"
	 * 
	 *    Type 1 (Payment Settlement):
	 *      Format: "[Payee] paid $[amount] to [reason]"
	 *      Example: "Bob paid $50 to Alice"
	 *      Note: For payments, 'reason' field contains recipient name
	 * 
	 * 4. UI CONSTRUCTION:
	 *    - Creates JList with formatted transaction descriptions
	 *    - Adds list to scrollable pane (250x200)
	 *    - Creates "Delete" and "Back" buttons
	 *    - Arranges components in BorderLayout (list west, buttons south)
	 * 
	 * 5. EMPTY STATE:
	 *    If no transactions found, displays "No transactions Yet"
	 * 
	 * 6. EVENT HANDLERS:
	 *    - Delete button → action command "delete"
	 *    - Back button → action command "back"
	 * 
	 * DATABASE QUERY:
	 * - db5 table: Transaction history
	 * - Returns all transactions for the group (no filtering)
	 * 
	 * POST-CONDITIONS:
	 * - Frame created but invisible (call runGUI() to display)
	 * - Transaction list populated with all group transactions
	 * - Buttons configured with action listeners
	 */
	public DeleteTransaction(String username, String code) {
	
		uname = username;
		gcode = code;
		frame = new JFrame("Splitwise - Settle Payment");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/* Create a content pane */
		listPanel = new JPanel();
		listPanel.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		
		info = ApiCaller.ApiCaller1("http://localhost:8080/db5/GetRowData?table="+ gcode);
		
		String[] data;
		if (info.length!= 0) {
			data = new String[info.length];
			for (int i = 0; i < info.length; i++) {
				if (info[i][4].equals("0")) {
					data[i] = info[i][1] + " added a payment of $"+info[i][2] + " for " + info[i][3];
				} else if (info[i][4].equals("1")){
					data[i] = info[i][1] + " paid $" + info[i][2] + " to " + info[i][3];
				}
			}
		} else {
			data = new String[1];
			data[0]="No transactions Yet";
		}
		
		transactionList = new JList<>(data);
		
		scrollPane = new JScrollPane(transactionList);
		scrollPane.setPreferredSize(new Dimension(250, 200));
		listPanel.add(scrollPane);
		
		buttonPanel = new JPanel();
		
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        // Add some buttons to the buttonPanel
		delete = new JButton("Delete");
		back = new JButton("Back");
		delete.addActionListener(this);
		delete.setActionCommand("delete");
		back.addActionListener(this);
		back.setActionCommand("back");
		
		buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(delete);
        buttonPanel.add(back);
        
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15,0,20,50));
		 
		 /* Add content pane to frame */
		 frame.add(listPanel,BorderLayout.WEST);
		 frame.add(buttonPanel,BorderLayout.SOUTH);
		 
		 frame.pack();
		 frame.setVisible(false);
		 
	}
	
	/**
	 * actionPerformed - Handle User Actions (Delete Button, Back Button)
	 * 
	 * PURPOSE:
	 * Event handler for all user interactions in the DeleteTransaction screen.
	 * Manages transaction deletion and navigation.
	 * 
	 * EVENT TYPES:
	 * 
	 * 1. "delete" - Delete Selected Transaction
	 *    - Gets selected index from transactionList
	 *    - Retrieves transaction info from info array
	 *    - Calls Repay() to reverse balance changes
	 *    - Deletes transaction from db5 (transaction history)
	 *    - Deletes transaction from db8 (transaction details)
	 *    - Proceeds to MainPage (both events lead here)
	 * 
	 * 2. "back" - Return to Main Page
	 *    - Creates MainPage instance
	 *    - Shows MainPage
	 *    - Disposes current frame
	 * 
	 * DELETION FLOW:
	 * After deletion, always navigates to MainPage regardless of which
	 * button was clicked (delete or back).
	 * 
	 * TRANSACTION INFO ARRAY:
	 * - [0]: id
	 * - [1]: payee (who created transaction)
	 * - [2]: amount
	 * - [3]: reason (or recipient for Type 1)
	 * - [4]: type (0=expense, 1=payment)
	 * - [5]: tid (transaction ID)
	 * 
	 * @param event The ActionEvent containing the action command
	 */
	public void actionPerformed(ActionEvent event) {
			
		String eventName = event.getActionCommand();
		
		if (eventName.equals("delete")) {
			int sIndex = transactionList.getSelectedIndex();
			String[] transactionInfo = info[sIndex];
			System.out.println(transactionInfo[0]);
			Repay(transactionInfo);
			String val = ApiCaller.ApiCaller2("http://localhost:8080/db5/DeleteRowData?table=" + gcode + "&tID="+transactionInfo[5]);
			val = ApiCaller.ApiCaller2("http://localhost:8080/db8/DeleteRowData?table=" + gcode + "&tID="+transactionInfo[5]);

		}
		
		MainPage mpage = new MainPage(uname,gcode);
		mpage.runGUI();
		frame.dispose();
	
	}
	
	/**
	 * Repay - Reverse Transaction Effects on Balances
	 * 
	 * PURPOSE:
	 * Reverses all balance changes made by a transaction before deletion.
	 * Undoes the effects on individual balances and inter-member debts.
	 * 
	 * @param tInfo Transaction information array
	 *              [0]=id, [1]=payee, [2]=amount, [3]=reason, [4]=type, [5]=tid
	 * 
	 * ALGORITHM:
	 * 
	 * 1. GET TRANSACTION DETAILS:
	 *    - Queries db8 for transaction breakdown (per-member amounts)
	 *    - tDetails[0]: Column names (uids = member usernames)
	 *    - tDetails[1]: Amount values for each member
	 * 
	 * 2. REVERSE TOTAL BALANCE (for Type 0 only):
	 *    If transaction is an expense (type 0):
	 *    - Get current "Total" balance from db1
	 *    - Subtract transaction amount from Total
	 *    - Updates group's total spending
	 * 
	 * 3. REVERSE INTER-MEMBER BALANCES (db6):
	 *    For each member (starting from index 3):
	 *    - Query db6 for balance relationship with transaction creator
	 *    - Check both Member1/Member2 combinations
	 *    - If record exists as Member1: Add back the member's amount
	 *    - If record exists as Member2: Subtract back the member's amount
	 *    - Reverses the debt changes made by original transaction
	 * 
	 * 4. REVERSE INDIVIDUAL BALANCES (db1, for Type 0 only):
	 *    If transaction is an expense (type 0):
	 *    - For transaction creator: Subtract (full amount + their share)
	 *    - For other members: Subtract their individual share
	 *    - Reverses personal balance changes
	 * 
	 * BALANCE REVERSAL LOGIC:
	 * - Original transaction added amounts → Repay adds them back (reverses by adding opposite)
	 * - Member amounts in db8 can be positive or negative
	 * - Adding negative amount effectively subtracts, adding positive adds
	 * 
	 * DATABASE OPERATIONS:
	 * - db8: Read transaction details (per-member breakdown)
	 * - db1: Update Total and individual member balances (Type 0 only)
	 * - db6: Update inter-member payment relationships
	 */
	public void Repay(String[] tInfo) {
		
		String val;
		
		String[][] tDetails = ApiCaller.ApiCaller1("http://localhost:8080/db8/GetRowData?table=" + gcode + "&tID=" + tInfo[5]);
		String[] uids = tDetails[0];
		String[] data = tDetails[1];

		if (tInfo[4].equals("0")){

			String[] amount = ApiCaller.ApiCaller3("http://localhost:8080/db1/GetSpecificData?val=Amount&table="+gcode +"&Name=Total");

			val = ApiCaller.ApiCaller2("http://localhost:8080/db1/UpdateData?table=" + gcode + "&where=Name='Total'&Amount=" + (Double.parseDouble(amount[0]) - Double.parseDouble(tInfo[2])));

		}

		for (int i = 3;  i < uids.length; i++){
			String[] amount1 = ApiCaller.ApiCaller3("http://localhost:8080/db6/GetSpecificData?val=Amount&table="+gcode +"&Member1=" + uids[i] + "&Member2=" + data[1]);
			String[] amount2 = ApiCaller.ApiCaller3("http://localhost:8080/db6/GetSpecificData?val=Amount&table="+gcode +"&Member2=" + uids[i] + "&Member1=" + data[1]);
			if (amount1.length > 0){
				val = ApiCaller.ApiCaller2("http://localhost:8080/db6/UpdateData?table=" + gcode + "&where=Member1='"+uids[i]+"'%20AND%20Member2='"+data[1]+"'&Amount=" + (Double.parseDouble(amount1[0]) + Double.parseDouble(data[i])));
			} else if (amount2.length > 0) {
				val = ApiCaller.ApiCaller2("http://localhost:8080/db6/UpdateData?table=" + gcode + "&where=Member1='"+data[1]+"'%20AND%20Member2='"+uids[i]+"'&Amount=" + (Double.parseDouble(amount2[0]) - Double.parseDouble(data[i])));
			}

			if (tInfo[4].equals("0")){
				String[] amount = ApiCaller.ApiCaller3("http://localhost:8080/db1/GetSpecificData?val=Amount&table="+ gcode +"&Name=" + uids[i]);
				if (uids[i].equalsIgnoreCase(data[1])){
					val = ApiCaller.ApiCaller2("http://localhost:8080/db1/UpdateData?table=" + gcode + "&where=Name='"+uids[i]+"'&Amount=" + ((Double.parseDouble(amount[0]) - Double.parseDouble(tInfo[2]) - Double.parseDouble(data[i]))));
				} else {
					val = ApiCaller.ApiCaller2("http://localhost:8080/db1/UpdateData?table=" + gcode + "&where=Name='"+uids[i]+"'&Amount=" + (Double.parseDouble(amount[0]) - Double.parseDouble(data[i])));
				}
			}

		}
	}
		
	/**
	 * runGUI - Display the DeleteTransaction Window
	 * 
	 * PURPOSE:
	 * Makes the DeleteTransaction frame visible to the user.
	 * Final step in showing the transaction deletion interface.
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
	 * Called on DeleteTransaction instance to display its frame.
	 * 
	 * USAGE:
	 * Called by MainPage.java after creating DeleteTransaction instance.
	 * Currently used: 1 call site (MainPage.actionPerformed)
	 */
	public void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
	
	}
	
}

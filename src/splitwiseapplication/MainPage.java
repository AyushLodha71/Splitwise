package splitwiseapplication;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;

/**
 * MainPage - Group Transaction Dashboard
 * 
 * This class provides the central interface for managing group expenses and activities.
 * It displays a transaction history feed and provides access to all group management features:
 * 
 * Key Features:
 * - Transaction history display (payments, settlements, member exits)
 * - Add new expense transactions
 * - Settle debts between members
 * - Delete incorrect transactions
 * - Check balance summaries
 * - View individual spending totals
 * - Exit group (with debt validation)
 * - Navigate back to groups list
 * 
 * The interface uses a BorderLayout with:
 * - WEST: Scrollable transaction history list
 * - EAST: Vertical button panel for all actions
 * 
 * @version 2.0 - Enhanced with comprehensive documentation and improved UX
 */
public class MainPage implements ActionListener{
	
	// Main application frame
	static JFrame frame;
	
	// UI panels: transaction list area and action button column
	JPanel listPanel, buttonPanel;
	
	// Scrollable list displaying transaction history
	JList<String> transactionList;
	JScrollPane scrollPane;
	
	// User and group identifiers
	String uname,gcode;
	
	// Transaction data loaded from payment history file
	ArrayList<String[]> info;
	
	// Action buttons for all group management features
	JButton addTransaction,settlePayment, deleteTransaction,checkBalances,checkAmountSpent,back,exitGroup;
	
	// File reference for payment history
	File fileLoc;
	
	/**
	 * Constructor - Builds the Group Dashboard Interface
	 * 
	 * Creates the main page UI with:
	 * 1. Transaction history panel (left side) - Shows all group activities
	 * 2. Action button panel (right side) - Access to all features
	 * 
	 * Transaction History Format:
	 * - Type 0: "User added a payment of $X for Description"
	 * - Type 1: "User paid $X to Recipient" (settlement)
	 * - Type 2: "User left the group"
	 * 
	 * @param username The logged-in user's username
	 * @param code The group code for the current group
	 */
	public MainPage(String username, String code) {
	
		uname = username;
		gcode = code;
		
		/* Setup main frame */
		frame = new JFrame("Splitwise - Main Page");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/* Create transaction history panel (left side) */
		listPanel = new JPanel();
		listPanel.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		
		/* Load payment history for this group */
		fileLoc = new File("src/splitwiseapplication/PaymentHistory/" + gcode);
		info = Exists.contents(fileLoc,">");
		
		/* Build transaction history display */
		String[] data;
		if (!info.isEmpty()) {
			// Format each transaction for display
			data = new String[info.size()];
			for (int i = 0; i < info.size(); i++) {
				// Transaction format: [username, amount, description/recipient, type]
				if (info.get(i)[3].equals("0")) {
					// Type 0: Expense added
					data[i] = info.get(i)[0] + " added a payment of $"+info.get(i)[1] + " for " + info.get(i)[2];
				} else if (info.get(i)[3].equals("1")){
					// Type 1: Direct payment/settlement
					data[i] = info.get(i)[0] + " paid $" + info.get(i)[1] + " to " + info.get(i)[2];
				}else if (info.get(i)[3].equals("2")){
					// Type 2: Member exit
					data[i] = info.get(i)[0] + " left the group";
				}
			}
		} else {
			// No transactions yet - show placeholder
			data = new String[1];
			data[0]="No transactions Yet";
		}
		
		/* Create scrollable transaction list */
		transactionList = new JList<>(data);
		transactionList.setToolTipText("Transaction history for this group");
		
		scrollPane = new JScrollPane(transactionList);
		scrollPane.setPreferredSize(new Dimension(250, 300));
		listPanel.add(scrollPane);
		
		/* Create action button panel (right side) */
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

		/* Initialize all action buttons with consistent sizing */
		addTransaction = new JButton("Add Transaction");
		addTransaction.setMinimumSize(new Dimension(150,30));
		addTransaction.setMaximumSize(new Dimension(150,30));
		addTransaction.setPreferredSize(new Dimension(150,30));
		addTransaction.setToolTipText("Add a new expense to split among members");
		
		settlePayment = new JButton("Settle Payment");
		settlePayment.setMinimumSize(new Dimension(150,30));
		settlePayment.setMaximumSize(new Dimension(150,30));
		settlePayment.setPreferredSize(new Dimension(150,30));
		settlePayment.setToolTipText("Record a debt payment to another member");
		
		deleteTransaction = new JButton("Delete Transaction");
		deleteTransaction.setMinimumSize(new Dimension(150,30));
		deleteTransaction.setMaximumSize(new Dimension(150,30));
		deleteTransaction.setPreferredSize(new Dimension(150,30));
		deleteTransaction.setToolTipText("Remove an incorrect transaction");
		
		checkBalances = new JButton("Check Balances");
		checkBalances.setMinimumSize(new Dimension(150,30));
		checkBalances.setMaximumSize(new Dimension(150,30));
		checkBalances.setPreferredSize(new Dimension(150,30));
		checkBalances.setToolTipText("View who owes whom and how much");
		
		checkAmountSpent = new JButton("Check Amount Spent");
		checkAmountSpent.setMinimumSize(new Dimension(150,30));
		checkAmountSpent.setMaximumSize(new Dimension(150,30));
		checkAmountSpent.setPreferredSize(new Dimension(150,30));
		checkAmountSpent.setToolTipText("See individual spending totals");
		
		back = new JButton("Back");
		back.setMinimumSize(new Dimension(150,30));
		back.setMaximumSize(new Dimension(150,30));
		back.setPreferredSize(new Dimension(150,30));
		back.setToolTipText("Return to groups list");
		
		exitGroup = new JButton("Exit Group");
		exitGroup.setMinimumSize(new Dimension(150,30));
		exitGroup.setMaximumSize(new Dimension(150,30));
		exitGroup.setPreferredSize(new Dimension(150,30));
		exitGroup.setToolTipText("Leave this group (must settle all debts first)");
		
		/* Center-align all buttons */
		addTransaction.setAlignmentX(Component.CENTER_ALIGNMENT);
		settlePayment.setAlignmentX(Component.CENTER_ALIGNMENT);
		deleteTransaction.setAlignmentX(Component.CENTER_ALIGNMENT);
		checkBalances.setAlignmentX(Component.CENTER_ALIGNMENT);
		checkAmountSpent.setAlignmentX(Component.CENTER_ALIGNMENT);
		back.setAlignmentX(Component.CENTER_ALIGNMENT);
		exitGroup.setAlignmentX(Component.CENTER_ALIGNMENT);

		/* Register action listeners and commands for all buttons */
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
		
		
		/* Add buttons to panel with vertical spacing */
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
		 
		 /* Add content panels to frame */
		 frame.add(listPanel,BorderLayout.WEST);
		 frame.add(buttonPanel,BorderLayout.EAST);
		 
		 /* Size and display the frame */
		 frame.pack();
		 frame.setVisible(false);
		 
	}
	
	/**
	 * Event Handler - Processes user button clicks
	 * 
	 * Routes user actions to appropriate feature screens:
	 * - addTransaction: Open expense entry form
	 * - settlePayment: Open debt payment form
	 * - deleteTransaction: Open transaction removal tool
	 * - checkBalances: Display balance summary
	 * - checkAmountSpent: Show individual spending
	 * - back: Return to groups list
	 * - exit: Leave group (validates debt status first)
	 * 
	 * @param event The action event from button click
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		if(eventName.equals("addTransaction")) {
			// Navigate to Add Transaction page
			AddTransaction atransaction = new AddTransaction(uname,gcode);
			atransaction.runGUI();
			frame.dispose();
			
		} else if (eventName.equals("settlePayment")) {
			// Navigate to Settle Payment page
			SettlePayment sPayment = new SettlePayment(uname,gcode);
			sPayment.runGUI();
			frame.dispose();
			
		} else if(eventName.equals("deleteTransaction")) {
			// Navigate to Delete Transaction page
			DeleteTransaction dTransaction = new DeleteTransaction(uname,gcode);
			dTransaction.runGUI();
			frame.dispose();
			
		} else if (eventName.equals("checkBalances")) {
			// Navigate to Check Balances page
			CheckBalances cBalances = new CheckBalances(uname,gcode);
			cBalances.runGUI();
			frame.dispose();
			
		} else if (eventName.equals("checkAmountSpent")) {
			// Navigate to Check Amount Spent page
			CheckAmountSpent cAmtSpent = new CheckAmountSpent(uname,gcode);
			cAmtSpent.runGUI();
			frame.dispose();
			
		} else if (eventName.equals("back")) {
			// Return to Groups list
			Groups groups = new Groups(uname);
			groups.runGUI();
			frame.dispose();
			
		} else if (eventName.equals("exit")) {
			// Attempt to exit group (validates debt status first)
			if (exitEligibility()) {
				// No pending debts - can exit
				DeleteRecords();
				Groups groups = new Groups(uname);
				groups.runGUI();
				frame.dispose();
			} else {
				// Has pending debts - cannot exit
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
	 * Validates Exit Eligibility
	 * 
	 * Checks if the user can leave the group by verifying they have no pending debts.
	 * Users must settle all amounts they owe or are owed before exiting to prevent
	 * unresolved financial obligations.
	 * 
	 * @return true if user has no pending debts (can exit), false otherwise
	 */
	public Boolean exitEligibility() {
		
		File newFileU = new File("src/splitwiseapplication/PendingAmount/" + gcode);
		ArrayList<String[]> records = Exists.contents(newFileU, ">");
		
		// Check all pending amount records for this user
		for (int i = 1; i < records.size(); i++) {
			String[] row = records.get(i);
			// If user is involved (as debtor or creditor)
			if (row[0].equals(uname) || row[2].equals(uname)) {
				// Check if amount is non-zero
				if (Double.parseDouble(row[1]) != 0) {
					return false; // Has pending debt - cannot exit
				}
			}
		}
		
		return true; // No pending debts - can exit
		
	}
	
	/**
	 * Removes User from Group
	 * 
	 * Deletes all user records from group files and logs the exit in payment history.
	 * This is a coordinated operation that ensures clean removal from:
	 * 1. Pending amounts file
	 * 2. Check amount spent file
	 * 3. Group members file
	 * 4. Personal folders file
	 * 5. Payment history (logs exit event)
	 */
	public void DeleteRecords() {
		
		DeletePA();  // Remove from pending amounts
		DeleteCAS(); // Remove from amount spent tracking
		DeleteGrp(); // Remove from group members list
		DeletePF();  // Remove group from personal folder
		
		// Log exit event in payment history
		File newFile = new File("src/splitwiseapplication/PaymentHistory/"+gcode);
		String[] data = {uname,"","","2"}; // Type 2 = user left group
		UpdateFile.Update(data,newFile,">");
		
	}
	
	/**
	 * Removes User from Pending Amounts File
	 * 
	 * Deletes all pending amount records where this user is involved
	 * (either as debtor or creditor). Preserves header row and all
	 * unrelated records.
	 */
	public void DeletePA() {
		
		File newFilePA = new File("src/splitwiseapplication/PendingAmount/" + gcode);
		ArrayList<String[]> optionsPA = Exists.contents(newFilePA, ">");
		
		if (optionsPA.isEmpty()) {
			return; // Safety check - no data to process
		}
		
		// Rewrite file starting with header row
		UpdateFile.Write(optionsPA.get(0)[0], optionsPA.get(0)[1], optionsPA.get(0)[2], newFilePA);
		
		// Add back only records not involving this user
		for (int i = 1; i < optionsPA.size(); i++) {
			String[] row = optionsPA.get(i);
			if (!(row[0].equals(uname) || row[2].equals(uname))) {
				UpdateFile.Update(optionsPA.get(i)[0], optionsPA.get(i)[1], optionsPA.get(i)[2], newFilePA);
			}
		}
		
	}
	
	/**
	 * Removes User from Amount Spent Tracking File
	 * 
	 * Deletes the user's spending record from the Check Amount Spent file.
	 * Preserves header row and other users' spending data.
	 */
	public void DeleteCAS() {
		
		File newFileCAS = new File("src/splitwiseapplication/CheckAmountSpentFolder/" + gcode);
		ArrayList<String[]> optionsCAS = Exists.contents(newFileCAS, ",");
		
		if (optionsCAS.isEmpty()) {
			return; // Safety check - no data to process
		}
		
		// Rewrite file starting with header row
		UpdateFile.Write(optionsCAS.get(0)[0], optionsCAS.get(0)[1], newFileCAS);
		
		// Add back only records not belonging to this user
		for (int i = 1; i < optionsCAS.size(); i++) {
			String[] row = optionsCAS.get(i);
			if (!(row[0].equals(uname))) {
				UpdateFile.Update(optionsCAS.get(i)[0], optionsCAS.get(i)[1], newFileCAS);
			}
		}
		
	}
	
	/**
	 * Removes User from Group Members List
	 * 
	 * Deletes the username from the group's members file.
	 * Preserves all other members in the group.
	 */
	public void DeleteGrp() {
		
		File newFileGrp = new File("src/splitwiseapplication/Groups/" + gcode);
		ArrayList<String> optionsGrp = Exists.contents_STR(newFileGrp);
		
		if (optionsGrp.isEmpty()) {
			return; // Safety check - no data to process
		}
		
		String row = optionsGrp.get(0);
		
		// Rewrite file, excluding this user
		if (!(row.equals(uname))) {
			UpdateFile.Write(row, newFileGrp);
		}
		
		// Add back remaining members (except this user)
		for (int i = 1; i < optionsGrp.size(); i++) {
			row = optionsGrp.get(i);
			if (!(row.equals(uname))) {
				UpdateFile.Update(optionsGrp.get(i), newFileGrp);
			}
		}
		
	}
	
	/**
	 * Removes Group from User's Personal Folder
	 * 
	 * Deletes the group name from the user's personal groups list.
	 * This removes the group from the user's view while preserving
	 * it for remaining members.
	 */
	public void DeletePF() {
		
		// First, find the group name from the group code
		File newFileGrp = new File("src/splitwiseapplication/groups.txt");
		ArrayList<String[]> optionsGrp = Exists.contents(newFileGrp, ",");
		String gName = "";
		
		for (int i = 0; i < optionsGrp.size(); i++) {
			String[] row = optionsGrp.get(i);
			if ((row[0].equals(gcode))) {
				gName = row[1]; // Found group name
			}
		}
		
		// Now remove group from user's personal folder
		File newFilePF = new File("src/splitwiseapplication/Personal_Folders/" + uname);
		ArrayList<String> optionsPF = Exists.contents_STR(newFilePF);
		
		if (optionsPF.isEmpty()) {
			return; // Safety check - no data to process
		}
		
		String row = optionsPF.get(0);
		
		// If first row is the group we're removing, start with blank file
		if (!(row.equals(gName))) {
			UpdateFile.Write(row, newFilePF);
		} else {
			UpdateFile.WriteBlank(newFilePF);
		}
		
		// Add back all other groups
		for (int i = 1; i < optionsPF.size(); i++) {
			row = optionsPF.get(i);
			if (!(row.equals(gName))) {
				UpdateFile.Update(optionsPF.get(i), newFilePF);
			}
		}
		
	}

	/**
	 * Display the Main Page GUI
	 * 
	 * Makes the main page frame visible to the user.
	 * Should be called after constructing a MainPage object.
	 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}

}

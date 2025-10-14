package splitwiseapplication;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

/**
 * DeleteTransaction - Transaction History Management Interface
 * 
 * This class provides functionality to view and delete past transactions from
 * the PaymentHistory. Users can:
 * - View a complete history of all group transactions
 * - See both expenses (Type 0) and settlements (Type 1)
 * - Select and delete any transaction
 * 
 * Transaction Types Displayed:
 * - Type 0 (Expense): "User added a payment of $X for Description"
 * - Type 1 (Settlement): "User paid $X to Member"
 * 
 * Deletion Process:
 * When a transaction is deleted, the system must reverse its effects:
 * - For expenses (Type 0): Reverses all balance changes and spending totals
 * - For settlements (Type 1): Reverses the payment between the two members
 * 
 * Files Updated During Deletion:
 * 1. PaymentHistory - Removes the transaction record
 * 2. PendingAmount - Reverses balance changes
 * 3. CheckAmountSpentFolder - Reverses spending totals (for expenses only)
 * 4. TransactionDetails - Retrieved to know how much each member paid/owed
 * 
 * This is a complex operation that maintains financial integrity by ensuring
 * all related files stay synchronized when a transaction is removed.
 * 
 * @version 2.0 - Enhanced with comprehensive documentation and improved UX
 */
public class DeleteTransaction implements ActionListener{

	// Main application frame
	static JFrame frame;
	
	// UI panels for transaction list and buttons
	JPanel listPanel, buttonPanel;
	
	// Scrollable list displaying all transactions
	JList<String> transactionList;
	JScrollPane scrollPane;
	
	// Current user and group identifiers
	String uname,gcode;
	
	// Transaction history data
	ArrayList<String[]> info;
	
	// Action buttons
	JButton delete,back;
	
	// Unused field (can be removed in future refactoring)
	ArrayList<String[]> options = new ArrayList<>();
	
	// PaymentHistory file location
	File fileLoc;
	
	/**
	 * Constructs the DeleteTransaction interface displaying transaction history.
	 * 
	 * This constructor:
	 * 1. Loads all transactions from PaymentHistory file
	 * 2. Formats each transaction based on its type
	 * 3. Creates a scrollable list of all transactions
	 * 4. Provides "Delete" button to remove selected transaction
	 * 
	 * Transaction Display Format:
	 * - Type 0 (Expense): "User added a payment of $X for Description"
	 *   Example: "john added a payment of $50.00 for Groceries"
	 * 
	 * - Type 1 (Settlement): "User paid $X to Member"
	 *   Example: "sarah paid $25.00 to john"
	 * 
	 * PaymentHistory File Format: PaymentHistory/{groupCode}
	 * Line format: "payer>amount>description>type>transactionID"
	 * - Type 0 = expense transaction
	 * - Type 1 = settlement transaction
	 * 
	 * @param username The current user (unused in display but kept for context)
	 * @param code The group code to load transaction history for
	 */
	public DeleteTransaction(String username, String code) {
	
		uname = username;
		gcode = code;
		frame = new JFrame("Splitwise - Delete Transaction");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Create main panel for transaction history list
		listPanel = new JPanel();
		listPanel.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		
		// Load transaction history from PaymentHistory file
		fileLoc = new File("src/splitwiseapplication/PaymentHistory/" + gcode);
		info = Exists.contents(fileLoc,">");
		
		// Format each transaction for display
		String[] data;
		if (!info.isEmpty()) {
			data = new String[info.size()];
			for (int i = 0; i < info.size(); i++) {
				if (info.get(i)[3].equals("0")) {
					// Type 0: Expense transaction
					data[i] = info.get(i)[0] + " added a payment of $"+info.get(i)[1] + " for " + info.get(i)[2];
				} else if (info.get(i)[3].equals("1")){
					// Type 1: Settlement transaction
					data[i] = info.get(i)[0] + " paid $" + info.get(i)[1] + " to " + info.get(i)[2];
				}
			}
		} else {
			// No transaction history yet
			data = new String[1];
			data[0]="No transactions Yet";
		}
		
		// Create scrollable list of transactions
		transactionList = new JList<>(data);
		transactionList.setToolTipText("Select a transaction to delete");
		
		scrollPane = new JScrollPane(transactionList);
		scrollPane.setPreferredSize(new Dimension(250, 200));
		listPanel.add(scrollPane);
		
		// Create action buttons panel
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		// Action buttons: Delete (remove transaction) and Back (return to dashboard)
		delete = new JButton("Delete");
		delete.setToolTipText("Delete the selected transaction and reverse its effects");
		back = new JButton("Back");
		back.setToolTipText("Return to group dashboard");
		
		delete.addActionListener(this);
		delete.setActionCommand("delete");
		back.addActionListener(this);
		back.setActionCommand("back");
		
		buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(delete);
        buttonPanel.add(back);
        
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15,0,20,50));
		 
		// Assemble frame layout
		frame.add(listPanel,BorderLayout.WEST);
		frame.add(buttonPanel,BorderLayout.SOUTH);
		 
		frame.pack();
		frame.setVisible(false);
		 
	}
		
	/**
	 * Handles button click events for the DeleteTransaction interface.
	 * 
	 * Action Handling:
	 * - "Delete" Button:
	 *   1. Gets the selected transaction from the list
	 *   2. Calls Repay() to reverse the transaction's financial effects
	 *   3. Removes the transaction from the in-memory list
	 *   4. Rewrites PaymentHistory file without the deleted transaction
	 *   5. Returns to MainPage
	 * 
	 * - "Back" Button:
	 *   1. Returns to MainPage (group dashboard) without making changes
	 * 
	 * Deletion Safety:
	 * - Validates selected index is within bounds before processing
	 * - Only updates file if transactions remain after deletion
	 * 
	 * File Rewrite Process:
	 * After deletion, PaymentHistory is completely rewritten:
	 * - First transaction written with Write() (overwrites file)
	 * - Remaining transactions appended with Update()
	 * 
	 * @param event The button click event containing the action command
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
			
		String eventName = event.getActionCommand();
		
		if (eventName.equals("delete")) {
			// Get selected transaction
			int sIndex = transactionList.getSelectedIndex();
			
			// Validate selection exists
			if (sIndex >= 0 && sIndex < info.size()) {
				String[] transactionInfo = info.get(sIndex);
				
				// Reverse the transaction's financial effects
				Repay(transactionInfo);
				
				// Remove from in-memory list
				info.remove(sIndex);
				
				// Rewrite PaymentHistory file without deleted transaction
				if (!info.isEmpty()) {
					UpdateFile.Write(info.get(0), fileLoc, ">");
					for (int i = 1; i < info.size(); i++) {
						UpdateFile.Update(info.get(i), fileLoc, ">");
					}
				}
			}
		}
		
		// Return to group dashboard (both delete and back buttons)
		MainPage mpage = new MainPage(uname,gcode);
		mpage.runGUI();
		frame.dispose();
	
	}
	
	/**
	 * Reverses the financial effects of a deleted transaction.
	 * 
	 * This is the most complex method in DeleteTransaction. It must undo all changes
	 * that occurred when the transaction was originally created. The logic differs
	 * based on transaction type:
	 * 
	 * TYPE 0 (Expense Transaction):
	 * An expense affects TWO data structures:
	 * 1. PendingAmount - tracks who owes whom
	 * 2. CheckAmountSpentFolder - tracks total spending per member
	 * 
	 * Reversal Process for Expenses:
	 * a) Load TransactionDetails to see how much each member paid/owed
	 * b) For each debt pair in PendingAmount:
	 *    - If payer is creditor: INCREASE their owed amount (undo the payment)
	 *    - If payer is debtor: DECREASE their owed amount (undo the debt)
	 * c) For each member in CheckAmountSpentFolder:
	 *    - SUBTRACT their contribution from their total spending
	 * 
	 * TYPE 1 (Settlement Transaction):
	 * A settlement only affects PendingAmount (no spending change).
	 * 
	 * Reversal Process for Settlements:
	 * - Find the debt pair between the two members
	 * - If payer is creditor: DECREASE the debt (undo the payment received)
	 * - If payer is debtor: INCREASE the debt (undo the payment made)
	 * 
	 * Files Modified:
	 * - PendingAmount/{groupCode} - Always updated for both types
	 * - CheckAmountSpentFolder/{groupCode} - Only updated for Type 0
	 * 
	 * Data Sources:
	 * - Groups/{groupCode} - Member list for indexing
	 * - TransactionDetails/{transactionID} - Original split amounts
	 * 
	 * @param tInfo Transaction info array: [payer, amount, description, type, transactionID]
	 */
	public void Repay(String[] tInfo) {
		
		// Load all necessary data files
		File newFileU = new File("src/splitwiseapplication/PendingAmount/" + gcode);
		ArrayList<String[]> records = Exists.contents(newFileU, ">");
		
		File newFileCAS = new File("src/splitwiseapplication/CheckAmountSpentFolder/" + gcode);
		ArrayList<String[]> optionsCAS = Exists.contents(newFileCAS, ",");
		
		File newFileG = new File("src/splitwiseapplication/Groups/" + gcode);
		ArrayList<String> groupMembers = Exists.contents_STR(newFileG);
		
		File newFileTD = new File("src/splitwiseapplication/TransactionDetails/" + gcode);
		String[] td = Exists.exists_Array(tInfo[4],newFileTD);
		
		// Safety check: ensure data exists
		if (records.isEmpty() || optionsCAS.isEmpty()) {
			return;
		}
		
		int t;
		
		// Rewrite PendingAmount file with first record
		UpdateFile.Write(records.get(0)[0], records.get(0)[1], records.get(0)[2], newFileU);
		
		// Check transaction type
		if (Integer.parseInt(tInfo[3]) == 0) {
			// TYPE 0: Expense Transaction - Reverse both balances and spending
			
			// Reverse PendingAmount changes
			for (int i = 1; i < records.size(); i++) {
				String[] row = records.get(i);
				
				// Check if this debt pair involves the payer
				if (containsValue(row,tInfo[0])) {
					if (row[2].equals(tInfo[0])) {
						// Payer is debtor in this pair
						// Get creditor's index to find their contribution
						t = groupMembers.indexOf(row[0]);
						// INCREASE debt (undo the payment that reduced it)
						records.get(i)[1] = Double.toString(Double.parseDouble(records.get(i)[1]) + Double.parseDouble(td[t+1]));
					} else {
						// Payer is creditor in this pair
						// Get debtor's index to find their contribution
						t = groupMembers.indexOf(row[2]);
						// DECREASE debt (undo the debt that was added)
						records.get(i)[1] = Double.toString(Double.parseDouble(records.get(i)[1]) - Double.parseDouble(td[t+1]));
					}
				}
				UpdateFile.Update(records.get(i)[0], records.get(i)[1], records.get(i)[2], newFileU);
			}
			
			// Reverse CheckAmountSpent changes
			UpdateFile.Write(optionsCAS.get(0)[0], optionsCAS.get(0)[1], newFileCAS);
			for (int i = 1; i < optionsCAS.size(); i++) {
				// SUBTRACT each member's contribution from their total spending
				optionsCAS.get(i)[1] = Double.toString(Double.parseDouble(optionsCAS.get(i)[1]) - Double.parseDouble(td[i]));
				UpdateFile.Update(optionsCAS.get(i)[0], optionsCAS.get(i)[1], newFileCAS);
			}
		} else {
			// TYPE 1: Settlement Transaction - Only reverse PendingAmount
			
			for (int i = 1; i < records.size(); i++) {
				String[] row = records.get(i);
				
				// Find the debt pair between payer and recipient
				if (containsValue(row,tInfo[0]) && containsValue(row,tInfo[2])) {
					if (row[0].equals(tInfo[0])) {
						// Payer is creditor: DECREASE debt (undo payment received)
						records.get(i)[1] = Double.toString(Double.parseDouble(records.get(i)[1]) - Double.parseDouble(tInfo[1]));
					} else {
						// Payer is debtor: INCREASE debt (undo payment made)
						records.get(i)[1] = Double.toString(Double.parseDouble(records.get(i)[1]) + Double.parseDouble(tInfo[1]));
					}
				}
				UpdateFile.Update(records.get(i)[0], records.get(i)[1], records.get(i)[2], newFileU);
			}
		}
		
	}
	
	/**
	 * Checks if a string array contains a specific value.
	 * 
	 * Used in Repay() to determine if a debt pair involves a specific user.
	 * 
	 * @param array The string array to search in
	 * @param targetValue The value to search for
	 * @return true if the value is found, false otherwise
	 */
	public static boolean containsValue(String[] array, String targetValue) {
        if (array == null) {
            return false; // The array itself is null
        }
        for (String element : array) {
            // Use .equals() for String comparison
            if (element != null && element.equals(targetValue)) {
                return true; // Found the specific value
            }
        }
        return false; // Value not found
    }
	
	/**
	 * Displays the DeleteTransaction window.
	 * 
	 * Makes the transaction history interface visible to the user.
	 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
	
	}
	
}

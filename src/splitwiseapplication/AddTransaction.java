package splitwiseapplication;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

/**
 * AddTransaction - Expense Entry Interface
 * 
 * This class provides a comprehensive interface for adding new expenses to a group
 * and splitting them among members using various methods:
 * 
 * Split Methods Supported:
 * 1. All Equally - Divides expense equally among all group members
 * 2. Equally By Some - Divides expense equally among selected members only
 * 3. Unequally - Allows custom amounts for each member
 * 4. By Percentages - Allows percentage-based split (must sum to 100%)
 * 
 * The process:
 * - User enters transaction amount and description
 * - Selects split method from dropdown
 * - Interface dynamically updates to show appropriate input fields
 * - System calculates individual shares and updates all balances
 * - Transaction is logged with unique ID for tracking
 * 
 * All transactions update:
 * - PaymentHistory (transaction log)
 * - PendingAmount (balance tracking)
 * - CheckAmountSpent (spending totals)
 * - TransactionDetails (detailed split information)
 * 
 * @version 2.0 - Enhanced with comprehensive documentation and improved UX
 */
public class AddTransaction implements ActionListener {
	
	// Main application frame
	static JFrame frame;
	
	// UI container panel
	JPanel contentPane;
	
	// UI labels for prompts and feedback
	JLabel amountPrompt,reasonPrompt,splitTypePrompt,response,blank;
	
	// Input fields for amount and reason
	JTextField amount,reason;
	
	// Dropdown for split method selection
	JComboBox options;
	
	// Checkbox for member selection (Equally By Some mode)
	JCheckBox nameEQS;
	
	// Dynamic text fields for unequal and percentage splits
	JTextField nameU, nameBP;
	
	// Navigation and submission buttons
	JButton backButton, enter;
	
	// Current user, group code, and transaction ID
	String uname, code,tID;
	
	// Selected members for "Equally By Some" split
	ArrayList<Integer> chosenEQS = new ArrayList<Integer>();
	
	// Dynamic input fields for unequal splits
	ArrayList<JTextField> chosenU = new ArrayList<JTextField>();
	
	// Dynamic input fields for percentage splits
	ArrayList<JTextField> chosenBP = new ArrayList<JTextField>();
	
	// Calculated amounts for unequal splits
	ArrayList<Double> valuesU = new ArrayList<Double>();
	
	// Calculated percentages for percentage-based splits
	ArrayList<Double> valuesBP = new ArrayList<Double>();
	
	// Transaction amount and reason values
	String amountVal = "0";
	String reasonVal;
	
	// Transaction details file reference
	File tdFile;
	
	/**
	 * Constructor - Builds the Add Transaction Interface
	 * 
	 * Creates a dynamic form with:
	 * 1. Amount input field with Enter key support
	 * 2. Reason/description input field
	 * 3. Split method dropdown (4 options)
	 * 4. Dynamic member selection area (changes based on split method)
	 * 5. Back button and response area for feedback
	 * 
	 * The interface adapts based on the selected split method,
	 * showing appropriate input fields for each member.
	 * 
	 * @param username The logged-in user's username
	 * @param gcode The current group code
	 */
	public AddTransaction(String username, String gcode) {
		
		uname = username;
		code = gcode;
		
		 /* Setup main frame */
		 frame = new JFrame("Splitwise - Add Transaction");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 
		 /* Create content pane with 2-column grid layout */
		 contentPane = new JPanel();
		 contentPane.setLayout(new GridLayout(0, 2, 10, 5));
		 contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		 
		 /* Amount input section */
		 amountPrompt = new JLabel("Enter the Transaction Amount");
		 contentPane.add(amountPrompt);
		 amount = new JTextField();
		 amount.setToolTipText("Enter amount in dollars (e.g., 50.00)");
		 amount.addActionListener(this);
		 amount.setActionCommand("Add Amount"); // Enter key triggers processing
		 contentPane.add(amount);
		 
		 /* Reason input section */
		 reasonPrompt = new JLabel("Enter the Reason for this transaction");
		 contentPane.add(reasonPrompt);
		 reason = new JTextField();
		 reason.setToolTipText("Enter description (e.g., Dinner, Movie tickets)");
		 reason.addActionListener(this);
		 reason.setActionCommand("Reason"); // Enter key triggers processing
		 contentPane.add(reason);
		 
		 /* Split method selection dropdown */
		 splitTypePrompt = new JLabel("Choose how you want to split money");
		 contentPane.add(splitTypePrompt);
		 options = new JComboBox();
		 options.setToolTipText("Select how to divide the expense");
		 options.addItem("All Equally");
		 options.addItem("Equally By Some");
		 options.addItem("Unequally");
		 options.addItem("By Percentages");
		 options.addActionListener(this);
		 options.setActionCommand("Selected");
		 
		 contentPane.add(options);
		 
		 /* Back button - return to main page */
		 backButton = new JButton("Back");
		 backButton.setToolTipText("Return to main page without adding transaction");
		 backButton.addActionListener(this);
		 backButton.setActionCommand("Back");
		 contentPane.add(backButton);
		 
		 /* Placeholder for dynamic content */
		 blank = new JLabel("");
		 contentPane.add(blank);
		 
		 /* Response area for feedback messages */
		 response = new JLabel("");
		 contentPane.add(response);
		 
		 /* Finalize frame setup */
		 frame.setContentPane(contentPane);
		 frame.pack();
		 frame.setVisible(false);
	
	}
	
	/**
	 * Event Handler - Processes all user interactions
	 * 
	 * This complex handler manages multiple states:
	 * 
	 * 1. "Add Amount"/"Reason" - Validates input and creates transaction
	 *    - Validates amount and reason are not empty
	 *    - Generates unique transaction ID
	 *    - Logs transaction to PaymentHistory
	 *    - Dynamically shows appropriate split method interface:
	 *      * All Equally: Processes immediately
	 *      * Equally By Some: Shows member checkboxes
	 *      * Unequally: Shows amount input fields per member
	 *      * By Percentages: Shows percentage input fields per member
	 * 
	 * 2. "Back" - Returns to main page without saving
	 * 
	 * 3. Member name (checkbox) - Toggles member selection for "Equally By Some"
	 * 
	 * 4. "EQS" - Processes "Equally By Some" split
	 *    - Divides amount equally among selected members only
	 * 
	 * 5. "U" - Processes "Unequally" split
	 *    - Validates that individual amounts sum to total
	 *    - Allows ±$0.10 tolerance for rounding
	 * 
	 * 6. "BP" - Processes "By Percentages" split
	 *    - Validates that percentages sum to 100%
	 *    - Allows ±0.1% tolerance for rounding
	 * 
	 * @param event The action event from user interaction
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		File txtFile = new File("src/splitwiseapplication/Groups/"+code);
		ArrayList<String> members = Exists.contents_STR(txtFile);
		
		if ("Add Amount".equals(eventName) || "Reason".equals(eventName)) {
			// User entered amount and/or reason
			amountVal = amount.getText();
			reasonVal = reason.getText();
			
			if (amountVal.isEmpty() || reasonVal.isEmpty()) { 
				response.setText("Enter valid values");
			} else {
				// Generate unique transaction ID
				tdFile = new File("src/splitwiseapplication/TransactionDetails/" + code);
				ArrayList<String> usedCodes = Exists.exists(uname,tdFile);
				tID = createCode(usedCodes);
				
				// Log transaction to payment history
				File newFile = new File("src/splitwiseapplication/PaymentHistory/"+code);
				String[] data = {uname,amountVal,reasonVal,"0", tID};
				UpdateFile.Update(data,newFile,">"); 
				
				// Process based on selected split method
				String selectedOption = (String) options.getSelectedItem();
				if ("All Equally".equals(selectedOption)){
					// Split equally among all members - process immediately
					UpdatePAEqually(Integer.parseInt(amountVal));
					response.setText("Transaction added");
					
				} else if("Equally By Some".equals(selectedOption)) {
					// Show checkboxes for member selection
					for (int i = 0; i < members.size(); i++) {
						chosenEQS.add(0); // 0 = not selected
						nameEQS = new JCheckBox(members.get(i));
						nameEQS.setToolTipText("Select to include in split");
						nameEQS.addActionListener(this);
						nameEQS.setActionCommand(members.get(i));
						contentPane.add(nameEQS);
					}
					enter = new JButton("Enter");
					enter.setToolTipText("Confirm selection and split equally");
					enter.addActionListener(this);
					enter.setActionCommand("EQS");
					contentPane.add(enter);
					frame.setContentPane(contentPane);
					frame.pack();
					
				} else if("Unequally".equals(selectedOption)) {
					// Show input fields for custom amounts per member
					for (int i = 0; i < members.size(); i++) {
						nameU = new JTextField(members.get(i));
						nameU.setToolTipText("Enter amount for " + members.get(i));
						chosenU.add(nameU);
						contentPane.add(nameU);
					}
					enter = new JButton("Enter");
					enter.setToolTipText("Amounts must sum to total");
					enter.addActionListener(this);
					enter.setActionCommand("U");
					contentPane.add(enter);
					frame.setContentPane(contentPane);
					frame.pack();
					
				} else if("By Percentages".equals(selectedOption)) {
					// Show input fields for percentages per member
					for (int i = 0; i < members.size(); i++) {
						nameBP = new JTextField(members.get(i));
						nameBP.setToolTipText("Enter percentage for " + members.get(i));
						chosenBP.add(nameBP);
						contentPane.add(nameBP);
					}
					enter = new JButton("Enter");
					enter.setToolTipText("Percentages must sum to 100");
					enter.addActionListener(this);
					enter.setActionCommand("BP");
					contentPane.add(enter);
					frame.setContentPane(contentPane);
					frame.pack();
				}
				
				// Prevent duplicate submissions
				amount.setActionCommand("Amount Added");
				reason.setActionCommand("Reason Provided");
				options.setActionCommand("Selection Confirmed");
			} 
			
		} else if ("Back".equals(eventName)) {
			// Return to main page without saving
			MainPage mpage = new MainPage(uname,code);
			mpage.runGUI();
			frame.dispose();
			
		} else if(members.contains(eventName) ) {
			// Toggle checkbox for "Equally By Some" member selection
			if (chosenEQS.get(members.indexOf(eventName)) == 0){
				chosenEQS.set(members.indexOf(eventName),1); // Selected
			} else {
				chosenEQS.set(members.indexOf(eventName),0); // Deselected
			}
			
		} else if ("EQS".equals(eventName)) {
			// Process "Equally By Some" split
			File newFile = new File("src/splitwiseapplication/PendingAmount/"+code);
			ArrayList<String[]> PAMembers = Exists.contents(newFile, ">");
			UpdatePAEqBySome(Integer.parseInt(amountVal), PAMembers, members, chosenEQS);
			
		}  else if("U".equals(eventName)) {
			// Process "Unequally" split - validate amounts sum to total
			double sum = 0;
			valuesU.clear();
			for (int i = 0; i < chosenU.size(); i++) {
				JTextField field = chosenU.get(i);
	            double text = Double.parseDouble(field.getText());
	            sum += text;
	            valuesU.add(text);
			}
			// Allow ±$0.10 tolerance for rounding
			if (Double.parseDouble(amountVal) - sum <= 0.1 && Double.parseDouble(amountVal) - sum >= -0.1) {
				UpdatePAUnequally(Integer.parseInt(amountVal), valuesU, members);
			}
			
		} else if("BP".equals(eventName)) {
			// Process "By Percentages" split - validate percentages sum to 100
			double sum = 0;
			valuesBP.clear();
			for (int i = 0; i < chosenBP.size(); i++) {
				nameBP = chosenBP.get(i);
	            double text = Double.parseDouble(nameBP.getText());
	            sum += text;
	            valuesBP.add(text);
			}
			// Allow ±0.1% tolerance for rounding
			if (100 - sum <= 0.1 && 100 - sum >= -0.1) {
				UpdatePAByPercentages(Integer.parseInt(amountVal), valuesBP, members);
			}
		}
		
	}
	
	/**
	 * Updates Balances for "All Equally" Split
	 * 
	 * Divides the transaction amount equally among all group members.
	 * Updates:
	 * - PendingAmount file (who owes whom)
	 * - CheckAmountSpent file (individual spending totals)
	 * - TransactionDetails file (detailed split record)
	 * 
	 * The payer (current user) owes negative amounts to others,
	 * while others owe positive amounts to the payer.
	 * 
	 * @param cost Total transaction amount to split
	 */
	public void UpdatePAEqually (int cost) {
		
		File newFilePA = new File("src/splitwiseapplication/PendingAmount/"+code);
		ArrayList<String[]> optionsPA = Exists.contents(newFilePA, ">");
		File newFileCAS = new File("src/splitwiseapplication/CheckAmountSpentFolder/"+code);
		ArrayList<String[]> optionsCAS = Exists.contents(newFileCAS, ",");
		
		File txtFile = new File("src/splitwiseapplication/Groups/"+code);
		ArrayList<String> members = Exists.contents_STR(txtFile);
		
		if (optionsPA.isEmpty() || optionsCAS.isEmpty() || members.isEmpty()) {
			return; // Safety check: cannot proceed without data
		}
		
		double costPP = (double)cost/(double)members.size();
		
		UpdateFile.Write(optionsPA.get(0)[0], optionsPA.get(0)[1], optionsPA.get(0)[2], newFilePA);
		optionsCAS.get(0)[1] = Double.toString(Double.parseDouble(optionsCAS.get(0)[1]) + cost);
		UpdateFile.Write(optionsCAS.get(0)[0], optionsCAS.get(0)[1], newFileCAS);
		
		for (int i = 1; i < optionsPA.size(); i++) {
			String[] vals = optionsPA.get(i);
			if (containsValue(vals,uname)) {
				if (optionsPA.get(i)[0].equals(uname)) {
					optionsPA.get(i)[1] = Double.toString(Double.parseDouble(optionsPA.get(i)[1]) + costPP);
				} else {
					optionsPA.get(i)[1] = Double.toString(Double.parseDouble(optionsPA.get(i)[1]) - costPP);
				}
			}
			UpdateFile.Update(optionsPA.get(i)[0], optionsPA.get(i)[1], optionsPA.get(i)[2], newFilePA);
		}
		
		for (int i = 1; i < optionsCAS.size(); i++) {
			optionsCAS.get(i)[1] = Double.toString(Double.parseDouble(optionsCAS.get(i)[1]) + costPP);
			UpdateFile.Update(optionsCAS.get(i)[0], optionsCAS.get(i)[1], newFileCAS);
		}
		
		String[] transaction = new String[members.size()+1];
		
		transaction[0] = tID;
		
		for (int i = 1; i < transaction.length; i++) {
			if (members.get(i-1).equals(uname)) {
				transaction[i] = Double.toString(costPP - cost);
			} else {
				transaction[i] = Double.toString(costPP);
			}
		}
		
		// Save transaction details for tracking
		UpdateFile.Update(transaction, tdFile, ",");
		
	}
	
	/**
	 * Updates Balances for "Equally By Some" Split
	 * 
	 * Divides the transaction amount equally among selected members only.
	 * Non-selected members are not affected by this transaction.
	 * 
	 * Updates:
	 * - PendingAmount file (adjusts balances for selected members)
	 * - CheckAmountSpent file (adds cost per person to selected members)
	 * - TransactionDetails file (records which members participated)
	 * 
	 * @param cost Total transaction amount
	 * @param lst Current pending amount records
	 * @param names List of all group members
	 * @param selected Binary array (1=selected, 0=not selected) for each member
	 */
	public void UpdatePAEqBySome (int cost, ArrayList<String[]> lst, ArrayList<String> names, ArrayList<Integer> selected) {
		
		File newFilePA = new File("src/splitwiseapplication/PendingAmount/"+code);
		File newFileCAS = new File("src/splitwiseapplication/CheckAmountSpentFolder/"+code);
		ArrayList<String[]> optionsCAS = Exists.contents(newFileCAS, ",");
		
		if (lst.isEmpty() || optionsCAS.isEmpty()) {
			return; // Safety check
		}
		
		int sum = 0;
		
		for (int i : selected) {
			sum += i;
		}
		
		if (sum == 0) {
			return; // Avoid division by zero
		}
		
		double costPP = (double)cost/(double)sum;
		UpdateFile.Write(lst.get(0)[0], lst.get(0)[1], lst.get(0)[2], newFilePA);
		optionsCAS.get(0)[1] = Double.toString(Double.parseDouble(optionsCAS.get(0)[1]) + cost);
		UpdateFile.Write(optionsCAS.get(0)[0], optionsCAS.get(0)[1], newFileCAS);
		
		for (int i = 0; i < names.size(); i++) {
			if (selected.get(i) == 1 && !names.get(i).equals(uname)) {
				for (int j = 1; j < lst.size(); j++) {
					String[] vals = lst.get(j);
					if (containsValue(vals,names.get(i))) {
						if (vals[0].equals(uname)) {
							lst.get(j)[1] = Double.toString(Double.parseDouble(lst.get(j)[1]) + costPP);
						} else if (vals[2].equals(uname)) {
							lst.get(j)[1] = Double.toString(Double.parseDouble(lst.get(j)[1]) - costPP);
						}
					}
				}
			}
		}
		
		for (int i = 1; i < optionsCAS.size(); i++) {
			if(selected.get(i-1) == 1) {
				optionsCAS.get(i)[1] = Double.toString(Double.parseDouble(optionsCAS.get(i)[1]) + costPP);
			}
			UpdateFile.Update(optionsCAS.get(i)[0], optionsCAS.get(i)[1], newFileCAS);
		}
		
		for (int i = 1; i <lst.size(); i++) {
			UpdateFile.Update(lst.get(i)[0], lst.get(i)[1], lst.get(i)[2], newFilePA);
		}
		
		String[] transaction = new String[selected.size()+1];
		
		transaction[0] = tID;
		
		for (int i = 1; i < transaction.length; i++) {
			if (names.get(i-1).equals(uname)) {
				if (selected.get(i-1) == 1) {
					transaction[i] = Double.toString(costPP - cost);
				} else {
					transaction[i] = Double.toString(-1 * cost);
				}
			} else {
				if (selected.get(i-1) == 1) {
					transaction[i] = Double.toString(costPP);
				} else {
					transaction[i] = Double.toString(0);
				}
			}
		}
		
		// Save transaction details
		UpdateFile.Update(transaction, tdFile, ",");
		
	}
	
	/**
	 * Updates Balances for "Unequally" Split
	 * 
	 * Applies custom amounts for each member (as entered by user).
	 * Each member can have a different share of the expense.
	 * 
	 * Updates:
	 * - PendingAmount file (adjusts balances based on individual amounts)
	 * - CheckAmountSpent file (adds individual amounts to each member)
	 * - TransactionDetails file (records exact amounts per member)
	 * 
	 * @param cost Total transaction amount
	 * @param amounts List of individual amounts for each member
	 * @param groupMembers List of all group members
	 */
	public void UpdatePAUnequally(int cost, ArrayList<Double> amounts, ArrayList<String> groupMembers) {
		
		File newFileU = new File("src/splitwiseapplication/PendingAmount/"+code);
		ArrayList<String[]> records = Exists.contents(newFileU, ">");
		File newFileCAS = new File("src/splitwiseapplication/CheckAmountSpentFolder/"+code);
		ArrayList<String[]> optionsCAS = Exists.contents(newFileCAS, ",");
		
		if (records.isEmpty() || optionsCAS.isEmpty()) {
			return; // Safety check
		}
		
		int t;
		
		UpdateFile.Write(records.get(0)[0], records.get(0)[1], records.get(0)[2], newFileU);
		optionsCAS.get(0)[1] = Double.toString(Double.parseDouble(optionsCAS.get(0)[1]) + cost);
		UpdateFile.Write(optionsCAS.get(0)[0], optionsCAS.get(0)[1], newFileCAS);
		
		for (int i = 1; i < records.size(); i++) {
			String[] row = records.get(i);
			if (containsValue(row,uname)) {
				if (row[2].equals(uname)) {
					t = groupMembers.indexOf(row[0]);
					records.get(i)[1] = Double.toString(Double.parseDouble(records.get(i)[1]) - amounts.get(t));
				} else {
					t = groupMembers.indexOf(row[2]);
					records.get(i)[1] = Double.toString(Double.parseDouble(records.get(i)[1]) + amounts.get(t));
				}
			}
			UpdateFile.Update(records.get(i)[0], records.get(i)[1], records.get(i)[2], newFileU);
		}
		
		for (int i = 1; i < optionsCAS.size(); i++) {
			optionsCAS.get(i)[1] = Double.toString(Double.parseDouble(optionsCAS.get(i)[1]) + amounts.get(i-1));
			UpdateFile.Update(optionsCAS.get(i)[0], optionsCAS.get(i)[1], newFileCAS);
		}
		
		String[] transaction = new String[groupMembers.size()+1];
		
		transaction[0] = tID;
		
		for (int i= 1; i < transaction.length; i++) {
			if (groupMembers.get(i-1).equals(uname)) {
				transaction[i] = Double.toString(amounts.get(i-1) - (double)cost);
			} else {
				transaction[i] = Double.toString(amounts.get(i-1));
			}
		}
		
		// Save transaction details
		UpdateFile.Update(transaction, tdFile, ",");
		
	}
	
	/**
	 * Updates Balances for "By Percentages" Split
	 * 
	 * Applies percentage-based split (e.g., 40%, 30%, 30%).
	 * Converts percentages to actual dollar amounts and updates balances.
	 * 
	 * Updates:
	 * - PendingAmount file (adjusts balances based on percentage shares)
	 * - CheckAmountSpent file (adds calculated amounts to each member)
	 * - TransactionDetails file (records percentage-based amounts)
	 * 
	 * @param cost Total transaction amount
	 * @param percentages List of percentage shares for each member (must sum to 100)
	 * @param groupMembers List of all group members
	 */
	public void UpdatePAByPercentages(int cost, ArrayList<Double> percentages, ArrayList<String> groupMembers) {
		
		File newFile = new File("src/splitwiseapplication/PendingAmount/"+code);
		ArrayList<String[]> records = Exists.contents(newFile, ">");
		File newFileCAS = new File("src/splitwiseapplication/CheckAmountSpentFolder/"+code);
		ArrayList<String[]> optionsCAS = Exists.contents(newFileCAS, ",");
		
		if (records.isEmpty() || optionsCAS.isEmpty()) {
			return; // Safety check
		}
		
		int t;
		
		UpdateFile.Write(records.get(0)[0], records.get(0)[1], records.get(0)[2], newFile);
		optionsCAS.get(0)[1] = Double.toString(Double.parseDouble(optionsCAS.get(0)[1]) + cost);
		UpdateFile.Write(optionsCAS.get(0)[0], optionsCAS.get(0)[1], newFileCAS);
		
		for (int i = 1; i < records.size(); i++) {
			String[] row = records.get(i);
			if (containsValue(row,uname)) {
				if (row[2].equals(uname)) {
					t = groupMembers.indexOf(row[0]);
					records.get(i)[1] = Double.toString(Double.parseDouble(records.get(i)[1]) - (double)cost*(double)percentages.get(t)/100.0);
				} else {
					t = groupMembers.indexOf(row[2]);
					records.get(i)[1] = Double.toString(Double.parseDouble(records.get(i)[1]) + (double)cost*(double)percentages.get(t)/100.0);
				}
			}
			UpdateFile.Update(records.get(i)[0], records.get(i)[1], records.get(i)[2], newFile);
		}
		
		for (int i = 1; i < optionsCAS.size(); i++) {
			optionsCAS.get(i)[1] = Double.toString(Double.parseDouble(optionsCAS.get(i)[1]) + (double)cost*(double)percentages.get(i-1)/100.0);
			UpdateFile.Update(optionsCAS.get(i)[0], optionsCAS.get(i)[1], newFileCAS);
		}
		
		String[] transaction = new String[groupMembers.size()+1];
		
		transaction[0] = tID;
		
		for (int i= 1; i < transaction.length; i++) {
			if (groupMembers.get(i-1).equals(uname)) {
				transaction[i] = Double.toString(((double)percentages.get(i-1) - 100.00) * (double)cost/100);
			} else {
				transaction[i] = Double.toString((double)percentages.get(i-1) * cost / 100);
			}
		}
		
		// Save transaction details
		UpdateFile.Update(transaction, tdFile, ",");
		
	}
	
	/**
	 * Checks if Array Contains Specific Value
	 * 
	 * Utility method to check if a string array contains a specific value.
	 * Used to determine if a pending amount record involves a particular user.
	 * 
	 * @param array The array to search
	 * @param targetValue The value to look for
	 * @return true if value found, false otherwise
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
	 * Display the Add Transaction GUI
	 * 
	 * Makes the add transaction frame visible to the user.
	 * Should be called after constructing an AddTransaction object.
	 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}
	
	/**
	 * Generates Unique Transaction ID
	 * 
	 * Creates a random 7-character alphanumeric ID for tracking transactions.
	 * Ensures uniqueness by checking against existing transaction IDs.
	 * 
	 * Format: 7 characters from [A-Za-z0-9]
	 * 
	 * @param codesUsed List of existing transaction IDs to avoid duplicates
	 * @return A unique 7-character transaction ID
	 */
	public static String createCode(ArrayList<String> codesUsed) {
		
		String newcode;
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
		
        // Keep generating until we find a unique ID
		do {
			newcode = "";
	        for (int i = 0; i < 7; i++) {
	            int index = random.nextInt(characters.length());
	            newcode += characters.charAt(index);
	        }
		} while (codesUsed.contains(newcode));
		
		return newcode;
		
	}
}

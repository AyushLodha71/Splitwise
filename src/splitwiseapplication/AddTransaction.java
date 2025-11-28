package splitwiseapplication;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import javax.swing.*;

/**
 * AddTransaction - Expense Recording Screen
 * 
 * PURPOSE:
 * Allows users to add a new expense transaction to the group and split
 * the cost among members using various splitting methods.
 * 
 * FUNCTIONALITY:
 * - Record expense amount and reason
 * - Choose from 4 splitting methods:
 *   1. All Equally - Split evenly among all group members
 *   2. Equally By Some - Select specific members to split evenly
 *   3. Unequally - Specify custom amount for each member
 *   4. By Percentages - Assign percentage of total to each member
 * - Updates member balances in real-time
 * - Records transaction history
 * - Generates unique transaction ID
 * 
 * SPLIT METHODS EXPLAINED:
 * 
 * 1. ALL EQUALLY:
 *    - Cost divided by number of members
 *    - Every member pays equal share
 *    - Example: $100 / 5 members = $20 each
 * 
 * 2. EQUALLY BY SOME:
 *    - User selects which members to include
 *    - Cost divided only among selected members
 *    - Example: $100 / 3 selected = $33.33 each
 * 
 * 3. UNEQUALLY:
 *    - User enters specific amount for each member
 *    - Sum must equal total cost
 *    - Example: Alice=$50, Bob=$30, Charlie=$20 (total=$100)
 * 
 * 4. BY PERCENTAGES:
 *    - User enters percentage for each member
 *    - Percentages must sum to 100%
 *    - Example: Alice=50%, Bob=30%, Charlie=20% of $100
 * 
 * DATABASE UPDATES:
 * - db1: Individual member balances
 * - db5: Transaction history (payee, amount, reason, type, tid)
 * - db6: Inter-member payment relationships
 * - db8: Transaction details per member
 * 
 * NAVIGATION:
 * - From: MainPage (Add Transaction button)
 * - To: MainPage (Back button)
 * 
 * USAGE:
 * Called from MainPage.java when user clicks "Add Transaction" button.
 * Currently used: 1 call site (MainPage.actionPerformed)
 */
public class AddTransaction implements ActionListener {
	
	static JFrame frame;
	JPanel contentPane;
	JLabel amountPrompt,reasonPrompt,splitTypePrompt,response,blank;
	JTextField amount,reason;
	JComboBox options;
	JCheckBox nameEQS;
	JTextField nameU, nameBP;
	JButton backButton, enterm, enter;
	String uname, code,tID;
	ArrayList<Integer> chosenEQS = new ArrayList<Integer>();
	ArrayList<JTextField> chosenU = new ArrayList<JTextField>();
	ArrayList<JTextField> chosenBP = new ArrayList<JTextField>();
	ArrayList<Double> valuesU = new ArrayList<Double>();
	ArrayList<Double> valuesBP = new ArrayList<Double>();
	String amountVal = "0";
	String reasonVal;
	
	/**
	 * AddTransaction Constructor - Initialize Transaction Entry Screen
	 * 
	 * PURPOSE:
	 * Creates the initial UI for adding a new expense transaction.
	 * Sets up amount, reason, and split method selection fields.
	 * 
	 * @param username The current user's username (transaction creator)
	 * @param gcode The group code
	 * 
	 * PROCESS:
	 * 
	 * 1. INITIALIZATION:
	 *    - Stores username and group code
	 *    - Creates frame with 2-column grid layout
	 *    - Sets up empty panel with border padding
	 * 
	 * 2. AMOUNT INPUT:
	 *    - Label: "Enter the Transaction Amount"
	 *    - TextField: For entering expense amount
	 *    - Action command: "Enter" (submit on Enter key)
	 * 
	 * 3. REASON INPUT:
	 *    - Label: "Enter the Reason for this transaction"
	 *    - TextField: For describing expense purpose
	 *    - Action command: "Enter" (submit on Enter key)
	 * 
	 * 4. SPLIT METHOD DROPDOWN:
	 *    - Label: "Choose how you want to split money"
	 *    - JComboBox with 4 options:
	 *      1. "All Equally" - Split among all members
	 *      2. "Equally By Some" - Select members to split among
	 *      3. "Unequally" - Custom amounts per member
	 *      4. "By Percentages" - Percentage distribution
	 *    - Action command: "Selected" (detect selection changes)
	 * 
	 * 5. BUTTONS:
	 *    - Back button: Return to MainPage without saving
	 *    - Enter button: Confirm inputs and proceed to split details
	 * 
	 * 6. UI LAYOUT:
	 *    +--------------------------------+
	 *    | Amount Label  | Amount Field   |
	 *    | Reason Label  | Reason Field   |
	 *    | Split Label   | Dropdown       |
	 *    | Back Button   | Enter Button   |
	 *    | (blank space for future labels)|
	 *    +--------------------------------+
	 * 
	 * DYNAMIC UI:
	 * After clicking Enter, additional UI elements are added based on
	 * selected split method (checkboxes, text fields, etc.)
	 * 
	 * FIELD USAGE:
	 * - chosenEQS: Tracks which members selected in "Equally By Some"
	 * - chosenU: Text fields for amounts in "Unequally"
	 * - chosenBP: Text fields for percentages in "By Percentages"
	 * - valuesU: Parsed amounts from chosenU
	 * - valuesBP: Parsed percentages from chosenBP
	 * 
	 * POST-CONDITIONS:
	 * - Frame created but invisible (call runGUI() to display)
	 * - All input fields registered with action listeners
	 * - Ready to accept user input for transaction details
	 */
	public AddTransaction(String username, String gcode) {
		
		uname = username;
		code = gcode;
		
		 /* Create and set up the frame */
		 frame = new JFrame("Splitwise - Add Transaction");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 /* Create a content pane */
		 contentPane = new JPanel();
		 contentPane.setLayout(new GridLayout(0, 2, 10, 5));
		 contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		 /* Create and add label */
		 amountPrompt = new JLabel("Enter the Transaction Amount");
		 contentPane.add(amountPrompt);
		 amount = new JTextField();
		 amount.addActionListener(this);
		 amount.setActionCommand("Enter");
		 contentPane.add(amount);
		 
		 reasonPrompt = new JLabel("Enter the Reason for this transaction");
		 contentPane.add(reasonPrompt);
		 reason = new JTextField();
		 reason.addActionListener(this);
		 reason.setActionCommand("Enter");
		 contentPane.add(reason);
		 
		 splitTypePrompt = new JLabel("Choose how you want to split money");
		 contentPane.add(splitTypePrompt);
		 options = new JComboBox();
		 options.addItem("All Equally");
		 options.addItem("Equally By Some");
		 options.addItem("Unequally");
		 options.addItem("By Percentages");
		 options.addActionListener(this);
		 options.setActionCommand("Selected");
		 
		 contentPane.add(options);
		 
		 backButton = new JButton("Back");
		 backButton.addActionListener(this);
		 backButton.setActionCommand("Back");
		 contentPane.add(backButton);

		enterm = new JButton("Enter");
		enterm.addActionListener(this);
		enterm.setActionCommand("Enter");
		contentPane.add(enterm);
		 
		 blank = new JLabel("");
		 contentPane.add(blank);
		 
		 /* Add content pane to frame */
		 frame.setContentPane(contentPane);
		 /* Size and then display the frame. */
		 frame.pack();
		 frame.setVisible(false);
	
	}
	
	/**
	 * actionPerformed - Handle User Actions and Transaction Processing
	 * 
	 * PURPOSE:
	 * Complex event handler managing all user interactions for transaction entry.
	 * Handles input validation, split method selection, member selection,
	 * and final transaction recording.
	 * 
	 * EVENT TYPES AND FLOW:
	 * 
	 * 1. "Enter" - Initial Submit
	 *    - Validates amount and reason fields (not empty)
	 *    - Generates unique transaction ID
	 *    - Records transaction in db5 (history table)
	 *    - Based on selected split method:
	 *      a) "All Equally" → Calls UpdatePAEqually(), shows confirmation
	 *      b) "Equally By Some" → Shows checkboxes for member selection
	 *      c) "Unequally" → Shows text fields for custom amounts
	 *      d) "By Percentages" → Shows text fields for percentages
	 *    - Disables further edits by changing action commands
	 * 
	 * 2. "Back" - Cancel and Return
	 *    - Creates MainPage instance
	 *    - Shows MainPage, disposes current frame
	 *    - No database changes made
	 * 
	 * 3. Member name (checkbox action) - For "Equally By Some"
	 *    - Toggles member selection: 0 → 1 (selected), 1 → 0 (unselected)
	 *    - Stored in chosenEQS ArrayList at member's index
	 *    - Used to determine split calculation
	 * 
	 * 4. "EQS" - Confirm "Equally By Some"
	 *    - Calls UpdatePAEqBySome() with selected members
	 *    - Shows "Transaction added" confirmation
	 *    - Disables Enter button
	 * 
	 * 5. "U" - Confirm "Unequally"
	 *    - Parses amounts from all text fields
	 *    - Validates: sum of amounts must equal total cost (±0.1 tolerance)
	 *    - If valid: Calls UpdatePAUnequally(), shows confirmation
	 *    - If invalid: Does nothing (silent failure)
	 * 
	 * 6. "BP" - Confirm "By Percentages"
	 *    - Parses percentages from all text fields
	 *    - Validates: sum of percentages must equal 100% (±0.1 tolerance)
	 *    - If valid: Calls UpdatePAByPercentages(), shows confirmation
	 *    - If invalid: Does nothing (silent failure)
	 * 
	 * VALIDATION:
	 * - Empty fields: Shows "Enter valid values" error
	 * - Sum validation: Uses ±0.1 tolerance for floating-point comparison
	 * - No validation for negative amounts or invalid numbers
	 * 
	 * @param event The ActionEvent containing the action command
	 */
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		ArrayList<String> members = new ArrayList<>(Arrays.asList(ApiCaller.ApiCaller3("http://localhost:8080/db4/GetSpecificData?val=name&table="+ code)));
		
		
		if (eventName.equals("Enter") == true ) {
			amountVal = amount.getText();
			reasonVal = reason.getText();
			
			if (amountVal.isEmpty() || reasonVal.isEmpty()) { 
				response.setText("Enter valid values");
			} else {
				
				String[] lst = ApiCaller.ApiCaller3("http://localhost:8080/db5/GetSpecificData?val=tID&table="+ code);
				System.out.println(lst.length);

				ArrayList<String> usedCodes = new ArrayList<>(Arrays.asList(ApiCaller.ApiCaller3("http://localhost:8080/db8/GetSpecificData?val=tID&table=" + code)));
				
				tID = createCode(usedCodes);

				String value = ApiCaller.ApiCaller2("http://localhost:8080/db5/InsertData?table="+ code +"&params=(payee,amount,reason,Ttype,tid)&info=(%27" + uname + "%27," + amountVal + ",%27" + reasonVal + "%27," + 0 + ",%27" + tID + "%27)");
				if (options.getSelectedItem() == "All Equally"){
					UpdatePAEqually(Double.parseDouble(amountVal));
					response = new JLabel("Transaction added");
					contentPane.add(response);
					frame.setContentPane(contentPane);
					frame.pack();
				} else if(options.getSelectedItem() == "Equally By Some") {
					for (int i = 0; i < members.size(); i++) {
						chosenEQS.add(0);
						nameEQS = new JCheckBox(members.get(i));
						nameEQS.addActionListener(this);
						nameEQS.setActionCommand(members.get(i));
						contentPane.add(nameEQS);
					}
					enter = new JButton("Enter");
					enter.addActionListener(this);
					enter.setActionCommand("EQS");
					contentPane.add(enter);
					frame.setContentPane(contentPane);
					frame.pack();
				} else if(options.getSelectedItem() == "Unequally") {
					for (int i = 0; i < members.size(); i++) {
						nameU = new JTextField(members.get(i));
						chosenU.add(nameU);
						contentPane.add(nameU);
					}
					enter = new JButton("Enter");
					enter.addActionListener(this);
					enter.setActionCommand("U");
					contentPane.add(enter);
					frame.setContentPane(contentPane);
					frame.pack();
				} else if(options.getSelectedItem() == "By Percentages") {
					for (int i = 0; i < members.size(); i++) {
						nameBP = new JTextField(members.get(i));
						chosenBP.add(nameBP);
						contentPane.add(nameBP);
					}
					enter = new JButton("Enter");
					enter.addActionListener(this);
					enter.setActionCommand("BP");
					contentPane.add(enter);
					frame.setContentPane(contentPane);
					frame.pack();
				}
				amount.setActionCommand("Entered");
				reason.setActionCommand("Entered");
				options.setActionCommand("Selection Confirmed");
				enterm.setActionCommand("Entered");
			} 
		} else if (eventName.equals("Back") == true) {
			MainPage mpage = new MainPage(uname,code);
			mpage.runGUI();
			frame.dispose();
		} else if(members.contains(eventName) ) {
			if (chosenEQS.get(members.indexOf(eventName)) == 0){
				chosenEQS.set(members.indexOf(eventName),1);
			} else {
				chosenEQS.set(members.indexOf(eventName),0);
			}
		} else if (eventName.equals("EQS") == true) {
			UpdatePAEqBySome(Double.parseDouble(amountVal), members, chosenEQS);
			enter.setActionCommand("Entered");
			response = new JLabel("Transaction added");
			contentPane.add(response);
			frame.setContentPane(contentPane);
			frame.pack();
		}  else if(eventName.equals("U") == true) {
			double sum = 0;
			valuesU.clear();
			for (int i = 0; i < chosenU.size(); i++) {
				JTextField nameU = chosenU.get(i); // Get each JTextField from the list
	            double text = Double.parseDouble(nameU.getText()); // Retrieve the text from the current text field
	            sum += text;
	            valuesU.add(text);
			}
			if (Double.parseDouble(amountVal) - sum <= 0.1 && Double.parseDouble(amountVal) - sum >= -0.1) {
				UpdatePAUnequally(Double.parseDouble(amountVal), valuesU, members);
				enter.setActionCommand("Entered");
				response = new JLabel("Transaction added");
		 		contentPane.add(response);
				frame.setContentPane(contentPane);
				frame.pack();
			}
		} else if(eventName.equals("BP") == true) {
			double sum = 0;
			valuesBP.clear();
			for (int i = 0; i < chosenBP.size(); i++) {
				nameBP = chosenBP.get(i); // Get each JTextField from the list
	            double text = Double.parseDouble(nameBP.getText()); // Retrieve the text from the current text field
	            sum += text;
	            valuesBP.add(text);
			}
			if (100 - sum <= 0.1 && 100 - sum >= -0.1) {
				UpdatePAByPercentages(Double.parseDouble(amountVal), valuesBP, members);
				enter.setActionCommand("Entered");
				response = new JLabel("Transaction added");
		 		contentPane.add(response);
				frame.setContentPane(contentPane);
				frame.pack();
			}
		}
		
	}
	
	/**
	 * UpdatePAEqually - Split Cost Equally Among All Members
	 * 
	 * PURPOSE:
	 * Updates all database tables when splitting a transaction equally
	 * among all group members.
	 * 
	 * @param cost Total transaction amount
	 * 
	 * ALGORITHM:
	 * 1. Calculate cost per person: cost / (total members - 2)
	 *    Note: -2 accounts for header row and "Total" row in db1
	 * 
	 * 2. Update db6 (inter-member balances):
	 *    - For records where user is Member1: Add costPP to Amount
	 *    - For records where user is Member2: Subtract costPP from Amount
	 *    - This adjusts who owes whom based on user spending money
	 * 
	 * 3. Update db1 (individual balances):
	 *    - Update "Total" row: Add full cost
	 *    - For user: Add (costPP - cost) → negative since user paid
	 *    - For others: Add costPP → positive since they owe their share
	 * 
	 * 4. Record transaction details in db8:
	 *    - Creates row with Creator, tID, and amount for each member
	 *    - User's amount: (costPP - cost) → negative
	 *    - Others' amounts: costPP → positive
	 * 
	 * BALANCE LOGIC:
	 * - User pays full amount, owes only their share
	 * - Others owe their share to the user
	 * - Example: $100 / 4 = $25 each
	 *   User balance: $25 - $100 = -$75 (paid $75 extra)
	 *   Others: +$25 each (owe their share)
	 * 
	 */
	public void UpdatePAEqually (double cost) {
		
		
		String[][] people = ApiCaller.ApiCaller1("http://localhost:8080/db1/GetRowData?table="+ code);
		
		double costPP = (double)cost/(double)(people.length-2);

		String[][] member1 = ApiCaller.ApiCaller1("http://localhost:8080/db6/GetRowData?table="+ code +"&Member1="+uname);
		String[][] member2 = ApiCaller.ApiCaller1("http://localhost:8080/db6/GetRowData?table="+ code +"&Member2="+uname);
		for (int i = 1; i < member1.length; i++) {

			String val = ApiCaller.ApiCaller2("http://localhost:8080/db6/UpdateData?table="+ code +"&where=Member1=%27" + uname + "%27&Amount=" + Double.toString((double)(Double.parseDouble(member1[i][2]) + costPP)));

		}

		for (int i = 1; i < member2.length; i++) {

			String val = ApiCaller.ApiCaller2("http://localhost:8080/db6/UpdateData?table="+ code +"&where=Member2=%27" + uname + "%27&Amount=" + Double.toString((double)(Double.parseDouble(member2[i][2]) - costPP)));

		}

		String val1 = ApiCaller.ApiCaller2("http://localhost:8080/db1/UpdateData?table="+ code +"&where=id=" + (1) + "&Amount=" + Double.toString((double)(Double.parseDouble(people[1][2]) + cost)));
		String params = "&params=(Creator,tID,";
		String info = "&info=(%27"+uname+"%27,%27"+tID+"%27,";
		for (int i = 2; i < people.length; i++) {

			String val = ApiCaller.ApiCaller2("http://localhost:8080/db1/UpdateData?table="+ code +"&where=Name=%27" + people[i][1] + "%27&Amount=" + Double.toString((double)(Double.parseDouble(people[i][2]) + costPP)));
			if (people[i][1].equals(uname)) {
				params += uname + ",";
				info += Double.toString((double)(costPP - cost)) + ",";
			}else{
				params += people[i][1] + ",";
				info += Double.toString((double)(costPP)) + ",";
			}

		}

		params = params.substring(0, params.length() - 1) + ")";
		info = info.substring(0, info.length() - 1) + ")";
		String val = ApiCaller.ApiCaller2("http://localhost:8080/db8/InsertData?table="+ code + params + info);

	}
	
	/**
	 * UpdatePAEqBySome - Split Cost Equally Among Selected Members
	 * 
	 * PURPOSE:
	 * Updates databases when splitting transaction equally among
	 * only specific selected members (not all group members).
	 * 
	 * @param cost Total transaction amount
	 * @param names List of all member names
	 * @param selected List of selection flags (1=selected, 0=not selected)
	 * 
	 * ALGORITHM:
	 * 1. Calculate number of selected members (sum of selected array)
	 * 2. Calculate cost per selected person: cost / selectedCount
	 * 3. Update db1 "Total" row: Add full cost
	 * 4. Update db6 for each selected member (except user):
	 *    - Query both Member1=user,Member2=member and vice versa
	 *    - Update whichever record exists
	 *    - Adjust balance by costPP
	 * 5. Update db1 for each selected member: Add costPP to balance
	 * 6. Record transaction in db8:
	 *    - User who paid: (costPP - cost) if selected, else -cost
	 *    - Selected members: costPP
	 *    - Unselected members: 0
	 * 
	 * EXAMPLE:
	 * Total: $100, 3 selected out of 5 members
	 * Cost per selected: $100 / 3 = $33.33 each
	 * 
	 */
	public void UpdatePAEqBySome (double cost, ArrayList<String> names, ArrayList<Integer> selected) {
		
		String[][] optionsCAS = ApiCaller.ApiCaller1("http://localhost:8080/db1/GetRowData?table="+ code);
		double sum = 0;
		
		for (double i : selected) {
			sum += i;
		}
		
		double costPP = (double)cost/(double)sum;
		String val = ApiCaller.ApiCaller2("http://localhost:8080/db1/UpdateData?table="+ code +"&where=id=" + 1 + "&Amount=" + Double.toString((double)(Double.parseDouble(optionsCAS[1][2]) + cost)));

		for (int j = 0; j < names.size(); j++) {
			if (selected.get(j) == 1 && !names.get(j).equals(uname)) {
				String[][] member1 = ApiCaller.ApiCaller1("http://localhost:8080/db6/GetRowData?table="+ code +"&Member1="+uname +"&Member2="+names.get(j));
				String[][] member2 = ApiCaller.ApiCaller1("http://localhost:8080/db6/GetRowData?table="+ code +"&Member2="+uname +"&Member1="+names.get(j));
				if (member1.length != 0) {
					val = ApiCaller.ApiCaller2("http://localhost:8080/db6/UpdateData?table="+ code +"&where=Member1=%27" + uname + "%27%20AND%20Member2=%27" + names.get(j) + "%27&Amount=" + (Double.toString((double)(Double.parseDouble(member1[1][2]) + costPP))));
				}
				if (member2.length != 0) {
					val = ApiCaller.ApiCaller2("http://localhost:8080/db6/UpdateData?table="+ code +"&where=Member2=%27" + uname + "%27%20AND%20Member1=%27" + names.get(j) + "%27&Amount=" + (Double.toString((double)(Double.parseDouble(member2[1][2]) - costPP))));
				}

			}
		}
		
		for (int i = 0; i < selected.size(); i++) {
			if(selected.get(i) == 1) {
				val = ApiCaller.ApiCaller2("http://localhost:8080/db1/UpdateData?table="+ code +"&where=Name=%27" + names.get(i) + "%27&Amount=" + Double.toString((double)(Double.parseDouble(optionsCAS[i+2][2]) + costPP)));
			}
		}
		
		String transaction = "(%27"+uname+"%27,%27"+tID+"%27,";
		
		for (int i = 0; i < selected.size(); i++) {
			if (names.get(i).equals(uname)) {
				if (selected.get(i) == 1) {
					transaction += Double.toString(costPP - cost);
				} else {
					transaction += Double.toString(-1 * cost);
				}
			} else {
				if (selected.get(i) == 1) {
					transaction += Double.toString(costPP);
				} else {
					transaction += Double.toString(0);
				}
			}

			transaction += ",";

		}

		transaction = 	transaction.substring(0, transaction.length() - 1) + ")";
		System.out.println(transaction);
		String val1 = ApiCaller.ApiCaller2("http://localhost:8080/db8/InsertData?table="+ code +"&params=(Creator,tID," + String.join(",", names) + ")&info=" + transaction);
		
	}
	
	/**
	 * UpdatePAUnequally - Split Cost with Custom Amounts Per Member
	 * 
	 * PURPOSE:
	 * Updates databases when each member pays a different, specified amount.
	 * User specifies exact amount for each member (must sum to total cost).
	 * 
	 * @param cost Total transaction amount
	 * @param amounts List of amounts for each member (parallel to names)
	 * @param names List of all member names
	 * 
	 * ALGORITHM:
	 * 1. Update db1 "Total" row: Add full cost
	 * 2. For each member, update db6:
	 *    - Query both Member1/Member2 combinations
	 *    - Update whichever record exists
	 *    - Adjust balance by member's specific amount
	 * 3. Update db1 for each member: Add their specific amount
	 * 4. Record transaction in db8:
	 *    - User who paid: (their amount - total cost) → negative
	 *    - Other members: their specific amounts → positive
	 * 
	 * EXAMPLE:
	 * Total: $100, Alice (user): $40, Bob: $35, Charlie: $25
	 * Alice balance: $40 - $100 = -$60 (paid $60 extra)
	 * Bob balance: +$35, Charlie balance: +$25
	 * 
	 */
	public void UpdatePAUnequally(double cost, ArrayList<Double> amounts, ArrayList<String> names) {
		System.out.println(names.get(0));
		String[][] optionsCAS = ApiCaller.ApiCaller1("http://localhost:8080/db1/GetRowData?table="+ code);
		
		String val = ApiCaller.ApiCaller2("http://localhost:8080/db1/UpdateData?table="+ code +"&where=id=" + 1 + "&Amount=" + Double.toString((double)(Double.parseDouble(optionsCAS[1][2]) + cost)));

		for (int i = 0; i < names.size(); i++) {
			String[][] member1 = ApiCaller.ApiCaller1("http://localhost:8080/db6/GetRowData?table="+ code +"&Member1="+uname +"&Member2="+names.get(i));
			String[][] member2 = ApiCaller.ApiCaller1("http://localhost:8080/db6/GetRowData?table="+ code +"&Member2="+uname +"&Member1="+names.get(i));
			if (member1.length != 0) {
				System.out.print((Double.parseDouble(member1[1][2]) + amounts.get(i)));
				System.out.println("!");
				val = ApiCaller.ApiCaller2("http://localhost:8080/db6/UpdateData?table="+ code +"&where=Member1=%27" + uname + "%27%20And%20Member2=%27" + names.get(i) + "%27&Amount=" + (Double.parseDouble(member1[1][2]) + amounts.get(i)));
			}
			if (member2.length != 0) {
				System.out.println((Double.parseDouble(member2[1][2]) + amounts.get(i)));
				val = ApiCaller.ApiCaller2("http://localhost:8080/db6/UpdateData?table="+ code +"&where=Member2=%27" + uname + "%27%20And%20Member1=%27" + names.get(i) + "%27&Amount=" + (Double.parseDouble(member2[1][2]) - amounts.get(i)));
			}

		}
		
		for (int i = 2; i < optionsCAS.length; i++) {
			val = ApiCaller.ApiCaller2("http://localhost:8080/db1/UpdateData?table="+ code +"&where=Name=%27" + names.get(i-2) + "%27&Amount=" + Double.toString((double)(Double.parseDouble(optionsCAS[i][2]) + amounts.get(i-2))));
		}
		
		String transaction = "(%27"+uname+"%27,%27"+tID+"%27,";
		
		for (int i = 0; i < names.size(); i++) {
			if (names.get(i).equals(uname)) {
				transaction += Double.toString(amounts.get(i) - (double)cost);
			} else {
				transaction += Double.toString(amounts.get(i));	
			}

			transaction += ",";

		}

		transaction = 	transaction.substring(0, transaction.length() - 1) + ")";
		System.out.println(transaction);
		String val1 = ApiCaller.ApiCaller2("http://localhost:8080/db8/InsertData?table="+ code +"&params=(Creator,tID," + String.join(",", names) + ")&info=" + transaction);
				
	}
	
	/**
	 * UpdatePAByPercentages - Split Cost by Percentage Distribution
	 * 
	 * PURPOSE:
	 * Updates databases when each member pays a specific percentage
	 * of the total cost (percentages must sum to 100%).
	 * 
	 * @param cost Total transaction amount
	 * @param percentages List of percentages for each member (parallel to names)
	 * @param names List of all member names
	 * 
	 * ALGORITHM:
	 * 1. Update db1 "Total" row: Add full cost
	 * 2. For each member, calculate: amount = (cost * percentage) / 100
	 * 3. Update db6 with calculated amounts for each member
	 * 4. Update db1 individual balances with calculated amounts
	 * 5. Record transaction in db8:
	 *    - User who paid: ((their % - 100%) * cost) / 100 → negative
	 *    - Other members: (their % * cost) / 100 → positive
	 * 
	 * EXAMPLE:
	 * Total: $100, Alice (user): 50%, Bob: 30%, Charlie: 20%
	 * Alice amount: 50% of $100 = $50, balance: -$50 (paid $50 extra)
	 * Bob amount: $30, Charlie amount: $20
	 * 
	 */
	public void UpdatePAByPercentages(double cost, ArrayList<Double> percentages, ArrayList<String> names) {
		
		String[][] optionsCAS = ApiCaller.ApiCaller1("http://localhost:8080/db1/GetRowData?table="+ code);
		
		String val = ApiCaller.ApiCaller2("http://localhost:8080/db1/UpdateData?table="+ code +"&where=id=" + 1 + "&Amount=" + Double.toString((double)(Double.parseDouble(optionsCAS[1][2]) + cost)));

		for (int i = 0; i < names.size(); i++) {
			String[][] member1 = ApiCaller.ApiCaller1("http://localhost:8080/db6/GetRowData?table="+ code +"&Member1="+uname +"&Member2="+names.get(i));
			String[][] member2 = ApiCaller.ApiCaller1("http://localhost:8080/db6/GetRowData?table="+ code +"&Member2="+uname +"&Member1="+names.get(i));
			if (member1.length != 0) {
				val = ApiCaller.ApiCaller2("http://localhost:8080/db6/UpdateData?table="+ code +"&where=Member1=%27" + uname + "%27%20And%20Member2=%27" + names.get(i) + "%27&Amount=" + (Double.parseDouble(member1[1][2]) + (cost*percentages.get(i))/100.00));
			}
			if (member2.length != 0) {
				val = ApiCaller.ApiCaller2("http://localhost:8080/db6/UpdateData?table="+ code +"&where=Member2=%27" + uname + "%27%20And%20Member1=%27" + names.get(i) + "%27&Amount=" + (Double.parseDouble(member2[1][2]) + (cost*percentages.get(i))/100.00));
			}

		}
		
		for (int i = 2; i < optionsCAS.length; i++) {
			val = ApiCaller.ApiCaller2("http://localhost:8080/db1/UpdateData?table="+ code +"&where=Name=%27" + names.get(i-2) + "%27&Amount=" + Double.toString((double)(Double.parseDouble(optionsCAS[i][2]) + (percentages.get(i-2) * cost / 100))));
		}
		
		String transaction = "(%27"+uname+"%27,%27"+tID+"%27,";
		
		for (int i = 0; i < names.size(); i++) {
			if (names.get(i).equals(uname)) {
				transaction += Double.toString((percentages.get(i) - 100) * (double)cost/100);
			} else {
				transaction += Double.toString(percentages.get(i)* (double)cost / 100);	
			}

			transaction += ",";

		}

		transaction = 	transaction.substring(0, transaction.length() - 1) + ")";
		System.out.println(transaction);
		String val1 = ApiCaller.ApiCaller2("http://localhost:8080/db8/InsertData?table="+ code +"&params=(Creator,tID," + String.join(",", names) + ")&info=" + transaction);
		
	}
	
	/**
	 * createCode - Generate Unique Transaction ID
	 * 
	 * PURPOSE:
	 * Creates a random 7-character alphanumeric code for transaction identification.
	 * Ensures uniqueness by checking against existing transaction IDs in the group.
	 * 
	 * @param codesUsed ArrayList of existing transaction IDs in the group
	 * @return A unique 7-character transaction ID
	 * 
	 * ALGORITHM:
	 * 1. Define character set: A-Z, a-z, 0-9 (62 characters)
	 * 2. Generate random 7-character string
	 * 3. Check if code already exists in codesUsed
	 * 4. If exists, repeat generation
	 * 5. If unique, return new code
	 * 
	 * CODE FORMAT:
	 * - Length: 7 characters
	 * - Characters: Uppercase, lowercase, digits
	 * - Example: "aB3xY9z"
	 * 
	 * COLLISION HANDLING:
	 * Uses do-while loop to regenerate if collision detected.
	 * With 62^7 = 3.5 trillion possibilities, collisions are extremely rare.
	 * 
	 * USAGE:
	 * Called during transaction creation to generate unique transaction ID
	 * before recording in database.
	 * 
	 * STATIC METHOD:
	 * Can be called without instance, used as utility function.
	 */
	public static String createCode(ArrayList<String> codesUsed) {
		
		String newcode;
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
		
		do {
			newcode = "";
	        for (int i = 0; i < 7; i++) {
	            int index = random.nextInt(characters.length());
	            newcode += characters.charAt(index);
	        }
		} while (codesUsed.contains(newcode));
		
		return newcode;
		
	}

	/**
	 * runGUI - Display the AddTransaction Window
	 * 
	 * PURPOSE:
	 * Makes the AddTransaction frame visible to the user.
	 * Final step in showing the transaction entry interface.
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
	 * Called on AddTransaction instance to display its frame.
	 * 
	 * USAGE:
	 * Called by MainPage.java after creating AddTransaction instance.
	 * Currently used: 1 call site (MainPage.actionPerformed)
	 */
	public void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}

}

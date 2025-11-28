package splitwiseapplication;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javax.swing.*;

/**
 * AmountSettled - Payment Settlement Entry Screen
 * 
 * PURPOSE:
 * Records the actual amount of money paid to settle a debt between two members.
 * Final step in the payment settlement process after selecting a debt in SettlePayment.
 * 
 * FUNCTIONALITY:
 * - Shows prompt indicating who is paying whom
 * - Pre-fills the full outstanding amount
 * - User can modify amount (for partial payments)
 * - Records payment in transaction history (db5)
 * - Updates debt balance in payment records (db6)
 * - Creates transaction detail record (db8)
 * 
 * PAYMENT FLOW:
 * 1. User selects debt in SettlePayment screen
 * 2. AmountSettled opens with payment details
 * 3. User enters/confirms amount paid
 * 4. Click "Finish" to record settlement
 * 5. Updates databases and shows confirmation
 * 
 * AMOUNT INTERPRETATION:
 * Based on selectedItem array and user position:
 * - Determines payment direction (who pays whom)
 * - Calculates if user is receiver (type=0) or payer (type=1)
 * - Adjusts debt balance accordingly
 * 
 * DATABASE UPDATES:
 * - db5: Transaction history (payee, amount, reason, type, tid)
 * - db6: Payment balances (updates existing debt record)
 * - db8: Transaction details (per-member breakdown)
 * 
 * NAVIGATION:
 * - From: SettlePayment (after selecting payment to settle)
 * - To: MainPage (via Back button or after recording)
 * 
 * USAGE:
 * Called from SettlePayment.java when user clicks "Pay" button.
 * Currently used: 1 call site (SettlePayment.actionPerformed)
 */
public class AmountSettled implements ActionListener{

	static JFrame frame;
	JPanel contentPane;
	static String uname;
	static String gcode;
	String prompt, tID;
	String[] SIInfo;
	JLabel amountPrompt,response;
	JTextField amount;
	JButton finish,back;
	double amt;
	int type = 0;
	
	/**
	 * AmountSettled Constructor - Initialize Payment Settlement Entry Screen
	 * 
	 * PURPOSE:
	 * Creates the UI for entering/confirming the amount of money being paid
	 * to settle a debt between two members.
	 * 
	 * @param selectedItem Array containing payment record [Member1, Amount, Member2]
	 * @param usrname Current user's username
	 * @param grpcode Group code
	 * @param index Original index of payment in SettlePayment's info list (stored but unused)
	 * 
	 * PROCESS:
	 * 
	 * 1. INITIALIZATION:
	 *    - Stores username, group code, payment info, index
	 *    - Creates frame with 2-column grid layout
	 * 
	 * 2. DETERMINE PAYMENT DIRECTION:
	 *    Analyzes selectedItem to determine who is paying whom:
	 * 
	 *    Case A: Amount > 0 (Member2 owes Member1)
	 *      - If user is Member1:
	 *        Prompt: "Enter the amount Member2 gave you"
	 *        type = 0 (user is receiver)
	 *      - If user is Member2:
	 *        Prompt: "Enter the amount you gave to Member2"
	 *        type = 1 (user is payer)
	 * 
	 *    Case B: Amount < 0 (Member1 owes Member2)
	 *      - If user is Member2:
	 *        Prompt: "Enter the amount Member1 gave you"
	 *        type = 0 (user is receiver)
	 *      - If user is Member1:
	 *        Prompt: "Enter the amount you gave to Member1"
	 *        type = 1 (user is payer)
	 * 
	 * 3. PRE-FILL AMOUNT:
	 *    - Calculates absolute value of outstanding debt
	 *    - Pre-fills text field with full amount
	 *    - User can modify for partial payments
	 * 
	 * 4. UI CONSTRUCTION:
	 *    - Label: Payment direction prompt
	 *    - TextField: Amount to pay (pre-filled)
	 *    - Finish button: Record payment
	 *    - Back button: Cancel and return to MainPage
	 * 
	 * 5. EVENT HANDLERS:
	 *    - Amount field: Enter key triggers "finish"
	 *    - Finish button: Records payment
	 *    - Back button: Returns to MainPage
	 * 
	 * TYPE FIELD:
	 * - type = 0: User is receiving money
	 * - type = 1: User is paying money
	 * - Used in Finish() to determine database update logic
	 * 
	 * POST-CONDITIONS:
	 * - Frame created but invisible (call runGUI() to display)
	 * - Amount field pre-filled with outstanding debt
	 * - Prompt shows correct payment direction
	 * - type variable set for database update logic
	 */
	public AmountSettled(String[] selectedItem, String usrname, String grpcode, int index) {
		
		uname = usrname;
		gcode = grpcode;
		SIInfo = selectedItem;
		
		frame = new JFrame("Splitwise - Settle payment Page");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/* Create a content pane */
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(0, 2, 10, 5));
		contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));

		if (Double.parseDouble(SIInfo[2]) > 0) {
			if(SIInfo[1].equals(usrname)) {
				prompt = "Enter the amount " + SIInfo[3] + " gave you";
			} else {
				prompt = "Enter the amount you gave to " + SIInfo[3];
				type = 1;
			}
			amt = Double.parseDouble(SIInfo[2]);
		} else {
			if(SIInfo[3].equals(usrname)) {
				prompt = "Enter the amount " + SIInfo[1] + " gave you";
			} else {
				prompt = "Enter the amount you gave to " + SIInfo[1];
				type = 1;
			}
			amt = -1.0 * Double.parseDouble(SIInfo[2]);
		}
		
		amountPrompt = new JLabel(prompt);
		contentPane.add(amountPrompt);
		
		amount = new JTextField(Double.toString(amt));
		amount.addActionListener(this);
		amount.setActionCommand("finish");
		contentPane.add(amount);
		
		finish = new JButton("Finish");
		finish.addActionListener(this);
		finish.setActionCommand("finish");
		contentPane.add(finish);
		
		back = new JButton("Back");
		back.addActionListener(this);
		back.setActionCommand("back");
		contentPane.add(back);
		
		frame.setContentPane(contentPane);
		frame.pack();
		frame.setVisible(false);
		
	}
	
	/**
	 * actionPerformed - Handle User Actions (Finish Button, Back Button)
	 * 
	 * PURPOSE:
	 * Event handler for all user interactions in the AmountSettled screen.
	 * Manages payment recording and navigation.
	 * 
	 * EVENT TYPES:
	 * 
	 * 1. "finish" - Record Payment Settlement
	 *    - Gets amount from text field
	 *    - Calls Finish() to update databases
	 *    - Adds "Amount Settled" confirmation label
	 *    - Refreshes frame display
	 *    - Note: Frame stays open to show confirmation
	 * 
	 * 2. "back" - Cancel and Return
	 *    - Creates MainPage instance with username and group code
	 *    - Shows MainPage screen
	 *    - Disposes current frame
	 *    - No database changes made
	 * 
	 * CONFIRMATION FLOW:
	 * After clicking Finish, user sees "Amount Settled" label
	 * but frame remains open. User must manually close or use
	 * navigation from MainPage to return to group view.
	 * 
	 * @param event The ActionEvent containing the action command
	 */
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		if(eventName.equals("finish")) {
			Finish(amount.getText(), SIInfo);
			response = new JLabel("Amount Settled");
			contentPane.add(response);
			frame.setContentPane(contentPane);
			frame.pack();
		} else {
			MainPage mpage = new MainPage(uname,gcode);
			mpage.runGUI();
			frame.dispose();
		}
		
	}
	
	/**
	 * Finish - Record Payment Settlement in Databases
	 * 
	 * PURPOSE:
	 * Performs all database updates to record a payment settlement.
	 * Updates balances, transaction history, and transaction details.
	 * 
	 * @param amount The amount of money being paid (as string)
	 * @param SII Selected Item Info array [Member1, Amount, Member2]
	 * 
	 * PROCESS:
	 * 
	 * 1. GENERATE TRANSACTION ID:
	 *    - Queries db8 for existing transaction IDs in this group
	 *    - Generates unique 7-character alphanumeric code
	 *    - Ensures no collision with existing IDs
	 * 
	 * 2. UPDATE PAYMENT BALANCE (db6):
	 *    Four cases based on user position and type:
	 * 
	 *    Case A: User is Member1, type=0 (user received money)
	 *      - Decreases debt: Amount = OldAmount - PaidAmount
	 *      - Records in db5: payee=Member2, reason=username
	 * 
	 *    Case B: User is Member1, type=1 (user paid money)
	 *      - Increases debt: Amount = OldAmount + PaidAmount
	 *      - Records in db5: payee=username, reason=Member2
	 * 
	 *    Case C: User is Member2, type=1 (user paid money)
	 *      - Decreases debt: Amount = OldAmount - PaidAmount
	 *      - Records in db5: payee=username, reason=Member1
	 * 
	 *    Case D: User is Member2, type=0 (user received money)
	 *      - Increases debt: Amount = OldAmount + PaidAmount
	 *      - Records in db5: payee=Member1, reason=username
	 * 
	 * 3. CREATE TRANSACTION HISTORY (db5):
	 *    - Table: group code
	 *    - Columns: payee, amount, reason, Ttype=1, tid
	 *    - Ttype=1 indicates payment settlement
	 * 
	 * 4. CREATE TRANSACTION DETAILS (db8):
	 *    - Gets all group members from db1
	 *    - Creates record with columns: Creator, tID, [member names]
	 *    - For each member:
	 *      * User: +/- amount (based on type)
	 *      * Other party: opposite sign of amount
	 *      * Everyone else: 0
	 *    - Records per-member impact of settlement
	 * 
	 * TYPE LOGIC:
	 * - type=0: User received money (amount positive for user)
	 * - type=1: User paid money (amount negative for user)
	 * 
	 * DATABASE TABLES:
	 * - db5: Transaction history
	 * - db6: Payment balances
	 * - db8: Transaction details per member
	 */
	public void Finish(String amount, String[] SII) {
	
		ArrayList<String> usedCodes = new ArrayList<>(Arrays.asList(ApiCaller.ApiCaller3("http://localhost:8080/db8/GetSpecificData?val=tID&table=" + gcode)));
		tID = createCode(usedCodes);
		if(SII[1].equals(uname)) {
			System.out.println("HI");
			if (type == 0) {
				String val = ApiCaller.ApiCaller2("http://localhost:8080/db6/UpdateData?table="+ gcode +"&where=Member1='" + uname + "'%20AND%20Member2='"+ SII[3]+"'&Amount=" + (Double.parseDouble(SII[2]) - Double.parseDouble(amount)));
				String value = ApiCaller.ApiCaller2("http://localhost:8080/db5/InsertData?table="+ gcode +"&params=(payee,amount,reason,Ttype,tid)&info=('" + SII[3] + "'," + amount + ",'" + uname + "'," + 1 + ",'" + tID + "')");
			} else {
				String val = ApiCaller.ApiCaller2("http://localhost:8080/db6/UpdateData?table="+ gcode +"&where=Member1='" + uname + "'%20AND%20Member2='"+ SII[3]+"'&Amount=" + (Double.parseDouble(SII[2]) + Double.parseDouble(amount)));
				String value = ApiCaller.ApiCaller2("http://localhost:8080/db5/InsertData?table="+ gcode +"&params=(payee,amount,reason,Ttype,tid)&info=('" + uname + "'," + amount + ",'" + SII[3] + "'," + 1 + ",'" + tID + "')");
			}
		} else {
			if (type == 1) {
				String val = ApiCaller.ApiCaller2("http://localhost:8080/db6/UpdateData?table="+ gcode +"&where=Member2='" + uname + "'%20AND%20Member1='"+ SII[1]+"'&Amount=" + (Double.parseDouble(SII[2]) - Double.parseDouble(amount)));
				String value = ApiCaller.ApiCaller2("http://localhost:8080/db5/InsertData?table="+ gcode +"&params=(payee,amount,reason,Ttype,tid)&info=('" + uname + "'," + amount + ",'" + SII[1] + "'," + 1 + ",'" + tID + "')");
			} else {
				String val = ApiCaller.ApiCaller2("http://localhost:8080/db6/UpdateData?table="+ gcode +"&where=Member2='" + uname + "'%20AND%20Member1='"+ SII[1]+"'&Amount=" + (Double.parseDouble(SII[2]) + Double.parseDouble(amount)));
				String value = ApiCaller.ApiCaller2("http://localhost:8080/db5/InsertData?table="+ gcode +"&params=(payee,amount,reason,Ttype,tid)&info=('" + SII[1] + "'," + amount + ",'" + uname + "'," + 1 + ",'" + tID + "')");
			}
			}
		
		String[][] people = ApiCaller.ApiCaller1("http://localhost:8080/db1/GetRowData?table="+ gcode);
		String params = "&params=(Creator,tID,";
		String info = "&info=('"+uname+"','"+tID+"',";
		for (int i = 2; i < people.length; i++) {
			if (people[i][1].equals(uname)) {
				params += uname + ",";
				if (type == 1){
					info += -1 * Double.parseDouble(amount) + ",";
				} else {
					info += amount + ",";
				}
			}else if (people[i][1].equals(SII[1])) {
				params += SII[1] + ",";
				if (type == 0){
					info += -1 * Double.parseDouble(amount) + ",";
				} else {
					info += amount + ",";
				}
			}else if (people[i][1].equals(SII[3])) {
				params += SII[3] + ",";
				if (type == 0){
					info += -1 * Double.parseDouble(amount) + ",";
				} else {
					info += amount + ",";
				}
			}else{
				params += people[i][1] + ",";
				info += 0 + ",";
			}

		}

		params = params.substring(0, params.length() - 1) + ")";
		info = info.substring(0, info.length() - 1) + ")";
		String val = ApiCaller.ApiCaller2("http://localhost:8080/db8/InsertData?table="+ gcode + params + info);
		
	}
	
	/**
	 * runGUI - Display the AmountSettled Window
	 * 
	 * PURPOSE:
	 * Makes the AmountSettled frame visible to the user.
	 * Final step in showing the payment entry interface.
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
	 * Called on AmountSettled instance to display its frame.
	 * 
	 * USAGE:
	 * Called by SettlePayment.java after creating AmountSettled instance.
	 * Currently used: 1 call site (SettlePayment.actionPerformed)
	 */
	public void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
	
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
	 * Called by Finish() method to generate transaction ID before
	 * recording payment settlement in database.
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
	
}

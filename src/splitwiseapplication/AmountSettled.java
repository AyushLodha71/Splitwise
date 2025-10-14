package splitwiseapplication;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

/**
 * AmountSettled - Payment Recording Dialog
 * 
 * This class handles the actual recording of a debt settlement payment.
 * It's opened from SettlePayment when a user clicks "Pay" on a selected debt.
 * 
 * User Flow:
 * 1. User views debts in SettlePayment interface
 * 2. User clicks "Pay" on a specific debt
 * 3. This dialog opens with the debt amount pre-filled
 * 4. User can adjust the amount (partial payment) or keep full amount
 * 5. Click "Finish" to record the payment
 * 
 * What This Does:
 * - Updates PendingAmount: Reduces/clears the debt amount
 * - Logs in PaymentHistory: Records as Type 1 (settlement) transaction
 * - Creates TransactionDetails: Records payment details for potential reversal
 * 
 * Complex Logic:
 * The debt can be recorded in various ways depending on:
 * - Who is the creditor vs debtor in the PendingAmount file
 * - Whether the amount is positive or negative (bilateral debt)
 * - Who initiated the payment (current user)
 * 
 * The Finish() method handles all 4 combinations:
 * 1. User is creditor, positive amount (user received payment)
 * 2. User is debtor, positive amount (user made payment)
 * 3. User is creditor, negative amount (bilateral - user made payment)
 * 4. User is debtor, negative amount (bilateral - user received payment)
 * 
 * @version 2.0 - Enhanced with comprehensive documentation and improved UX
 */
public class AmountSettled implements ActionListener{

	// Main application frame
	static JFrame frame;
	
	// Main content panel
	JPanel contentPane;
	
	// Current user and group identifiers
	static String uname;
	static String gcode;
	
	// UI prompt text and generated transaction ID
	String prompt, tID;
	
	// Selected debt information from SettlePayment
	String[] SIInfo;
	
	// UI components
	JLabel amountPrompt;
	JTextField amount;
	JButton finish,back;
	
	// File references for data updates
	File newFile, tdFile, grpFile;
	
	// Index of debt in PendingAmount file
	int i1;
	
	// Absolute value of debt amount
	double amt;
	
	/**
	 * Constructs the AmountSettled payment recording dialog.
	 * 
	 * This constructor determines the payment context and creates an appropriate
	 * interface for recording the settlement.
	 * 
	 * Payment Context Logic:
	 * The selectedItem array format is: [creditor, amount, debtor]
	 * - If amount > 0: debtor owes creditor
	 * - If amount < 0: creditor owes debtor (bilateral debt)
	 * 
	 * The prompt text changes based on who owes whom:
	 * - "Enter the amount X gave you" (you're receiving)
	 * - "Enter the amount you gave to X" (you're paying)
	 * 
	 * Pre-filled Amount:
	 * The text field is pre-filled with the full debt amount, but users
	 * can edit it to make partial payments.
	 * 
	 * @param selectedItem The debt info: [creditor, amount, debtor]
	 * @param usrname The current user recording the payment
	 * @param grpcode The group code
	 * @param index Position of this debt in PendingAmount file (for updating)
	 */
	public AmountSettled(String[] selectedItem, String usrname, String grpcode, int index) {
		
		uname = usrname;
		gcode = grpcode;
		SIInfo = selectedItem;
		i1 = index;
		
		frame = new JFrame("Splitwise - Settle payment Page");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Create main content panel with 2-column grid
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(0, 2, 10, 5));
		contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));

		// Determine payment direction and create appropriate prompt
		if (Double.parseDouble(SIInfo[1]) > 0) {
			// Positive amount: debtor owes creditor
			if(SIInfo[0].equals(usrname)) {
				// User is creditor - receiving payment
				prompt = "Enter the amount " + SIInfo[2] + " gave you";
			} else {
				// User is debtor - making payment
				prompt = "Enter the amount you gave to " + SIInfo[2];
			}
			amt = Double.parseDouble(SIInfo[1]);
		} else {
			// Negative amount: creditor owes debtor (bilateral debt)
			if(SIInfo[2].equals(usrname)) {
				// User is debtor in file, but actually receiving payment
				prompt = "Enter the amount " + SIInfo[0] + " gave you";
			} else {
				// User is creditor in file, but actually making payment
				prompt = "Enter the amount you gave to " + SIInfo[0];
			}
			amt = -1.0 * Double.parseDouble(SIInfo[1]);
		}
		
		// Create prompt label and amount input field
		amountPrompt = new JLabel(prompt);
		contentPane.add(amountPrompt);
		
		amount = new JTextField(Double.toString(amt));
		amount.setToolTipText("Enter payment amount (can be less than full debt for partial payment)");
		amount.addActionListener(this);
		amount.setActionCommand("finish");
		contentPane.add(amount);
		
		// Create action buttons
		finish = new JButton("Finish");
		finish.setToolTipText("Record this payment and update balances");
		finish.addActionListener(this);
		finish.setActionCommand("finish");
		contentPane.add(finish);
		
		back = new JButton("Back");
		back.setToolTipText("Cancel and return to debt list");
		back.addActionListener(this);
		back.setActionCommand("back");
		contentPane.add(back);
		
		frame.setContentPane(contentPane);
		frame.pack();
		frame.setVisible(false);
		
	}
	
	/**
	 * Handles button click events for the AmountSettled dialog.
	 * 
	 * Action Handling:
	 * - "Finish" Button (or Enter key):
	 *   Calls Finish() to record the payment and update all files
	 * 
	 * - "Back" Button:
	 *   Returns to MainPage without recording payment
	 * 
	 * @param event The button click event
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		if(eventName.equals("finish")) {
			// Record the payment
			Finish(amount.getText(), SIInfo);
		} else {
			// Cancel and return to dashboard
			MainPage mpage = new MainPage(uname,gcode);
			mpage.runGUI();
			frame.dispose();
		}
		
	}
	
	/**
	 * Records the settlement payment and updates all related files.
	 * 
	 * This is one of the most complex methods in the application. It must:
	 * 1. Add payment to PaymentHistory (Type 1 = settlement)
	 * 2. Update PendingAmount (reduce the debt)
	 * 3. Create TransactionDetails entry (for potential deletion/reversal)
	 * 
	 * The complexity comes from handling 4 different payment scenarios:
	 * 
	 * SCENARIO 1: Positive Amount + User is Creditor
	 * - User received payment from debtor
	 * - PaymentHistory: "debtor paid $X to user"
	 * - PendingAmount: DECREASE debt (debtor now owes less)
	 * 
	 * SCENARIO 2: Positive Amount + User is Debtor
	 * - User made payment to creditor
	 * - PaymentHistory: "user paid $X to creditor"
	 * - PendingAmount: DECREASE debt (user now owes less)
	 * 
	 * SCENARIO 3: Negative Amount + User is Debtor (in file)
	 * - Bilateral debt - user actually receiving payment
	 * - PaymentHistory: "creditor paid $X to user"
	 * - PendingAmount: INCREASE debt magnitude (moving toward positive)
	 * 
	 * SCENARIO 4: Negative Amount + User is Creditor (in file)
	 * - Bilateral debt - user actually making payment
	 * - PaymentHistory: "user paid $X to debtor"
	 * - PendingAmount: INCREASE debt magnitude (moving toward zero/positive)
	 * 
	 * TransactionDetails Format:
	 * Records payment with transaction ID for all members:
	 * "transactionID,member1Amount,member2Amount,..."
	 * - Payer's amount: positive (they paid)
	 * - Recipient's amount: negative (they received)
	 * - Other members: 0.0 (not involved)
	 * 
	 * Files Modified:
	 * 1. PaymentHistory/{groupCode} - Appends settlement record
	 * 2. PendingAmount/{groupCode} - Updates debt amount
	 * 3. TransactionDetails/{groupCode} - Records for potential reversal
	 * 
	 * @param amount The payment amount entered by user (can be partial)
	 * @param SII The selected debt info: [creditor, amount, debtor]
	 */
	public void Finish(String amount, String[] SII) {
	
		newFile = new File("src/splitwiseapplication/PendingAmount/"+gcode);
		ArrayList<String[]> PAMembers = Exists.contents(newFile, ">");
		newFile = new File("src/splitwiseapplication/PaymentHistory/"+gcode);
		tdFile = new File("src/splitwiseapplication/TransactionDetails/" + gcode);
		
		// Generate unique transaction ID
		ArrayList<String> usedCodes = Exists.exists(uname,tdFile);
		tID = createCode(usedCodes);
		
		String member2;  // The other member involved in the payment
		
		if (Double.parseDouble(SII[1]) > 0) { 
			// POSITIVE AMOUNT: Debtor owes creditor
			if(SII[0].equals(uname)) {
				// User is creditor - receiving payment from debtor
				member2 = SII[2];
				String[] data = {SII[2], amount,uname,"1",tID};
				UpdateFile.Update(data,newFile,">");
			} else {
				// User is debtor - making payment to creditor
				member2 = SII[0];
				String[] data = {uname,amount,SII[0],"1",tID};
				UpdateFile.Update(data,newFile,">");
			}
			// DECREASE debt: subtract payment from owed amount
			PAMembers.get(i1)[1] = Double.toString(Double.parseDouble(PAMembers.get(i1)[1]) - Double.parseDouble(amount));
		} else {
			// NEGATIVE AMOUNT: Bilateral debt (creditor owes debtor)
			if(SII[2].equals(uname)) {
				// User is debtor in file, but actually receiving payment
				member2 = SII[0];
				String[] data = {SII[0],amount,uname,"1",tID};
				UpdateFile.Update(data,newFile,">");
			} else {
				// User is creditor in file, but actually making payment
				member2 = SII[2];
				String[] data = {uname,amount,SII[2],"1",tID};
				UpdateFile.Update(data,newFile,">");
			}
			// INCREASE debt magnitude: add payment (moving toward less negative)
			PAMembers.get(i1)[1] = Double.toString(Double.parseDouble(PAMembers.get(i1)[1]) + Double.parseDouble(amount));
		}
		
		// Rewrite PendingAmount file with updated debt
		newFile = new File("src/splitwiseapplication/PendingAmount/"+gcode);
		
		if (PAMembers.isEmpty()) {
			return; // Safety check
		}
		
		UpdateFile.Write(PAMembers.get(0)[0], PAMembers.get(0)[1], PAMembers.get(0)[2], newFile);
		
		for (int i = 1; i < PAMembers.size(); i++) {
			UpdateFile.Update(PAMembers.get(i)[0], PAMembers.get(i)[1], PAMembers.get(i)[2], newFile);
		}
		
		// Create TransactionDetails entry for potential deletion
		grpFile = new File("src/splitwiseapplication/Groups/"+gcode);
		ArrayList<String> grpMembers = Exists.contents_STR(grpFile);
		
		String[] transaction = new String[grpMembers.size()+1];
		
		transaction[0] = tID;
		
		// Populate transaction details for each member
		for (int i = 1; i < transaction.length; i++) {
			if (grpMembers.get(i-1).equals(uname)) {
				// Current user's amount
				if (Double.parseDouble(SII[1]) > 0) { 
					if(SII[0].equals(uname)) {
						// User is creditor receiving payment (negative = received)
						transaction[i] = amount;
					} else {
						transaction[i] = Double.toString(-1*Double.parseDouble(amount));
					}
				} else {
					if(SII[2].equals(uname)) {
						transaction[i] = amount;
					} else {
						transaction[i] = Double.toString(-1*Double.parseDouble(amount));
					}
				}
			} else if (grpMembers.get(i-1).equals(member2)) {
				if (Double.parseDouble(SII[1]) > 0) { 
					if(SII[2].equals(uname)) {
						transaction[i] = amount;
					} else {
						transaction[i] = Double.toString(-1*Double.parseDouble(amount));
					}
				} else {
					if(SII[0].equals(uname)) {
						transaction[i] = amount;
					} else {
						transaction[i] = Double.toString(-1*Double.parseDouble(amount));
					}
				}
			} else {
				transaction[i] = Double.toString(0.0);
			}
		}
		
		UpdateFile.Update(transaction, tdFile, ",");
		
	}
	
	/**
	 * Displays the AmountSettled window.
	 * 
	 * Makes the payment recording dialog visible to the user.
	 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
	
	}
	
	/**
	 * Generates a unique 7-character transaction ID.
	 * 
	 * This ensures each settlement can be uniquely identified in TransactionDetails
	 * and PaymentHistory files. The ID uses alphanumeric characters (a-z, A-Z, 0-9).
	 * 
	 * Collision Avoidance:
	 * Continuously generates random codes until finding one not in codesUsed list.
	 * 
	 * @param codesUsed List of already-used transaction IDs
	 * @return A unique 7-character transaction ID
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

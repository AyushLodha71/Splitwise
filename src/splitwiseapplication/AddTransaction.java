package splitwiseapplication;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;

public class AddTransaction implements ActionListener {
	
	static JFrame frame;
	JPanel contentPane;
	JLabel amountPrompt,reasonPrompt;
	JTextField amount,reason;
	JButton backButton;
	String uname, code;
	
	public AddTransaction(String username, String gcode) {
		
		uname = username;
		code = gcode;
		
		 /* Create and set up the frame */
		 frame = new JFrame("Splitwise - Add Transaction");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 /* Create a content pane */
		 contentPane = new JPanel();
		 contentPane.setLayout(new GridLayout(0, 3, 10, 5));
		 contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		 /* Create and add label */
		 amountPrompt = new JLabel("Enter the Transaction Amount");
		 contentPane.add(amountPrompt);
		 amount = new JTextField();
		 amount.addActionListener(this);
		 amount.setActionCommand("Add Amount");
		 contentPane.add(amount);
		 
		 reasonPrompt = new JLabel("Enter the Reason for this transaction");
		 contentPane.add(reasonPrompt);
		 reason = new JTextField();
		 reason.addActionListener(this);
		 reason.setActionCommand("Reason");
		 contentPane.add(reason);
		 
		 backButton = new JButton("Back");
		 backButton.addActionListener(this);
		 backButton.setActionCommand("Back");
		 contentPane.add(backButton);
		 
		 /* Add content pane to frame */
		 frame.setContentPane(contentPane);
		 /* Size and then display the frame. */
		 frame.pack();
		 frame.setVisible(false);
	
	}
	
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		if (eventName.equals("Add Amount") == true || eventName.equals("Reason") == true) {
			String amountVal = amount.getText();
			String reasonVal = reason.getText();
			
			if (amountVal.isEmpty() || reasonVal.isEmpty()) { 
				System.out.println("Enter valid values");
			} else {
				System.out.println("Transaction added!");
			}
		} 
	}
	
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}

}

package splitwiseapplication;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

public class MainPage implements ActionListener{
	
	static JFrame frame;
	JPanel listPanel, buttonPanel;
	JList<String> transactionList;
	JScrollPane scrollPane;
	String uname,gcode;
	ArrayList<String[]> info;
	JButton addTransaction,settlePayment,checkBalances,checkAmountSpent,back,exitGroup;
	
	public MainPage(String username, String code) {
	
		uname = username;
		gcode = code;
		frame = new JFrame("Splitwise - Main Page");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/* Create a content pane */
		listPanel = new JPanel();
		listPanel.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		
		info = RetrieveTransactions(gcode);
		
		String[] data;
		if (info.size()!= 0) {
			data = new String[info.size()];
			for (int i = 0; i < info.size(); i++) {
				if (info.get(i)[3].equals("0")) {
					data[i] = info.get(i)[0] + " added a payment of "+info.get(i)[1] + " for " + info.get(i)[2];
				} else {
					data[i] = info.get(i)[0] + " paid " + info.get(i)[1] + " to " + info.get(i)[2];
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
		checkBalances.setAlignmentX(Component.CENTER_ALIGNMENT);
		checkAmountSpent.setAlignmentX(Component.CENTER_ALIGNMENT);
		back.setAlignmentX(Component.CENTER_ALIGNMENT);
		exitGroup.setAlignmentX(Component.CENTER_ALIGNMENT);

		addTransaction.addActionListener(this);
		addTransaction.setActionCommand("addTransaction");
		settlePayment.addActionListener(this);
		settlePayment.setActionCommand("settlePayment");
		checkBalances.addActionListener(this);
		checkBalances.setActionCommand("checkBalances");
		checkAmountSpent.addActionListener(this);
		checkAmountSpent.setActionCommand("checkAmountSpent");
		
		
        // Add some vertical space between buttons
        buttonPanel.add(Box.createVerticalStrut(10)); // Top padding
        buttonPanel.add(addTransaction);
        buttonPanel.add(Box.createVerticalStrut(10)); // Space between buttons
        buttonPanel.add(settlePayment);
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
		} else if (eventName.equals("checkBalances")) {
			CheckBalances cBalances = new CheckBalances(uname,gcode);
			cBalances.runGUI();
			frame.dispose();
		} else if (eventName.equals("checkAmountSpent")) {
			CheckAmountSpent cAmtSpent = new CheckAmountSpent(uname,gcode);
			cAmtSpent.runGUI();
			frame.dispose();
		}
		
	}
	
	public static ArrayList<String[]> RetrieveTransactions(String c) {
		
		FileReader in;
		BufferedReader readFile;
		String line;
		File textFile;
		ArrayList<String[]> transactionlst = new ArrayList<String[]>();
		int location;
		
		textFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PaymentHistory\\"+c);
		
		try {
			in = new FileReader(textFile);
			readFile = new BufferedReader(in);
			while ((line = readFile.readLine()) != null ) {
				String[] myArray = line.split(">");
				transactionlst.add(myArray);
			}
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException: "
					+ e.getMessage());
		} catch (IOException e) {;
			System.err.println("IOException: " + e.getMessage());
		}
		
		return (transactionlst);
		
	}
	
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}

}

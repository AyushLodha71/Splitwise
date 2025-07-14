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
	JButton addTransaction,settlePayment, deleteTransaction,checkBalances,checkAmountSpent,back,exitGroup;
	File fileLoc;
	
	public MainPage(String username, String code) {
	
		uname = username;
		gcode = code;
		frame = new JFrame("Splitwise - Main Page");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/* Create a content pane */
		listPanel = new JPanel();
		listPanel.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		
		fileLoc = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PaymentHistory\\" + gcode);
		info = Exists.contents(fileLoc,">");
		
		String[] data;
		if (info.size()!= 0) {
			data = new String[info.size()];
			for (int i = 0; i < info.size(); i++) {
				if (info.get(i)[3].equals("0")) {
					data[i] = info.get(i)[0] + " added a payment of $"+info.get(i)[1] + " for " + info.get(i)[2];
				} else if (info.get(i)[3].equals("1")){
					data[i] = info.get(i)[0] + " paid $" + info.get(i)[1] + " to " + info.get(i)[2];
				}else if (info.get(i)[3].equals("2")){
					data[i] = info.get(i)[0] + " left the group";
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
	
	public Boolean exitEligibility() {
		
		File newFileU = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PendingAmount\\" + gcode);
		ArrayList<String[]> records = Exists.contents(newFileU, ">");
		
		for (int i = 1; i < records.size(); i++) {
			String[] row = records.get(i);
			if (row[0].equals(uname) || row[2].equals(uname)) {
				if (Double.parseDouble(row[1]) != 0) {
					return false;
				}
			}
		}
		
		return true;
		
	}
	
	public void DeleteRecords() {
		
		DeletePA();
		DeleteCAS();
		DeleteGrp();
		DeletePF();
		File newFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PaymentHistory\\"+gcode);
		String[] data = {uname,"","","2"};
		UpdateFile.Update(data,newFile,">");
		
	}
	
	public void DeletePA() {
		
		File newFilePA = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PendingAmount\\" + gcode);
		ArrayList<String[]> optionsPA = Exists.contents(newFilePA, ">");
		
		UpdateFile.Write(optionsPA.get(0)[0], optionsPA.get(0)[1], optionsPA.get(0)[2], newFilePA);
		for (int i = 1; i < optionsPA.size(); i++) {
			String[] row = optionsPA.get(i);
			if (!(row[0].equals(uname) || row[2].equals(uname))) {
				UpdateFile.Update(optionsPA.get(i)[0], optionsPA.get(i)[1], optionsPA.get(i)[2], newFilePA);
			}
		}
		
	}
	
	public void DeleteCAS() {
		
		File newFileCAS = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\CheckAmountSpentFolder\\" + gcode);
		ArrayList<String[]> optionsCAS = Exists.contents(newFileCAS, ",");
		
		UpdateFile.Write(optionsCAS.get(0)[0], optionsCAS.get(0)[1], newFileCAS);
		for (int i = 1; i < optionsCAS.size(); i++) {
			String[] row = optionsCAS.get(i);
			if (!(row[0].equals(uname))) {
				UpdateFile.Update(optionsCAS.get(i)[0], optionsCAS.get(i)[1], newFileCAS);
			}
		}
		
	}
	
	public void DeleteGrp() {
		
		File newFileGrp = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\Groups\\" + gcode);
		ArrayList<String> optionsGrp = Exists.contents_STR(newFileGrp);
		String row = optionsGrp.get(0);
		
		if (!(row.equals(uname))) {
			UpdateFile.Write(row, newFileGrp);
		}
		
		for (int i = 1; i < optionsGrp.size(); i++) {
			row = optionsGrp.get(i);
			if (!(row.equals(uname))) {
				UpdateFile.Update(optionsGrp.get(i), newFileGrp);
			}
		}
		
	}
	
	public void DeletePF() {
		
		File newFileGrp = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\groups.txt");
		ArrayList<String[]> optionsGrp = Exists.contents(newFileGrp, ",");
		String gName = "";
		
		for (int i = 0; i < optionsGrp.size(); i++) {
			String[] row = optionsGrp.get(i);
			if ((row[0].equals(gcode))) {
				gName = row[1];
			}
		}
		
				
		File newFilePF = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\Personal_Folders\\" + uname);
		ArrayList<String> optionsPF = Exists.contents_STR(newFilePF);
		
		String row = optionsPF.get(0);
		
		
		if (!(row.equals(gName))) {
			UpdateFile.Write(row, newFilePF);
		} else {
			UpdateFile.WriteBlank(newFilePF);
		}
		
		for (int i = 1; i < optionsPF.size(); i++) {
			row = optionsPF.get(i);
			if (!(row.equals(gName))) {
				UpdateFile.Update(optionsPF.get(i), newFilePF);
			}
		}
		
	}

	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}

}

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
	JLabel amountPrompt,reasonPrompt,splitTypePrompt,response,blank;
	JTextField amount,reason;
	JComboBox options;
	JCheckBox nameEQS;
	JTextField nameU, nameBP;
	JButton backButton, enter;
	String uname, code;
	ArrayList<Integer> chosenEQS = new ArrayList<Integer>();
	ArrayList<JTextField> chosenU = new ArrayList<JTextField>();
	ArrayList<JTextField> chosenBP = new ArrayList<JTextField>();
	ArrayList<Double> valuesU = new ArrayList<Double>();
	String amountVal = "0";
	String reasonVal;
	
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
		 amount.setActionCommand("Add Amount");
		 contentPane.add(amount);
		 
		 reasonPrompt = new JLabel("Enter the Reason for this transaction");
		 contentPane.add(reasonPrompt);
		 reason = new JTextField();
		 reason.addActionListener(this);
		 reason.setActionCommand("Reason");
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
		 
		 blank = new JLabel("");
		 contentPane.add(blank);
		 
		 response = new JLabel("");
		 contentPane.add(response);
		 
		 /* Add content pane to frame */
		 frame.setContentPane(contentPane);
		 /* Size and then display the frame. */
		 frame.pack();
		 frame.setVisible(false);
	
	}
	
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		File txtFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\Groups\\"+code);
		ArrayList<String> members = Exists.contents_STR(txtFile);
		ArrayList<String[]> POI = new ArrayList<String[]>();
		
		if (eventName.equals("Add Amount") == true || eventName.equals("Reason") == true) {
			amountVal = amount.getText();
			reasonVal = reason.getText();
			
			if (amountVal.isEmpty() || reasonVal.isEmpty()) { 
				response.setText("Enter valid values");
			} else {
				File newFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PaymentHistory\\"+code);
				UpdateFile.Update(uname, "$" + amountVal,reasonVal,newFile);
				if (options.getSelectedItem() == "All Equally"){
					UpdatePAEqually(Integer.parseInt(amountVal));
					response.setText("Transaction added");
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
						nameBP = new JTextField(members.get(i));
						chosenBP.add(nameBP);
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
						nameU = new JTextField(members.get(i));
						chosenU.add(nameU);
						contentPane.add(nameU);
					}
					enter = new JButton("Enter");
					enter.addActionListener(this);
					enter.setActionCommand("BP");
					contentPane.add(enter);
					frame.setContentPane(contentPane);
					frame.pack();
				}
				amount.setActionCommand("Amount Added");
				reason.setActionCommand("Reason Provided");
				options.setActionCommand("Selection Confirmed");
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
			File newFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PendingAmount\\"+code);
			ArrayList<String[]> PAMembers = Exists.contents(newFile);	
			UpdatePAEqBySome(Integer.parseInt(amountVal), PAMembers, chosenEQS);
		}  else if(eventName.equals("U") == true) {
			double sum = 0;
			for (int i = 0; i < chosenU.size(); i++) {
				JTextField nameU = chosenU.get(i); // Get each JTextField from the list
	            double text = Double.parseDouble(nameU.getText()); // Retrieve the text from the current text field
	            sum += text;
	            valuesU.add(text);
			}
			if (Double.parseDouble(amountVal) - sum <= 0.1 && Double.parseDouble(amountVal) - sum >= -0.1) {
				UpdatePAUnequally(Integer.parseInt(amountVal), valuesU);
			}
		} else if(eventName.equals("BP") == true) {
			double sum = 0;
			for (int i = 0; i < chosenU.size(); i++) {
				JTextField nameU = chosenU.get(i); // Get each JTextField from the list
	            double text = Double.parseDouble(nameU.getText()); // Retrieve the text from the current text field
	            sum += text;
	            valuesU.add(text);
			}
			if (100 - sum <= 0.1 && 100 - sum >= -0.1) {
				UpdatePAByPercentages(Integer.parseInt(amountVal), valuesU);
			}
		}
		
	}
	
	public void UpdatePAEqually (int cost) {
		
		File newFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PendingAmount\\"+code);
		ArrayList<String[]> members = Exists.contents(newFile);
		double costPP = (double)cost/(double)members.size();
		
		if (members.get(0)[0].equals(uname)) {
			members.get(0)[1] = Double.toString(Double.parseDouble(members.get(0)[1]) + cost - costPP);
		} else {
			members.get(0)[1] = Double.toString(Double.parseDouble(members.get(0)[1]) - costPP);
		}
		
		UpdateFile.Write(members.get(0)[0], members.get(0)[1], newFile);
		
		for (int i = 1; i < members.size(); i++) {
			if (members.get(i)[0].equals(uname)) {
				members.get(i)[1] = Double.toString(Double.parseDouble(members.get(i)[1]) + cost - costPP);
			} else {
				members.get(i)[1] = Double.toString(Double.parseDouble(members.get(i)[1]) - costPP);
			}
			UpdateFile.Update(members.get(i)[0], members.get(i)[1], newFile);
		}
		
	}
	
	public void UpdatePAEqBySome (int cost, ArrayList<String[]> lst, ArrayList<Integer> selected) {
		
		File newFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PendingAmount\\"+code);
		
		int sum = 0;
		
		for (int i : selected) {
			sum += i;
		}
		
		double costPP = (double)cost/(double)sum;

		if (lst.get(0)[0].equals(uname) && selected.get(0) == 1) {
			lst.get(0)[1] = Double.toString(Double.parseDouble(lst.get(0)[1]) + cost - costPP);
		} else if (lst.get(0)[0].equals(uname) && selected.get(0) == 0) {
			lst.get(0)[1] = Double.toString(Double.parseDouble(lst.get(0)[1]) + cost);
		} else if(selected.get(0) == 1){
			lst.get(0)[1] = Double.toString(Double.parseDouble(lst.get(0)[1]) - costPP);
		}
		
		UpdateFile.Write(lst.get(0)[0], lst.get(0)[1], newFile);
		
		for (int i = 1; i < lst.size(); i++) {
			if (lst.get(i)[0].equals(uname) && selected.get(i) == 1) {
				lst.get(i)[1] = Double.toString(Double.parseDouble(lst.get(i)[1]) + cost - costPP);
			} else if (lst.get(i)[0].equals(uname) && selected.get(i) == 0) {
				lst.get(i)[1] = Double.toString(Double.parseDouble(lst.get(i)[1]) + cost);
			} else if(selected.get(i) == 1){
				lst.get(i)[1] = Double.toString(Double.parseDouble(lst.get(i)[1]) - costPP);
			}
			UpdateFile.Update(lst.get(i)[0], lst.get(i)[1], newFile);
		}
		
	}
	
	public void UpdatePAUnequally(int cost, ArrayList<Double> amounts) {
		
		File newFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PendingAmount\\"+code);
		ArrayList<String[]> members = Exists.contents(newFile);
		
		if (members.get(0)[0].equals(uname)) {
			members.get(0)[1] = Double.toString(Double.parseDouble(members.get(0)[1]) + cost - amounts.get(0));
		} else {
			members.get(0)[1] = Double.toString(Double.parseDouble(members.get(0)[1]) - amounts.get(0));
		}
		
		UpdateFile.Write(members.get(0)[0], members.get(0)[1], newFile);
		
		for (int i = 1; i < members.size(); i++) {
			if (members.get(i)[0].equals(uname)) {
				members.get(i)[1] = Double.toString(Double.parseDouble(members.get(i)[1]) + cost - amounts.get(i));
			} else {
				members.get(i)[1] = Double.toString(Double.parseDouble(members.get(i)[1]) - amounts.get(i));
			}
			UpdateFile.Update(members.get(i)[0], members.get(i)[1], newFile);
		}
		
	}
	
	public void UpdatePAByPercentages(int cost, ArrayList<Double> percentages) {
		
		File newFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PendingAmount\\"+code);
		ArrayList<String[]> members = Exists.contents(newFile);
		
		if (members.get(0)[0].equals(uname)) {
			members.get(0)[1] = Double.toString(Double.parseDouble(members.get(0)[1]) + (double)cost*(1.0 - (double)percentages.get(0)/100.0));
		} else {
			members.get(0)[1] = Double.toString(Double.parseDouble(members.get(0)[1]) - (double)cost*(double)percentages.get(0)/100.0);
		}
		
		UpdateFile.Write(members.get(0)[0], members.get(0)[1], newFile);
		
		for (int i = 1; i < members.size(); i++) {
			if (members.get(i)[0].equals(uname)) {
				members.get(i)[1] = Double.toString(Double.parseDouble(members.get(i)[1]) + (double)cost*(1.0 - (double)percentages.get(i)/100.0));
			} else {
				members.get(i)[1] = Double.toString(Double.parseDouble(members.get(i)[1]) - (double)cost*(double)percentages.get(i)/100.0);
			}
			UpdateFile.Update(members.get(i)[0], members.get(i)[1], newFile);
		}
		
	}
	
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}
}

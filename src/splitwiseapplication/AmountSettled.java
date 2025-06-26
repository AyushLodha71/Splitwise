package splitwiseapplication;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;

public class AmountSettled implements ActionListener{

	static JFrame frame;
	JPanel contentPane;
	static String uname;
	static String gcode;
	String prompt;
	String[] SIInfo;
	JLabel amountPrompt;
	JTextField amount;
	JButton finish,back;
	File newFile;
	int i1;
	double amt;
	
	public AmountSettled(String[] selectedItem, String usrname, String grpcode, int index) {
		
		uname = usrname;
		gcode = grpcode;
		SIInfo = selectedItem;
		i1 = index;
		
		frame = new JFrame("Splitwise - Settle payment Page");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/* Create a content pane */
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(0, 2, 10, 5));
		contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));

		if (Double.parseDouble(SIInfo[1]) > 0) {
			if(SIInfo[0].equals(usrname)) {
				prompt = "Enter the amount " + SIInfo[2] + " gave you";
			} else {
				prompt = "Enter the amount you gave to " + SIInfo[2];
			}
			amt = Double.parseDouble(SIInfo[1]);
		} else {
			if(SIInfo[2].equals(usrname)) {
				prompt = "Enter the amount " + SIInfo[2] + " gave you";
			} else {
				prompt = "Enter the amount you gave to " + SIInfo[2];
			}
			amt = -1.0 * Double.parseDouble(SIInfo[1]);
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
	
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		if(eventName.equals("finish")) {
			Finish(amount.getText(), SIInfo);
		} else {
			MainPage mpage = new MainPage(uname,gcode);
			mpage.runGUI();
			frame.dispose();
		}
		
	}
	
	public void Finish(String amount, String[] SII) {
	
		newFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PendingAmount\\"+gcode);
		ArrayList<String[]> PAMembers = Exists.contents(newFile, ">");
		System.out.println(i1 + " " + PAMembers.get(i1)[0] + PAMembers.get(i1)[1] + PAMembers.get(i1)[2]);
		if (Double.parseDouble(SII[1]) > 0) {
			if(SII[0].equals(uname)) {
				newFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PaymentHistory\\"+gcode);
				UpdateFile.Update(SII[2], "$" + amount,uname,"1",newFile);
			} else {
				newFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PaymentHistory\\"+gcode);
				UpdateFile.Update(uname, "$" + amount,SII[2],"1",newFile);
			}
			PAMembers.get(i1)[1] = Double.toString(Double.parseDouble(PAMembers.get(i1)[1]) - Double.parseDouble(amount));
		} else {
			if(SII[2].equals(uname)) {
				newFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PaymentHistory\\"+gcode);
				UpdateFile.Update(SII[2], "$" + amount,uname,"1",newFile);
			} else {
				newFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PaymentHistory\\"+gcode);
				UpdateFile.Update(uname, "$" + amount,SII[2],"1",newFile);
			}
			PAMembers.get(i1)[1] = Double.toString(Double.parseDouble(PAMembers.get(i1)[1]) + Double.parseDouble(amount));
		}
		
		newFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PendingAmount\\"+gcode);
		UpdateFile.Write(PAMembers.get(0)[0], PAMembers.get(0)[1], PAMembers.get(0)[2], newFile);
		
		for (int i = 1; i < PAMembers.size(); i++) {
			UpdateFile.Update(PAMembers.get(i)[0], PAMembers.get(i)[1], PAMembers.get(i)[2], newFile);
		}
		
	}
	
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
	
	}
	
}

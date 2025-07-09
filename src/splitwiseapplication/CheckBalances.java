package splitwiseapplication;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;

public class CheckBalances implements ActionListener{
	
	static JFrame frame;
	JPanel contentPane;
	String uname,gcode;
	ArrayList<Member_Info> info = new ArrayList<Member_Info>();
	Member_Info addMember;
	ArrayList<String[]> PACombinations;
	ArrayList<String> members;
	ArrayList<String> memberValues = new ArrayList<String>();
	JButton mmbrb, back;
	ArrayList<JLabel> memberLabels = new ArrayList<JLabel>();
	JLabel mmbrl;
	
	public CheckBalances(String usrname, String grpcode) {
		
		uname = usrname;
		gcode = grpcode;
		
		frame = new JFrame("Splitwise - Check Balances Page");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 500);
		/* Create a content pane */
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(0, 1, 10, 5));
		contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		
		members = Exists.contents_STR(new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\Groups\\"+gcode));
		
		for (String i: members) {
			addMember = new Member_Info(i);
			info.add(addMember);
			mmbrb = new JButton(i);
			mmbrb.addActionListener(this);
			mmbrb.setActionCommand(i);
			memberValues.add(i);
			mmbrl = new JLabel("");
			memberLabels.add(mmbrl);
			contentPane.add(mmbrb);
			contentPane.add(mmbrl);
		}
		
		back = new JButton("Back");
		back.addActionListener(this);
		back.setActionCommand("Back");
		contentPane.add(back);
		
		PACombinations = Exists.contents(new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PendingAmount\\"+gcode),">");
		
		for (int i = 1; i < PACombinations.size(); i++) {
			String[] t = PACombinations.get(i);
				if (Double.parseDouble(t[1]) > 0) {
					info.get(members.indexOf(t[0])).addMembers(t[2] + " owes " + t[1]);
					info.get(members.indexOf(t[2])).addMembers("You owe " + t[1] + " to " + t[0]);
				} else if (Double.parseDouble(t[1]) < 0) {
					double newVal = -1.0 * Double.parseDouble(t[1]);
					info.get(members.indexOf(t[2])).addMembers(t[0] + " owes " + newVal);
					info.get(members.indexOf(t[0])).addMembers("You owe " + newVal + " to " + t[2]);
				}
		}
		
		frame.setContentPane(contentPane);
		frame.setVisible(false);
		
	}
	
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		if (eventName.equals("Back")) {
			MainPage mpage = new MainPage(uname,gcode);
			mpage.runGUI();
			frame.dispose();
		} else {
			int t = memberValues.indexOf(eventName);
			String lText = memberLabels.get(t).getText();
			
			if (lText.equals("")) {
				memberLabels.get(t).setText(getString(info.get(t)));
			} else {
				memberLabels.get(memberValues.indexOf(eventName)).setText("");
			}
		}
		
	}
	
	public String getString(Member_Info pinformation) {
		
		String returnString = "<html>";
		
		for (String i: pinformation.getMembers()) {
			returnString += "• " + i + "<br>";
		}

		returnString += "</html>";
		
		return returnString;
		
	}
	
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}

}

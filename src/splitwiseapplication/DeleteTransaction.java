package splitwiseapplication;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

public class DeleteTransaction implements ActionListener{

	static JFrame frame;
	JPanel listPanel, buttonPanel;
	JList<String> transactionList;
	JScrollPane scrollPane;
	String uname,gcode;
	ArrayList<String[]> info;
	JButton delete,back;
	ArrayList<String[]> options = new ArrayList<String[]>();
	File fileLoc;
	
	public DeleteTransaction(String username, String code) {
	
		uname = username;
		gcode = code;
		frame = new JFrame("Splitwise - Settle Payment");
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
				} else {
					data[i] = info.get(i)[0] + " paid $" + info.get(i)[1] + " to " + info.get(i)[2];
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
		
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        // Add some buttons to the buttonPanel
		delete = new JButton("Delete");
		back = new JButton("Back");
		delete.addActionListener(this);
		delete.setActionCommand("delete");
		back.addActionListener(this);
		back.setActionCommand("back");
		
		buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(delete);
        buttonPanel.add(back);
        
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15,0,20,50));
		 
		 /* Add content pane to frame */
		 frame.add(listPanel,BorderLayout.WEST);
		 frame.add(buttonPanel,BorderLayout.SOUTH);
		 
		 frame.pack();
		 frame.setVisible(false);
		 
	}
		
	public void actionPerformed(ActionEvent event) {
			
		String eventName = event.getActionCommand();
		
		if (eventName.equals("delete")) {
			int sIndex = transactionList.getSelectedIndex();
			String[] transactionInfo = info.get(sIndex);
			Repay(transactionInfo);
			info.remove(sIndex);
			UpdateFile.Write(info.get(0), fileLoc, ">");
			for (int i = 1; i < info.size(); i++) {
				UpdateFile.Update(info.get(i), fileLoc, ">");
			}
		}
		
		MainPage mpage = new MainPage(uname,gcode);
		mpage.runGUI();
		frame.dispose();
	
	}
	
	public void Repay(String[] tInfo) {
		
		File newFileU = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PendingAmount\\" + gcode);
		ArrayList<String[]> records = Exists.contents(newFileU, ">");
		File newFileCAS = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\CheckAmountSpentFolder\\" + gcode);
		ArrayList<String[]> optionsCAS = Exists.contents(newFileCAS, ",");
		File newFileG = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\Groups\\" + gcode);
		ArrayList<String> groupMembers = Exists.contents_STR(newFileG);
		File newFileTD = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\TransactionDetails\\" + gcode);
		String[] td = Exists.exists_Array(tInfo[4],newFileTD);
		
		int t;
		
		UpdateFile.Write(records.get(0)[0], records.get(0)[1], records.get(0)[2], newFileU);
		
		if (Integer.parseInt(tInfo[3]) == 0) {
			for (int i = 1; i < records.size(); i++) {
				String[] row = records.get(i);
				if (containsValue(row,tInfo[0])) {
					if (row[2].equals(tInfo[0])) {
						t = groupMembers.indexOf(row[0]);
						records.get(i)[1] = Double.toString(Double.parseDouble(records.get(i)[1]) + Double.parseDouble(td[t+1]));
					} else {
						t = groupMembers.indexOf(row[2]);
						records.get(i)[1] = Double.toString(Double.parseDouble(records.get(i)[1]) - Double.parseDouble(td[t+1]));
					}
				}
				UpdateFile.Update(records.get(i)[0], records.get(i)[1], records.get(i)[2], newFileU);
			}
			
			UpdateFile.Write(optionsCAS.get(0)[0], optionsCAS.get(0)[1], newFileCAS);
			for (int i = 1; i < optionsCAS.size(); i++) {
				optionsCAS.get(i)[1] = Double.toString(Double.parseDouble(optionsCAS.get(i)[1]) - Double.parseDouble(td[i]));
				UpdateFile.Update(optionsCAS.get(i)[0], optionsCAS.get(i)[1], newFileCAS);
			}
		} else {
			for (int i = 1; i < records.size(); i++) {
				String[] row = records.get(i);
				if (containsValue(row,tInfo[0]) && containsValue(row,tInfo[2])) {
					if (row[0].equals(tInfo[0])) {
						records.get(i)[1] = Double.toString(Double.parseDouble(records.get(i)[1]) - Double.parseDouble(tInfo[1]));
					} else {
						records.get(i)[1] = Double.toString(Double.parseDouble(records.get(i)[1]) + Double.parseDouble(tInfo[1]));
					}
				}
				UpdateFile.Update(records.get(i)[0], records.get(i)[1], records.get(i)[2], newFileU);
			}
		}
		
	}
	
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
	
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
	
	}
	
}

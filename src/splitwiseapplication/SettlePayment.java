package splitwiseapplication;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

public class SettlePayment implements ActionListener{

	static JFrame frame;
	JPanel listPanel, buttonPanel;
	JList<String> transactionList;
	JScrollPane scrollPane;
	String uname,gcode;
	ArrayList<String[]> info;
	JButton pay,back;
	ArrayList<String[]> options = new ArrayList<String[]>();
	
	public SettlePayment(String username, String code) {
	
		uname = username;
		gcode = code;
		frame = new JFrame("Splitwise - Settle Payment");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/* Create a content pane */
		listPanel = new JPanel();
		listPanel.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		
		info = RetrievePendingAmount(gcode, uname);
		
		String[] data;
		if (options.size()!= 0) {
			data = new String[options.size()];
			for (int i = 0; i < options.size(); i++) {
				if (options.get(i)[0].equals(uname)) {
					if (Double.parseDouble(options.get(i)[1]) > 0) {
						data[i] = options.get(i)[2] + " needs to give you $" + options.get(i)[1];
					} else if (Double.parseDouble(options.get(i)[1]) < 0){
						double val = Double.parseDouble(options.get(i)[1]) * -1.0;
						data[i] = "You need to give $" + val + " to " + options.get(i)[2];
					}
				} else {
					if (Double.parseDouble(options.get(i)[1]) > 0) {
						data[i] = "You need to give $" + options.get(i)[1] + " to " + options.get(i)[0];
					} else if (Double.parseDouble(options.get(i)[1]) < 0){
						double val = Double.parseDouble(options.get(i)[1]) * -1.0;
						data[i] = options.get(i)[0] + " needs to give you $" + val;
					}
				}
			}
		} else {
			data = new String[1];
			data[0]="No transactions Yet";
		}
		
		transactionList = new JList<>(data);
		
		scrollPane = new JScrollPane(transactionList);
		scrollPane.setPreferredSize(new Dimension(500, 200));
		listPanel.add(scrollPane);
		
		buttonPanel = new JPanel();
		
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        // Add some buttons to the buttonPanel
		pay = new JButton("Pay");
		back = new JButton("Back");
		pay.addActionListener(this);
		pay.setActionCommand("pay");
		back.addActionListener(this);
		back.setActionCommand("back");
		
		buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(pay);
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
		
		if (eventName.equals("pay")) {
			int selectedItem = transactionList.getSelectedIndex();
			AmountSettled as = new AmountSettled(options.get(selectedItem), uname, gcode, info.indexOf(options.get(selectedItem)));
			as.runGUI();
			frame.dispose();
		} else if (eventName.equals("back")){
			MainPage mpage = new MainPage(uname,gcode);
			mpage.runGUI();
			frame.dispose();
		}
	
	}
		
	public ArrayList<String[]> RetrievePendingAmount(String c, String usnm) {
			
		FileReader in;
		BufferedReader readFile;
		String line;
		File textFile;
		ArrayList<String[]> transactionlst = new ArrayList<String[]>();
		int location;
		
		textFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PendingAmount\\"+c);
		
		try {
			in = new FileReader(textFile);
			readFile = new BufferedReader(in);
			while ((line = readFile.readLine()) != null ) {
				String[] myArray = line.split(">");
				transactionlst.add(myArray);
				if(myArray[0].equals(usnm) || myArray[2].equals(usnm)) {
					options.add(myArray);
				}
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

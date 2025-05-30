package splitwiseapplication;

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

public class EnterGroup implements ActionListener{

	static JFrame frame;
	JPanel contentPane;
	JLabel optionsPrompt;
	JComboBox options;
	JButton backButton, groupEnter;
	ArrayList<String> groups = new ArrayList<String>();
	String uname;
	String rcode = "";
	
	public EnterGroup(String username) {
		
		uname = username;
		frame = new JFrame("Splitwise - Create Group");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/* Create a content pane */
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(0, 3, 10, 5));
		contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		 
		options = new JComboBox();
		groups = Read(username);
		groups.add(0, "Select Group" );
		for (int i = 0; i < groups.size(); i++) {
			options.addItem(groups.get(i));
		}
		
		options.addActionListener(this);
		options.setActionCommand("First");
		contentPane.add(options);
		
		backButton = new JButton("Back");
		backButton.addActionListener(this);
		backButton.setActionCommand("Back");
		contentPane.add(backButton);
		
		frame.setContentPane(contentPane);
		/* Size and then display the frame. */
		frame.pack();
		frame.setVisible(false);
		
	}

	public static ArrayList<String> Read(String usrname) {
			
			FileReader in;
			BufferedReader readFile;
			String usrnme;
			File textFile;
			ArrayList<String> grouplist = new ArrayList<String>();
			
			//Creating a personal room for the username
			textFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\Personal_Folders\\"+usrname);
			
			try {
				in = new FileReader(textFile);
				readFile = new BufferedReader(in);
				while ((usrnme = readFile.readLine()) != null ) {
					String[] myArray = usrnme.split(",");
					grouplist.add(myArray[0]);
				}
				readFile.close();
				in.close();
			} catch (FileNotFoundException e) {
				System.err.println("FileNotFoundException: "
						+ e.getMessage());
			} catch (IOException e) {;
				System.err.println("IOException: " + e.getMessage());
			}
			
			Sorts.mergesort(grouplist, 0, grouplist.size()-1);
			
			return grouplist;
			
		}
	
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		String selectedItem = "";
		
		if (eventName.equals("Back") == true) {
			Groups groups = new Groups(uname);
			groups.runGUI();
			frame.dispose();
		} else if (eventName.equals("First") == true){
			selectedItem = (String) options.getSelectedItem();
			rcode = RetrieveCode(selectedItem);
			groupEnter = new JButton("Enter " + selectedItem + " Group");
			groupEnter.addActionListener(this);
			groupEnter.setActionCommand("Enter");
			contentPane.add(groupEnter);
			options.setActionCommand("Later");
			frame.setContentPane(contentPane);
			frame.pack();
		} else if (eventName.equals("Later") == true) {
			selectedItem = (String) options.getSelectedItem();
			rcode = RetrieveCode(selectedItem);
			groupEnter.setText("Enter " + selectedItem + " Group");
		} else if(eventName.equals("Enter") == true) {
			MainPage mpage = new MainPage(uname,rcode);
			mpage.runGUI();
			frame.dispose();
		}
		
	}
	
	public static String RetrieveCode(String n) {
		
		FileReader in;
		BufferedReader readFile;
		String line;
		File textFile;
		ArrayList<String> usrnamelst = new ArrayList<String>();
		int location;
		
		textFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\groups.txt");
		
		try {
			in = new FileReader(textFile);
			readFile = new BufferedReader(in);
			while ((line = readFile.readLine()) != null ) {
				String[] myArray = line.split(",");
				if (myArray[1].equals(n)) {
					return myArray[0];
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException: "
					+ e.getMessage());
		} catch (IOException e) {;
			System.err.println("IOException: " + e.getMessage());
		}
		
		return (n);
		
	}

	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}
	
}

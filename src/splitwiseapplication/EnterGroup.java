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
			textFile = new File("/Users/macbookpro/Desktop/Java/splitwiseApplication/src/splitwiseapplication/Personal_Folders/"+usrname);
			
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
		
		if (eventName.equals("Back") == true) {
			Groups groups = new Groups(uname);
			groups.runGUI();
			frame.dispose();
		} else if (eventName.equals("First") == true){
			String selectedItem = (String) options.getSelectedItem();
			groupEnter = new JButton("Enter " + selectedItem + " Group");
			contentPane.add(groupEnter);
			options.setActionCommand("Later");
			frame.setContentPane(contentPane);
			frame.pack();
		} else if (eventName.equals("Later") == true) {
			String selectedItem = (String) options.getSelectedItem();
			groupEnter.setText("Enter " + selectedItem + " Group");
		}
		
	}
	
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}
	
}

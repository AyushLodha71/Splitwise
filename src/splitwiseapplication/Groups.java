package splitwiseapplication;

import java.awt.GridLayout;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

public class Groups implements ActionListener{		
	static JFrame frame;
	JPanel contentPane;
	JLabel enterPrompt,joinPrompt, createPrompt;
	JComboBox options;
	JTextField join, create;
	ArrayList<String> groups = new ArrayList<String>();
	
	public Groups(String username) {
		
		 /* Create and set up the frame */
		 frame = new JFrame("Login Or Register");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 /* Create a content pane */
		 contentPane = new JPanel();
		 contentPane.setLayout(new GridLayout(0, 2, 10, 5));
		 contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		 /* Create and add label */
		 enterPrompt = new JLabel("Choose a group to enter");
		 contentPane.add(enterPrompt);
		 options = new JComboBox();
		 groups = Read(username);
		 groups.add("None");
		 
		 for (int i = 0; i < groups.size(); i++) {
			 options.addItem(groups.get(i));
		 }
		 
		 options.addActionListener(this);
		 contentPane.add(options);
		 joinPrompt = new JLabel("Enter code to join a group");
		 contentPane.add(joinPrompt);
		 join = new JTextField("Group Code");
		 contentPane.add(join);
		 createPrompt = new JLabel("Enter the name of group to make a new one");
		 contentPane.add(createPrompt);
		 create = new JTextField("Group Name");
		 contentPane.add(create);
		 
		 /* Add content pane to frame */
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
		
	}

		 /**
		 * Create and show the GUI.
		 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}
	
}

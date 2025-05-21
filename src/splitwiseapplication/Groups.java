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
	JLabel optionsPrompt;
	JComboBox options;
	ArrayList<String> groups = new ArrayList<String>();
	String uname;
	
	public Groups(String username) {
		
		 uname = username;
		 /* Create and set up the frame */
		 frame = new JFrame("Splitwise - Groups");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 /* Create a content pane */
		 contentPane = new JPanel();
		 contentPane.setLayout(new GridLayout(0, 2, 10, 5));
		 contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		 /* Create and add label */
		 optionsPrompt = new JLabel("Choose an option to proceed");
		 contentPane.add(optionsPrompt);
		 options = new JComboBox();
		 options.addItem("Select Item");
		 options.addItem("Enter Group");
		 options.addItem("Join Group");
		 options.addItem("Create Group");
		 options.addItem("Log out");
		 options.addActionListener(this);
		 contentPane.add(options);
		 
		 /* Add content pane to frame */
		 frame.setContentPane(contentPane);
		 /* Size and then display the frame. */
		 frame.pack();
		 frame.setVisible(false);
	
	}
	
	public void actionPerformed(ActionEvent event) {
		
		String selectedItem = (String) options.getSelectedItem();
		
		if (selectedItem.equals("Enter Group")) {
			EnterGroup egroup = new EnterGroup(uname);
			egroup.runGUI();
			frame.dispose();
		} else if (selectedItem.equals("Join Group")) {
			JoinGroup jgroup = new JoinGroup(uname);
			jgroup.runGUI();
			frame.dispose();
		} else if (selectedItem.equals("Create Group")) {
			CreateGroup cgroup = new CreateGroup(uname);
			cgroup.runGUI();
			frame.dispose();
		} else if (selectedItem.equals("Log out")){
			LoginOrRegister loginorRegisterGUI = new LoginOrRegister();
			loginorRegisterGUI.runGUI();
		}
		
	}

		 /**
		 * Create and show the GUI.
		 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}
	
}

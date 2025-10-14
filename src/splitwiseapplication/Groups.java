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

/**
 * Groups - Main Navigation Hub
 * 
 * This class provides the primary navigation interface after successful login.
 * It serves as the central hub where users can access all major features:
 * - Enter an existing group they belong to
 * - Join a group using an invitation code
 * - Create a new group and invite members
 * - Log out of the application
 * 
 * The interface uses a simple dropdown menu for action selection,
 * providing clear and straightforward navigation to all core features.
 * 
 * This is typically the first page users see after logging in or registering.
 * 
 * @version 2.0 - Enhanced with comprehensive documentation and improved UX
 */
public class Groups implements ActionListener{
	
	// Main application frame
	static JFrame frame;
	
	// UI container panel
	JPanel contentPane;
	
	// Prompt label for user guidance
	JLabel optionsPrompt;
	
	// Dropdown menu for action selection
	JComboBox options;
	
	// List of user's groups (not currently used in this class)
	ArrayList<String> groups = new ArrayList<String>();
	
	// Current logged-in username
	String uname;
	
	/**
	 * Constructor - Builds the Main Navigation Interface
	 * 
	 * Creates a simple dropdown menu interface with four main options:
	 * 1. Enter Group - Access an existing group the user belongs to
	 * 2. Join Group - Join a new group using an invitation code
	 * 3. Create Group - Create a new group and invite members
	 * 4. Log out - Exit the application and return to login screen
	 * 
	 * @param username The logged-in user's username
	 */
	public Groups(String username) {
		
		 uname = username;
		 
		 /* Setup main frame */
		 frame = new JFrame("Splitwise - Groups");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 
		 /* Create content pane with 2-column grid layout */
		 contentPane = new JPanel();
		 contentPane.setLayout(new GridLayout(0, 2, 10, 5));
		 contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		 
		 /* Add prompt label */
		 optionsPrompt = new JLabel("Choose an option to proceed");
		 contentPane.add(optionsPrompt);
		 
		 /* Create dropdown menu with all navigation options */
		 options = new JComboBox();
		 options.setToolTipText("Select an action from the dropdown");
		 options.addItem("Select Item");
		 options.addItem("Enter Group");
		 options.addItem("Join Group");
		 options.addItem("Create Group");
		 options.addItem("Log out");
		 options.addActionListener(this);
		 contentPane.add(options);
		 
		 /* Finalize frame setup */
		 frame.setContentPane(contentPane);
		 frame.pack();
		 frame.setVisible(false);
	
	}
	
	/**
	 * Event Handler - Processes dropdown selection
	 * 
	 * Routes the user to the appropriate page based on their selection:
	 * - "Enter Group" -> EnterGroup page (select from user's existing groups)
	 * - "Join Group" -> JoinGroup page (enter invitation code)
	 * - "Create Group" -> CreateGroup page (create new group)
	 * - "Log out" -> LoginOrRegister page (logout and return to login screen)
	 * 
	 * @param event The action event from dropdown selection
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		
		String selectedItem = (String) options.getSelectedItem();
		
		if ("Enter Group".equals(selectedItem)) {
			// Navigate to group selection page
			EnterGroup egroup = new EnterGroup(uname);
			egroup.runGUI();
			frame.dispose();
			
		} else if ("Join Group".equals(selectedItem)) {
			// Navigate to join group page
			JoinGroup jgroup = new JoinGroup(uname);
			jgroup.runGUI();
			frame.dispose();
			
		} else if ("Create Group".equals(selectedItem)) {
			// Navigate to create group page
			CreateGroup cgroup = new CreateGroup(uname);
			cgroup.runGUI();
			frame.dispose();
			
		} else if ("Log out".equals(selectedItem)){
			// Log out and return to login/register screen
			LoginOrRegister loginorRegisterGUI = new LoginOrRegister();
			loginorRegisterGUI.runGUI();
			frame.dispose();
		}
		
	}

	/**
	 * Display the Groups Navigation GUI
	 * 
	 * Makes the groups navigation frame visible to the user.
	 * Should be called after constructing a Groups object.
	 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}
	
}

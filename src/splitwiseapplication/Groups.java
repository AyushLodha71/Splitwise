package splitwiseapplication;

import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * Main group management screen shown after successful login/registration.
 * 
 * Purpose:
 * This GUI presents a dropdown menu with group-related operations that authenticated
 * users can perform. It serves as the main hub for all group interactions.
 * 
 * Available operations:
 * 1. Enter Group - Access an existing group you're a member of
 * 2. Join Group - Join an existing group using a group code
 * 3. Create Group - Create a new group and become its creator
 * 4. Log out - Return to login/register screen
 * 
 * Navigation context:
 * Users arrive here from:
 * - LoginPageGUI (after successful login)
 * - RegisterPageGUI (after successful registration)
 * - Back from EnterGroup/JoinGroup/CreateGroup screens
 * - Back from MainPage (group screen)
 * 
 * User flow:
*	 		LoginPageGUI/RegisterPageGUI
						↓
				Groups (this screen)
						↓
				┌─────┼─────┬─────┐
				↓     ↓     ↓     ↓
			  Enter Join  Create Logout → LoginOrRegister
			  Group Group Group
				↓     ↓     ↓   
				└─────┴─────┘
					  ↓
					MainPage
 * 
 * Usage locations (9 call sites):
 * - LoginPageGUI: After successful authentication (2 calls)
 * - RegisterPageGUI: After account creation (2 calls)
 * - EnterGroup: Back button navigation (1 call)
 * - JoinGroup: Back button navigation (1 call)
 * - CreateGroup: After group creation (1 call)
 * - MainPage: Back button from group view (2 calls)
 */
public class Groups implements ActionListener{
	
	static JFrame frame;
	JPanel contentPane;
	JLabel optionsPrompt;
	JComboBox options;
	String uname;
	
	/**
	 * Constructs the Groups management screen for an authenticated user.
	 * 
	 * Contract:
	 * - Requires username of authenticated user
	 * - Creates dropdown menu with 4 group operations + logout
	 * - Window is created but hidden (call runGUI() to display)
	 * 
	 * @param username  The authenticated user's username (passed from login/register)
	 * 
	 * UI structure:
	 * +----------------------------------+
	 * | Choose an option to proceed      |
	 * | [Select Item ▼]                  |
	 * |   - Enter Group                  |
	 * |   - Join Group                   |
	 * |   - Create Group                 |
	 * |   - Log out                      |
	 * +----------------------------------+
	 * 
	 * Post-conditions:
	 * - uname field stores the username
	 * - frame configured with dropdown menu
	 * - Window packed but hidden
	 */
	public Groups(String username) {
		
		 uname = username;
		 frame = new JFrame("Splitwise - Groups");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 contentPane = new JPanel();
		 contentPane.setLayout(new GridLayout(0, 2, 10, 5));
		 contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
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
		 
		 frame.setContentPane(contentPane);
		 frame.pack();
		 frame.setVisible(false);
	
	}
	
	/**
	 * Handles dropdown menu selection events.
	 * 
	 * Contract:
	 * - Called when user selects an item from the dropdown
	 * - Routes to appropriate screen based on selection
	 * - Disposes current window after navigation
	 * 
	 * @param event  The ActionEvent from dropdown selection
	 * 
	 * Behavior based on selection:
	 * 
	 * "Enter Group":
	 * - Opens EnterGroup screen
	 * - User can select from their existing groups
	 * - Navigates to MainPage for selected group
	 * 
	 * "Join Group":
	 * - Opens JoinGroup screen
	 * - User enters group code to join existing group
	 * - Adds user to group members if code is valid
	 * 
	 * "Create Group":
	 * - Opens CreateGroup screen
	 * - User creates new group with name
	 * - User becomes group creator and first member
	 * 
	 * "Log out":
	 * - Returns to LoginOrRegister screen
	 * - Ends current user session
	 * - No data is persisted locally (all in backend)
	 * 
	 * "Select Item":
	 * - Default/placeholder option
	 * - No action taken (handled implicitly by no else clause)
	 * 
	 * Navigation pattern:
	 * - Always disposes current window before opening next
	 * - New window instance created for each navigation
	 */
	@Override
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
			frame.dispose();
		}
		
	}

	/**
	 * Makes the Groups window visible to the user.
	 * 
	 * Contract:
	 * - Must be called after constructor to display the window
	 * - Sets default look and feel decoration
	 * - Makes the frame visible on screen
	 * 
	 * Usage pattern:
	 * 1. Create instance: Groups groups = new Groups(username)
	 * 2. Show window: groups.runGUI()
	 * 
	 * Note:
	 * - Instance method accessing static frame field
	 * - Must be called on Groups instance
	 * - Frame is shared across all instances (static field)
	 * 
	 * Called from (9 locations):
	 * - LoginPageGUI: After successful login
	 * - RegisterPageGUI: After registration
	 * - EnterGroup/JoinGroup/CreateGroup: Back navigation
	 * - MainPage: Back navigation
	 */
	public void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}
	
}

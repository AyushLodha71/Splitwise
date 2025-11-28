package splitwiseapplication;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;

/**
 * EnterGroup - Group Selection Screen for Entering Existing Groups
 * 
 * PURPOSE:
 * Provides a GUI screen where users can select one of their existing groups
 * and enter it to view group details, transactions, and balances.
 * This is the navigation point between the Groups menu and the MainPage.
 * 
 * FUNCTIONALITY:
 * - Displays a dropdown list of all groups the user belongs to
 * - Retrieves group list from database (db7 - user's group memberships)
 * - Allows user to select a group and confirm entry
 * - Fetches the group code for the selected group
 * - Navigates to MainPage with the selected group's code
 * - Provides back navigation to Groups menu
 * 
 * USER FLOW:
 * 1. User sees dropdown with "Select Group" and their group names
 * 2. User selects a group from dropdown
 * 3. "Enter [GroupName] Group" button appears
 * 4. User clicks button to enter the group
 * 5. Application navigates to MainPage for that specific group
 * 
 * NAVIGATION:
 * - Entry Point: Groups.java (when user selects "Enter Group")
 * - Exit Points: 
 *   - MainPage.java (when entering a group)
 *   - Groups.java (when clicking Back)
 * 
 * DATABASE INTERACTIONS:
 * - db7: Fetches user's group memberships (username table)
 * - db3.GroupNames: Fetches group_code for selected group name
 * 
 * UI COMPONENTS:
 * - JComboBox for group selection dropdown
 * - JButton for "Back" navigation
 * - JButton for "Enter [GroupName] Group" (appears after selection)
 * - GridLayout with 3 columns
 * 
 * DESIGN PATTERN:
 * - Implements ActionListener for event handling
 * - Static frame pattern (single shared JFrame instance)
 * - Constructor builds UI, runGUI() displays it
 * 
 * @author Original Development Team
 */
public class EnterGroup implements ActionListener{

	static JFrame frame;
	JPanel contentPane;
	JComboBox options;
	JButton backButton, groupEnter;
	ArrayList<String> groups = new ArrayList<String>();
	String uname;
	String[] rcode;
	
	/**
	 * Constructor - Initialize EnterGroup GUI
	 * 
	 * PURPOSE:
	 * Creates the group selection interface for a specific user.
	 * Builds all UI components and retrieves user's group list from database.
	 * 
	 * BEHAVIOR:
	 * - Creates new JFrame with title "Splitwise - Enter Group"
	 * - Sets up GridLayout with 3 columns
	 * - Fetches user's groups via Read() method (db7 query)
	 * - Populates dropdown with "Select Group" + all user groups
	 * - Creates Back button for navigation
	 * - Configures frame but keeps it invisible (runGUI() displays it)
	 * 
	 * UI LAYOUT:
	 * Initially shows only:
	 * [Dropdown] [Back Button] [Empty]
	 * 
	 * After group selection:
	 * [Dropdown] [Back Button] [Enter GroupName Button]
	 * 
	 * @param username The username of the logged-in user
	 */
	public EnterGroup(String username) {
		
		uname = username;
		frame = new JFrame("Splitwise - Enter Group");
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

	/**
	 * Read - Fetch User's Group List from Database
	 * 
	 * PURPOSE:
	 * Retrieves all group names that the specified user belongs to.
	 * Queries the database for user's group memberships.
	 * 
	 * BEHAVIOR:
	 * - Queries db7 database with username as table name
	 * - Extracts group names from column index 1 of returned data
	 * - Skips row 0 (assumed to be headers)
	 * - Returns ArrayList of group names
	 * 
	 * DATABASE QUERY:
	 * API: GET http://localhost:8080/db7/GetRowData?table=[username]
	 * Response: 2D String array where each row contains group information
	 * Column 1 (index [i][1]): Group name
	 * 
	 * ALGORITHM:
	 * 1. Call ApiCaller1 to get all rows for user
	 * 2. Loop through rows starting from index 1 (skip header row)
	 * 3. Extract group name from column 1
	 * 4. Add to ArrayList
	 * 5. Return completed list
	 * 
	 * USAGE:
	 * Called by constructor to populate the dropdown menu.
	 * Currently used: 1 call site (EnterGroup constructor)
	 * 
	 * @param usrname The username whose groups should be retrieved
	 * @return ArrayList of group names the user belongs to
	 */
	public static ArrayList<String> Read(String usrname) {
			
		String[][] lst = ApiCaller.ApiCaller1("http://localhost:8080/db7/GetRowData?table="+ usrname);
		ArrayList<String> values = new ArrayList<String>();
			
			for (int i = 1; i < lst.length; i++) {
				values.add(lst[i][1]);
			}
			
			return values;
			
		}
	
	/**
	 * actionPerformed - Handle User Actions (Button Clicks, Dropdown Selection)
	 * 
	 * PURPOSE:
	 * Event handler for all user interactions in the EnterGroup screen.
	 * Manages navigation, group selection, and entry confirmation.
	 * 
	 * EVENT TYPES:
	 * 
	 * 1. "Back" - Navigate back to Groups menu
	 *    - Creates new Groups instance
	 *    - Shows Groups screen
	 *    - Disposes current frame
	 * 
	 * 2. "First" - Initial group selection from dropdown
	 *    - Gets selected group name from dropdown
	 *    - Fetches group_code from db3.GroupNames table
	 *    - Creates "Enter [GroupName] Group" button
	 *    - Adds button to UI
	 *    - Changes dropdown action command to "Later" (for subsequent selections)
	 * 
	 * 3. "Later" - Subsequent group selections (after first selection)
	 *    - Gets newly selected group name
	 *    - Fetches updated group_code from database
	 *    - Updates button text to match new selection
	 *    - Doesn't recreate button (just updates text)
	 * 
	 * 4. "Enter" - Confirm entry into selected group
	 *    - Creates MainPage with username and group code
	 *    - Shows MainPage screen
	 *    - Disposes current frame
	 * 
	 * DATABASE QUERIES:
	 * - db3.GroupNames: Fetches group_code for selected group name
	 * 
	 * DESIGN PATTERN:
	 * Uses action command strings to differentiate button/dropdown events.
	 * "First" vs "Later" distinction prevents creating duplicate Enter buttons.
	 * 
	 * @param event The ActionEvent containing the action command
	 */
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		String selectedItem = "";
		
		if (eventName.equals("Back") == true) {
			Groups groups = new Groups(uname);
			groups.runGUI();
			frame.dispose();
		} else if (eventName.equals("First") == true){
			selectedItem = (String) options.getSelectedItem();
			rcode = ApiCaller.ApiCaller3("http://localhost:8080/db3/GetSpecificData?val=group_code&table=GroupNames&group_name=" + selectedItem);
			groupEnter = new JButton("Enter " + selectedItem + " Group");
			groupEnter.addActionListener(this);
			groupEnter.setActionCommand("Enter");
			contentPane.add(groupEnter);
			options.setActionCommand("Later");
			frame.setContentPane(contentPane);
			frame.pack();
		} else if (eventName.equals("Later") == true) {
			selectedItem = (String) options.getSelectedItem();
			rcode = ApiCaller.ApiCaller3("http://localhost:8080/db3/GetSpecificData?val=group_code&table=GroupNames&group_name=" + selectedItem);
			groupEnter.setText("Enter " + selectedItem + " Group");
		} else if(eventName.equals("Enter") == true) {
			MainPage mpage = new MainPage(uname,rcode[0]);
			mpage.runGUI();
			frame.dispose();
		}
		
	}

	/**
	 * runGUI - Display the EnterGroup Window
	 * 
	 * PURPOSE:
	 * Makes the EnterGroup frame visible to the user.
	 * Final step in showing the group selection interface.
	 * 
	 * BEHAVIOR:
	 * - Enables default window decorations (title bar, close button, etc.)
	 * - Makes the instance frame visible
	 * 
	 * DESIGN PATTERN:
	 * Follows the two-step initialization pattern used throughout the app:
	 * 1. Constructor builds the UI (but keeps frame invisible)
	 * 2. runGUI() displays the frame
	 * 
	 * This separation allows for UI setup before display.
	 * 
	 * INSTANCE METHOD:
	 * Called on EnterGroup instance to display its frame.
	 * 
	 * USAGE:
	 * Called by Groups.java after creating EnterGroup instance.
	 * Currently used: 1 call site (Groups.actionPerformed)
	 */
	public void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}
	
}

package splitwiseapplication;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;

/**
 * JoinGroup - Screen for Joining Existing Groups Using Group Code
 * 
 * PURPOSE:
 * Provides a GUI interface where users can join existing groups by entering
 * a group code. Validates the code and adds the user to all relevant database
 * tables for the group.
 * 
 * FUNCTIONALITY:
 * - Accepts group code input from user
 * - Validates that:
 *   1. The group code exists in the system
 *   2. The user is not already a member of that group
 * - Adds user to group across multiple databases (db1, db4, db6, db7, db8)
 * - Sets up permissions and relationships with existing group members
 * - Allows immediate entry into the newly joined group
 * - Provides back navigation to Groups menu
 * 
 * USER FLOW:
 * 1. User enters a group code in text field
 * 2. User presses Enter to submit
 * 3. System validates code
 * 4. If valid: Shows "Success!" message and "Enter" button
 * 5. If invalid: Shows "Wrong Code" error
 * 6. User can click Enter to go to MainPage or Back to return
 * 
 * VALIDATION LOGIC:
 * - Group code must exist in db3.GroupNames
 * - User must NOT already be in db4.[GroupCode] table
 * - Uses Exists.exist() for both checks
 * 
 * DATABASE OPERATIONS (AddNewUser):
 * When user joins a group, the system:
 * - db4: Adds user to group members table
 * - db8: Creates new column for user in expense tracking
 * - db8: Initializes user's balance to 0
 * - db7: Adds group entry to user's group list
 * - db1: Creates initial balance record (0) for user in group
 * - db6: Sets up payment permissions with all existing members
 * 
 * NAVIGATION:
 * - Entry Point: Groups.java (when user selects "Join Group")
 * - Exit Points:
 *   - MainPage.java (when entering newly joined group)
 *   - Groups.java (when clicking Back)
 * 
 * UI COMPONENTS:
 * - JTextField for group code input
 * - JButton for Back navigation
 * - JLabel for success/error messages
 * - JButton for Enter (appears after successful join)
 * - GridLayout with 3 columns
 * 
 * DESIGN PATTERN:
 * - Implements ActionListener for event handling
 * - Static frame pattern (single shared JFrame instance)
 * - Constructor builds UI, runGUI() displays it
 * 
 * @author Original Development Team
 */
public class JoinGroup implements ActionListener {

	static JFrame frame;
	JPanel contentPane;
	JLabel joinPrompt, success;
	JTextField joinCode;
	JButton enterButton,backButton;
	ArrayList<String> groups = new ArrayList<String>();
	static String uname;
	
	/**
	 * Constructor - Initialize JoinGroup GUI
	 * 
	 * PURPOSE:
	 * Creates the group joining interface for a specific user.
	 * Builds a simple form with text field for entering group code.
	 * 
	 * BEHAVIOR:
	 * - Creates new JFrame with title "Splitwise - Join Group"
	 * - Sets up GridLayout with 3 columns
	 * - Creates label prompting for group code
	 * - Creates text field for code input (Enter key submits)
	 * - Creates Back button for navigation
	 * - Configures frame but keeps it invisible (runGUI() displays it)
	 * 
	 * UI LAYOUT:
	 * Initially shows:
	 * [Label: "Enter the group code to join"] [TextField] [Back Button]
	 * 
	 * After valid code submission:
	 * [Label] [TextField] [Back Button]
	 * [Success!] [Enter Button] [Empty]
	 * 
	 * After invalid code:
	 * [Label] [TextField] [Back Button]
	 * [Wrong Code] [Empty] [Empty]
	 * 
	 * @param username The username of the logged-in user attempting to join a group
	 */
	public JoinGroup(String username) {
		
		uname = username;
		
		 /* Create and set up the frame */
		 frame = new JFrame("Splitwise - Join Group");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 /* Create a content pane */
		 contentPane = new JPanel();
		 contentPane.setLayout(new GridLayout(0, 3, 10, 5));
		 contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		 /* Create and add label */
		 joinPrompt = new JLabel("Enter the group code to join");
		 contentPane.add(joinPrompt);
		 joinCode = new JTextField();
		 joinCode.addActionListener(this);
		 joinCode.setActionCommand("Join Group");
		 contentPane.add(joinCode);
		 
		 backButton = new JButton("Back");
		 backButton.addActionListener(this);
		 backButton.setActionCommand("Back");
		 contentPane.add(backButton);
		 
		 /* Add content pane to frame */
		 frame.setContentPane(contentPane);
		 /* Size and then display the frame. */
		 frame.pack();
		 frame.setVisible(false);
	
	}
	
	/**
	 * actionPerformed - Handle User Actions (Text Field Enter, Button Clicks)
	 * 
	 * PURPOSE:
	 * Event handler for all user interactions in the JoinGroup screen.
	 * Manages code validation, group joining, and navigation.
	 * 
	 * EVENT TYPES:
	 * 
	 * 1. "Enter" - Navigate to the newly joined group
	 *    - Gets group code from text field
	 *    - Creates MainPage with username and group code
	 *    - Shows MainPage screen
	 *    - Disposes current frame
	 * 
	 * 2. "Join Group" - Validate and join group (triggered by Enter key in text field)
	 *    - Gets group code from text field
	 *    - Validates code using two checks:
	 *      a) Group exists: db3.GroupNames contains the code
	 *      b) Not already member: db4.[GroupCode] doesn't contain username
	 *    - If valid:
	 *      - Calls AddNewUser() to add user to all group tables
	 *      - Shows "Success!" label
	 *      - Creates "Enter" button to join the group
	 *      - Changes text field action to "Group Joined" (prevents resubmission)
	 *    - If invalid:
	 *      - Shows "Wrong Code" error label
	 * 
	 * 3. "Back" - Navigate back to Groups menu
	 *    - Creates new Groups instance
	 *    - Shows Groups screen
	 *    - Disposes current frame
	 * 
	 * VALIDATION QUERIES:
	 * - db3.GroupNames: Check if group code exists
	 * - db4.[GroupCode]: Check if user already member
	 * 
	 * ERROR HANDLING:
	 * Shows "Wrong Code" if:
	 * - Group code doesn't exist
	 * - User is already a member of that group
	 * 
	 * @param event The ActionEvent containing the action command
	 */
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		if (eventName.equals("Enter") == true) {
			String code = joinCode.getText();
			MainPage mpage = new MainPage(uname,code);
			mpage.runGUI();
			frame.dispose();
		} else if ((eventName.equals("Join Group") == true)){
			String code = joinCode.getText();
			
			if (Exists.exist("https://splitwise.up.railway.app/db3/GetRowData?table=GroupNames&group_code=" + code) && Exists.exist("https://splitwise.up.railway.app/db4/GetRowData?table="+code+"&name="+uname) == false) {
				AddNewUser(code,uname);
				success = new JLabel("Success!");
				enterButton = new JButton("Enter");
				joinCode.setActionCommand("Group Joined");
				enterButton.addActionListener(this);
				enterButton.setActionCommand("Enter");
				contentPane.add(success);
				contentPane.add(enterButton);
				frame.setContentPane(contentPane);
				frame.pack();
			} else {
				JLabel displayError = new JLabel("Wrong Code");
				contentPane.add(displayError);
				frame.setContentPane(contentPane);
				frame.pack();
			}
		} else if (eventName.equals("Back") == true) {
			Groups groups = new Groups(uname);
			groups.runGUI();
			frame.dispose();
		}
		
	}

	/**
	 * AddNewUser - Add User to All Group-Related Database Tables
	 * 
	 * PURPOSE:
	 * Performs all necessary database operations to fully integrate a new user
	 * into an existing group. Updates multiple databases to track memberships,
	 * balances, and permissions.
	 * 
	 * BEHAVIOR:
	 * Executes a series of database operations in specific order:
	 * 
	 * 1. AddPerms(code) - Set up payment permissions with existing members
	 * 2. db4: Add user to group members table
	 * 3. Fetch group name from db3 for user's group list
	 * 4. db8: Add column for user in expense tracking table
	 * 5. db8: Initialize all expense values to 0 for new user
	 * 6. db7: Add group to user's personal group list
	 * 7. db1: Create initial balance record (0) for user in group
	 * 
	 * DATABASE OPERATIONS DETAIL:
	 * - db4.[GroupCode]: INSERT (name) VALUES (username) - Add to members
	 * - db3.GroupNames: SELECT group_name WHERE group_code = code - Get name
	 * - db8.[GroupCode]: ALTER TABLE ADD COLUMN username - Track expenses
	 * - db8.[GroupCode]: UPDATE SET username=0 WHERE 1=1 - Initialize balances
	 * - db7.[Username]: INSERT (GroupID, GroupName) - Add to user's list
	 * - db1.[GroupCode]: INSERT (Name, Amount) VALUES (username, 0) - Balance tracking
	 * 
	 * PERMISSION SETUP:
	 * Calls AddPerms() which creates bidirectional payment records in db6
	 * between the new user and all existing group members.
	 * 
	 * USAGE:
	 * Called by actionPerformed() after successful code validation.
	 * Currently used: 1 call site (JoinGroup.actionPerformed)
	 * 
	 * @param code The group code of the group to join
	 * @param usrname The username of the user joining the group
	 */
	public static void AddNewUser(String code, String usrname) {
		
		String[] gname;
		
		AddPerms(code);
		
		ApiCaller.ApiCaller1("https://splitwise.up.railway.app/db4/InsertData?table="+code+"&params=(name)&info=('" + uname + "')");
		
		gname = ApiCaller.ApiCaller3("https://splitwise.up.railway.app/db3/GetSpecificData?val=group_name&table=GroupNames&group_code=" + code);
		
		ApiCaller.ApiCaller1("https://splitwise.up.railway.app/db8/AddColumn?table="+code+"&uname="+usrname);

		ApiCaller.ApiCaller1("https://splitwise.up.railway.app/db8/UpdateData?table="+code+"&where=1=1&"+uname+"=0");
		ApiCaller.ApiCaller1("https://splitwise.up.railway.app/db7/InsertData?table="+uname+"&params=(GroupID,GroupName)&info=('" + code + "','" + gname[0] + "')");
		
		ApiCaller.ApiCaller1("https://splitwise.up.railway.app/db1/InsertData?table="+code+"&params=(Name,Amount)&info=('" + uname + "',0)");
		
	}
	
	/**
	 * AddPerms - Set Up Payment Permissions Between New User and Existing Members
	 * 
	 * PURPOSE:
	 * Creates bidirectional payment permission records in db6 between the newly
	 * joining user and all existing group members. This allows tracking of who
	 * owes money to whom.
	 * 
	 * BEHAVIOR:
	 * 1. Fetches all existing members from db4.[GroupCode] table
	 * 2. Extracts member names from column index 1
	 * 3. For each existing member:
	 *    - Creates a permission record: (ExistingMember, 0, NewUser)
	 *    - Initializes amount to 0 (no debt initially)
	 * 
	 * DATABASE OPERATIONS:
	 * - db4.[GroupCode]: SELECT * - Get all members
	 * - db6.[GroupCode]: INSERT (Member1, Amount, Member2) VALUES (existing, 0, newUser)
	 *   - Creates records for each existing member
	 *   - Member1 = existing member
	 *   - Amount = 0 (initial balance)
	 *   - Member2 = newly joining user
	 * 
	 * ALGORITHM:
	 * 1. Query db4 to get 2D array of members (row 0 = headers)
	 * 2. Create String array sized for data rows only (length-1)
	 * 3. Loop from row 1 to end, extracting names from column 1
	 * 4. For each name, insert permission record in db6
	 * 
	 * PERMISSION STRUCTURE:
	 * Each record represents a potential debt relationship.
	 * The Amount field will be updated when transactions occur.
	 * 
	 * USAGE:
	 * Called by AddNewUser() as the first step in group joining.
	 * Currently used: 1 call site (JoinGroup.AddNewUser)
	 * 
	 * @param gcode The group code for which to set up permissions
	 */
	public static void AddPerms(String gcode) {

		String[][] lst = ApiCaller.ApiCaller1("https://splitwise.up.railway.app/db4/GetRowData?table="+ gcode);
		String[] values = new String[lst.length-1];
		
		for (int i = 1; i < lst.length; i++) {
			values[i-1] = lst[i][1];
		}
		for (String i : values) {
			ApiCaller.ApiCaller1("https://splitwise.up.railway.app/db6/InsertData?table="+gcode+"&params=(Member1,Amount,Member2)&info=('" + i + "',0,'" + uname + "')");
		}
	}
	

	/**
	 * runGUI - Display the JoinGroup Window
	 * 
	 * PURPOSE:
	 * Makes the JoinGroup frame visible to the user.
	 * Final step in showing the group joining interface.
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
	 * Called on JoinGroup instance to display its frame.
	 * 
	 * USAGE:
	 * Called by Groups.java after creating JoinGroup instance.
	 * Currently used: 1 call site (Groups.actionPerformed)
	 */
	public void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}

	
}

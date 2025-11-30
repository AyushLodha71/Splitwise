package splitwiseapplication;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import java.util.Random;

/**
 * CreateGroup - Screen for Creating New Groups with Auto-Generated Codes
 * 
 * PURPOSE:
 * Provides a GUI interface where users can create new groups by entering
 * a group name. Automatically generates a unique 7-character group code
 * and sets up all necessary database tables and records for the new group.
 * 
 * FUNCTIONALITY:
 * - Accepts group name input from user
 * - Generates unique 7-character alphanumeric group code
 * - Validates code uniqueness against existing groups
 * - Creates all necessary database tables (5 tables across db1, db4, db5, db6, db8)
 * - Adds group to system-wide group registry (db3.GroupNames)
 * - Adds creator as first member
 * - Displays generated code to user
 * - Allows immediate entry into the newly created group
 * - Provides back navigation to Groups menu
 * 
 * USER FLOW:
 * 1. User enters a group name in text field
 * 2. User presses Enter to submit
 * 3. System generates unique 7-character code
 * 4. System creates all database tables and records
 * 5. Screen displays: "The group code is: [CODE]"
 * 6. "Enter" button appears
 * 7. User can share code with others or click Enter to use group
 * 
 * CODE GENERATION:
 * - Uses alphanumeric characters (A-Z, a-z, 0-9)
 * - Length: 7 characters
 * - Validates uniqueness via db3.group_list
 * - Regenerates if collision detected
 * 
 * DATABASE TABLES CREATED:
 * - db4.[GroupCode]: Group members table
 * - db5.[GroupCode]: Payment history/transactions table
 * - db6.[GroupCode]: Payment permissions/debts table
 * - db1.[GroupCode]: Balance tracking table
 * - db8.[GroupCode]: Expense splitting/tracking table
 * 
 * INITIAL RECORDS:
 * - db3.GroupNames: Adds (group_name, group_code) entry
 * - db4.[GroupCode]: Adds creator as first member
 * - db7.[Username]: Adds group to creator's group list
 * - db1.[GroupCode]: Creates "Total" and creator balance records (both 0)
 * 
 * NAVIGATION:
 * - Entry Point: Groups.java (when user selects "Create Group")
 * - Exit Points:
 *   - MainPage.java (when entering newly created group)
 *   - Groups.java (when clicking Back)
 * 
 * UI COMPONENTS:
 * - JTextField for group name input
 * - JButton for Back navigation
 * - JLabel for displaying generated code
 * - JButton for Enter (appears after successful creation)
 * - GridLayout with 3 columns
 * 
 * DESIGN PATTERN:
 * - Implements ActionListener for event handling
 * - Static frame pattern (single shared JFrame instance)
 * - Constructor builds UI, runGUI() displays it
 * 
 * @author Original Development Team
 */
public class CreateGroup implements ActionListener{

	static JFrame frame;
	JPanel contentPane;
	JLabel createPrompt,displayCode;
	JTextField createGroup;
	JButton enterButton,backButton;
	ArrayList<String> groups = new ArrayList<String>();
	String uname, code;
	
	/**
	 * Constructor - Initialize CreateGroup GUI
	 * 
	 * PURPOSE:
	 * Creates the group creation interface for a specific user.
	 * Builds a simple form with text field for entering group name.
	 * 
	 * BEHAVIOR:
	 * - Creates new JFrame with title "Splitwise - Create Group"
	 * - Sets up GridLayout with 3 columns
	 * - Creates label prompting for group name
	 * - Creates text field for name input (Enter key submits)
	 * - Creates Back button for navigation
	 * - Configures frame but keeps it invisible (runGUI() displays it)
	 * 
	 * UI LAYOUT:
	 * Initially shows:
	 * [Label: "Enter the name of the new group"] [TextField] [Back Button]
	 * 
	 * After group creation:
	 * [Label] [TextField] [Back Button]
	 * [Label: "The group code is: XXXXXXX"] [Enter Button] [Empty]
	 * 
	 * @param username The username of the logged-in user creating the group
	 */
	public CreateGroup(String username) {
		
		uname = username;
		
		 /* Create and set up the frame */
		 frame = new JFrame("Splitwise - Create Group");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 /* Create a content pane */
		 contentPane = new JPanel();
		 contentPane.setLayout(new GridLayout(0, 3, 10, 5));
		 contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		 /* Create and add label */
		 createPrompt = new JLabel("Enter the name of the new group");
		 contentPane.add(createPrompt);
		 createGroup = new JTextField();
		 createGroup.addActionListener(this);
		 createGroup.setActionCommand("Create New Group");
		 contentPane.add(createGroup);
		 
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
	 * Event handler for all user interactions in the CreateGroup screen.
	 * Manages group creation, code generation, and navigation.
	 * 
	 * EVENT TYPES:
	 * 
	 * 1. "Enter" - Navigate to the newly created group
	 *    - Gets group code from stored variable
	 *    - Creates MainPage with username and group code
	 *    - Shows MainPage screen
	 *    - Disposes current frame
	 * 
	 * 2. "Create New Group" - Create new group (triggered by Enter key in text field)
	 *    - Gets group name from text field
	 *    - Calls createCode() to generate unique 7-character code
	 *    - Calls AddNewGroup() to create all database tables and records
	 *    - Displays generated code to user
	 *    - Creates "Enter" button to join the group
	 *    - Changes text field action to "Group Created" (prevents re-creation)
	 * 
	 * 3. "Back" - Navigate back to Groups menu
	 *    - Creates new Groups instance
	 *    - Shows Groups screen
	 *    - Disposes current frame
	 * 
	 * WORKFLOW:
	 * User enters name → Code generated → Tables created → Code displayed → User can enter
	 * 
	 * @param event The ActionEvent containing the action command
	 */
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		if (eventName.equals("Enter") == true) {
			String gname = createGroup.getText();
			MainPage mpage = new MainPage(uname,code);
			mpage.runGUI();
			frame.dispose();
		} else if ((eventName.equals("Create New Group") == true)){
			String text = createGroup.getText();
			
			code = createCode();
			
			AddNewGroup(code,text);
			
			displayCode = new JLabel("The group code is: " + code);
			enterButton = new JButton("Enter");
			createGroup.setActionCommand("Group Created");
			enterButton.addActionListener(this);
			enterButton.setActionCommand("Enter");
			contentPane.add(displayCode);
			contentPane.add(enterButton);
			frame.setContentPane(contentPane);
			frame.pack();
			
		} else if (eventName.equals("Back") == true) {
			Groups groups = new Groups(uname);
			groups.runGUI();
			frame.dispose();
		}
		
	}

	/**
	 * createCode - Generate Unique 7-Character Alphanumeric Group Code
	 * 
	 * PURPOSE:
	 * Creates a random, unique group code for identifying a new group.
	 * Ensures no collisions with existing group codes.
	 * 
	 * BEHAVIOR:
	 * - Generates random 7-character string from alphanumeric characters
	 * - Validates uniqueness against db3.group_list
	 * - Regenerates if collision detected (do-while loop)
	 * - Returns unique code when found
	 * 
	 * CHARACTER SET:
	 * "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
	 * - 26 uppercase letters
	 * - 26 lowercase letters
	 * - 10 digits
	 * - Total: 62 possible characters per position
	 * - Total combinations: 62^7 = 3,521,614,606,208 (3.5+ trillion)
	 * 
	 * ALGORITHM:
	 * 1. Initialize empty string
	 * 2. Loop 7 times:
	 *    - Generate random index (0 to 61)
	 *    - Append character at that index
	 * 3. Check if code exists in db3.group_list
	 * 4. If exists, repeat from step 1
	 * 5. Return unique code
	 * 
	 * VALIDATION:
	 * Uses Exists.exist() with db3.group_list query.
	 * Continues loop until a non-existing code is found.
	 * 
	 * USAGE:
	 * Called by actionPerformed() when creating a new group.
	 * Currently used: 1 call site (CreateGroup.actionPerformed)
	 * 
	 * @return A unique 7-character alphanumeric group code
	 */
	public static String createCode() {

		String newcode;
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
		
		do {
			newcode = "";
	        for (int i = 0; i < 7; i++) {
	            int index = random.nextInt(characters.length());
	            newcode += characters.charAt(index);
	        }
		} while (Exists.exist("https://splitwise.up.railway.app/db3/GetRowData?table=group_list&group_code=" + newcode) == false);
		return newcode;
		
	}
	
	/**
	 * AddNewGroup - Create All Database Tables and Records for New Group
	 * 
	 * PURPOSE:
	 * Sets up complete database infrastructure for a newly created group.
	 * Creates 5 separate tables across multiple databases and initializes
	 * essential records including the group registry and creator membership.
	 * 
	 * BEHAVIOR:
	 * Executes database operations in two phases:
	 * 
	 * PHASE 1: Create Tables (5 tables)
	 * 1. db4.[GroupCode] - Group members table
	 *    Schema: id (INT AUTO_INCREMENT PK), name (VARCHAR 100 NOT NULL)
	 * 
	 * 2. db5.[GroupCode] - Payment history/transactions table
	 *    Schema: id (PK), Payee (VARCHAR 100), Amount (DECIMAL 10,2), 
	 *            Reason (VARCHAR 100), TType (INT), tID (VARCHAR 100)
	 * 
	 * 3. db6.[GroupCode] - Payment permissions/debts table
	 *    Schema: id (PK), Member1 (VARCHAR 100), Amount (DECIMAL 10,2), 
	 *            Member2 (VARCHAR 100)
	 * 
	 * 4. db1.[GroupCode] - Balance tracking table
	 *    Schema: id (PK), Name (VARCHAR 100), Amount (DECIMAL 10,2)
	 * 
	 * 5. db8.[GroupCode] - Expense splitting/tracking table
	 *    Schema: id (PK), Creator (VARCHAR 100), tID (VARCHAR 100),
	 *            [username] (DECIMAL 10,2 NOT NULL)
	 *    Note: Creates column for group creator immediately
	 * 
	 * PHASE 2: Insert Initial Records (5 inserts)
	 * 1. db3.GroupNames - Register group globally
	 *    INSERT: (group_name, group_code)
	 * 
	 * 2. db4.[GroupCode] - Add creator as first member
	 *    INSERT: (name) VALUES (username)
	 * 
	 * 3. db7.[Username] - Add group to creator's group list
	 *    INSERT: (GroupID, GroupName)
	 * 
	 * 4. db1.[GroupCode] - Create "Total" balance tracker
	 *    INSERT: (Name, Amount) VALUES ('Total', 0)
	 * 
	 * 5. db1.[GroupCode] - Create creator's balance record
	 *    INSERT: (Name, Amount) VALUES (username, 0)
	 * 
	 * CRITICAL DETAILS:
	 * - All tables use INT AUTO_INCREMENT PRIMARY KEY for id column
	 * - Amount fields use DECIMAL(10,2) for precise currency handling
	 * - db8 table immediately includes creator's column for expense tracking
	 * - "Total" record in db1 tracks overall group balance
	 * 
	 * URL ENCODING:
	 * Uses %20 for spaces, %2C for commas in SQL statements sent via HTTPS.
	 * 
	 * USAGE:
	 * Called by actionPerformed() after code generation.
	 * Currently used: 1 call site (CreateGroup.actionPerformed)
	 * 
	 * @param gcode The unique 7-character group code
	 * @param gname The group name entered by the user
	 */
	public void AddNewGroup(String gcode, String gname) {
		
		ApiCaller.ApiCaller1("https://splitwise.up.railway.app/db4/CreateTable?table="+gcode+"&columns=id%20INT%20AUTO_INCREMENT%20PRIMARY%20KEY,%20name%20VARCHAR(100)%20NOT%20NULL");
		ApiCaller.ApiCaller1("https://splitwise.up.railway.app/db5/CreateTable?table="+gcode+"&columns=id%20INT%20AUTO_INCREMENT%20PRIMARY%20KEY%2C%20Payee%20VARCHAR(100)%20NOT%20NULL%2C%20Amount%20DECIMAL(10%2C2)%20NOT%20NULL%2C%20Reason%20VARCHAR(100)%20NOT%20NULL%2C%20TType%20INT%20NOT%20NULL%2C%20tID%20VARCHAR(100)%20COLLATE%20utf8mb4_bin%20NOT%20NULL");
		ApiCaller.ApiCaller1("https://splitwise.up.railway.app/db6/CreateTable?table="+gcode+"&columns=id%20INT%20AUTO_INCREMENT%20PRIMARY%20KEY,%20Member1%20VARCHAR(100)%20NOT%20NULL,%20Amount%20DECIMAL(10%2C2)%20NOT%20NULL,%20Member2%20VARCHAR(100)%20NOT%20NULL");
		ApiCaller.ApiCaller1("https://splitwise.up.railway.app/db1/CreateTable?table="+gcode+"&columns=id%20INT%20AUTO_INCREMENT%20PRIMARY%20KEY,%20Name%20VARCHAR(100)%20NOT%20NULL,%20Amount%20DECIMAL(10%2C2)%20NOT%20NULL");
		ApiCaller.ApiCaller1("https://splitwise.up.railway.app/db8/CreateTable?table="+gcode+"&columns=id%20INT%20AUTO_INCREMENT%20PRIMARY%20KEY,%20Creator%20VARCHAR(100)%20,%20tID%20VARCHAR(100)%20COLLATE%20utf8mb4_bin,"+uname+"%20DECIMAL(10%2C2)%20NOT%20NULL");
		
		ApiCaller.ApiCaller1("https://splitwise.up.railway.app/db3/InsertData?table=GroupNames&params=(group_name,group_code)&info=('" + gname + "','" + gcode + "')");
		ApiCaller.ApiCaller1("https://splitwise.up.railway.app/db4/InsertData?table="+gcode+"&params=(name)&info=('" + uname + "')");
		ApiCaller.ApiCaller1("https://splitwise.up.railway.app/db7/InsertData?table="+uname+"&params=(GroupID,GroupName)&info=('" + gcode + "','" + gname + "')");
		ApiCaller.ApiCaller1("https://splitwise.up.railway.app/db1/InsertData?table="+gcode+"&params=(Name,Amount)&info=('Total',0)");
		ApiCaller.ApiCaller1("https://splitwise.up.railway.app/db1/InsertData?table="+gcode+"&params=(Name,Amount)&info=('" + uname + "',0)");
		
	}
	
	/**
	 * runGUI - Display the CreateGroup Window
	 * 
	 * PURPOSE:
	 * Makes the CreateGroup frame visible to the user.
	 * Final step in showing the group creation interface.
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
	 * Called on CreateGroup instance to display its frame.
	 * 
	 * USAGE:
	 * Called by Groups.java after creating CreateGroup instance.
	 * Currently used: 1 call site (Groups.actionPerformed)
	 */
	public void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}


	
}

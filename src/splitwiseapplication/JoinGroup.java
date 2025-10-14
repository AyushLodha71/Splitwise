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

/**
 * JoinGroup - Group Invitation Interface
 * 
 * This class allows users to join existing groups using an invitation code.
 * The process involves:
 * 1. User enters a 7-character group code (received from group creator/member)
 * 2. System validates the code exists and user isn't already a member
 * 3. If valid, adds user to group and initializes their balance records
 * 4. Displays success message and provides button to enter the joined group
 * 
 * The interface handles validation errors such as:
 * - Invalid/non-existent group codes
 * - Attempting to join a group the user is already in
 * 
 * @version 2.0 - Enhanced with comprehensive documentation and improved UX
 */
public class JoinGroup implements ActionListener {

	// Main application frame
	static JFrame frame;
	
	// UI container panel
	JPanel contentPane;
	
	// UI labels for prompts and status messages
	JLabel joinPrompt, success;
	
	// Text field for entering group code
	JTextField joinCode;
	
	// Action buttons
	JButton enterButton,backButton;
	
	// List of user's groups (not currently used)
	ArrayList<String> groups = new ArrayList<String>();
	
	// Current username (static for access by helper methods)
	static String uname;
	
	/**
	 * Constructor - Builds the Join Group Interface
	 * 
	 * Creates a simple form with:
	 * - A text field for entering the group code
	 * - Enter key support for quick submission
	 * - Back button to return to groups page
	 * 
	 * After successful join, displays success message and
	 * provides an "Enter" button to immediately access the joined group.
	 * 
	 * @param username The logged-in user's username
	 */
	public JoinGroup(String username) {
		
		uname = username;
		
		 /* Setup main frame */
		 frame = new JFrame("Splitwise - Join Group");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 
		 /* Create content pane with 3-column grid layout */
		 contentPane = new JPanel();
		 contentPane.setLayout(new GridLayout(0, 3, 10, 5));
		 contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		 
		 /* Add prompt label */
		 joinPrompt = new JLabel("Enter the group code to join");
		 contentPane.add(joinPrompt);
		 
		 /* Group code input field with Enter key support */
		 joinCode = new JTextField();
		 joinCode.setToolTipText("Enter the 7-character group code");
		 joinCode.addActionListener(this);
		 joinCode.setActionCommand("Join Group"); // Pressing Enter attempts to join
		 contentPane.add(joinCode);
		 
		 /* Back button - return to groups page */
		 backButton = new JButton("Back");
		 backButton.setToolTipText("Return to groups page");
		 backButton.addActionListener(this);
		 backButton.setActionCommand("Back");
		 contentPane.add(backButton);
		 
		 /* Finalize frame setup */
		 frame.setContentPane(contentPane);
		 frame.pack();
		 frame.setVisible(false);
	
	}
	
	/**
	 * Event Handler - Processes user actions
	 * 
	 * Handles three states:
	 * 1. "Join Group" - Validates code and adds user to group if valid
	 * 2. "Enter" - Enters the newly joined group
	 * 3. "Back" - Returns to groups page without joining
	 * 
	 * Validation checks:
	 * - Group code must exist
	 * - User must not already be a member
	 * 
	 * @param event The action event from user interaction
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		if ("Enter".equals(eventName)) {
			// Enter the newly joined group
			String code = joinCode.getText();
			MainPage mpage = new MainPage(uname,code);
			mpage.runGUI();
			frame.dispose();
			
		} else if ("Join Group".equals(eventName)){
			// Attempt to join group with entered code
			String code = joinCode.getText();
			File groupFile = new File("src/splitwiseapplication/Groups/"+code);
			
			// Validate: group exists AND user not already a member
			if (checkExistence(code) && Exists.exist(uname,groupFile)==false) {
				// Valid - add user to group
				AddNewUser(code,uname);
				success = new JLabel("Success!");
				enterButton = new JButton("Enter");
				enterButton.setToolTipText("Enter the group you just joined");
				joinCode.setActionCommand("Group Joined"); // Prevent duplicate joins
				enterButton.addActionListener(this);
				enterButton.setActionCommand("Enter");
				contentPane.add(success);
				contentPane.add(enterButton);
				frame.setContentPane(contentPane);
				frame.pack();
			} else {
				// Invalid - show error
				JLabel displayError = new JLabel("Wrong Code or Already a Member");
				contentPane.add(displayError);
				frame.setContentPane(contentPane);
				frame.pack();
			}
			
		} else if ("Back".equals(eventName)) {
			// Return to groups page without joining
			Groups groupsPage = new Groups(uname);
			groupsPage.runGUI();
			frame.dispose();
		}
		
	}
	
	/**
	 * Checks if Group Code Exists
	 * 
	 * Validates whether a group file exists for the given code.
	 * 
	 * @param ucode The group code to check
	 * @return true if group exists, false otherwise
	 */
	public static boolean checkExistence(String ucode) {
		
		File textFile;
		
		textFile = new File("src/splitwiseapplication/Groups/"+ucode);
		
		return textFile.exists();
		
	}

	/**
	 * Adds New User to Existing Group
	 * 
	 * Performs complete user onboarding to a group:
	 * 1. Adds user to group's member list
	 * 2. Adds group to user's personal folder
	 * 3. Initializes user's spending to $0
	 * 4. Creates pending amount records with all existing members
	 * 
	 * @param code The group code to join
	 * @param usrname The username of the joining user
	 */
	public static void AddNewUser(String code, String usrname) {
		
		File userFile, newFile, casFile;
		String gname;
		
		// Create bidirectional pending amount records with existing members
		AddPerms(code);
		
		// Add user to group's member list
		newFile = new File("src/splitwiseapplication/Groups/"+code);
		UpdateFile.Update(usrname,newFile);
		
		// Add group to user's personal folder
		userFile = new File("src/splitwiseapplication/Personal_Folders/"+usrname);
		gname = RetrieveName(code);
		UpdateFile.Update(gname,userFile); 
		
		// Initialize user's spending amount to $0
		casFile = new File("src/splitwiseapplication/CheckAmountSpentFolder/"+code);
		UpdateFile.Update(uname,"0",casFile);
		
	}
	
	/**
	 * Creates Pending Amount Records for New Member
	 * 
	 * When a user joins a group, creates balance tracking entries
	 * between the new user and all existing members.
	 * Format: username>0>existingMember (initially $0 owed)
	 * 
	 * @param gcode The group code being joined
	 */
	public static void AddPerms(String gcode) {
		
		File newFile = new File("src/splitwiseapplication/Groups/"+gcode);
		ArrayList<String> lst = Exists.exists(gcode, newFile);
		
		// Create pending amount entry for each existing member
		for (String i : lst) {
			UpdateFile.Update(uname,"0", i, new File("src/splitwiseapplication/PendingAmount/"+gcode));
		}
	}
	
	/**
	 * Retrieves Group Name from Group Code
	 * 
	 * Looks up the human-readable group name associated with a group code
	 * by searching the groups.txt file.
	 * 
	 * File format: groupCode,groupName
	 * 
	 * @param c The group code to look up
	 * @return The group name corresponding to the code
	 */
	public static String RetrieveName(String c) {
		
		String line;
		File textFile;
		
		textFile = new File("src/splitwiseapplication/groups.txt");
		
		try (FileReader in = new FileReader(textFile);
		     BufferedReader readFile = new BufferedReader(in)) {
			while ((line = readFile.readLine()) != null ) {
				String[] myArray = line.split(",");
				if (myArray[0].equals(c)) {
					return myArray[1]; // Return the group name
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException: "
					+ e.getMessage());
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
		return (c); // Fallback: return code if not found
		
	}
	
	/**
	 * Display the Join Group GUI
	 * 
	 * Makes the join group frame visible to the user.
	 * Should be called after constructing a JoinGroup object.
	 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}

	
}

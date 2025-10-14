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
import java.util.Random;

/**
 * CreateGroup - New Group Creation Interface
 * 
 * This class provides functionality for users to create new expense-sharing groups.
 * The process involves:
 * 1. User enters a group name
 * 2. System generates a unique 7-character alphanumeric group code
 * 3. Creates all necessary data files for the group (members, transactions, balances, etc.)
 * 4. Displays the group code for sharing with other members
 * 5. Allows immediate entry into the newly created group
 * 
 * The generated group code serves as both an identifier and invitation code
 * that other users can use to join the group.
 * 
 * @version 2.0 - Enhanced with comprehensive documentation and improved UX
 */
public class CreateGroup implements ActionListener{

	// Main application frame
	static JFrame frame;
	
	// UI container panel
	JPanel contentPane;
	
	// UI labels for prompts and displaying generated code
	JLabel createPrompt,displayCode;
	
	// Text field for entering group name
	JTextField createGroup;
	
	// Action buttons
	JButton enterButton,backButton;
	
	// List of user's groups (not currently used)
	ArrayList<String> groups = new ArrayList<String>();
	
	// Current username and generated group code
	String uname, code;
	
	/**
	 * Constructor - Builds the Create Group Interface
	 * 
	 * Creates a simple form with:
	 * - A text field for entering the new group name
	 * - Enter key support for quick submission
	 * - Back button to return to groups page
	 * 
	 * After group creation, displays the generated group code and
	 * provides an "Enter" button to immediately access the new group.
	 * 
	 * @param username The logged-in user's username (group creator)
	 */
	public CreateGroup(String username) {
		
		uname = username;
		
		 /* Setup main frame */
		 frame = new JFrame("Splitwise - Create Group");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 
		 /* Create content pane with 3-column grid layout */
		 contentPane = new JPanel();
		 contentPane.setLayout(new GridLayout(0, 3, 10, 5));
		 contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		 
		 /* Add prompt label */
		 createPrompt = new JLabel("Enter the name of the new group");
		 contentPane.add(createPrompt);
		 
		 /* Group name input field with Enter key support */
		 createGroup = new JTextField();
		 createGroup.setToolTipText("Enter a name for your new group");
		 createGroup.addActionListener(this);
		 createGroup.setActionCommand("Create New Group"); // Pressing Enter creates group
		 contentPane.add(createGroup);
		 
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
	 * 1. "Create New Group" - Generates code, creates files, displays code
	 * 2. "Enter" - Enters the newly created group
	 * 3. "Back" - Returns to groups page without creating
	 * 
	 * @param event The action event from user interaction
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		if ("Enter".equals(eventName)) {
			// Enter the newly created group
			MainPage mpage = new MainPage(uname,code);
			mpage.runGUI();
			frame.dispose();
			
		} else if ("Create New Group".equals(eventName)){
			// Create new group with entered name
			String text = createGroup.getText();
			
			// Generate unique group code
			File txtFile = new File("src/splitwiseapplication/groups.txt");
			ArrayList<String> usedCodes = Exists.exists(uname,txtFile);
			code = createCode(usedCodes);
			
			// Create all group files and initialize data
			AddNewGroup(code,text);
			
			// Display the generated code and provide Enter button
			displayCode = new JLabel("The group code is: " + code);
			enterButton = new JButton("Enter");
			enterButton.setToolTipText("Enter your new group");
			createGroup.setActionCommand("Group Created"); // Prevent duplicate creation
			enterButton.addActionListener(this);
			enterButton.setActionCommand("Enter");
			contentPane.add(displayCode);
			contentPane.add(enterButton);
			frame.setContentPane(contentPane);
			frame.pack();
			
		} else if ("Back".equals(eventName)) {
			// Return to groups page without creating
			Groups groupsPage = new Groups(uname);
			groupsPage.runGUI();
			frame.dispose();
		}
		
	}
	
	/**
	 * Generates a Unique Group Code
	 * 
	 * Creates a random 7-character alphanumeric code that doesn't conflict
	 * with any existing group codes. The code uses uppercase letters,
	 * lowercase letters, and digits (62 possible characters).
	 * 
	 * Format: 7 random characters from [A-Za-z0-9]
	 * Example: "mKEILR5"
	 * 
	 * @param codesUsed List of existing group codes to avoid duplicates
	 * @return A unique 7-character group code
	 */
	public static String createCode(ArrayList<String> codesUsed) {
		
		String newcode;
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
		
        // Keep generating until we find a unique code
		do {
			newcode = "";
	        for (int i = 0; i < 7; i++) {
	            int index = random.nextInt(characters.length());
	            newcode += characters.charAt(index);
	        }
		} while (codesUsed.contains(newcode));
		
		return newcode;
		
	}
	
	/**
	 * Creates New Group with All Required Files
	 * 
	 * Initializes a complete group environment by:
	 * 1. Creating group entry in groups.txt (code,name mapping)
	 * 2. Creating Groups/[code] file (member list)
	 * 3. Creating PaymentHistory/[code] file (transaction log)
	 * 4. Creating PendingAmount/[code] file (balance tracking)
	 * 5. Creating CheckAmountSpentFolder/[code] file (spending totals)
	 * 6. Creating TransactionDetails/[code] file (detailed records)
	 * 7. Adding group to creator's personal folder
	 * 8. Initializing creator as first member with $0 spent
	 * 
	 * @param gcode The generated unique group code
	 * @param gname The user-provided group name
	 */
	public void AddNewGroup(String gcode, String gname) {
		
		File textFile,newFile, userFile, casFile,tdFile, paFile;
		
		// Define all group-related files
		textFile = new File("src/splitwiseapplication/groups.txt");
		newFile = new File("src/splitwiseapplication/Groups/"+gcode);
		casFile = new File("src/splitwiseapplication/CheckAmountSpentFolder/"+gcode);
		tdFile = new File("src/splitwiseapplication/TransactionDetails/"+gcode);
		userFile = new File("src/splitwiseapplication/Personal_Folders/"+uname);
		paFile = new File("src/splitwiseapplication/PendingAmount/"+gcode);
        
		// Create all necessary files
		CreateFile(newFile);
		CreateFile(new File("src/splitwiseapplication/PaymentHistory/"+gcode));
		CreateFile(paFile);
		CreateFile(casFile);
		CreateFile(tdFile);
		
		// Initialize file contents
		UpdateFile.Update(gcode,gname,textFile);          // Add to groups registry
		UpdateFile.Update(uname,newFile);                  // Add creator as first member
		UpdateFile.Update(gname,userFile);                 // Add to creator's personal folder
		UpdateFile.Update("Member1>Amt>Member2",userFile); // Header for pending amounts
		UpdateFile.Update("Total","0",casFile);            // Initialize total spending
		UpdateFile.Update(uname,"0",casFile);              // Initialize creator's spending
		
	}

	/**
	 * Creates a New File
	 * 
	 * Simple file creation utility that handles IOException.
	 * 
	 * @param newFile The file to create
	 */
	public void CreateFile(File newFile) {
		
		try {
			 newFile.createNewFile();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
	}
	
	/**
	 * Display the Create Group GUI
	 * 
	 * Makes the create group frame visible to the user.
	 * Should be called after constructing a CreateGroup object.
	 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}


	
}

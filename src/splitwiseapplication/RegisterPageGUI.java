package splitwiseapplication;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

/**
 * RegisterPageGUI - User Registration Interface
 * 
 * This class provides a Swing-based graphical user interface for new user registration
 * in the Splitwise application. It allows users to:
 * - Create a new account with a unique username and password
 * - Toggle password visibility for easier typing
 * - Navigate back to the login/register selection page
 * 
 * The interface validates that usernames are unique before creating an account.
 * Upon successful registration, the user is automatically logged in and directed
 * to their groups page. Each new user gets a personal folder created for their data.
 * 
 * Security: Passwords are stored as char arrays during entry and cleared from memory
 * after use to minimize security risks.
 * 
 * @version 2.0 - Enhanced with comprehensive documentation and ResourceManager integration
 */
public class RegisterPageGUI implements ActionListener{
	
	// Main application frame
	static JFrame frame;
	
	// UI container panels for organizing components
	JPanel contentPane,passwordPane;
	
	// Text labels prompting user input
	JLabel usernamePrompt, passwordPrompt;
	
	// Interactive buttons: show/hide password, submit registration, return to previous page
	JButton hideShowButton, submitButton, backButton;
	
	// Text input field for username entry
	JTextField username;
	
	// Password field with masked character display
	JPasswordField password;
	
	// Stores the default password masking character for toggle functionality
	char defaultEchoChar;
	
	// Icons for password visibility toggle (loaded once via ResourceManager for performance)
	ImageIcon openEyeIcon, closedEyeIcon;
	
	/**
	 * Constructor - Builds the Registration User Interface
	 * 
	 * Initializes and configures all GUI components for the registration page:
	 * - Sets up the main frame with appropriate title and close operation
	 * - Creates a grid layout for organized component placement
	 * - Adds username and password input fields with labels
	 * - Implements password visibility toggle button with eye icons
	 * - Configures Register and Back navigation buttons
	 * - Sets up tooltips for user guidance
	 * - Loads password visibility icons via ResourceManager (cached for performance)
	 * 
	 * The frame is initially hidden and displayed via runGUI() method.
	 */
	public RegisterPageGUI() {
		
		 /* Create and set up the main application frame */
		 frame = new JFrame("Splitwise - Register Page");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 
		 /* Create a content pane with 2-column grid layout and padding */
		 contentPane = new JPanel();
		 contentPane.setLayout(new GridLayout(0, 2, 10, 5)); // 2 columns, 10px horizontal gap, 5px vertical gap
		 contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50)); // Add 20px top/bottom, 50px left/right padding
		 
		 /* Create a panel to hold password field and visibility toggle button side-by-side */
		 passwordPane = new JPanel();
		 passwordPane.setLayout(new GridBagLayout());
		 
		 /* Username input section */
		 usernamePrompt = new JLabel("Enter your username: ");
		 usernamePrompt.setToolTipText("Choose a unique username for your account");
		 contentPane.add(usernamePrompt);
		 
		 username = new JTextField(); // Start with empty field (no placeholder)
		 username.setToolTipText("Enter a username (must be unique)");
		 contentPane.add(username);
		 
		 /* Password input section */
		 passwordPrompt = new JLabel("Enter your password: ");
		 passwordPrompt.setToolTipText("Create a secure password");
		 contentPane.add(passwordPrompt);
		 
		 /* Password field with Enter key support */
		 password = new JPasswordField(10);
		 password.setToolTipText("Enter your desired password");
		 password.addActionListener(this);
		 password.setActionCommand("Submit"); // Pressing Enter in password field triggers registration
		 passwordPane.add(password);
		 
		 // Store the default masking character for password field (usually '•' or '*')
		 defaultEchoChar = password.getEchoChar();
		 
		 /* Load password visibility icons from ResourceManager (cached singleton) */
		 openEyeIcon = ResourceManager.getInstance().getOpenEyeIcon();
		 closedEyeIcon = ResourceManager.getInstance().getClosedEyeIcon();
		 
		 /* Password visibility toggle button */
		 hideShowButton = new JButton();
		 hideShowButton.setIcon(openEyeIcon); // Start with "show password" icon
		 hideShowButton.setToolTipText("Show password");
		 hideShowButton.addActionListener(this);
		 hideShowButton.setActionCommand("show"); // Initial action is to show password
		 hideShowButton.setPreferredSize(new Dimension(openEyeIcon.getIconWidth(), openEyeIcon.getIconHeight()));
		 passwordPane.add(hideShowButton);
		 
		 /* Add password panel to content pane (combines password field + toggle button) */
		 contentPane.add(passwordPane);
		 
		 /* Register button - submits the registration form */
		 submitButton = new JButton("Register");
		 submitButton.setToolTipText("Create your account");
		 submitButton.addActionListener(this);
		 contentPane.add(submitButton);
		 
		 /* Back button - returns to login/register selection page */
		 backButton = new JButton("Back");
		 backButton.setToolTipText("Return to login/register selection");
		 backButton.addActionListener(this);
		 backButton.setActionCommand("Back");
		 contentPane.add(backButton);
		 
		 /* Add content pane to frame */
		 frame.setContentPane(contentPane);
		 /* Size the frame to fit all components */
		 frame.pack();
		 /* Initially hidden - displayed via runGUI() */
		 frame.setVisible(false);
	
	}
	
	/**
	 * Event Handler - Processes all user interactions
	 * 
	 * Handles three types of actions:
	 * 1. Submit/Register - Validates username uniqueness and creates new account
	 * 2. Password Visibility Toggle - Shows/hides password characters
	 * 3. Back Navigation - Returns to login/register selection screen
	 * 
	 * @param event The action event triggered by user interaction
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		// Handle password visibility toggle
		if ("show".equals(eventName)) {
			// Show password in plain text
			password.setEchoChar((char) 0); // 0 = no masking character
			hideShowButton.setIcon(closedEyeIcon);
			hideShowButton.setActionCommand("hide");
			hideShowButton.setToolTipText("Hide password");
			
		} else if ("hide".equals(eventName)) {
			// Hide password with masking characters
			password.setEchoChar(defaultEchoChar); // Restore default masking (• or *)
			hideShowButton.setIcon(openEyeIcon);
			hideShowButton.setActionCommand("show");
			hideShowButton.setToolTipText("Show password");
			
		} else if ("Submit".equals(eventName)) {
			// Attempt to register new user
			attemptRegistration();
			
		} else {
			// Back button pressed - return to login/register selection
			LoginOrRegister loginorRegisterGUI = new LoginOrRegister();
			loginorRegisterGUI.runGUI();
			frame.dispose();
		}
		
	}
	
	/**
	 * Attempts User Registration
	 * 
	 * Validates that the chosen username is unique, then creates a new user account.
	 * - Retrieves username and password from input fields
	 * - Checks credentials file to ensure username doesn't already exist
	 * - If unique: creates account, creates personal folder, navigates to Groups page
	 * - If taken: displays error message and clears password field
	 * 
	 * Security: Password is securely cleared from memory after use
	 */
	private void attemptRegistration() {
		String usrname = username.getText();
		char[] pwdChars = password.getPassword();
		String pwd = new String(pwdChars);
		
		File textFile = new File("src/splitwiseapplication/credentials.txt");
		
		// Check if username already exists
		if (Exists.exist(usrname,textFile) == false) {
			// Username is unique - proceed with registration
			AddNewUser(usrname,pwd);
			java.util.Arrays.fill(pwdChars, '0'); // Clear password from memory for security
			
			// Registration successful - navigate to Groups page
			Groups groups = new Groups(usrname);
			groups.runGUI();
			frame.dispose();
			
		} else {
			// Username is already taken
			JOptionPane.showMessageDialog(frame,
				"<html><font color='red'><b>Username already taken!</b></font><br>" +
				"Please choose a different username.</html>",
				"Registration Failed",
				JOptionPane.ERROR_MESSAGE);
			
			// Clear password field for security and user convenience
			password.setText("");
			java.util.Arrays.fill(pwdChars, '0'); // Clear password from memory
		}
	}

	/**
	 * Adds a New User to the System
	 * 
	 * Performs two critical setup tasks for a new user:
	 * 1. Writes username and password to credentials.txt file for future authentication
	 * 2. Creates a personal folder for the user to store their expense data
	 * 
	 * The personal folder is essential for maintaining user-specific transaction history,
	 * balances, and other financial records separate from other users.
	 * 
	 * @param uname The username chosen by the new user
	 * @param psswd The password chosen by the new user
	 */
	public void AddNewUser(String uname, String psswd) {
		
		File textFile,newFile;
		
		// Step 1: Add username and password to credentials.txt
		textFile = new File("src/splitwiseapplication/credentials.txt");
		UpdateFile.Update(uname,psswd,textFile);
		
		// Step 2: Create a personal folder for this user's data
		newFile = new File("src/splitwiseapplication/Personal_Folders/"+uname);
		
		try {
			 newFile.createNewFile();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
	 
	}
	
	/**
	 * Display the Registration GUI
	 * 
	 * Makes the registration frame visible to the user.
	 * Should be called after constructing a RegisterPageGUI object.
	 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}
	
}

package splitwiseapplication;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;

/**
 * LoginPageGUI - User interface for user login functionality.
 * 
 * This class provides a login form where existing users can enter their
 * credentials to access the Splitwise application. Features include:
 * - Username and password input fields
 * - Password visibility toggle (show/hide password)
 * - Credential verification against stored user data
 * - Navigation to main groups page on successful login
 * 
 * @version 2.0
 */
public class LoginPageGUI implements ActionListener{
	
	// UI Components
	static JFrame frame;                    // Main application window
	JPanel contentPane, passwordPane;       // Container panels for layout
	JLabel usernamePrompt, passwordPrompt;  // Text labels for input fields
	JButton hideShowButton, submitButton, backButton;  // Action buttons
	JTextField username;                    // Username input field
	JPasswordField password;                // Secure password input field
	char defaultEchoChar;                   // Original password masking character
	
	// Cached icons from ResourceManager (loaded once, reused everywhere)
	ImageIcon openEyeIcon, closedEyeIcon;
	
	/**
	 * Constructor - Initializes the login page UI components.
	 * Sets up the layout, creates input fields, buttons, and configures
	 * event listeners for user interactions.
	 */
	public LoginPageGUI() {
		
		// Create main application window
		frame = new JFrame("Splitwise - Login Page");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Setup main content panel with grid layout (2 columns)
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(0, 2, 10, 5));
		contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		
		// Password panel uses GridBagLayout for password field + show/hide button
		passwordPane = new JPanel();
		passwordPane.setLayout(new GridBagLayout());
		
		// Username label and input field
		usernamePrompt = new JLabel("Enter your username: ");
		contentPane.add(usernamePrompt);
		
		username = new JTextField();
		username.setToolTipText("Enter your registered username");
		username.addActionListener(this);  // Pressing Enter triggers login
		username.setActionCommand("Submit");
		contentPane.add(username);
		
		// Password label and input field
		passwordPrompt = new JLabel("Enter your password: ");
		contentPane.add(passwordPrompt);
		
		password = new JPasswordField(10);
		password.setToolTipText("Enter your password (press Enter to login)");
		password.addActionListener(this);  // Pressing Enter triggers login
		password.setActionCommand("Submit");
		passwordPane.add(password);
		
		// Remember default password masking character
		defaultEchoChar = password.getEchoChar();
		
		// Load icons from ResourceManager (cached, not reloaded each time)
		ResourceManager resourceManager = ResourceManager.getInstance();
		openEyeIcon = resourceManager.getOpenEyeIcon();
		closedEyeIcon = resourceManager.getClosedEyeIcon();
		
		// Button to toggle password visibility
		hideShowButton = new JButton();
		hideShowButton.setIcon(openEyeIcon);
		hideShowButton.setToolTipText("Show/Hide password");
		hideShowButton.addActionListener(this);
		hideShowButton.setActionCommand("show");
		hideShowButton.setPreferredSize(new Dimension(openEyeIcon.getIconWidth(), openEyeIcon.getIconHeight()));
		passwordPane.add(hideShowButton);
		
		contentPane.add(passwordPane);
		
		// Submit button for login
		submitButton = new JButton("Login");
		submitButton.setToolTipText("Click to login or press Enter in any field");
		submitButton.addActionListener(this);
		submitButton.setActionCommand("Submit");
		contentPane.add(submitButton);
		
		// Back button to return to login/register selection
		backButton = new JButton("Back");
		backButton.setToolTipText("Return to previous screen");
		backButton.addActionListener(this);
		backButton.setActionCommand("Back");
		contentPane.add(backButton);
		
		// Finalize frame setup
		frame.setContentPane(contentPane);
		frame.pack();
		frame.setVisible(false);  // Will be shown by runGUI() method
	}
	
	/**
	 * Event handler for all button clicks and Enter key presses.
	 * Handles: password visibility toggle, login submission, navigation back.
	 * 
	 * @param event The action event triggered by user interaction
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		// Toggle password visibility
		if (eventName.equals("show")) {
			password.setEchoChar((char) 0);  // Show password as plain text
			hideShowButton.setIcon(closedEyeIcon);
			hideShowButton.setToolTipText("Hide password");
			hideShowButton.setActionCommand("hide");
		} 
		// Hide password (return to masked mode)
		else if (eventName.equals("hide")) {
			password.setEchoChar(defaultEchoChar);  // Mask password with default character
			hideShowButton.setIcon(openEyeIcon);
			hideShowButton.setToolTipText("Show password");
			hideShowButton.setActionCommand("show");
		} 
		// Handle login attempt (from button or Enter key)
		else if (eventName.equals("Submit")) {
			attemptLogin();
		} 
		// Navigate back to login/register selection screen
		else if (eventName.equals("Back")){
			LoginOrRegister loginorRegisterGUI = new LoginOrRegister();
			loginorRegisterGUI.runGUI();
			frame.dispose();
		} 
		// Retry login after error (prevents duplicate error messages)
		else if (eventName.equals("Submitted")) {
			attemptLogin();
		}
	}
	
	/**
	 * Attempts to log in the user with provided credentials.
	 * Extracts username and password, verifies them, and navigates to groups page on success.
	 * Shows error message on failure.
	 */
	private void attemptLogin() {
		// Get username from text field
		String usrname = username.getText().trim();
		
		// Get password securely (as char array, not String)
		char[] pwdChars = password.getPassword();
		String pwd = new String(pwdChars);
		
		// Verify credentials against stored data
		boolean loginSuccess = VerifyCredentials(usrname, pwd);
		
		// Security: Clear password from memory immediately after use
		java.util.Arrays.fill(pwdChars, '0');
		
		if (loginSuccess) {
			// Login successful - navigate to groups page
			Groups groups = new Groups(usrname);
			groups.runGUI();
			frame.dispose();
		} else {
			// Login failed - show error message
			JLabel displayError = new JLabel("<html><font color='red'>Wrong Username or Password</font></html>");
			
			// Change action commands to prevent showing multiple error messages
			password.setActionCommand("Submitted");
			submitButton.setActionCommand("Submitted");
			username.setActionCommand("Submitted");
			
			// Add error message to UI
			contentPane.add(displayError);
			frame.setContentPane(contentPane);
			frame.pack();
			
			// Clear password field for security
			password.setText("");
		}
	}
	
	/**
	 * Verifies user credentials against the stored credentials file.
	 * Reads credentials.txt file and checks if username and password match.
	 * 
	 * @param uname The username to verify
	 * @param pass The password to verify
	 * @return true if credentials are valid, false otherwise
	 */
	public boolean VerifyCredentials(String uname, String pass) {
		
		String usrnme;
		File textFile;
		
		// Read from credentials file (format: username,password per line)
		textFile = new File("src/splitwiseapplication/credentials.txt");
		try (FileReader in = new FileReader(textFile);
		     BufferedReader readFile = new BufferedReader(in)) {
			// Check each line for matching credentials
			while ((usrnme = readFile.readLine()) != null ) {
				String[] myArray = usrnme.split(",");
				// Compare username and password
				if (myArray.length >= 2 && myArray[0].equals(uname) && myArray[1].equals(pass)) {
					return true;  // Valid credentials found
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("Credentials file not found: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Error reading credentials: " + e.getMessage());
		}
		return false;
		
	}		 /**
		 * Create and show the GUI.
		 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}
	
}

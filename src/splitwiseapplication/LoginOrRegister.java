package splitwiseapplication;

import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * LoginOrRegister - Initial Welcome Screen
 * 
 * This is the first screen users see when launching the Splitwise application.
 * It provides a simple choice between:
 * - Login: For existing users to access their account
 * - Register: For new users to create an account
 * 
 * Interface Design:
 * A 2x2 grid layout with:
 * - Top row: Prompts for login and registration
 * - Bottom row: Buttons to proceed to respective pages
 * 
 * Navigation Flow:
 * 1. User starts here on application launch
 * 2. Clicks Login → Opens LoginPageGUI
 * 3. Clicks Register → Opens RegisterPageGUI
 * 4. After successful login/register → Opens Groups page
 * 
 * @version 2.0 - Enhanced with comprehensive documentation and improved UX
 */
public class LoginOrRegister implements ActionListener{
	
	// Main application frame
	static JFrame frame;
	
	// Main content panel
	JPanel contentPane;
	
	// Prompt labels
	JLabel loginPrompt,registerPrompt;
	
	// Navigation buttons
	JButton loginButton,registerButton;
	
	/**
	 * Constructs the LoginOrRegister welcome screen.
	 * 
	 * Creates a simple interface with two options:
	 * - Login button for existing users
	 * - Register button for new users
	 * 
	 * Layout: 2-column grid with prompts above buttons
	 */
	public LoginOrRegister() {
		
		// Create and set up the frame
		frame = new JFrame("Splitwise - Welcome");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Create content pane with 2-column grid layout
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(0, 2, 10, 5));
		contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		
		// Create prompts
		loginPrompt = new JLabel("Already have an account?");
		contentPane.add(loginPrompt);
		
		registerPrompt = new JLabel("New to Splitwise?");
		contentPane.add(registerPrompt);
		
		// Create navigation buttons
		loginButton = new JButton("Login");
		loginButton.setToolTipText("Sign in to your existing account");
		loginButton.setActionCommand("Login");
		loginButton.addActionListener(this);
		contentPane.add(loginButton);
		
		registerButton = new JButton("Register");
		registerButton.setToolTipText("Create a new account");
		registerButton.addActionListener(this);
		registerButton.setActionCommand("Register");
		contentPane.add(registerButton);
		
		// Assemble frame
		frame.setContentPane(contentPane);
		frame.pack();
		frame.setVisible(false);
	
	}
	
	/**
	 * Handles navigation button clicks.
	 * 
	 * Action Handling:
	 * - "Login" Button:
	 *   Opens LoginPageGUI for existing user authentication
	 * 
	 * - "Register" Button:
	 *   Opens RegisterPageGUI for new account creation
	 * 
	 * Both actions close the current welcome screen.
	 * 
	 * @param event The button click event
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		if (eventName.equals("Login")) {
			// Open login interface
			LoginPageGUI loginGUI = new LoginPageGUI();
			loginGUI.runGUI();
			frame.dispose();
		} else {
			// Open registration interface
			RegisterPageGUI registerGUI = new RegisterPageGUI();
			registerGUI.runGUI();
			frame.dispose();
		}
		
	}
	
	/**
	 * Displays the LoginOrRegister welcome screen.
	 * 
	 * Makes the initial choice screen visible to the user.
	 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}
	
}

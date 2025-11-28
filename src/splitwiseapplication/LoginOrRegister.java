package splitwiseapplication;

import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * Initial landing screen for the Splitwise application.
 * 
 * Purpose:
 * This GUI class presents the user with two options when the application starts:
 * 1. Login - for existing users
 * 2. Register - for new users creating an account
 * 
 * Screen layout:
 * - Two-column grid layout
 * - Left column: "Click the button below to login" + Login button
 * - Right column: "Click the button below to register" + Register button
 * 
 * Navigation flow:
 * - Clicking "Login" → Opens LoginPageGUI (username/password entry)
 * - Clicking "Register" → Opens RegisterPageGUI (create new account)
 * - After navigation, this window is disposed (closed)
 * 
 * Back navigation:
 * This screen can be returned to from:
 * - LoginPageGUI (back button)
 * - RegisterPageGUI (back button)
 * - Groups screen (logout functionality)
 * 
 * Usage locations:
 * - SplitwiseApplicationMain: Initial application launch (1 call)
 * - LoginPageGUI: Back button navigation (1 call)
 * - RegisterPageGUI: Back button navigation (1 call)
 * - Groups: Logout functionality (1 call)
 */
public class LoginOrRegister implements ActionListener{
	
	static JFrame frame;
	JPanel contentPane;
	JLabel loginPrompt,registerPrompt;
	JButton loginButton,registerButton;
	
	/**
	 * Constructs the LoginOrRegister GUI.
	 * 
	 * Contract:
	 * - Creates the main window frame
	 * - Sets up a 2-column grid layout
	 * - Adds labels and buttons for login/register options
	 * - Registers this object as action listener for buttons
	 * - Window is created but not visible (call runGUI() to display)
	 * 
	 * UI structure:
	 * +----------------------------------+
	 * | Login prompt    | Register prompt |
	 * | [Login button]  | [Register button] |
	 * +----------------------------------+
	 * 
	 * Post-conditions:
	 * - frame is initialized and configured
	 * - All UI components are added to contentPane
	 * - Window is packed but hidden (visible = false)
	 * - Exit on close behavior set to EXIT_ON_CLOSE
	 */
	public LoginOrRegister() {
		
		 /* Create and set up the frame */
		 frame = new JFrame("Splitwise - Login Or Register");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 /* Create a content pane */
		 contentPane = new JPanel();
		 contentPane.setLayout(new GridLayout(0, 2, 10, 5));
		 contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		 /* Create and add label */
		 loginPrompt = new JLabel("Click the button below to login");
		 contentPane.add(loginPrompt);
		 registerPrompt = new JLabel("Click the button below to register");
		 contentPane.add(registerPrompt);
		 loginButton = new JButton("Login");
		 loginButton.setActionCommand("Login");
		 loginButton.addActionListener(this);
		 contentPane.add(loginButton);
		 registerButton = new JButton("Register here");
		 registerButton.addActionListener(this);
		 registerButton.setActionCommand("Register");
		 contentPane.add(registerButton);
		 /* Add content pane to frame */
		 frame.setContentPane(contentPane);
		 /* Size and then display the frame. */
		 frame.pack();
		 frame.setVisible(false);
	
	}
	
	/**
	 * Handles button click events for login and register buttons.
	 * 
	 * Contract:
	 * - Called automatically when user clicks Login or Register button
	 * - Determines which button was clicked via action command
	 * - Opens appropriate next screen (LoginPageGUI or RegisterPageGUI)
	 * - Closes (disposes) this window after navigation
	 * 
	 * @param event  The ActionEvent containing button click information
	 * 
	 * Behavior:
	 * - If "Login" button clicked:
	 *   1. Create new LoginPageGUI instance
	 *   2. Display the login page
	 *   3. Dispose this window
	 * 
	 * - If "Register" button clicked:
	 *   1. Create new RegisterPageGUI instance
	 *   2. Display the register page
	 *   3. Dispose this window
	 * 
	 * Navigation pattern:
	 * - Always disposes current window before showing next
	 * - New window instance created each time (no window reuse)
	 * - Back navigation recreates this window from scratch
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		if (eventName.equals("Login")) {
			LoginPageGUI loginGUI = new LoginPageGUI();
			loginGUI.runGUI();
			frame.dispose();
		} else {
			RegisterPageGUI registerGUI = new RegisterPageGUI();
			registerGUI.runGUI();
			frame.dispose();
		}
		
	}
	
	/**
	 * Makes the LoginOrRegister window visible to the user.
	 * 
	 * Contract:
	 * - Must be called after constructor to display the window
	 * - Sets default look and feel decoration
	 * - Makes the frame visible on screen
	 * - Should be called on Event Dispatch Thread (handled by caller)
	 * 
	 * Usage pattern:
	 * 1. Create instance: LoginOrRegister lor = new LoginOrRegister()
	 * 2. Show window: lor.runGUI()
	 * 
	 * Note:
	 * - Instance method accessing static frame field
	 * - Must be called on LoginOrRegister instance
	 * - Frame is shared across all instances (static field)
	 * 
	 * Called from:
	 * - SplitwiseApplicationMain: Application startup
	 * - LoginPageGUI: Back button navigation
	 * - RegisterPageGUI: Back button navigation  
	 * - Groups: Logout functionality
	 */
	public void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}
	
}

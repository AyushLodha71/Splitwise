package splitwiseapplication;

/**
 * Main entry point for the Splitwise Application.
 * 
 * Purpose:
 * This class contains the main() method that launches the Splitwise desktop application.
 * It initializes the user interface by creating and displaying the login/register screen.
 * 
 * Application flow:
 * 1. Application starts here via main()
 * 2. Creates LoginOrRegister GUI (initial screen)
 * 3. User chooses to login or register
 * 4. Upon successful authentication, navigates to Groups screen
 * 5. User can then select/create/join groups and manage expenses
 * 
 * Architecture:
 * - Swing-based desktop application
 * - Communicates with Spring Boot backend via HTTP REST API (localhost:8080)
 * - Uses multiple GUI classes for different screens
 * - Data persistence handled by backend database
 * 
 * To run the application:
 * 1. Ensure Spring Boot backend server is running on localhost:8080
 * 2. Execute this class: java splitwiseapplication.SplitwiseApplicationMain
 * 3. The login/register window will appear
 * 
 * Dependencies:
 * - Java 11+ (uses HttpClient)
 * - Swing (javax.swing) for GUI
 * - Backend REST API server must be running
 */
public class SplitwiseApplicationMain {

	/**
	 * Application entry point.
	 * 
	 * Contract:
	 * - Creates the initial LoginOrRegister GUI
	 * - Displays the window to the user
	 * - Hands off control to Swing event dispatch thread
	 * 
	 * @param args  Command line arguments (currently not used)
	 * 
	 * Execution flow:
	 * 1. Instantiate LoginOrRegister GUI object
	 * 2. Call runGUI() to make the window visible
	 * 3. Application continues running until user closes all windows
	 * 
	 * Note:
	 * - This method returns immediately after showing the GUI
	 * - The application continues running on the Swing Event Dispatch Thread
	 * - User interaction is handled by action listeners in GUI classes
	 */
	public static void main(String[] args) {
		
		LoginOrRegister loginorRegisterGUI = new LoginOrRegister();
		
		loginorRegisterGUI.runGUI();
		
	}

}

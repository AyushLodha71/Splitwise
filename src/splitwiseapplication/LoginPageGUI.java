package splitwiseapplication;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * LoginPageGUI - User Login Screen with Password Visibility Toggle
 * 
 * PURPOSE:
 * Provides a GUI interface for existing users to authenticate and access
 * their account. Includes username/password fields with password visibility
 * toggle and validates credentials against the database.
 * 
 * FUNCTIONALITY:
 * - Accepts username and password input
 * - Validates credentials against db2.Credentials table
 * - Provides show/hide password toggle with eye icons
 * - Navigates to Groups menu after successful authentication
 * - Displays error if credentials are incorrect
 * - Allows navigation back to LoginOrRegister screen
 * 
 * USER FLOW:
 * 1. User enters username and password
 * 2. User can toggle password visibility (eye icon button)
 * 3. User clicks Submit or presses Enter
 * 4. System validates credentials in db2.Credentials
 * 5. If valid: Navigates to Groups menu
 * 6. If invalid: Shows "Wrong Username or Password" error
 * 7. Alternative: User clicks Back → returns to LoginOrRegister
 * 
 * AUTHENTICATION:
 * - Username AND password must match a record in db2.Credentials
 * - Query: db2.Credentials WHERE username=[input] AND password=[input]
 * - Uses Exists.exist() to check if matching record exists
 * - No password hashing (plain text comparison)
 * 
 * PASSWORD VISIBILITY:
 * - Default: Password hidden (dots/asterisks)
 * - Open eye icon = password hidden (click to show)
 * - Closed eye icon = password visible (click to hide)
 * - Toggle implemented by setting JPasswordField echo char to 0 or default
 * 
 * ICON LOADING:
 * Uses loadIconCrossPlatform() for cross-platform compatibility:
 * - Tries classpath first (works in JAR and IDE)
 * - Falls back to filesystem relative path
 * - Scales icons to 18x18 pixels
 * 
 * NAVIGATION:
 * - Entry Point: LoginOrRegister.java (when user selects "Login")
 * - Exit Points:
 *   - Groups.java (after successful login)
 *   - LoginOrRegister.java (when clicking Back)
 * 
 * UI COMPONENTS:
 * - JTextField for username (default text: "username")
 * - JPasswordField for password (10 character width)
 * - JButton with eye icon for show/hide toggle
 * - JButton for Submit
 * - JButton for Back
 * - GridLayout with 2 columns
 * 
 * DESIGN PATTERN:
 * - Implements ActionListener for event handling
 * - Static frame pattern (single shared JFrame instance)
 * - Constructor builds UI, runGUI() displays it
 * - GridBagLayout for password field with inline toggle button
 * 
 * SECURITY CONSIDERATIONS:
 * - ⚠️ Plain text password comparison (no hashing)
 * - ⚠️ Password visible in memory as String (not char[])
 * - ⚠️ No brute force protection or rate limiting
 * 
 * @author Original Development Team
 */
public class LoginPageGUI implements ActionListener{
	
	static JFrame frame;
	JPanel contentPane,passwordPane;
	JLabel usernamePrompt, passwordPrompt;
	JButton hideShowButton, submitButton, backButton;
	JTextField username;
	JPasswordField password;
	char defaultEchoChar;
	ImageIcon openEyeIcon, closedEyeIcon;
	
	/**
	 * Constructor - Initialize LoginPageGUI
	 * 
	 * PURPOSE:
	 * Creates the login interface with username/password fields
	 * and password visibility toggle.
	 * 
	 * BEHAVIOR:
	 * - Creates JFrame with title "Splitwise - Login Page"
	 * - Sets up GridLayout with 2 columns
	 * - Creates username field (pre-filled with "username")
	 * - Creates password field (10 character width)
	 * - Stores default password echo character for hide/show toggle
	 * - Loads eye icons for password visibility toggle (18x18px)
	 * - Creates eye icon button next to password field
	 * - Creates Submit and Back buttons
	 * - Configures frame but keeps invisible (runGUI() displays it)
	 * 
	 * ICON LOADING:
	 * - Uses loadIconCrossPlatform() for cross-platform compatibility
	 * - Tries classpath resource first, then filesystem fallback
	 * - visible.png = open eye (password hidden state)
	 * - invisible.png = closed eye (password visible state)
	 * 
	 * PASSWORD PANEL:
	 * - Uses GridBagLayout to place password field and button inline
	 * - Button shows open eye initially (password is hidden)
	 * 
	 * UI LAYOUT:
	 * [Username Label]    [Username TextField]
	 * [Password Label]    [Password Field + Eye Button]
	 * [Submit Button]     [Back Button]
	 * 
	 * ENTER KEY BEHAVIOR:
	 * Pressing Enter in either username or password field triggers Submit.
	 */
	public LoginPageGUI() {
		
		 /* Create and set up the frame */
		/* Create and set up the frame */
		 frame = new JFrame("Splitwise - Login Page");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 /* Create a content pane */
		 contentPane = new JPanel();
		 contentPane.setLayout(new GridLayout(0, 2, 10, 5));
		 contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		 
		 passwordPane = new JPanel();
		 passwordPane.setLayout(new GridBagLayout());
		 
		 /* Create and add label */
		 usernamePrompt = new JLabel("Enter your username: ");
		 contentPane.add(usernamePrompt);
		 
		 username = new JTextField("username");
		 username.addActionListener(this);
		 username.setActionCommand("Submit");
		 contentPane.add(username);
		 
		 passwordPrompt = new JLabel("Enter your password: ");
		 contentPane.add(passwordPrompt);
		 
		 password = new JPasswordField(10);
		 password.addActionListener(this);
		 password.setActionCommand("Submit");
		 passwordPane.add(password);
		 
		 defaultEchoChar = password.getEchoChar();
		 
		int iconSize = 18;
		openEyeIcon = loadIconCrossPlatform("/visible.png", iconSize, "src", "splitwiseapplication", "visible.png");
		closedEyeIcon = loadIconCrossPlatform("/invisible.png", iconSize, "src", "splitwiseapplication", "invisible.png");


		 hideShowButton = new JButton();
		 hideShowButton.setIcon(openEyeIcon);
		 hideShowButton.addActionListener(this);
		 hideShowButton.setActionCommand("show");
		 hideShowButton.setPreferredSize(new Dimension(openEyeIcon.getIconWidth(), openEyeIcon.getIconHeight()));
		 passwordPane.add(hideShowButton);
		 
		 contentPane.add(passwordPane);
		 
		 submitButton = new JButton("Submit");
		 submitButton.addActionListener(this);
		 submitButton.setActionCommand("Submit");
		 contentPane.add(submitButton);
		 
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
	 * actionPerformed - Handle User Actions (Button Clicks, Enter Key, Password Toggle)
	 * 
	 * PURPOSE:
	 * Event handler for all user interactions in the LoginPageGUI.
	 * Manages authentication, password visibility toggle, and navigation.
	 * 
	 * EVENT TYPES:
	 * 
	 * 1. "show" - Show password (from eye button click)
	 *    - Sets echo char to 0 (makes password visible as plain text)
	 *    - Changes button icon to closed eye
	 *    - Changes action command to "hide"
	 * 
	 * 2. "hide" - Hide password (from eye button click)
	 *    - Restores default echo char (dots/asterisks)
	 *    - Changes button icon to open eye
	 *    - Changes action command to "show"
	 * 
	 * 3. "Submit" - Initial login attempt (from Submit button or Enter key)
	 *    - Gets username and password from fields
	 *    - Validates credentials exist in db2.Credentials
	 *    - If valid:
	 *      - Navigates to Groups menu with username
	 *      - Disposes current frame
	 *    - If invalid:
	 *      - Displays "Wrong Username or Password" error label
	 *      - Changes action commands to "Submitted" (prevents re-checking)
	 * 
	 * 4. "Back" - Navigate back to LoginOrRegister
	 *    - Creates LoginOrRegister instance
	 *    - Shows LoginOrRegister screen
	 *    - Disposes current frame
	 * 
	 * 5. "Submitted" - Login attempt after credentials were invalid
	 *    - Same validation logic as "Submit"
	 *    - Allows user to try again with corrected credentials
	 *    - Re-validates against database
	 *    - Only navigates if valid (no duplicate error on failure)
	 * 
	 * AUTHENTICATION QUERY:
	 * db2.Credentials WHERE username=[input] AND password=[input]
	 * - Must match both username AND password
	 * - Plain text comparison (no hashing)
	 * 
	 * PASSWORD TOGGLE LOGIC:
	 * - Echo char = 0: Password visible as plain text
	 * - Echo char = default: Password hidden (usually a dot or asterisk)
	 * - Icon reflects current state, not next action
	 * 
	 * ERROR HANDLING:
	 * After showing error, changes all action commands to "Submitted" to
	 * prevent duplicate error labels on subsequent login attempts.
	 * 
	 * @param event The ActionEvent containing the action command
	 */
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		if (eventName.equals("show")) {
			password.setEchoChar((char) 0);
			hideShowButton.setIcon(closedEyeIcon);
			hideShowButton.setActionCommand("hide");
		} else if (eventName.equals("hide")) {
			password.setEchoChar(defaultEchoChar);
			hideShowButton.setIcon(openEyeIcon);
			hideShowButton.setActionCommand("show");
		} else if (eventName.equals("Submit")) {
			String usrname = username.getText();
			String pwd = password.getText();
			if (Exists.exist("https://splitwise.up.railway.app/db2/GetRowData?table=Credentials&username=" + usrname + "&password=" + pwd) == true) {
				Groups groups = new Groups(usrname);
				groups.runGUI();
				frame.dispose();
			} else {
				JLabel displayError = new JLabel("Wrong Username or Password");
				password.setActionCommand("Submitted");
				submitButton.setActionCommand("Submitted");
				username.setActionCommand("Submitted");
				contentPane.add(displayError);
				frame.setContentPane(contentPane);
				frame.pack();
			}
		} else if (eventName.equals("Back")){
			LoginOrRegister loginorRegisterGUI = new LoginOrRegister();
			loginorRegisterGUI.runGUI();
			frame.dispose();
		} else if (eventName.equals("Submitted")) {
			String usrname = username.getText();
			String pwd = password.getText();
			if (Exists.exist("https://splitwise.up.railway.app/db2/GetRowData?table=Credentials&username=" + usrname + "&password=" + pwd) == true) {
				Groups groups = new Groups(usrname);
				groups.runGUI();
				frame.dispose();
			}
		}
		
	}

	/**
	 * loadIconCrossPlatform - Load Icon with Fallback Support
	 * 
	 * PURPOSE:
	 * Loads image icons in a cross-platform way that works in both IDE
	 * and packaged JAR environments, with filesystem fallback.
	 * 
	 * BEHAVIOR:
	 * Tries two approaches in order:
	 * 
	 * APPROACH 1: Classpath Resource
	 * - Attempts to load via getClass().getResourceAsStream()
	 * - Works in IDE when resources are in classpath
	 * - Works in JAR when images are packaged in resources
	 * - If successful, reads image and scales to specified size
	 * 
	 * APPROACH 2: Filesystem Fallback
	 * - Constructs path relative to user.dir (working directory)
	 * - Combines fallbackPathParts into filesystem path
	 * - Checks if file exists using Files.exists()
	 * - If found, reads image and scales to specified size
	 * 
	 * APPROACH 3: Failure
	 * - Returns null if both approaches fail
	 * - Caller should handle null gracefully
	 * 
	 * SCALING:
	 * - If size > 0: Scales image to size x size using SCALE_SMOOTH
	 * - If size <= 0: Returns original image size
	 * 
	 * ERROR HANDLING:
	 * - Catches IOException from both approaches
	 * - Prints stack trace (for debugging)
	 * - Falls through to next approach rather than failing completely
	 * 
	 * USAGE:
	 * Called by constructor to load visible.png and invisible.png icons.
	 * Currently used: 2 call sites (LoginPageGUI constructor)
	 * 
	 * EXAMPLE:
	 * loadIconCrossPlatform("/visible.png", 18, "src", "splitwiseapplication", "visible.png")
	 * - First tries: classpath:/visible.png
	 * - Then tries: [user.dir]/src/splitwiseapplication/visible.png
	 * - Scales to 18x18 pixels
	 * 
	 * @param classpathResource The resource path starting with "/" for classpath lookup
	 * @param size The target width/height in pixels (0 or negative = no scaling)
	 * @param fallbackPathParts Variable args combined to form filesystem path
	 * @return ImageIcon with scaled image, or null if not found
	 */
	private ImageIcon loadIconCrossPlatform(String classpathResource, int size, String... fallbackPathParts) {
		// 1) try classpath (works in IDE and inside JAR)
		try (InputStream is = getClass().getResourceAsStream(classpathResource)) {
			if (is != null) {
				BufferedImage img = ImageIO.read(is);
				if (img != null) {
					Image scaled = (size > 0) ? img.getScaledInstance(size, size, Image.SCALE_SMOOTH) : img;
					return new ImageIcon(scaled);
				}
			}
		} catch (IOException e) {
			// fallthrough to fallback
			e.printStackTrace();
		}

		// 2) try a portable filesystem fallback (relative to working dir)
		if (fallbackPathParts != null && fallbackPathParts.length > 0) {
			Path p = Paths.get(System.getProperty("user.dir"), fallbackPathParts);
			if (Files.exists(p)) {
				try {
					BufferedImage img = ImageIO.read(p.toFile());
					if (img != null) {
						Image scaled = (size > 0) ? img.getScaledInstance(size, size, Image.SCALE_SMOOTH) : img;
						return new ImageIcon(scaled);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// 3) not found
		return null;
	}
	

	/**
	 * runGUI - Display the LoginPageGUI Window
	 * 
	 * PURPOSE:
	 * Makes the LoginPageGUI frame visible to the user.
	 * Final step in showing the login interface.
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
	 * Called on LoginPageGUI instance to display its frame.
	 * 
	 * USAGE:
	 * Called by LoginOrRegister.java after creating LoginPageGUI instance.
	 * Currently used: 1 call site (LoginOrRegister.actionPerformed)
	 */
	public void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}
	
}

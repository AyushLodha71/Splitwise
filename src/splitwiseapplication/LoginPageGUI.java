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
import java.net.URL;

public class LoginPageGUI implements ActionListener{
	
	static JFrame frame;
	JPanel contentPane,passwordPane;
	JLabel usernamePrompt, passwordPrompt;
	JButton hideShowButton, submitButton, backButton;
	JTextField username;
	JPasswordField password;
	char defaultEchoChar;
	ImageIcon openEyeIcon, closedEyeIcon;
	
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
		 
		 
		 try {
	            openEyeIcon = new ImageIcon(new URL("https://img.icons8.com/material/24/000000/visible.png"));
	            closedEyeIcon = new ImageIcon(new URL("https://img.icons8.com/material/24/000000/invisible.png"));
	        } catch (Exception e) {
	            System.err.println("Error loading icons from URLs. Please provide local paths.");
	            e.printStackTrace();
	            // Fallback: create empty icons to prevent NullPointerExceptions
	            openEyeIcon = new ImageIcon();
	            closedEyeIcon = new ImageIcon();
	        }
		 
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
			boolean loginSuccess;
			loginSuccess = VerifyCredentials(usrname,pwd);
			if (loginSuccess == true) {
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
			boolean loginSuccess;
			loginSuccess = VerifyCredentials(usrname,pwd);
			if (loginSuccess == true) {
				Groups groups = new Groups(usrname);
				groups.runGUI();
				frame.dispose();
			}
		}
		
	}
	
	public boolean VerifyCredentials(String uname, String pass) {
		
		FileReader in;
		BufferedReader readFile;
		String usrnme;
		File textFile;
		
		textFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\credentials.txt");
		try {
			in = new FileReader(textFile);
			readFile = new BufferedReader(in);
			while ((usrnme = readFile.readLine()) != null ) {
				String[] myArray = usrnme.split(",");
				if (myArray[0].equals(uname) && myArray[1].equals(pass)) {
					return true;
				}
			}
			readFile.close();
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException: "
					+ e.getMessage());
		} catch (IOException e) {;
			System.err.println("IOException: " + e.getMessage());
		}
		return false;
		
	}

		 /**
		 * Create and show the GUI.
		 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}
	
}

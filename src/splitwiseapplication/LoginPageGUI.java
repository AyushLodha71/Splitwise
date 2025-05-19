package splitwiseapplication;

import java.awt.GridLayout;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.*;

public class LoginPageGUI implements ActionListener{
	
	static JFrame frame;
	JPanel contentPane;
	JLabel usernamePrompt, passwordPrompt;
	JButton submitButton,backButton;
	JTextField username, password;
	
	public LoginPageGUI() {
		
		 /* Create and set up the frame */
		/* Create and set up the frame */
		 frame = new JFrame("Login Page");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 /* Create a content pane */
		 contentPane = new JPanel();
		 contentPane.setLayout(new GridLayout(0, 2, 10, 5));
		 contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		 /* Create and add label */
		 usernamePrompt = new JLabel("Enter your username: ");
		 contentPane.add(usernamePrompt);
		 
		 username = new JTextField("username");
		 contentPane.add(username);
		 
		 passwordPrompt = new JLabel("Enter your password: ");
		 contentPane.add(passwordPrompt);
		 
		 password = new JTextField("password");
		 contentPane.add(password);
		 
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
		
		if (eventName.equals("Submit")) {
			String usrname = username.getText();
			String pwd = password.getText();
			boolean loginSuccess;
			loginSuccess = VerifyCredentials(usrname,pwd);
			if (loginSuccess == true) {
				Groups groups = new Groups(usrname);
				groups.runGUI();
				frame.dispose();
			} else {
				System.out.println("Wrong username or password");
			}
		} else {
			LoginOrRegister loginorRegisterGUI = new LoginOrRegister();
			loginorRegisterGUI.runGUI();
			frame.dispose();
		}
		
	}
	
	public boolean VerifyCredentials(String uname, String pass) {
		
		FileReader in;
		BufferedReader readFile;
		String usrnme;
		File textFile;
		
		textFile = new File("/Users/macbookpro/Desktop/Java/splitwiseApplication/src/splitwiseapplication/credentials.txt");
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

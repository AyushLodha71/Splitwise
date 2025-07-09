package splitwiseapplication;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.ArrayList;
import javax.swing.*;

public class RegisterPageGUI implements ActionListener{
	
	static JFrame frame;
	JPanel contentPane,passwordPane;
	JLabel usernamePrompt, passwordPrompt;
	JButton hideShowButton, submitButton, backButton;
	JTextField username;
	JPasswordField password;
	char defaultEchoChar;
	ImageIcon openEyeIcon, closedEyeIcon;
	
	public RegisterPageGUI() {
		
		 /* Create and set up the frame */
		 frame = new JFrame("Splitwise - Register Page");
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
		
		if (eventName == "Submit") {
			String usrname = username.getText();
			String pwd = password.getText();
			File textFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\credentials.txt");
			if (Exists.exist(usrname,textFile) == false) {
				AddNewUser(usrname,pwd);
				Groups groups = new Groups(usrname);
				groups.runGUI();
				frame.dispose();
			} else {
				System.out.println("Username already taken");
			}
		} else {
			LoginOrRegister loginorRegisterGUI = new LoginOrRegister();
			loginorRegisterGUI.runGUI();
			frame.dispose();
		}
		
	}

	public void AddNewUser(String uname, String psswd) {
		
		File textFile,newFile;
		
		// Adding Username and Password to  credentials.txt
		textFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\credentials.txt");
		
		UpdateFile.Update(uname,psswd,textFile);
		
		//Creating a personal room for the username
		newFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\Personal_Folders\\"+uname);
		
		try {
			 newFile.createNewFile();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
	 
	}
	
		 /**
		 * Create and show the GUI.
		 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}
	
}

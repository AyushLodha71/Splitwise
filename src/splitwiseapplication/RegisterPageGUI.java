package splitwiseapplication;

import java.awt.GridLayout;
import java.awt.List;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import javax.swing.*;

public class RegisterPageGUI implements ActionListener{
	
	static JFrame frame;
	JPanel contentPane;
	JLabel usernamePrompt, passwordPrompt;
	JButton submitButton, backButton;
	JTextField username, password;
	
	public RegisterPageGUI() {
		
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
			if (exists(usrname) == false) {
				AddNewUser(usrname,pwd);
				Groups.runGUI();
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
	
	public boolean exists(String uname) {
		
		FileReader in;
		BufferedReader readFile;
		String usrnme;
		File textFile;
		ArrayList<String> usrnamelst = new ArrayList<String>();
		int location;
		
		textFile = new File("/Users/macbookpro/Desktop/Java/splitwiseApplication/src/splitwiseapplication/credentials.txt");
		
		try {
			in = new FileReader(textFile);
			readFile = new BufferedReader(in);
			while ((usrnme = readFile.readLine()) != null ) {
				String[] myArray = usrnme.split(",");
				usrnamelst.add(myArray[0]);
			}
			readFile.close();
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException: "
					+ e.getMessage());
		} catch (IOException e) {;
			System.err.println("IOException: " + e.getMessage());
		}
		
		Sorts.mergesort(usrnamelst, 0, usrnamelst.size()-1);
		
		location = Searches.binarySearch(usrnamelst, 0, usrnamelst.size()-1, uname);
		
		if (location == -1) {
			return false;
		} else {
			return true;
		}
		
	}

	public void AddNewUser(String uname, String psswd) {
		
		File textFile,newFile;
		
		// Adding Username and Password to  credentials.txt
		textFile = new File("/Users/macbookpro/Desktop/Java/splitwiseApplication/src/splitwiseapplication/credentials.txt");
		
		UpdateFile.Update(uname,psswd,textFile);
		
		//Creating a personal room for the username
		newFile = new File("/Users/macbookpro/Desktop/Java/splitwiseApplication/src/splitwiseapplication/Personal_Folders/"+uname);
		
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

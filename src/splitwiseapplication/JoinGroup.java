package splitwiseapplication;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

public class JoinGroup implements ActionListener {

	static JFrame frame;
	JPanel contentPane;
	JLabel joinPrompt, success;
	JTextField joinCode;
	JButton enterButton,backButton;
	ArrayList<String> groups = new ArrayList<String>();
	static String uname;
	
	public JoinGroup(String username) {
		
		uname = username;
		
		 /* Create and set up the frame */
		 frame = new JFrame("Splitwise - Join Group");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 /* Create a content pane */
		 contentPane = new JPanel();
		 contentPane.setLayout(new GridLayout(0, 3, 10, 5));
		 contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		 /* Create and add label */
		 joinPrompt = new JLabel("Enter the group code to join");
		 contentPane.add(joinPrompt);
		 joinCode = new JTextField();
		 joinCode.addActionListener(this);
		 joinCode.setActionCommand("Join Group");
		 contentPane.add(joinCode);
		 
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
		
		if (eventName.equals("Enter") == true) {
			String code = joinCode.getText();
			MainPage mpage = new MainPage(uname,code);
			mpage.runGUI();
			frame.dispose();
		} else if ((eventName.equals("Join Group") == true)){
			String code = joinCode.getText();
			File groupFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\Groups\\"+code);
			
			if (checkExistence(code) && Exists.exist(uname,groupFile)==false) {
				AddNewUser(code,uname);
				success = new JLabel("Success!");
				enterButton = new JButton("Enter");
				joinCode.setActionCommand("Group Joined");
				enterButton.addActionListener(this);
				enterButton.setActionCommand("Enter");
				contentPane.add(success);
				contentPane.add(enterButton);
				frame.setContentPane(contentPane);
				frame.pack();
			} else {
				JLabel displayError = new JLabel("Wrong Code");
				contentPane.add(displayError);
				frame.setContentPane(contentPane);
				frame.pack();
			}
		} else if (eventName.equals("Back") == true) {
			Groups groups = new Groups(uname);
			groups.runGUI();
			frame.dispose();
		}
		
	}
	
	public static boolean checkExistence(String ucode) {
		
		FileReader in;
		File textFile;
		
		textFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\Groups\\"+ucode);
		
		if (textFile.exists()) {
			return true;
		} else {
			return false;
		}
		
	}

	public static void AddNewUser(String code, String usrname) {
		
		File userFile, newFile, casFile;
		
		String gname;
		
		AddPerms(code);
		
		newFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\Groups\\"+code);
		
		UpdateFile.Update(usrname,newFile);
		
		userFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\Personal_Folders\\"+usrname);
		
		gname = RetrieveName(code);
		
		UpdateFile.Update(gname,userFile); 
		
		casFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\CheckAmountSpentFolder\\"+code);
		
		UpdateFile.Update(uname,"0",casFile);
		
	}
	
	public static void AddPerms(String gcode) {
		
		File newFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\Groups\\"+gcode);
		ArrayList<String> lst = new ArrayList<String>(); 
		lst = Exists.exists(gcode, newFile);
		
		for (String i : lst) {
			UpdateFile.Update(uname,"0", i, new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PendingAmount\\"+gcode));
		}
	}
	
	public static String RetrieveName(String c) {
		
		FileReader in;
		BufferedReader readFile;
		String line;
		File textFile;
		ArrayList<String> usrnamelst = new ArrayList<String>();
		int location;
		
		textFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\groups.txt");
		
		try {
			in = new FileReader(textFile);
			readFile = new BufferedReader(in);
			while ((line = readFile.readLine()) != null ) {
				String[] myArray = line.split(",");
				if (myArray[0].equals(c)) {
					return myArray[1];
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException: "
					+ e.getMessage());
		} catch (IOException e) {;
			System.err.println("IOException: " + e.getMessage());
		}
		
		return (c);
		
	}
	
		 /**
		 * Create and show the GUI.
		 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}

	
}

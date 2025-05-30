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
import java.util.Random;

public class CreateGroup implements ActionListener{

	static JFrame frame;
	JPanel contentPane;
	JLabel createPrompt,displayCode;
	JTextField createGroup;
	JButton enterButton,backButton;
	ArrayList<String> groups = new ArrayList<String>();
	String uname, code;
	
	public CreateGroup(String username) {
		
		uname = username;
		
		 /* Create and set up the frame */
		 frame = new JFrame("Splitwise - Create Group");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 /* Create a content pane */
		 contentPane = new JPanel();
		 contentPane.setLayout(new GridLayout(0, 3, 10, 5));
		 contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		 /* Create and add label */
		 createPrompt = new JLabel("Enter the name of the new group");
		 contentPane.add(createPrompt);
		 createGroup = new JTextField();
		 createGroup.addActionListener(this);
		 createGroup.setActionCommand("Create New Group");
		 contentPane.add(createGroup);
		 
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
			String gname = createGroup.getText();
			MainPage mpage = new MainPage(uname,code);
			mpage.runGUI();
			frame.dispose();
		} else if ((eventName.equals("Create New Group") == true)){
			String text = createGroup.getText();
			
			File txtFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\groups.txt");
			ArrayList<String> usedCodes = Exists.exists(uname,txtFile);
			
			code = createCode(usedCodes);
			
			AddNewGroup(code,text);
			
			displayCode = new JLabel("The group code is: " + code);
			enterButton = new JButton("Enter");
			createGroup.setActionCommand("Group Created");
			enterButton.addActionListener(this);
			enterButton.setActionCommand("Enter");
			contentPane.add(displayCode);
			contentPane.add(enterButton);
			frame.setContentPane(contentPane);
			frame.pack();
			
		} else if (eventName.equals("Back") == true) {
			Groups groups = new Groups(uname);
			groups.runGUI();
			frame.dispose();
		}
		
	}
	
	public static String createCode(ArrayList<String> codesUsed) {
		
		String newcode;
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
		
		do {
			newcode = "";
	        for (int i = 0; i < 7; i++) {
	            int index = random.nextInt(characters.length());
	            newcode += characters.charAt(index);
	        }
		} while (codesUsed.contains(newcode));
		
		return newcode;
		
	}
	
	public void AddNewGroup(String gcode, String gname) {
		
		File textFile,newFile, userFile;
		
		// Adding Username and Password to  credentials.txt
		textFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\groups.txt");
		
		UpdateFile.Update(gcode,gname,textFile);
		
		newFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\Groups\\"+gcode);
		CreateFile(newFile);
		CreateFile(new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PaymentHistory\\"+gcode));
		CreateFile(new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PendingAmount\\"+gcode));
		UpdateFile.Update(uname,newFile);
		UpdateFile.Update(uname,"0",new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\PendingAmount\\"+gcode));
		userFile = new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\Personal_Folders\\"+uname);
		UpdateFile.Update(gname,userFile);
		
	}

	public void CreateFile(File newFile) {
		
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

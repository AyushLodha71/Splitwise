package splitwiseapplication;

import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;

public class LoginOrRegister implements ActionListener{
	
	static JFrame frame;
	JPanel contentPane;
	JLabel loginPrompt,registerPrompt;
	JButton loginButton,registerButton;
	
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
		 * Create and show the GUI.
		 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}
	
}

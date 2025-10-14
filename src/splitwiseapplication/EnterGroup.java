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

/**
 * EnterGroup - Group Selection Interface
 * 
 * This class provides a GUI for users to select and enter one of their existing groups.
 * It displays a dropdown list of all groups the user belongs to, allowing them to:
 * - View all their groups in a sorted list
 * - Select a group from the dropdown
 * - Enter the selected group to view transactions and perform actions
 * - Navigate back to the main groups page
 * 
 * The interface dynamically updates to show an "Enter [GroupName] Group" button
 * once a group is selected from the dropdown.
 * 
 * Groups are loaded from the user's personal folder and displayed in alphabetical order.
 * 
 * @version 2.0 - Enhanced with comprehensive documentation and improved UX
 */
public class EnterGroup implements ActionListener{

	// Main application frame
	static JFrame frame;
	
	// UI container panel
	JPanel contentPane;
	
	// Dropdown list for group selection
	JLabel optionsPrompt;
	JComboBox options;
	
	// Navigation and action buttons
	JButton backButton, groupEnter;
	
	// List of user's groups loaded from personal folder
	ArrayList<String> groups = new ArrayList<String>();
	
	// Current username
	String uname;
	
	// Selected group code for entering
	String rcode = "";
	
	/**
	 * Constructor - Builds the Group Selection Interface
	 * 
	 * Creates a dropdown list populated with the user's groups and provides
	 * navigation options:
	 * 1. Loads user's groups from personal folder (sorted alphabetically)
	 * 2. Creates dropdown with "Select Group" as default option
	 * 3. Dynamically displays "Enter [GroupName] Group" button when selection changes
	 * 
	 * @param username The logged-in user's username
	 */
	public EnterGroup(String username) {
		
		uname = username;
		
		/* Setup main frame */
		frame = new JFrame("Splitwise - Enter Group");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/* Create content pane with 3-column grid layout */
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayout(0, 3, 10, 5));
		contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		 
		/* Create and populate group selection dropdown */
		options = new JComboBox();
		options.setToolTipText("Select a group to enter");
		groups = Read(username);
		groups.add(0, "Select Group" ); // Add placeholder at top
		for (int i = 0; i < groups.size(); i++) {
			options.addItem(groups.get(i));
		}
		
		// Listen for group selection changes
		options.addActionListener(this);
		options.setActionCommand("First"); // First selection creates Enter button
		contentPane.add(options);
		
		/* Back button - return to groups page */
		backButton = new JButton("Back");
		backButton.setToolTipText("Return to groups page");
		backButton.addActionListener(this);
		backButton.setActionCommand("Back");
		contentPane.add(backButton);
		
		/* Finalize frame setup */
		frame.setContentPane(contentPane);
		frame.pack();
		frame.setVisible(false);
		
	}

	/**
	 * Reads User's Group List from Personal Folder
	 * 
	 * Loads all group names that the user belongs to from their personal folder file.
	 * Groups are sorted alphabetically using merge sort before being returned.
	 * 
	 * @param usrname The username whose groups to load
	 * @return Sorted list of group names the user belongs to
	 */
	public static ArrayList<String> Read(String usrname) {
			
			String usrnme;
			File textFile;
			ArrayList<String> grouplist = new ArrayList<>();
			
			// Load groups from user's personal folder
			textFile = new File("src/splitwiseapplication/Personal_Folders/"+usrname);
			
			try (FileReader in = new FileReader(textFile);
			     BufferedReader readFile = new BufferedReader(in)) {
				while ((usrnme = readFile.readLine()) != null ) {
					String[] myArray = usrnme.split(",");
					grouplist.add(myArray[0]); // Group name is first element
				}
			} catch (FileNotFoundException e) {
				System.err.println("FileNotFoundException: "
						+ e.getMessage());
			} catch (IOException e) {
				System.err.println("IOException: " + e.getMessage());
			}
			
			// Sort groups alphabetically for easier navigation
			Sorts.mergesort(grouplist, 0, grouplist.size()-1);
			
			return grouplist;
			
		}
	
	/**
	 * Event Handler - Processes user interactions
	 * 
	 * Handles four types of actions:
	 * 1. Back - Returns to main groups page
	 * 2. First - Initial group selection, creates "Enter Group" button
	 * 3. Later - Subsequent group selections, updates button text
	 * 4. Enter - Enters the selected group's main page
	 * 
	 * The interface dynamically updates the Enter button text based on
	 * the currently selected group.
	 * 
	 * @param event The action event from user interaction
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		String selectedItem;
		
		if ("Back".equals(eventName)) {
			// Navigate back to groups page
			Groups groupsPage = new Groups(uname);
			groupsPage.runGUI();
			frame.dispose();
			
		} else if ("First".equals(eventName)){
			// First group selection - create Enter button
			selectedItem = (String) options.getSelectedItem();
			rcode = RetrieveCode(selectedItem);
			
			groupEnter = new JButton("Enter " + selectedItem + " Group");
			groupEnter.setToolTipText("Click to enter " + selectedItem);
			groupEnter.addActionListener(this);
			groupEnter.setActionCommand("Enter");
			contentPane.add(groupEnter);
			
			// Change action command for subsequent selections
			options.setActionCommand("Later");
			frame.setContentPane(contentPane);
			frame.pack();
			
		} else if ("Later".equals(eventName)) {
			// Subsequent group selection - update button text
			selectedItem = (String) options.getSelectedItem();
			rcode = RetrieveCode(selectedItem);
			groupEnter.setText("Enter " + selectedItem + " Group");
			groupEnter.setToolTipText("Click to enter " + selectedItem);
			
		} else if("Enter".equals(eventName)) {
			// Enter the selected group
			MainPage mpage = new MainPage(uname,rcode);
			mpage.runGUI();
			frame.dispose();
		}
		
	}
	
	/**
	 * Retrieves Group Code from Group Name
	 * 
	 * Searches the groups.txt file to find the unique group code
	 * associated with a given group name. The group code is used
	 * to access group-specific data files.
	 * 
	 * File format: groupCode,groupName
	 * 
	 * @param n The group name to look up
	 * @return The group code corresponding to the group name
	 */
	public static String RetrieveCode(String n) {
		
		String line;
		File textFile;
		
		textFile = new File("src/splitwiseapplication/groups.txt");
		
		try (FileReader in = new FileReader(textFile);
		     BufferedReader readFile = new BufferedReader(in)) {
			while ((line = readFile.readLine()) != null ) {
				String[] myArray = line.split(",");
				if (myArray[1].equals(n)) {
					return myArray[0]; // Return the group code
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException: "
					+ e.getMessage());
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
		return (n); // Fallback: return the name itself if not found
		
	}

	/**
	 * Display the Enter Group GUI
	 * 
	 * Makes the group selection frame visible to the user.
	 * Should be called after constructing an EnterGroup object.
	 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}
	
}

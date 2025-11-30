package splitwiseapplication;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * CheckAmountSpent - Expenditure Summary Screen
 * 
 * PURPOSE:
 * Displays a table showing how much each group member has spent or owes.
 * Provides a quick overview of individual member balances in the group.
 * 
 * FUNCTIONALITY:
 * - Shows table with 3 columns: Id, Name, Amount Spent
 * - Displays all group members and their total spending
 * - Read-only view (no editing)
 * - Back button returns to MainPage
 * 
 * TABLE STRUCTURE:
 * - Column 1 (Id): Database row ID
 * - Column 2 (Name): Member username or "Total" for group total
 * - Column 3 (Amount Spent): Total amount spent by member
 * 
 * AMOUNT INTERPRETATION:
 * - Positive amount: Member has spent money (paid expenses)
 * - Negative amount: Member owes money
 * - "Total" row: Shows total group expenditure
 * 
 * DATA SOURCE:
 * - db1 table: Individual member balances
 * - First row typically contains "Total" for entire group
 * - Subsequent rows contain individual member data
 * 
 * UI FEATURES:
 * - Scrollable table (auto-sized to content)
 * - Vertical box layout
 * - Centered "Back" button below table
 * - Auto-sized frame based on table content
 * 
 * NAVIGATION:
 * - From: MainPage (Check Amount Spent button)
 * - To: MainPage (Back button)
 * 
 * USAGE:
 * Called from MainPage.java when user clicks "Check Amount Spent" button.
 * Currently used: 1 call site (MainPage.actionPerformed)
 */
public class CheckAmountSpent implements ActionListener{
	
	static JFrame frame;
	JPanel contentPane;
	String uname, gcode;
	ArrayList<String[]> values = new ArrayList<String[]>();
	JButton back;
	JTable expenditureTable;
	DefaultTableModel tableModel;
	String[] columnNames = new String[3];
	
	/**
	 * CheckAmountSpent Constructor - Initialize Expenditure Summary Screen
	 * 
	 * PURPOSE:
	 * Creates the UI for viewing group member spending in a table format.
	 * Fetches balance data and displays in organized, readable table.
	 * 
	 * @param username The current user's username
	 * @param groupcode The group code
	 * 
	 * PROCESS:
	 * 
	 * 1. INITIALIZATION:
	 *    - Stores username and group code
	 *    - Creates frame with BoxLayout (vertical arrangement)
	 *    - Sets fixed frame size: 300x500
	 *    - Adds padding border (20,50,20,50)
	 * 
	 * 2. DATA RETRIEVAL:
	 *    - Queries db1 for all member balances
	 *    - values array contains: [id, name, amount] for each member
	 *    - First row typically "Total" showing group total
	 * 
	 * 3. TABLE SETUP:
	 *    - Column headers: "Id", "Name", "Amount Spent"
	 *    - Creates 2D array for table data
	 *    - Populates from index 1 (skips header row from API)
	 *    - Creates DefaultTableModel with data and column names
	 * 
	 * 4. UI CONSTRUCTION:
	 *    - Creates JTable with table model
	 *    - Wraps table in JScrollPane for scrolling
	 *    - Auto-sizes scroll pane based on table content
	 *    - Adds vertical spacing (10 pixels)
	 *    - Adds centered "Back" button below table
	 * 
	 * 5. TABLE SIZING:
	 *    - Calculates preferred height: table height + header height
	 *    - Width: table width + 40 pixels padding
	 *    - Ensures all data visible without excessive white space
	 * 
	 * 6. BUTTON SETUP:
	 *    - Back button centered horizontally
	 *    - Action command: "Back"
	 *    - Returns to MainPage when clicked
	 * 
	 * DATABASE QUERY:
	 * - db1 table: Individual member balances
	 * - Returns all rows including "Total" row
	 * 
	 * POST-CONDITIONS:
	 * - Frame created but invisible (call runGUI() to display)
	 * - Table populated with current member spending data
	 * - Back button configured with action listener
	 */
	public CheckAmountSpent(String username, String groupcode) {
		
		uname = username;
		gcode = groupcode;
		
		frame = new JFrame("Splitwise - Check Balances Page");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 500);
		/* Create a content pane */
		contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		
		values = new ArrayList<>(Arrays.asList(ApiCaller.ApiCaller1("https://splitwise.up.railway.app/db1/GetRowData?table="+ gcode)));

        columnNames[0] = "Id";
		columnNames[1] = "Name";
        columnNames[2] = "Amount Spent";
        
		Object[][] rowData = new Object[values.size()][values.get(0).length];

		for (int i = 1; i < values.size(); i++) {
			rowData[i] = values.get(i);
		}

        tableModel = new DefaultTableModel(rowData, columnNames);
        
		expenditureTable = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(expenditureTable);
		int preferredTableHeight = expenditureTable.getPreferredSize().height + expenditureTable.getTableHeader().getPreferredSize().height;
        scrollPane.setPreferredSize(new Dimension(expenditureTable.getPreferredSize().width + 40, preferredTableHeight + 3));
		contentPane.add(scrollPane);

        
        contentPane.add(Box.createVerticalStrut(10));
		back = new JButton("Back");
		back.addActionListener(this);
		back.setActionCommand("Back");
		back.setAlignmentX(Component.CENTER_ALIGNMENT); 
		contentPane.add(back);
		
		frame.setContentPane(contentPane);
		frame.pack();
		frame.setVisible(false);
		
	}
	
	/**
	 * actionPerformed - Handle User Actions (Back Button)
	 * 
	 * PURPOSE:
	 * Event handler for user interactions in the CheckAmountSpent screen.
	 * Currently only handles navigation back to MainPage.
	 * 
	 * EVENT TYPES:
	 * 
	 * 1. "Back" - Return to Main Page
	 *    - Creates MainPage instance with username and group code
	 *    - Shows MainPage screen
	 *    - Disposes current frame
	 * 
	 * SIMPLE NAVIGATION:
	 * This screen is read-only (no data modification), so only
	 * navigation action is returning to MainPage.
	 * 
	 * @param event The ActionEvent containing the action command
	 */
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		if (eventName.equals("Back")) {
			MainPage mpage = new MainPage(uname,gcode);
			mpage.runGUI();
			frame.dispose();
		} 
		
	}
	
	/**
	 * runGUI - Display the CheckAmountSpent Window
	 * 
	 * PURPOSE:
	 * Makes the CheckAmountSpent frame visible to the user.
	 * Final step in showing the expenditure summary interface.
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
	 * Called on CheckAmountSpent instance to display its frame.
	 * 
	 * USAGE:
	 * Called by MainPage.java after creating CheckAmountSpent instance.
	 * Currently used: 1 call site (MainPage.actionPerformed)
	 */
	public void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}

}

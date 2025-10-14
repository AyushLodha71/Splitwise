package splitwiseapplication;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * CheckAmountSpent - Individual Spending Summary Interface
 * 
 * This class displays a table showing how much each group member has spent
 * in total across all transactions. This is different from CheckBalances:
 * 
 * CheckAmountSpent: Shows TOTAL SPENDING per member
 * - "John spent $150.00 total"
 * - Tracks contributions to expenses only
 * - Does NOT show who owes whom
 * 
 * CheckBalances: Shows WHO OWES WHOM
 * - "John owes Sarah $25.00"
 * - Shows current debt state
 * - Accounts for both expenses and settlements
 * 
 * Use Case:
 * This interface is useful for:
 * - Seeing who has contributed the most to group expenses
 * - Verifying your total spending on the group
 * - Understanding spending patterns across members
 * 
 * Data Source: CheckAmountSpentFolder/{groupCode}
 * File Format: "memberName,totalSpent"
 * - Updated when expenses are added (Type 0 transactions)
 * - NOT updated for settlements (Type 1 transactions)
 * - Represents cumulative spending since group creation
 * 
 * Display Format:
 * A simple two-column table:
 * | Name    | Amount Spent |
 * |---------|--------------|
 * | John    | $150.00      |
 * | Sarah   | $75.50       |
 * 
 * @version 2.0 - Enhanced with comprehensive documentation and improved UX
 */
public class CheckAmountSpent implements ActionListener{
	
	// Main application frame
	static JFrame frame;
	
	// Main content panel
	JPanel contentPane;
	
	// Current user and group identifiers
	String uname, gcode;
	
	// Spending data: each entry is [memberName, totalSpent]
	ArrayList<String[]> values = new ArrayList<>();
	
	// Back button to return to dashboard
	JButton back;
	
	// Table displaying spending data
	JTable expenditureTable;
	JLabel mmbrl;
	Object[][] rowData;
	DefaultTableModel tableModel;
	
	// Table column headers
	String[] columnNames = new String[2];
	
	
	/**
	 * Constructs the CheckAmountSpent interface displaying spending totals.
	 * 
	 * This constructor:
	 * 1. Loads spending data from CheckAmountSpentFolder file
	 * 2. Creates a two-column table (Name | Amount Spent)
	 * 3. Populates the table with member spending data
	 * 4. Sizes the table to fit content exactly
	 * 
	 * Table Layout:
	 * - Column 1: Member name
	 * - Column 2: Total amount spent by that member
	 * 
	 * The table auto-sizes to show all data without scrolling
	 * (unless there are many members).
	 * 
	 * Data Loading:
	 * Reads CheckAmountSpentFolder/{groupCode} file
	 * Format: "memberName,totalSpent"
	 * Each line represents one member's cumulative spending
	 * 
	 * @param username The current user (for returning to MainPage)
	 * @param groupcode The group code to load spending data for
	 */
	public CheckAmountSpent(String username, String groupcode) {
		
		uname = username;
		gcode = groupcode;
		
		frame = new JFrame("Splitwise - Check Amount Spent");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 500);
		
		// Create main content panel with vertical layout
		contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		
		// Load spending data from file
		values = Exists.contents(new File("src/splitwiseapplication/CheckAmountSpentFolder/"+gcode),",");

		// Set up table column headers
        columnNames[0] = "Name";
        columnNames[1] = "Amount Spent";
        
		// Populate table data from values list
		Object[][] rowData;
		if (values.isEmpty()) {
			// No spending data yet
			rowData = new Object[0][2];
		} else {
			// Convert ArrayList to 2D array for JTable
			rowData = new Object[values.size()][values.get(0).length];
			for (int i = 0; i < values.size(); i++) {
				rowData[i] = values.get(i);
			}
		}
        
		// Create table with spending data
        tableModel = new DefaultTableModel(rowData, columnNames);
        expenditureTable = new JTable(tableModel);
        expenditureTable.setToolTipText("Total spending by each member");
        
		// Wrap table in scrollable pane
		JScrollPane scrollPane = new JScrollPane(expenditureTable);
		
		// Size scrollpane to fit table content exactly
		int preferredTableHeight = expenditureTable.getPreferredSize().height + expenditureTable.getTableHeader().getPreferredSize().height;
        scrollPane.setPreferredSize(new Dimension(expenditureTable.getPreferredSize().width + 40, preferredTableHeight + 3));
		contentPane.add(scrollPane);

        // Add spacing before back button
        contentPane.add(Box.createVerticalStrut(10));
        
		// Add back button
		back = new JButton("Back");
		back.setToolTipText("Return to group dashboard");
		back.addActionListener(this);
		back.setActionCommand("Back");
		back.setAlignmentX(Component.CENTER_ALIGNMENT); 
		contentPane.add(back);
		
		frame.setContentPane(contentPane);
		frame.pack();
		frame.setVisible(false);
		
	}
	
	/**
	 * Handles the Back button click event.
	 * 
	 * Returns the user to the MainPage (group dashboard).
	 * 
	 * @param event The button click event
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		if (eventName.equals("Back")) {
			// Return to group dashboard
			MainPage mpage = new MainPage(uname,gcode);
			mpage.runGUI();
			frame.dispose();
		}
		
	}
	
	/**
	 * Displays the CheckAmountSpent window.
	 * 
	 * Makes the spending summary interface visible to the user.
	 */
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}}

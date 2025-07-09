package splitwiseapplication;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CheckAmountSpent implements ActionListener{
	
	static JFrame frame;
	JPanel contentPane;
	String uname, gcode;
	ArrayList<String[]> values = new ArrayList<String[]>();
	JButton back;
	JTable expenditureTable;
	JLabel mmbrl;
	Object[][] rowData;
	DefaultTableModel tableModel;
	String[] columnNames = new String[2];
	
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
		
		values = Exists.contents(new File("D:\\Ayush\\SplitwiseApplication\\src\\splitwiseapplication\\CheckAmountSpent\\"+gcode),",");

        columnNames[0] = "Name";
        columnNames[1] = "Amount Spent";
        
		Object[][] rowData = new Object[values.size()][values.get(0).length];
        
        for (int i = 0; i < values.size(); i++) {
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
	
	public void actionPerformed(ActionEvent event) {
		
		String eventName = event.getActionCommand();
		
		if (eventName.equals("Back")) {
			MainPage mpage = new MainPage(uname,gcode);
			mpage.runGUI();
			frame.dispose();
		} 
		
	}
	
	public static void runGUI() {
		 
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setVisible(true);
		
	}

}

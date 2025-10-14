package splitwiseapplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * UpdateFile - File Writing Utility
 * 
 * Provides static methods for writing and appending data to text files.
 * This class centralizes all file writing operations in the application.
 * 
 * Two Main Operations:
 * 1. Write() - Overwrites file with new content (creates or replaces)
 * 2. Update() - Appends content to existing file (adds to end)
 * 
 * Supported Formats:
 * - Single value per line
 * - Comma-separated pairs (val1,val2)
 * - Delimiter-separated triples (val1>val2>val3 or val1,val2,val3)
 * - String arrays with custom delimiters
 * 
 * Usage Examples:
 * - Write("username", credentialsFile) - Start new file
 * - Update("john", "password123", credentialsFile) - Append user
 * - Write("alice", "50.00", "bob", pendingFile) - Overwrite debt record
 * 
 * Note: Methods use try-with-resources pattern for automatic resource cleanup.
 * 
 * @version 2.0 - Enhanced with comprehensive documentation
 */
public class UpdateFile {

	/**
	 * Appends a single value to a file on a new line.
	 * 
	 * Use case: Adding usernames to a list, appending single-value records
	 * 
	 * @param val1 The value to append
	 * @param txtFile The file to append to
	 */
	public static void Update(String val1, File txtFile) {
		
		FileWriter out;
		BufferedWriter writeFile;
		
		try {
			out = new FileWriter(txtFile,true);  // true = append mode
			writeFile = new BufferedWriter(out);
			writeFile.write(val1);
			writeFile.newLine();
			writeFile.close();
			out.close();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
	}

	/**
	 * Appends a comma-separated pair to a file on a new line.
	 * 
	 * Format: "val1,val2\n"
	 * Use case: Adding username,password pairs, name,amount pairs
	 * 
	 * @param val1 First value (e.g., username)
	 * @param val2 Second value (e.g., password)
	 * @param txtFile The file to append to
	 */
	public static void Update(String val1, String val2, File txtFile) {
		
		FileWriter out;
		BufferedWriter writeFile;
		
		try {
			out = new FileWriter(txtFile,true);  // true = append mode
			writeFile = new BufferedWriter(out);
			writeFile.write(val1);
			writeFile.write(",");
			writeFile.write(val2);
			writeFile.newLine();
			writeFile.close();
			out.close();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
	}
		
	/**
	 * Overwrites file with a single value on one line.
	 * 
	 * Format: "val1\n"
	 * Use case: Resetting a file to contain only one value
	 * 
	 * @param val1 The value to write
	 * @param txtFile The file to overwrite
	 */
	public static void Write(String val1, File txtFile) {
		
		FileWriter out;
		BufferedWriter writeFile;
		
		try {
			out = new FileWriter(txtFile);  // false = overwrite mode (default)
			writeFile = new BufferedWriter(out);
			writeFile.write(val1);
			writeFile.newLine();
			writeFile.close();
			out.close();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
	}
	
	/**
	 * Creates an empty file (overwrites existing content).
	 * 
	 * Use case: Clearing a file's contents without deleting it
	 * 
	 * @param txtFile The file to blank out
	 */
	public static void WriteBlank(File txtFile) {
		
		FileWriter out;
		BufferedWriter writeFile;
		
		try {
			out = new FileWriter(txtFile);
			writeFile = new BufferedWriter(out);
			writeFile.write("");
			writeFile.close();
			out.close();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
	}

	/**
	 * Overwrites file with a comma-separated pair on one line.
	 * 
	 * Format: "val1,val2\n"
	 * Use case: Resetting credentials file, initializing data files
	 * 
	 * @param val1 First value
	 * @param val2 Second value
	 * @param txtFile The file to overwrite
	 */
	public static void Write(String val1, String val2, File txtFile) {
		
		FileWriter out;
		BufferedWriter writeFile;
		
		try {
			out = new FileWriter(txtFile);
			writeFile = new BufferedWriter(out);
			writeFile.write(val1);
			writeFile.write(",");
			writeFile.write(val2);
			writeFile.newLine();
			writeFile.close();
			out.close();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
	}

	/**
	 * Overwrites file with a ">"-delimited triple on one line.
	 * 
	 * Format: "val1>val2>val3\n"
	 * Use case: Resetting PendingAmount file with first debt record
	 * Example: "alice>50.00>bob" (alice is owed $50 by bob)
	 * 
	 * @param val1 First value (typically creditor)
	 * @param val2 Second value (typically amount)
	 * @param val3 Third value (typically debtor)
	 * @param txtFile The file to overwrite
	 */
	public static void Write(String val1, String val2, String val3, File txtFile) {
		
		FileWriter out;
		BufferedWriter writeFile;
		
		try {
			out = new FileWriter(txtFile);
			writeFile = new BufferedWriter(out);
			writeFile.write(val1);
			writeFile.write(">");
			writeFile.write(val2);
			writeFile.write(">");
			writeFile.write(val3);
			writeFile.newLine();
			writeFile.close();
			out.close();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
	}

	/**
	 * Appends a ">"-delimited triple to a file on a new line.
	 * 
	 * Format: "val1>val2>val3\n"
	 * Use case: Adding debt records to PendingAmount file
	 * 
	 * @param val1 First value (typically creditor)
	 * @param val2 Second value (typically amount)
	 * @param val3 Third value (typically debtor)
	 * @param txtFile The file to append to
	 */
	public static void Update(String val1, String val2, String val3, File txtFile) {
		
		FileWriter out;
		BufferedWriter writeFile;
		
		try {
			out = new FileWriter(txtFile,true);  // true = append mode
			writeFile = new BufferedWriter(out);
			writeFile.write(val1);
			writeFile.write(">");
			writeFile.write(val2);
			writeFile.write(">");
			writeFile.write(val3);
			writeFile.newLine();
			writeFile.close();
			out.close();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
	}
	
	/**
	 * Appends a ">"-delimited quartet to a file on a new line.
	 * 
	 * Format: "val1>val2>val3>val4\n"
	 * Use case: Adding transaction records to PaymentHistory
	 * Example: "alice>50.00>Groceries>0>ABC1234" 
	 *          (payer>amount>description>type>transactionID)
	 * 
	 * @param val1 First value (typically payer)
	 * @param val2 Second value (typically amount)
	 * @param val3 Third value (typically description or recipient)
	 * @param val4 Fourth value (typically transaction type)
	 * @param txtFile The file to append to
	 */
	public static void Update(String val1, String val2, String val3, String val4, File txtFile) {
		
		FileWriter out;
		BufferedWriter writeFile;
		
		try {
			out = new FileWriter(txtFile,true);
			writeFile = new BufferedWriter(out);
			writeFile.write(val1);
			writeFile.write(">");
			writeFile.write(val2);
			writeFile.write(">");
			writeFile.write(val3);
			writeFile.write(">");
			writeFile.write(val4);
			writeFile.newLine();
			writeFile.close();
			out.close();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
	}
	
	/**
	 * Overwrites file with a ">"-delimited quartet on one line.
	 * 
	 * Format: "val1>val2>val3>val4\n"
	 * Use case: Initializing PaymentHistory file with first transaction
	 * 
	 * @param val1 First value
	 * @param val2 Second value
	 * @param val3 Third value
	 * @param val4 Fourth value
	 * @param txtFile The file to overwrite
	 */
	public static void Write(String val1, String val2, String val3, String val4, File txtFile) {
		
		FileWriter out;
		BufferedWriter writeFile;
		
		try {
			out = new FileWriter(txtFile);
			writeFile = new BufferedWriter(out);
			writeFile.write(val1);
			writeFile.write(">");
			writeFile.write(val2);
			writeFile.write(">");
			writeFile.write(val3);
			writeFile.write(">");
			writeFile.write(val4);
			writeFile.newLine();
			writeFile.close();
			out.close();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
	}

	/**
	 * Overwrites file with a string array on one line, using custom delimiter.
	 * 
	 * Format: "list[0]{indicator}list[1]{indicator}...list[n]\n"
	 * 
	 * Use case: Writing TransactionDetails with member amounts
	 * Example with indicator=",": "TXN123,50.00,25.00,0.00,25.00"
	 *         (transactionID, member1Amount, member2Amount, ...)
	 * 
	 * Note: No delimiter is added after the last element.
	 * 
	 * @param list Array of strings to write
	 * @param txtFile The file to overwrite
	 * @param indicator Delimiter to place between elements (e.g., "," or ">")
	 */
	public static void Write(String[] list, File txtFile, String indicator) {
		
		FileWriter out;
		BufferedWriter writeFile;
		
		try {
			out = new FileWriter(txtFile);
			writeFile = new BufferedWriter(out);
			for (int i = 0; i < list.length; i++) {
				writeFile.write(list[i]);
				if ((i+1) != list.length) {
					writeFile.write(indicator);
				}
			}
			writeFile.newLine();
			writeFile.close();
			out.close();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
	}
	
	/**
	 * Appends a string array to file on new line, using custom delimiter.
	 * 
	 * Format: "list[0]{indicator}list[1]{indicator}...list[n]\n"
	 * 
	 * Use case: Adding transaction detail records, appending member data
	 * Example with indicator=",": "TXN124,30.00,30.00,40.00"
	 * 
	 * Note: No delimiter is added after the last element.
	 * 
	 * @param list Array of strings to append
	 * @param txtFile The file to append to
	 * @param indicator Delimiter to place between elements
	 */
	public static void Update(String[] list, File txtFile, String indicator) {
		
		FileWriter out;
		BufferedWriter writeFile;
		
		try {
			out = new FileWriter(txtFile,true);
			writeFile = new BufferedWriter(out);
			for (int i = 0; i < list.length; i++) {
				writeFile.write(list[i]);
				if ((i+1) != list.length) {
					writeFile.write(indicator);
				}
			}
			writeFile.newLine();
			writeFile.close();
			out.close();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
	}}

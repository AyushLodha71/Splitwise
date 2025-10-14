package splitwiseapplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Exists - File Reading and Existence Checking Utility
 * 
 * Provides static methods for reading file contents and checking if values exist in files.
 * This class centralizes all file reading operations in the application.
 * 
 * Main Operations:
 * 1. exist() - Check if a value exists in a file (uses binary search for efficiency)
 * 2. exists() - Extract first column values from a delimited file
 * 3. exists_Array() - Find and return a specific row from a delimited file
 * 4. contents() - Read entire file as array of delimited rows
 * 5. contents_STR() - Read entire file as list of strings (one per line)
 * 
 * Performance Note:
 * exist() method sorts the data and uses binary search (O(log n)) for fast lookups.
 * This is more efficient than linear search for large files.
 * 
 * @version 2.0 - Enhanced with comprehensive documentation
 */
public class Exists {

	/**
	 * Extracts the first column from each row of a delimited file.
	 * 
	 * File Format: Each line contains comma-separated values
	 * Example file:
	 *   TXN123,50.00,25.00
	 *   TXN124,30.00,40.00
	 * 
	 * Returns: ["TXN123", "TXN124"]
	 * 
	 * Use case: Getting list of transaction IDs, extracting usernames
	 * 
	 * @param uname Not used in this method (legacy parameter)
	 * @param textFile The file to read from
	 * @return ArrayList of first column values from each row
	 */
	public static ArrayList<String> exists(String uname, File textFile) {
		
		FileReader in;
		BufferedReader readFile;
		String line;
		ArrayList<String> grouplist = new ArrayList<>();
		int location;
		
		try {
			in = new FileReader(textFile);
			readFile = new BufferedReader(in);
			while ((line = readFile.readLine()) != null ) {
				String[] myArray = line.split(",");
				grouplist.add(myArray[0]);  // Extract first column only
			}
			readFile.close();
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException: "
					+ e.getMessage());
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
		return grouplist;
		
	}
	
	/**
	 * Finds and returns a specific row from a delimited file.
	 * 
	 * Searches for a row where the first column matches the find parameter.
	 * 
	 * File Format: Each line contains comma-separated values
	 * Example: "TXN123,50.00,25.00,0.00"
	 * 
	 * Use case: Retrieving transaction details by ID, finding user data by username
	 * 
	 * @param find The value to search for in the first column
	 * @param textFile The file to search in
	 * @return String array of the matching row, or empty array if not found
	 */
	public static String[] exists_Array(String find, File textFile) {
		
		FileReader in;
		BufferedReader readFile;
		String line;
		String[] grouplist = new String[0];
		int location;
		
		try {
			in = new FileReader(textFile);
			readFile = new BufferedReader(in);
			while ((line = readFile.readLine()) != null ) {
				String[] myArray = line.split(",");
				if(myArray[0].equals(find)) {
					return myArray;  // Found the row, return immediately
				}
			}
			readFile.close();
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException: "
					+ e.getMessage());
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
		return grouplist;  // Not found, return empty array
		
	}
	
	/**
	 * Checks if a value exists in the first column of a comma-delimited file.
	 * 
	 * This method is optimized for performance:
	 * 1. Reads all first-column values
	 * 2. Sorts them using merge sort
	 * 3. Performs binary search (O(log n))
	 * 
	 * File Format: Each line contains comma-separated values
	 * Example: "username,password"
	 * 
	 * Use case: Checking if username exists, validating group codes
	 * 
	 * Performance: O(n log n) for sorting + O(log n) for search
	 * Better than O(n) linear search for repeated lookups.
	 * 
	 * @param find The value to search for
	 * @param textFile The file to search in
	 * @return true if value exists, false otherwise
	 */
	public static Boolean exist(String find, File textFile) {
		
		FileReader in;
		BufferedReader readFile;
		String usrnme;
		ArrayList<String> usrnamelst = new ArrayList<>();
		int location;
		
		try {
			in = new FileReader(textFile);
			readFile = new BufferedReader(in);
			while ((usrnme = readFile.readLine()) != null ) {
				String[] myArray = usrnme.split(",");
				usrnamelst.add(myArray[0]);  // Extract first column
			}
			readFile.close();
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException: "
					+ e.getMessage());
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
		// Sort the list for binary search
		Sorts.mergesort(usrnamelst, 0, usrnamelst.size()-1);
		
		// Perform binary search
		location = Searches.binarySearch(usrnamelst, 0, usrnamelst.size()-1, find);
		
		
		if (location == -1) {
			return false;  // Not found
		} else {
			return true;  // Found
		}
		
	}
	
	/**
	 * Reads entire file and returns as array of delimited rows.
	 * 
	 * Each line is split by the specified delimiter and returned as a String array.
	 * 
	 * File Format Examples:
	 * - indicator=">": "alice>50.00>bob" → ["alice", "50.00", "bob"]
	 * - indicator=",": "john,password123" → ["john", "password123"]
	 * 
	 * Use cases:
	 * - Reading PendingAmount file (delimiter=">")
	 * - Reading CheckAmountSpent file (delimiter=",")
	 * - Reading PaymentHistory file (delimiter=">")
	 * 
	 * @param textFile The file to read
	 * @param indicator The delimiter to split each line by
	 * @return ArrayList of String arrays, one per line
	 */
	public static ArrayList<String[]> contents(File textFile, String indicator) {
		
		FileReader in;
		BufferedReader readFile;
		String line;
		ArrayList<String[]> grouplist = new ArrayList<String[]>();
		int location;
		
		try {
			in = new FileReader(textFile);
			readFile = new BufferedReader(in);
			while ((line = readFile.readLine()) != null ) {
				String[] myArray = line.split(indicator);
				grouplist.add(myArray);
			}
			readFile.close();
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException: "
					+ e.getMessage());
		} catch (IOException e) {;
			System.err.println("IOException: " + e.getMessage());
		}
		
		return grouplist;
		
	}

	/**
	 * Reads entire file and returns as list of strings (one per line).
	 * 
	 * Each line is kept as a complete string without splitting.
	 * 
	 * File Format: Each line is returned as-is
	 * Example file:
	 *   alice
	 *   bob
	 *   charlie
	 * 
	 * Returns: ["alice", "bob", "charlie"]
	 * 
	 * Use cases:
	 * - Reading Groups/{groupCode} file (list of member names)
	 * - Reading simple text lists
	 * 
	 * @param textFile The file to read
	 * @return ArrayList of strings, one per line
	 */
	public static ArrayList<String> contents_STR(File textFile) {
		
		FileReader in;
		BufferedReader readFile;
		String line;
		ArrayList<String> grouplist = new ArrayList<>();
		
		try {
			in = new FileReader(textFile);
			readFile = new BufferedReader(in);
			while ((line = readFile.readLine()) != null ) {
				grouplist.add(line);
			}
			readFile.close();
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException: "
					+ e.getMessage());
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
		return grouplist;
		
	}
	
}

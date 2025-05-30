package splitwiseapplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Exists {

	public static ArrayList<String> exists(String uname, File textFile) {
		
		FileReader in;
		BufferedReader readFile;
		String line;
		ArrayList<String> grouplist = new ArrayList<String>();
		int location;
		
		try {
			in = new FileReader(textFile);
			readFile = new BufferedReader(in);
			while ((line = readFile.readLine()) != null ) {
				String[] myArray = line.split(",");
				grouplist.add(myArray[0]);
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
	
	public static Boolean exist(String find, File textFile) {
		
		FileReader in;
		BufferedReader readFile;
		String usrnme;
		ArrayList<String> usrnamelst = new ArrayList<String>();
		int location;
		
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
		
		location = Searches.binarySearch(usrnamelst, 0, usrnamelst.size()-1, find);
		
		
		if (location == -1) {
			return false;
		} else {
			return true;
		}
		
	}
	
}

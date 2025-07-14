package splitwiseapplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class UpdateFile {

	public static void Update(String val1, File txtFile) {
		
		FileWriter out;
		BufferedWriter writeFile;
		
		try {
			out = new FileWriter(txtFile,true);
			writeFile = new BufferedWriter(out);
			writeFile.write(val1);
			writeFile.newLine();
			writeFile.close();
			out.close();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
	}

	public static void Update(String val1, String val2, File txtFile) {
		
		FileWriter out;
		BufferedWriter writeFile;
		
		try {
			out = new FileWriter(txtFile,true);
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

	public static void Update(String val1, String val2, String val3, File txtFile) {
		
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
			writeFile.newLine();
			writeFile.close();
			out.close();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
	}
	
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
		
	}

}

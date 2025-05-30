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

}

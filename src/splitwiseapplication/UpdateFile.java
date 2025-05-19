package splitwiseapplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class UpdateFile {

	public static void Update(String usrname, String passwd, File txtFile) {
		
		FileWriter out;
		BufferedWriter writeFile;
		
		try {
			out = new FileWriter(txtFile,true);
			writeFile = new BufferedWriter(out);
			writeFile.write(usrname);
			writeFile.write(",");
			writeFile.write(passwd);
			writeFile.newLine();
			writeFile.close();
			out.close();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		}
		
	}
	
}

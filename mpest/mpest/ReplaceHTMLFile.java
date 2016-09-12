import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;


public class ReplaceHTMLFile {

	public static void main(String[] args) {
		
		try {
			String fileName = args[0];
			String fileName2 = args[1];
			String outputFile = args[2];
		    FileWriter fwriter = new FileWriter(outputFile);
		    BufferedWriter out = new BufferedWriter(fwriter);

		    String tree = "";
			FileInputStream fstream = new FileInputStream(fileName2);
			DataInputStream din = new DataInputStream(fstream); 
			BufferedReader in = new BufferedReader(new InputStreamReader(din));			
			while (in.ready()) {
				String str = in.readLine();
				tree += str + "\n";
			}
			in.close();
			fstream = new FileInputStream(fileName);
			din = new DataInputStream(fstream); 
			in = new BufferedReader(new InputStreamReader(din));			
			while (in.ready()) {
				String str = in.readLine();
				
				str = str.replaceAll("ReplaceMeHere", tree.replaceAll("\n", "").replaceAll("\r", "").trim());
				
				out.write(str + "\n");
			}
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

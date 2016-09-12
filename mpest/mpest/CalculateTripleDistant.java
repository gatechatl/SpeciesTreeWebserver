
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class CalculateTripleDistant {

	public static void main(String[] args) {
		
		try {
			String fileName = args[0]; // number of lines in SNA
			LinkedList list = new LinkedList();
			FileInputStream fstream = new FileInputStream(fileName);
 			DataInputStream din = new DataInputStream(fstream); 
 			BufferedReader in = new BufferedReader(new InputStreamReader(din));			 			
 			while (in.ready()) { 				 			
 				String str = in.readLine();
 				if (str.trim().length() > 0) {
 					list.add(str);
 				}
 			}
 			in.close();
 			
 			

 			
 			String outtree = "";
 			String fileName3 = args[2]; // output tre
 			fstream = new FileInputStream(fileName3);
 			din = new DataInputStream(fstream); 
 			in = new BufferedReader(new InputStreamReader(din));
 			while (in.ready()) {
 				String str = in.readLine();
 				outtree = str;
 			}
 			in.close();
 			
 			String outputFile = args[3]; // new output tree file
 			FileWriter fwriter = new FileWriter(outputFile);
		    BufferedWriter out = new BufferedWriter(fwriter);
		    out.write(outtree + "\n");
		   
                        int num = 0; 
 			String fileName2 = args[1]; // number of trees in input.tre
 			HashMap trees = new HashMap();
			fstream = new FileInputStream(fileName2);
 			din = new DataInputStream(fstream); 
 			in = new BufferedReader(new InputStreamReader(din));
 			while (in.ready()) {
 				String str = in.readLine();
 				if (!str.trim().equals("")) {
                                        num++;
 					//trees.put(str, str);
 					out.write(str + "\n");
 				}
 			}
 			in.close();
 			out.close();
 			
 			String outputFile2 = args[4]; // new controlFile
 			FileWriter fwriter2 = new FileWriter(outputFile2);
		    BufferedWriter out2 = new BufferedWriter(fwriter2);
		    out2.write("tripleControl.tre\n");
		    out2.write("0\n-1\n");
		    out2.write((num + 1)  + " " + list.size() + "\n");
		    Iterator itr = list.iterator();
		    while (itr.hasNext()) {
		    	String str = (String)itr.next();
		    	out2.write(str + "\n");
		    }
		    out2.write("0");
		    out2.close();
		} catch (Exception e) {
			
		}
	}
}


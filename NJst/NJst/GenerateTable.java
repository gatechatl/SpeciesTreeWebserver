
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;



public class GenerateTable {

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

			String fileName2 = args[1]; // number of lines in SNA
			LinkedList list2 = new LinkedList();
			FileInputStream fstream2 = new FileInputStream(fileName2);
 			DataInputStream din2 = new DataInputStream(fstream2); 
 			BufferedReader in2 = new BufferedReader(new InputStreamReader(din2));			 			
 			while (in2.ready()) { 				 			
 				String str = in2.readLine();
 				if (str.trim().length() > 0) {
 					list2.add(str);
 				}
 			}
 			in.close();
 			StringBuffer tablehtml = new StringBuffer();
 			String outputFile = args[2]; // new output tree file
 			FileWriter fwriter = new FileWriter(outputFile);
		    BufferedWriter out = new BufferedWriter(fwriter);
		    
 			int i = 1;
			if (list.size() == list2.size()) {
				Iterator itr = list.iterator();
				Iterator itr2 = list2.iterator();
				while (itr.hasNext()) {
					String line = "GeneTree" + i + "\t";
					String str1 = (String)itr2.next();
					line += str1 + "\t";
					String str2 = (String)itr.next();
					line += str2 + "\t";
					line += "GeneTree" + i + ".pdf\t";
                                        //line += "diffSpecies" + i + ".svg\t";
					line += "diffGene" + i + ".svg\t";
					out.write(line + "\n");
					tablehtml.append(tableRow(line));
					i++;					
				}
			}
 			
			out.close();
			
			
			
			
 			outputFile = args[4]; // finalHtml file
 			fwriter = new FileWriter(outputFile);
		    out = new BufferedWriter(fwriter);
		    
		    String html = "";
		    
		    String htmlFile = args[3]; // raw html
            fstream = new FileInputStream(htmlFile);
            din = new DataInputStream(fstream);
            in = new BufferedReader(new InputStreamReader(din));
            while (in.ready()) {
                String str = in.readLine();
                System.out.println(str);
                str = str.replaceAll("(ReplaceTable)", tablehtml.toString());
                html += str += "\n";

            }
            in.close();
            out.write(html);
            out.close();
            
            out.close();




		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String tableRow(String stuff) {
	    String[] split = stuff.split("\t");
	    String html = "<tr class=\"gradeA\">\n";
	    for (int i = 0; i < split.length; i++) {
	            html += "<td align=\"center\">" + split[i] + "</td>\n";
	    }
	    html += "</tr>\n";
	    return html;
	}
}


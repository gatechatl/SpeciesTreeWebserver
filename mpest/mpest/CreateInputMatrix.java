import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;


public class CreateInputMatrix {

    public static void main(String[] args) {
        try {
            String fileName = args[0];
            String spnameOutputFile = args[1]; // species
            String outputFile = args[2];
            String taxaFile = args[3];
            
            boolean first = true;
            boolean taxafirst = true;
            FileWriter spnamefwriter = new FileWriter(spnameOutputFile);
            BufferedWriter spname_out = new BufferedWriter(spnamefwriter);
            
            
            FileWriter fwriter = new FileWriter(outputFile);
            BufferedWriter out = new BufferedWriter(fwriter);
            
            FileWriter taxa_fwriter = new FileWriter(taxaFile);
            BufferedWriter taxa_out = new BufferedWriter(taxa_fwriter);
            
            
            FileInputStream fstream = new FileInputStream(fileName);
            DataInputStream din = new DataInputStream(fstream); 
            BufferedReader in = new BufferedReader(new InputStreamReader(din));         
            while (in.ready()) {
                String str = in.readLine();
                str = str.replaceAll("\t", " ");
                while (str.contains("  ")) {
                    str = str.replaceAll("  ", " ");
                }
                
                String[] split = str.split(" ");
                if (split.length >= 3) {
                    //int num = new Integer(split[1]);
                    if (first) {
                        out.write(split[1]);
                        spname_out.write(split[0]);
                        for (int i = 2; i < split.length; i++) {
                            if (taxafirst) {
                                taxa_out.write(split[i]);
                                taxafirst = false;
                            } else {
                                taxa_out.write("\t" + split[i]);
                            }
                        }
                        first = false;
                    } else {
                        out.write("\t" + split[1]);
                        spname_out.write("\t" + split[0]);
                        for (int i = 2; i < split.length; i++) {
                            taxa_out.write("\t" + split[i]);
                        }
                    }
                    
                }
            }
            in.close();
            out.close();
            taxa_out.close();
            spname_out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


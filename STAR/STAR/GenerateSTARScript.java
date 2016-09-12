import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;


public class GenerateSTARScript {

	
	public static void main(String[] args) {
		
		try {
			
			String path = args[0];
			String fileName = path + "/script.r";
		    FileWriter fwriter = new FileWriter(fileName);
		    BufferedWriter out = new BufferedWriter(fwriter);
		    String script = "library(phybase)\n" + 
		    		"genetrees <- read.tree.string(\"input.tre\",format=\"phylip\")$tree\n" + 
		    		"spname <- scan(\"spname.txt\",what=\"character\")\n" + 
		    		"nspecies <- length(spname)\n" + 
		    		"taxaname <- scan(\"taxaname.txt\", what=\"character\")\n" + 
		    		"numsgenenodes <- as.numeric(scan(\"numgenenode.txt\",what=\"numeric\"))\n" + 
		    		"species.structure <- spstructure(numsgenenodes)\n" +
		    		"output <- star.sptree(genetrees, spname, taxaname, species.structure, method=\"nj\")\n" + 
		    		"write(output, file = \"output.tre\")\n";
		    out.write(script);
		    out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


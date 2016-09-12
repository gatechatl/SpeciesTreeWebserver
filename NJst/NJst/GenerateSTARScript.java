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
		    		"genetrees <- scan(\"" + path + "/input.tre\",what=\"character\")\n" + 
		    		"spname<-species.name(genetrees[1])\n" + 
		    		"nspecies<-length(spname)\n" + 
		    		"taxaname <- spname\n" + 
		    		"species.structure<-matrix(0,nspecies,nspecies)\n" + 
		    		"diag(species.structure)<-1\n" + 
		    		"output = star.sptree(genetrees, spname, taxaname, species.structure,outgroup=\"W\",method=\"nj\")\n" + 
		    		"write(output, file = \"" + path + "output.tre\");\n";
		    out.write(script);
		    out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


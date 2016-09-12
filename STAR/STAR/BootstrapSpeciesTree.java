import java.io.* ;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A simple class to demonstrate a recursive directory traversal
 * in Java.
 *
 * Error handling was left out to make the code easier to understand.
 * In production code, you should check if the arguments from the
 * command line are really file files or directories.
 */
public class BootstrapSpeciesTree
{
   /**
    * Works on a single file system entry and
    * calls itself recursively if it turns out
    * to be a directory.
    * @param file A file or a directory to process
    */
   public String traverse( File file )
   {
      // Print the name of the entry
      //System.out.println( file ) ;
      String fileName = file.getPath();
      // Check if it is a directory
      if( file.isDirectory() )
      {
         // Get a list of all the entries in the directory
         String entries[] = file.list() ;

         // Ensure that the list is not null
         if( entries != null )
         {
            // Loop over all the entries
            for( String entry : entries )
            {
               // Recursive call to traverse
               fileName += "\t" + traverse( new File(file,entry) ) ;
            }
         }
      }
      return fileName;
   }

   /**
    * The program starts here.
    * @param args The arguments from the command line
    */
   public static void main( String args[] )
   {
      // Create an object of this class
      BootstrapSpeciesTree rt = new BootstrapSpeciesTree() ;

      
      // If there are no arguments, traverse the current directory
      String bootstrapFolder = args[1];
      String outputFinalTree = args[2];
      String files = rt.traverse( new File(args[0]) ) ;
      System.out.println(files);
      String[] split = files.split("\t");
      try {
         HashMap map = new HashMap();
         int count = 0;
         
         for (int i = 0; i < split.length; i++) {
            
            String fileName = split[i];

            File file = new File(fileName);

            if (!(fileName.contains("DS_Store") || fileName.contains("__MACOSX")) && !file.isDirectory()) {

//            if (!fileName.contains("__MACOSX")) {
                String[] split2 = fileName.split("\\.");
                String fileType = split2[split2.length - 1];
		int total = 0;
                if (fileType.contains("boot") || fileType.contains("bs") || fileType.contains("nwk") || fileType.contains("tre") || fileType.contains("new") || fileType.contains("txt") || fileType.contains("phylip") || fileType.contains("out") || fileType.contains("phy")) {
                                                        
                    int min = 0;
                    FileWriter fwriter = new FileWriter(fileName + ".oneline.tre");
                    BufferedWriter out = new BufferedWriter(fwriter);
                    
                    FileInputStream fstream = new FileInputStream(fileName);
                    DataInputStream din = new DataInputStream(fstream); 
                    BufferedReader in = new BufferedReader(new InputStreamReader(din));         
                    while (in.ready()) {
                        String str = in.readLine();
                        str = str.replaceAll(";", ";\n");
                        out.write(str);
                    }
                    in.close();
                    out.close();
                    if (!checkIfFileIsRooted(fileName + ".oneline.tre", bootstrapFolder)) {
                        System.out.println("Not Rooted");
                        System.exit(0);
                    }
                    String trees = "";
                    fstream = new FileInputStream(fileName + ".oneline.tre");
                    din = new DataInputStream(fstream); 
                    in = new BufferedReader(new InputStreamReader(din));            
                    while (in.ready()) {
                        String str = in.readLine();
                        min++;
                        if (trees.equals("")) {
                            trees += str;
                        } else {
                            trees += "\t" + str;
                        }
                    }
                    in.close();
                    if (count == 0) {
                        count = min;
                    }
                    if (count > min) {
                        count = min;
                    }
                    map.put(fileName + ".oneline.tre", trees);                  
                }
            }
         }        
         String finalTrees = "";
         for (int i = 0; i < count; i++) {
            FileWriter fwriter = new FileWriter(bootstrapFolder + "/speciesTree" + i + ".tre");
            BufferedWriter out = new BufferedWriter(fwriter);

            FileWriter fwriter2 = new FileWriter(bootstrapFolder + "/format_input_pre.tre");
            BufferedWriter out2 = new BufferedWriter(fwriter2);
             Iterator itr = map.keySet().iterator();
             while (itr.hasNext()) {
                    String fileName = (String)itr.next();
                    String trees = (String)map.get(fileName);
                    String[] tree = trees.split("\t");
                    out.write(tree[i] + "\n");
                    out2.write(tree[i] + "\n");
                    
             }
             out.close();
             out2.close();
	     executeCommand(bootstrapFolder, "./inputtree_rewrite.py " + bootstrapFolder + "/format_input_pre.tre > " + bootstrapFolder + "/format_input.tre");
             executeCommand(bootstrapFolder, "cp script.r " + bootstrapFolder + "/script.r");
             executeCommand(bootstrapFolder, "cd " + bootstrapFolder + "\n" + "R --vanilla < script.r \ncp output.tre output_" + i + ".tre");

             
            FileInputStream fstream = new FileInputStream(bootstrapFolder + "/output.tre");
            DataInputStream din = new DataInputStream(fstream); 
            BufferedReader in = new BufferedReader(new InputStreamReader(din));         
            while (in.ready()) {
                String str = in.readLine();
                finalTrees += str;
            }
            in.close();
            finalTrees += "\n";
         }

          FileWriter fwriter = new FileWriter(outputFinalTree);
          BufferedWriter out = new BufferedWriter(fwriter);  
          out.write(finalTrees);
          out.close();
      } catch (Exception e) {
          e.printStackTrace();
      }      
   }
	   public static boolean checkIfFileIsRooted(String inputFile, String bootstrapFolder) {
		   try {
	 		    FileWriter fwriter = new FileWriter(bootstrapFolder + "/testRoot.r");
	 		    BufferedWriter out = new BufferedWriter(fwriter);
	 		    out.write("library(phybase)\n");
	 		    out.write("bad = FALSE;\n");
	 		    out.write("write(bad, file = \"" + bootstrapFolder + "/isroot\");\n");
	 		    out.write("genetrees <- read.tree.string(\"" + inputFile + "\",format=\"phylip\")$tree\n");
	 		    out.write("ntree <- length(genetrees);\n");
	 		    out.write("rooted = TRUE;\n");
	 		    //out.write("if (!exists(\"ntree\") {\n");
	 		    //out.write("    rooted = FALSE;\n");
	 		    //out.write("}\n");
	 		    out.write("if (ntree == 0) {\n");
	 		    out.write("    rooted = FALSE;\n");
	 		    out.write("}\n");
	 		    out.write("for(i in 1:ntree) {\n");
	 		    out.write("    if (!is.rootedtree(genetrees[i])) {\n");
	 		    out.write("        rooted = FALSE;\n");
	 		    out.write("    }\n");
	 		    out.write("}\n");
	 		    out.write("write(rooted, file = \"" + bootstrapFolder + "/isroot\");\n");	 		    	
	 		    out.close();
		
	 		    executeCommand(bootstrapFolder, "R --vanilla < " + bootstrapFolder + "/testRoot.r");
	 			FileInputStream fstream = new FileInputStream(bootstrapFolder + "/isroot");
	 			DataInputStream din = new DataInputStream(fstream); 
	 			BufferedReader in = new BufferedReader(new InputStreamReader(din));			
	 			while (in.ready()) {
	 				String str = in.readLine();
	 				if (str.equals("TRUE")) {
	 					in.close();
	 					return true;
	 				} else {
	 					in.close();
	 					return false;
	 				}
	 			}
	 			in.close();
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
		   return false;
	   }
    public static void executeCommand(String file, String executeThis) {
        try {
            writeFile(file + "/tempexecuteCommand.sh", executeThis);
            String[] command = {"sh", file + "/tempexecuteCommand.sh"};
            Process p1 = Runtime.getRuntime().exec(command);                
            BufferedReader inputn = new BufferedReader(new InputStreamReader(p1.getInputStream()));            
            String line=null;
            while((line=inputn.readLine()) != null) {}                        
            inputn.close();
             
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void writeFile(String fileName, String command) {
        try {
            FileWriter fwriter2 = new FileWriter(fileName);
            BufferedWriter out2 = new BufferedWriter(fwriter2);
            out2.write(command + "\n");                 
            out2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

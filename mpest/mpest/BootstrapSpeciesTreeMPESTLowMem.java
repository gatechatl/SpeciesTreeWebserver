import java.io.* ;
import java.util.HashMap;
import java.util.Iterator;
import java.io.PrintWriter;
/**
 * A simple class to demonstrate a recursive directory traversal
 * in Java.
 *
 * Error handling was left out to make the code easier to understand.
 * In production code, you should check if the arguments from the
 * command line are really file files or directories.
 */
public class BootstrapSpeciesTreeMPESTLowMem
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
      BootstrapSpeciesTreeMPEST rt = new BootstrapSpeciesTreeMPEST() ;

      
      // If there are no arguments, traverse the current directory
      String bootstrapFolder = args[1];
      String outputFinalTree = args[2];
      String files = rt.traverse( new File(args[0]) ) ;
      System.out.println(files);
      String[] split = files.split("\t");
      try {
         HashMap map = new HashMap();
         int count = 0;
         int index_n = 0; 
         for (int i = 0; i < split.length; i++) {
            
            String fileName = split[i];
            File file = new File(fileName);
            if (!(fileName.contains("DS_Store") || fileName.contains("__MACOSX")) && !file.isDirectory()) {
		System.out.println(fileName);

//            if (!fileName.contains("__MACOSX")) {
                String[] split2 = fileName.split("\\.");
                String fileType = split2[split2.length - 1];
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

                    String trees = "";
                    fstream = new FileInputStream(fileName + ".oneline.tre" );
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
		    map.put(index_n, fileName + ".oneline.tre");
		    index_n++;
                    //map.put(fileName + ".oneline.tre", trees);                 
                    String[] split4 = trees.split("\t");

                    for (int k = 0; k < split4.length; k++) {
                    //FileWriter fwriter = new FileWriter(bootstrapFolder + "/speciesTree" + i + ".tre");
                    //BufferedWriter out = new BufferedWriter(fwriter);
                        PrintWriter out3 = new PrintWriter(new BufferedWriter(new FileWriter(bootstrapFolder + "/speciesTree" + k + ".tre", true)));
                        out3.println(split4[k]);
                        out3.close();
                    }

 
                }
            }
         }        
         String finalTrees = "";

         FileWriter fwriter25 = new FileWriter(bootstrapFolder + "/unrootedlist.txt");
         BufferedWriter out25 = new BufferedWriter(fwriter25);

         for (int i = 0; i < count; i++) {
            /*FileWriter fwriter = new FileWriter(bootstrapFolder + "/speciesTree" + i + ".tre");
            BufferedWriter out = new BufferedWriter(fwriter);

            FileWriter fwriter2 = new FileWriter(bootstrapFolder + "/input_pre.tre");
            BufferedWriter out2 = new BufferedWriter(fwriter2);
             Iterator itr = map.keySet().iterator();
             while (itr.hasNext()) {
                    String fileName = (String)itr.next();
                    String trees = (String)map.get(fileName);
                    String[] tree = trees.split("\t");
                    out.write(fileName + "\t" + tree[i] + "\n");
                    out2.write(tree[i] + "\n");
                    
             }
             out.close();
             out2.close();*/

	    HashMap map2 = new HashMap();
	    int index = 0;
            FileInputStream fstream77 = new FileInputStream(bootstrapFolder + "/speciesTree" + i + ".tre");
            DataInputStream din77 = new DataInputStream(fstream77);
            BufferedReader in77 = new BufferedReader(new InputStreamReader(din77));
            while (in77.ready()) {
                String str = in77.readLine();
		if (map.containsKey(index)) {
	  	    String fileName = (String)map.get(index);
		    map2.put(fileName, str);
	        }
                index++;
            }
            in77.close();
            FileWriter fwriter77 = new FileWriter(bootstrapFolder + "/input_pre.tre");
            BufferedWriter out77 = new BufferedWriter(fwriter77);
	    Iterator itr77 = map2.keySet().iterator();
	    while (itr77.hasNext()) {
	        String fileName = (String)itr77.next();
		String tree = (String)map2.get(fileName);
                out77.write(tree + "\n");

	    }
            out77.close();

	     //executeCommand(bootstrapFolder, "cp -r " + bootstrapFolder + "/speciesTree" + i + ".tre " + bootstrapFolder + "/input_pre.tre");
             executeCommand(bootstrapFolder, "./inputtree_rewrite.py " + bootstrapFolder + "/input_pre.tre > " + bootstrapFolder + "/input.tre");

            boolean checkTree = false;
            FileInputStream fstream25 = new FileInputStream(bootstrapFolder + "/input.tre");
            DataInputStream din25 = new DataInputStream(fstream25);
            BufferedReader in25 = new BufferedReader(new InputStreamReader(din25));
            while (in25.ready()) {
                String str = in25.readLine();
                if (str.contains(";")) {
			checkTree = true;
                }
            }
            in25.close();

            if (!checkTree) {
                executeCommand(bootstrapFolder, "rm -rf " + bootstrapFolder + "/input.tre");
		executeCommand(bootstrapFolder, "cp " + bootstrapFolder + "/input_pre.tre " + bootstrapFolder + "/input.tre");
            }
             // create SNA file if necessary

                    if (!checkIfFileIsRooted(bootstrapFolder + "/speciesTree" + i + ".tre", bootstrapFolder)) {
                        out25.write("speciesTree" + i + ".tre\n");
                        out25.flush();
			out25.close();
                        System.out.println("Not Rooted");
                        System.exit(0);
                    } else {

             
             executeCommand(bootstrapFolder, "sh runCONTROLBOOT.sh " + bootstrapFolder);
             
             String controlScript = "";
             FileInputStream fstream = new FileInputStream(bootstrapFolder + "/control");
             DataInputStream din = new DataInputStream(fstream);
             BufferedReader in = new BufferedReader(new InputStreamReader(din));
             while (in.ready()) {
                 String str = in.readLine();
                 controlScript += str + "\n";
             }
             in.close();

             
             int countSpecies = 0;
             String realsna = "";
             fstream = new FileInputStream(bootstrapFolder + "/controlFile");
             din = new DataInputStream(fstream);
             in = new BufferedReader(new InputStreamReader(din));
             while (in.ready()) {
                 String str = in.readLine();
                 countSpecies++;
                 realsna += str + "\n";
             }
             in.close();

             controlScript = controlScript.replaceAll("ReplaceMeCountGeneTrees", map.size() + "").replaceAll("ReplaceMeCountSpecies", countSpecies + "").replaceAll("ReplaceMeSNA", realsna);

             FileWriter fwriter = new FileWriter(bootstrapFolder + "/control");
             BufferedWriter out = new BufferedWriter(fwriter);
             out.write(controlScript);
             out.close();

             //executeCommand("cp script.r " + bootstrapFolder + "/script.r");
             //executeCommand("cd " + bootstrapFolder + "\n" + "R --vanilla < script.r");
             executeCommand(bootstrapFolder, "cd " + bootstrapFolder + "\n../../../mpest_1.2/src/mpest control > mpest_progress.txt\ncp ../../../convert.R convert.R\nR --vanilla < convert.R\ncat output.nex | tail -n 1 > output.tre\ncp output.tre output_" + i + ".tre");
             
            fstream = new FileInputStream(bootstrapFolder + "/output.tre");
            din = new DataInputStream(fstream); 
            in = new BufferedReader(new InputStreamReader(din));         
            while (in.ready()) {
                String str = in.readLine();
                finalTrees += str;
            }
            in.close();
            finalTrees += "\n";

            } // end if statement
         }

          FileWriter fwriter = new FileWriter(outputFinalTree);
          BufferedWriter out = new BufferedWriter(fwriter);  
          out.write(finalTrees);
          out.close();
          out25.close();
      } catch (Exception e) {
          /*try {
          PrintWriter pw = new PrintWriter(new FileOutputStream("log")); 
          e.printStackTrace(pw);
          } catch (Exception ex) {

          }*/
          e.printStackTrace();
          
      }      
   }
    public static void executeCommand(String bootstrapFolder, String executeThis) {
        try {
            writeFile(bootstrapFolder + "/tempexecuteCommand.sh", executeThis);
            String[] command = {"sh", bootstrapFolder + "tempexecuteCommand.sh"};
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

}

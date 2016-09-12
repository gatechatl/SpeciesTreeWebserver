import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class ExamineGeneTrees {

	public static void main(String[] args) {
		
		try {
			
			String allTreeFile = args[0];
			String speciesTreeFile = args[1];
			String spaFile = args[2];
			String filepath = args[3];
			String geneTreeFile = args[4];
			String distantFile = args[5];
			
			FileInputStream fstream = new FileInputStream(allTreeFile);
			DataInputStream din = new DataInputStream(fstream); 
			BufferedReader in = new BufferedReader(new InputStreamReader(din));
			String tree = "";			
			while (in.ready()) {
				String str = in.readLine();
				tree += str;
			}
			in.close();
			LinkedList list = new LinkedList();
			String[] split = tree.split(";");
			for (int i = 0; i < split.length; i++) {
				if (!split[i].trim().equals("")) {
					list.add(split[i]);
				}
			}
			
			String[] trees = new String[list.size()];
			Iterator itr = list.iterator();
			int j = 0;
			while (itr.hasNext()) {
				trees[j] = (String)itr.next();			
                                System.out.println(trees[j]);
				j++;
			}
			
            String speciestree = "";
            fstream = new FileInputStream(speciesTreeFile);
            din = new DataInputStream(fstream);
            in = new BufferedReader(new InputStreamReader(din));
            while (in.ready()) {
                    speciestree = in.readLine();                               
            }
            in.close();

            String[] taxas = grabTaxa(trees, filepath);
            HashMap spaOther = grabSPAOther(spaFile);
            System.out.println("spaOther: " + spaOther.size());
            HashMap spa = grabSPA(spaFile);
            System.out.println("SPA: " + spa.size());
            
            String[] removeThese = removedNodes(taxas, spaOther);

            
            
		    FileWriter fwriter = new FileWriter(geneTreeFile);
		    BufferedWriter out = new BufferedWriter(fwriter);
		    out.write(createGeneTree(list));
		    out.close();

            fwriter = new FileWriter(distantFile);
            out = new BufferedWriter(fwriter);

            out.write("GeneTreeName,TreeDistance\n");
            String[] geneTreesName = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                geneTreesName[i] = "GeneTree" + (i + 1);
            }
            /*int index = 0;
            String[] geneTrees = new String[list.size()];
            itr = list.iterator();
            while (itr.hasNext()) {
                tree = (String)itr.next() + ";";
                geneTrees[index] = tree;
                index++;
            }*/
            String[] geneTrees = RemoveTreeNodes(trees, removeThese, filepath);
            String[] finalTaxas = new String[geneTrees.length];
            String[] newtaxas = grabTaxa(geneTrees, filepath);
            for (int i = 0; i < newtaxas.length; i++) {
            	String[] split3 = newtaxas[i].split("\t");
            	String stuff = "";
            	for (int k = 1; k < split3.length; k++) {
            		
            		geneTrees[i] = geneTrees[i].replaceAll("," + split3[k] + ":", "," + (String)spa.get(split3[k]) + ":");
            		geneTrees[i] = geneTrees[i].replaceAll("\\(" + split3[k] + ":", "\\(" + (String)spa.get(split3[k]) + ":");
            		
            	}
            	//finalTaxas[i] = stuff;
                //System.out.println(stuff);
            }
            
            /*String[] finalTaxas = new String[newtaxas.length];
            
            
            for (int i = 0; i < newtaxas.length; i++) {
            	String[] split3 = newtaxas[i].split("\t");
            	String stuff = "";
            	for (int k = 1; k < split3.length; k++) {
            		if (k == 1) {
            			stuff = (String)spa.get(split3[k]);
            		} else {
            			stuff += "\t" + (String)spa.get(split3[k]);
            		}
            	}
            	finalTaxas[i] = stuff;
                System.out.println(stuff);
            }*/
            
            
            //out.write(calculateTreeDist(geneTrees, speciestree, geneTreesName, finalTaxas, filepath) + "\n");
            //out.write(calculateTreeDist(geneTrees, speciestree, geneTreesName, filepath) + "\n");
        	out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}			
	
	public static String[] RemoveTreeNodes(String[] trees, String[] removeThese, String folder) {
		   String[] result = new String[trees.length];
		   try {
	 		    FileWriter fwriter = new FileWriter(folder + "/createNewTrees.r");
	 		    BufferedWriter out = new BufferedWriter(fwriter);
	 		    out.write("library(phybase)\n");	 		    
	 		    out.write("removeNode <- function(tree, nodes) {\n");
	 		    out.write("resultTree = tree;\n");
	 		    out.write("finalTree = tree;\n");
	 		    out.write("for (i in 1:length(nodes)) {\n");
	 		    out.write("spname<-read.tree.nodes(resultTree)$names;\n");    
	 		    out.write("nodematrix<-read.tree.nodes(resultTree)$nodes;\n");
	 		    out.write("remove_index = which(spname==nodes[i]);\n");
	 			out.write("finalTree = del.node(remove_index,spname,nodematrix)$treestr;\n");
	 			out.write("resultTree = finalTree;\n");
	 			out.write("}\n");
	 			out.write("return(resultTree);\n");
	 			out.write("}\n");

	 		    for (int i = 0; i < trees.length; i++) {
		 		out.write("tree <- '" + trees[i] + "'\n");
	 		    	System.out.println("removeThese: " + removeThese[i]);
	 		    	if (removeThese[i].split("\t").length > 0) {
                                        boolean findBlank = false;
		 		    	String removeScript = "finalTree <- removeNode(tree, c('";
		 		    	String[] split = removeThese[i].split("\t");
		 		    	for (int j = 0; j < split.length; j++) {
		 		    		if (j == 0) {
		 		    			removeScript += split[j] + "'";
		 		    		} else {
		 		    			removeScript += ", '" + split[j] + "'";
		 		    		}
                                                if (split[j].trim().equals("")) {
                                                     findBlank = true;
                                                }
		 		    	}
		 		    	removeScript += "));\n";
                                        if (!findBlank) {	 		    		 		    	
		 		    	    out.write(removeScript + "\n");
		 		    	    out.write("write(finalTree, file = \"" + folder + "/NodeRemovedNode\", append=TRUE);\n");
                                        } else {
	 		    		    out.write("write(tree, file = \"" + folder + "/NodeRemovedNode\", append=TRUE);\n");

                                        }
	 		    	} else {
	 		    		out.write("write(tree, file = \"" + folder + "/NodeRemovedNode\", append=TRUE);\n");
	 		    	}
	 		    		 		    	
	 		    }
	 		    
	 		        	
	 		    out.close();	
	 		    File f = new File(folder + "/NodeRemovedNode");
	 		    if (f.exists()) {
	 		    	f.delete();
	 		    }
	 		    
	 		    executeCommand(folder, "R --vanilla < " + folder + "/createNewTrees.r");
	 		    int i = 0;
	 			FileInputStream fstream = new FileInputStream(folder + "/NodeRemovedNode");
	 			DataInputStream din = new DataInputStream(fstream); 
	 			BufferedReader in = new BufferedReader(new InputStreamReader(din));			 			
	 			while (in.ready()) { 				 			
	 				String str = in.readLine();
	 				result[i] = str;
	 				i++;
	 			}
	 			in.close();
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
		   return result;
	}
	
	public static String[] removedNodes(String[] nodes, HashMap map) {
		String[] remove = new String[nodes.length]; 
		for (int i = 0; i < nodes.length; i++) {
			HashMap finalMap = new HashMap();
			String[] split = nodes[i].split("\t");
			
			String addition = "";
			for (int j = 1; j < split.length; j++) {
				
				boolean find = false;
				Iterator itr = map.keySet().iterator();
				while (itr.hasNext()) {
					String key = (String)itr.next();
					String stuff = (String)map.get(key);
					
					String[] split2 = stuff.split("\t");
					for (int k = 0; k < split2.length; k++) {
						if (split2[k].equals(split[j]))  {
							if (!finalMap.containsKey(key)) {
								finalMap.put(key,  split2[k]);
								find = true;
							} else {
								if (addition.equals("")) {
									addition = split[j];
								} else {
									addition += "\t" + split[j];
								}
								
							}
						}
					}
				}				
			}			
			remove[i] = addition;
		}
		return remove;
	}
	
	

	public static String[] grabTaxa(String[] trees, String folder) {
		
		   String[] result = new String[trees.length];
		   try {
	 		    FileWriter fwriter = new FileWriter(folder + "/grabTaxa.r");
	 		    BufferedWriter out = new BufferedWriter(fwriter);
	 		    out.write("library(phybase)\n");
	 		    for (int i = 0; i < trees.length; i++) {
	 		    	out.write("tree <- '" + trees[i] + "'\n");
	 		    	out.write("spname<-read.tree.nodes(tree)$names;\n");
	 		    	out.write("stuff=''\n");
	 		    	out.write("for (i in 1:length(spname)) {\n");
	 		    		out.write("stuff = paste(stuff, spname[i], sep=\"\t\")\n");
	 		    	out.write("}\n");	 		    	
	 		    	out.write("write(stuff, file = \"" + folder + "/grabTaxa\", append=TRUE);\n");
	 		    }
	 		    
	 		        	
	 		    out.close();	
	 		    File f = new File(folder + "/grabTaxa");
	 		    if (f.exists()) {
	 		    	f.delete();
	 		    }
	 		    executeCommand(folder, "R --vanilla < " + folder + "/grabTaxa.r");
	 		    int i = 0;
	 			FileInputStream fstream = new FileInputStream(folder + "/grabTaxa");
	 			DataInputStream din = new DataInputStream(fstream); 
	 			BufferedReader in = new BufferedReader(new InputStreamReader(din));			 			
	 			while (in.ready()) { 				 			
	 				String str = in.readLine();
	 				result[i] = str;
	 				i++;
	 			}
	 			in.close();
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
		   return result;
	}
	
	
	public static HashMap grabSPA(String file) {
		HashMap map = new HashMap();
		try {						
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream din = new DataInputStream(fstream); 
			BufferedReader in = new BufferedReader(new InputStreamReader(din));
			String trees = "";			
			while (in.ready()) {
				String str = in.readLine();
				String[] split = str.split(" ");		
				for (int i = 2; i < split.length; i++) {
					map.put(split[i], split[0]);
				}
				
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static HashMap grabSPAOther(String file) {
		HashMap map = new HashMap();
		try {						
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream din = new DataInputStream(fstream); 
			BufferedReader in = new BufferedReader(new InputStreamReader(din));
			String trees = "";			
			while (in.ready()) {
				String str = in.readLine();
				String[] split = str.split(" ");
				String stuff = split[2];				
				for (int i = 3; i < split.length; i++) {
					stuff += "\t" + split[i];
				}
				map.put(split[0], stuff);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static String calculateTreeDist(String[] tree1, String tree2, String[] geneTreesName, String[] speciesName, String folder) {
        String result = "";
	   try {
		    FileWriter fwriter = new FileWriter(folder + "/testDist.r");
		    BufferedWriter out = new BufferedWriter(fwriter);
		    out.write("library(phybase)\n"); 		     		     		    
                 for (int i = 0; i < tree1.length; i++) {
		        out.write("tree1 <- \"" + tree1[i] + "\"\n");
		        out.write("tree2 <- \"" + tree2 + "\"\n");
		             String name1 = "name1 <- species.name(c(";
		             String[] split = speciesName[i].split("\t");
		             for (int j = 0; j < split.length; j++) {
		            	 if (j == 0) {
		            		 name1 += "'" + split[j] + "'";
		            	 } else {
		            		 name1 += ",'" + split[j] + "'";
		            	 }
		             }
		             name1 += "));\n";
                     //out.write("name1<-species.name(tree1)\n");
		             out.write(name1);		             
                     out.write("name2<-species.name(tree2)\n");
                     out.write("if (length(name1)==length(name2)) {\n");
                     out.write("nodematrix1<-read.tree.nodes(tree1,name1)$nodes\n");
                     out.write("nodematrix2<-read.tree.nodes(tree2,name2)$nodes\n");                        
                     out.write("dist" + i + " <- treedist(nodematrix1,nodematrix2)\n");
                     out.write("write(dist" + i + ", file = \"" + folder + "/treedist\", append = TRUE);\n");
                     out.write("} else {\n");
                     out.write("write(-1, file = \"" + folder + "/treedist\", append = TRUE);\n");
                 	 out.write("}\n");
                 }
		    //out.write("dist <- treedist(tree1, tree2)\n");
 	 		    File f = new File(folder + "/treedist");
 	 		    if (f.exists()) {
 	 		    	f.delete();
 	 		    }		    
		    out.close();	
		    executeCommand(folder, "R --vanilla < " + folder + "/testDist.r");
                 int index = 0;
			FileInputStream fstream = new FileInputStream(folder + "/treedist");
			DataInputStream din = new DataInputStream(fstream); 
			BufferedReader in = new BufferedReader(new InputStreamReader(din));			 			
                     while (in.ready()) {
                         String str = in.readLine();
                         if (!str.equals("")) { 			                                        
                             result+= geneTreesName[index] + "," + str + "\n";
                             index++;
                         }
                     }
			in.close();
	   } catch (Exception e) {
		   e.printStackTrace();
	   }
	   
		return result;
	}
	
	public static String calculateTreeDist(String[] tree1, String tree2, String[] geneTreesName, String folder) {
           String result = "";
	   try {
 		    FileWriter fwriter = new FileWriter(folder + "/testDist.r");
 		    BufferedWriter out = new BufferedWriter(fwriter);
 		    out.write("library(phybase)\n"); 		     		     		    
                    for (int i = 0; i < tree1.length; i++) {
 		        out.write("tree1 <- \"" + tree1[i] + "\"\n");
 		        out.write("tree2 <- \"" + tree2 + "\"\n");
                        out.write("name1<-species.name(tree1)\n");
                        out.write("name2<-species.name(tree2)\n");
                        out.write("if (length(name1)==length(name2)) {\n");
                        out.write("nodematrix1<-read.tree.nodes(tree1,name1)$nodes\n");
                        out.write("nodematrix2<-read.tree.nodes(tree2,name2)$nodes\n");                        
                        out.write("dist" + i + " <- treedist(nodematrix1,nodematrix2)\n");
                        out.write("write(dist" + i + ", file = \"" + folder + "/treedist\", append = TRUE);\n");
                        out.write("} else {\n");
                        out.write("write(-1, file = \"" + folder + "/treedist\", append = TRUE);\n");
                    	out.write("}\n");	 		    	
                    }
 		    //out.write("dist <- treedist(tree1, tree2)\n");
 		    
 		    out.close();	
 		    executeCommand(folder, "R --vanilla < " + folder + "/testDist.r");
                    int index = 0;
 			FileInputStream fstream = new FileInputStream(folder + "/treedist");
 			DataInputStream din = new DataInputStream(fstream); 
 			BufferedReader in = new BufferedReader(new InputStreamReader(din));			 			
                        while (in.ready()) {
                            String str = in.readLine();
                            if (!str.equals("")) { 			                                        
                                result+= geneTreesName[index] + "," + str + "\n";
                                index++;
                            }
                        }
 			in.close();
	   } catch (Exception e) {
		   e.printStackTrace();
	   }
	   
		return result;
	}
	public static void executeCommand(String fileDir, String executeThis) {
		try {
			writeFile(fileDir + "/tempexecuteCommand.sh", executeThis);
	        String[] command = {"sh", fileDir + "/tempexecuteCommand.sh"};
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
	public static String createGeneTree(LinkedList list) {
		String str = "<!doctype html>\n";
                str += "<html><head> </head> <body> <meta charset='utf-8' /> <meta name='viewport' content='width=1024' /> <meta name='apple-mobile-web-app-capable' content='yes' /> <title>impress.js | presentation tool based on the power of CSS3 transforms and transitions in modern browsers | by Bartek Szopka @bartaz</title> <meta name='author' content='Nicolas Wormser' /> <link href='http://fonts.googleapis.com/css?family=Open+Sans:regular,semibold,italic,italicsemibold|PT+Sans:400,700,400italic,700italic|PT+Serif:400,700,400italic,700italic' rel='stylesheet' /> <link href='../../../css/impress-demo.css' rel='stylesheet' /><script type='text/javascript' src='../../../raphael-min.js' ></script><script type='text/javascript' src='../../../jsphylosvg-min.js'></script>";
		str += "<script type='text/javascript'>window.onload = function(){";
		
		Iterator itr = list.iterator();
		int index = 1;
		while (itr.hasNext()) {
			String tree = (String)itr.next();
            if (index <= 50) {
            tree = "(" + tree + ");";
            tree = tree.replaceAll(":0.", ":5.");
            tree = tree.replaceAll(":1.", ":5.");
            tree = tree.replaceAll(":2.", ":5.");
            tree = tree.replaceAll(":3.", ":5.");
            tree = tree.replaceAll(":4.", ":5.");
            tree = tree.replaceAll(":5.", ":5.");
            tree = tree.replaceAll(":6.", ":5.");
            tree = tree.replaceAll(":7.", ":5.");
            tree = tree.replaceAll(":8.", ":5.");
            tree = tree.replaceAll(":9.", ":5.");
            str += "var dataObject" + index + " = { newick: '" + tree + "' };\n";
            str += "phylocanvas" + index + " = new Smits.PhyloCanvas(\n";
            str += "dataObject" + index + ",\n";
            str += "'svgCanvas" + index + "',\n";
            str += "800, 800,'circular'\n";
            str += ");\n";
                        
            index++;
                        }

		}
		str += "}\n";
		str += "</script></head><body><div id='impress' class='impress-not-supported'>    <div class='fallback-message'> <p>Your browser <b>doesn't support the features required</b> by impress.js, so you are presented with a simplified version of this presentation.</p> <p>For the best experience please use the latest <b>Chrome</b>, <b>Safari</b> or <b>Firefox</b> browser. Upcoming version 10 of Internet Explorer <i>should</i> also handle it.</p> </div>\n";
		
	    str += "<div id='soccent' class='step' data-x='0' data-y='0' data-scale='80'>\n";
	    str += "<h1>Gene Tree Viewer<br />\n";
	    str += "</h1>\n";
	    str += "<h5>Press Arrow <br />Left or Right</h5>\n";
	    str += "</div>\n";
	    
		for (int i = 1; i < index; i++) {
	        //int i = 1;
			int num = 50000 * i;
		    str += "<div id='stree" + i + "' class='step' data-x='" + num + "' data-y='0' data-scale='80'>\n";
		    str += "<br /><br /><br /><br /><br /><br /><br /><br /><br />\n";
		    str += "<center><h5> Gene Tree " + i + "<h5></center/>\n";
		    str += "</div>\n";
		    str += "<div id='svgCanvas" + i + "' class='step' data-x='" + num + "' data-y = '0' data-scale='80'></div>\n";
		}
		
                str += "    <div id='overview' class='step' data-x='100000' data-y='0' data-scale='300'>\n";
	        str += " </div>\n";

		str += "</div>\n";
		str += "<script src='../../../impress.js'></script>\n";
		str += "<script>impress();</script>\n";
		str += "</body>\n";
		str += "</html>\n";
		
		return str;

	}
}




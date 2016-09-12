import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This program creates the R script for plotting HIV plots
 * @author Tim Shaw
 *
 */
public class ViralRPlotting {
	
	private static double _TITLE_FONT_SIZE = 20; // graph title font size
	private static double _AXIS_FONT_SIZE = 10; // axis font size for each plot
	private static double _LABEL_FONT_SIZE = 0.8; // font size for each plot
	private static String _TITLE = ""; // title of the graph
	private static double _WIDTH = 1500; // width of the plot
	private static double _HEIGHT = 1500; // height of the plot
	private static int _PAR = 2; // number of blocks to display
	//private static String _HIGHLIGHTFILE = "C:\\School Notes\\HIV_Diversity\\RNA Structure\\R_Plot_Package\\Highlight_Structure.txt"; // currently set this permanently
	//private static String _HIGHLIGHTFILE = "C:\\School Notes\\HIV_Diversity\\RNA Structure\\R_Plot_Package\\BF_Highlight.txt"; // currently set this permanently
	private static String _HIGHLIGHTFILE = "Highlight.txt"; // Set as no highlight
	
	/**
	 * Each Input File will become one block in the graphics
	 * Additional lines can be taken on the bottom screen
	 * first file is the output file
	 * additional files will be input file and its parameter file to indicate how it can be plotted
	 * second to last file is the list of extended landmarks in addition to HIV
	 * last file are the parameters being used
	 * @param args
	 */
    public static void main(String[] args) {    	    	
    	try {
    		int par_num = (args.length - 2) / 2 + 1;
    		if (par_num < 1 || args.length % 2 != 0) {
    			System.out.println("The number of argument is wrong...");
    			System.out.println("First file must be image file output location.");
    			System.out.println("Second and Third file must be input_data and parameter file.");
    			System.out.println("Repeat number of input_data and parameter file as necessary.");
    			System.out.println("Last file must be r script output location.");    			
    			System.exit(0);
    		}
    		System.out.println(par_num);
    		_PAR = par_num;
    		//_PAR = par_num;
    		// read through files once to determine how many PAR is necessary
    		
	    	String outputFile = args[0];
	    	String script = "";
	    	script += resetVariables();
	    	 	
	    	String r_script_file = args[args.length - 1]; // last parameter is always the r script file name
	    	script += defineOutputGraph(outputFile, _PAR);	    	
	    	
	    	if (args.length >= 3) {
	    		
	    	    for (int i = 1; i < args.length - 1; i = i + 2) {
	    	    		    	    		    	    	
	    	    	String inputFile = args[i];
	    	    	String parameterFile = args[i + 1];
	    	    	
	    	    	script += readDataOnce(inputFile);
	    	    	// parameter file determine 
	    			FileInputStream fstreamFull = new FileInputStream(parameterFile);
	    			DataInputStream dinFull = new DataInputStream(fstreamFull); 
	    			BufferedReader in = new BufferedReader(new InputStreamReader(dinFull));
	    			boolean firstLine = true;
	    			int number_of_line = -1;
	    			double system_min = -1;
	    			double system_max = -1;
	    			int flag_position = 0; // place axis on left or right
	    			
	    			//int flag_statistic = 0; // statistics for each type listed
	    			LinkedList types = new LinkedList<String>();
	    			int index = 1;
	    			while (in.ready()) {
	    				String str = in.readLine();
	    				String[] split = str.split("\t");
	    				// the first line is the parameter being used for the entire system
	    				if (firstLine) {
	    					number_of_line = new Integer(split[0]);
	    					
	    					system_min = new Double(split[1]);
	    					system_max = new Double(split[2]);
	    					if (split[3].toUpperCase().equals("LEFT ONLY")) {
	    						flag_position = 1;
	    					} else if (split[3].toUpperCase().equals("RIGHT ONLY")) {
	    						flag_position = 2;
	    					} else if (split[3].toUpperCase().equals("ALL")) {
	    						flag_position = 0;
	    					}

	    					for (int j = 4; j < split.length; j++) {
	    						types.add(split[j]);
	    					}
	    					script += initialize_empty_plot();
	    					script += highlightRegion(_HIGHLIGHTFILE);
	    				} else if ((number_of_line + 1) >= index){
	    					System.out.println(str);
	    					index++; // add one to the indexes
	    					String data_type = split[0];
	    					double data_min = new Double(split[1]);
	    					double data_max = new Double(split[2]);
	    					String data_color = split[3]; // a color name
	    					String data_line_type = split[4]; // this should be a number for lwd
	    					String data_position = split[5]; // LEFT or RIGHT
	    					String data_add = split[6]; // whether to add to previous plot will be ADD or NOADD
	    					String data_label_name = split[7];
	    					String data_plot_type = split[8];
	    					String data_plot_title = split[9];
	    					//if (data_type.equals("B") || data_type.equals("C") || data_type.equals("All_Subtype")) {
	    					    script += plot_data(index, data_plot_type, data_label_name, data_min, data_max, data_color, data_position, data_plot_title);	    					    					
	    					    script += defineAxis(data_position);
	    					//}
	    					// read line and plot one
	    				} else {
	    					
	    				}
	    				firstLine = false;
	    			}
	    			System.out.println("Index Total: " + index);
	    			    
	    			
	    			in.close();
	    	    }	    	
	    	}
	    	
	    	script += createHIVLandMark();
	    	script += releaseImage();
	    	
			FileWriter fwriter = new FileWriter(r_script_file);
		    BufferedWriter out = new BufferedWriter(fwriter);
		    out.write(script + "\n");
		    out.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    public static String defineAxis(String position) {
    	String returnStr = "";
    	if (position.equals("LEFT")) {
    	    returnStr += "Axis(side=2, axTicks(1), labels=TRUE, lty = 3, lwd = 3, cex.axis = 2);\n";
    	} else if (position.equals("RIGHT")) {
    		returnStr += "Axis(side=4, axTicks(1), labels=TRUE, lty = 3, lwd = 3, cex.axis = 2);\n";
    	}

    	return returnStr;
    	//legend("bottomleft", c("A Subtype", "B Subtype", "C Subtype", "D Subtype", "F Subtype", "G Subtype", "H Subtype", "J Subtype", "K Subtype"), col=c("blue", "dark gray", "green", "black", "yellow", "red", "pink", "orange", "purple"), lwd = 3, cex = 3);
    }
    
    public static String replaceSlashes(String fileName) {
    	return fileName.replaceAll("\\\\", "/");
    }
    /**
     * 
     * @param fileName
     * @return
     */
    public static String readDataOnce(String fileName) {
    	String returnStr = "";
    	
    	returnStr += "location = \"" + replaceSlashes(fileName) + "\";\n";
    	returnStr += "filename = paste (location, sep = \"\");\n";
    	returnStr += "raw_data=read.csv(filename, sep = \"\\t\", header=FALSE);\n";
    	returnStr += "hxb2_numbering = raw_data[,1];\n";
    	return returnStr;
    }
    /**
     * Initialize plot by plotting an empty plot
     */
    public static String initialize_empty_plot() {
    	return "plot(0, 0, xlim = c(-1000, 11719), axes=FALSE, col = \"white\");\n";
    }
    /**
     * Make sure readDataOnce is run before this function 
     * @param index
     * @param flag_type
     * @param label_name
     * @param min
     * @param max
     * @param color
     * @param position // LEFT or RIGHT
     * @return
     */
    public static String plot_data(int index, String flag_type, String label_name, double min, double max, String color, String position, String title) {
    	String returnStr = "";
    	
    	returnStr += "par(new=TRUE);\n";
    	returnStr += "plot(0, 0, xlim=c(-1000, 11719), ylab = \"" + label_name + "\", ylim=c(" + min + ", " + max + "), xlab = \"\", axes=FALSE, col = \"white\", type = \"p\", cex.lab = 1.7, cex.main = 3, main=\"" + title + "\");\n"; // plot nothing first
    	//System.out.println("hello: " + flag_type);
    	if (flag_type.equals("MOUNTAINTOP")) {
    		returnStr += "plot_data_var = raw_data[," + index + "];\n";
    	    returnStr += "for (i in 1:9719) {\n";
    	    returnStr += "if (is.nan(plot_data_var[i]) == FALSE) {\n";
    	    returnStr += "points(hxb2_numbering[c(i,i+1)],c(" + max + ", plot_data_var[i]), xlim=c(-1000, 11719), ylab = \"" + label_name + "\", ylim=c(" + min + ", " + max + "), xlab = \"\", axes=FALSE, col = \"" + color + "\", type = \"l\", cex.lab = 1.7, cex.main = 3);\n";
    	    //returnStr += "points(hxb2_numbering[c(i,i+1)],c(max(plot_data_var), plot_data_var[i]), xlim=c(-1000, 11719), ylab = \"" + label_name + "\", ylim=c(" + min + ", " + max + "), xlab = \"\", axes=FALSE, col = \"" + color + "\", type = \"l\", cex.lab = 1.7, cex.main = 3);\n";
    	    //returnStr += "segments(i, max(plot_data_var, na.rm = TRUE), i, plot_data_var[i], col = \"" + color + "\");\n";
    	    returnStr += "}\n";
    	    returnStr += "}\n";    	    
    		returnStr += "";	
    	} else if (flag_type.equals("MOUNTAINBOTTOM")) {
    		returnStr += "plot_data_var = raw_data[," + index + "];\n";
    		//returnStr += "points(hxb2_numbering, plot_data_var, xlim=c(-1000, 11719), ylab = \"" + label_name + "\", ylim=c(" + min + ", " + max + "), xlab = \"\", axes=FALSE, col = \"blue\", type = \"l\", cex.lab = 1.7, cex.main = 3);\n";
    	    returnStr += "for (i in 1:9719) {\n";
    	    returnStr += "if (is.nan(plot_data_var[i]) == FALSE) {\n";
    	    returnStr += "points(hxb2_numbering[c(i,i+1)],c(" + min + ", plot_data_var[i]), xlim=c(-1000, 11719), ylab = \"" + label_name + "\", ylim=c(" + min + ", " + max + "), xlab = \"\", axes=FALSE, col = \"" + color + "\", type = \"l\", cex.lab = 1.7, cex.main = 3);\n";
    	    //returnStr += "points(hxb2_numbering[c(i,i+1)],c(min(plot_data_var), plot_data_var[i]), xlim=c(-1000, 11719), ylab = \"" + label_name + "\", ylim=c(" + min + ", " + max + "), xlab = \"\", axes=FALSE, col = \"" + color + "\", type = \"l\", cex.lab = 1.7, cex.main = 3);\n";
    	    //returnStr += "segments(i, min(plot_data_var, na.rm = TRUE), i, plot_data_var[i], col = \"" + color + "\");\n";
    	    returnStr += "}\n";
    	    returnStr += "}\n";    		
    	} else if (flag_type.equals("LINE")) {
    		returnStr += "plot_data_var = raw_data[," + index + "];\n";
    		returnStr += "points(hxb2_numbering, plot_data_var, xlim=c(-1000, 11719), ylab = \"" + label_name + "\", ylim=c(" + min + ", " + max + "), xlab = \"\", axes=FALSE, col = \"" + color + "\", type = \"l\", cex.lab = 1.7, cex.main = 3);\n";    		
    	}    	    	
    	returnStr += addVerticalLines();
    	return returnStr;
    }
    /**
     * This read the fileName for a particular variable
     * Note line 0 is the hxb2 indexes
     * I don't think we need this function...
     * @param fileName
     * @param line
     */
    public static HashMap ReadInputValues(String fileName, int line) {
    	HashMap map = new HashMap(); // HXB2 number is the index with 
    	try {
			FileInputStream fstreamFull = new FileInputStream(fileName);
			DataInputStream dinFull = new DataInputStream(fstreamFull); 
			BufferedReader in = new BufferedReader(new InputStreamReader(dinFull));
			while (in.ready()) {
				String str = in.readLine();
				
			}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return map;
    }
    
    public static String resetVariables() {
    	String returnStr = "";
    	returnStr += "rm(list=ls());\n";
    	returnStr += "x = 0;\n";
    	returnStr += "y = 0;\n";
    	returnStr += "graphics.off();\n";
    	return returnStr;
    }
    
    
    public static String defineOutputGraph(String fileName, int par) {
    	String returnStr = "location = '" + replaceSlashes(fileName) + "';\n";
    	returnStr += "filename = paste (location, sep = \"\");\n";
    	returnStr += "png(filename , width=" + _WIDTH + ", height=" + _HEIGHT + ");\n";
    	returnStr += "par(mfrow=c(" + par + ", 1));\n";
    	return returnStr;
    }
    
    public static String releaseImage() {
    	return "dev.off();";
    }
    
    /**
     * Read in vertical line from vertical line input file
     * @param inputFile
     */
    public static String addVerticalLines() {
    	String returnStr = "";
    	    	
    	returnStr += addVerticalLine(454, "black"); // tar start
    	returnStr += addVerticalLine(514, "blue"); // tar end
    	
    	returnStr += addVerticalLine(2085, "dark green"); // gag
    	returnStr += addVerticalLine(790, "black"); // gag
    	returnStr += addVerticalLine(1186, "blue"); // gag
    	
    	returnStr += addVerticalLine(2085, "dark green"); // gag
    	returnStr += addVerticalLine(1879, "black"); // gag
    	returnStr += addVerticalLine(1921, "blue"); // gag
    	
    	returnStr += addVerticalLine(2085, "dark green"); // FE Start
    	returnStr += addVerticalLine(2136, "black"); // FE End
    	returnStr += addVerticalLine(2253, "blue"); // POL prot Start
    	returnStr += addVerticalLine(2466, "dark green"); // POL RNA Start
    	returnStr += addVerticalLine(2550, "black"); // POL prot End
    	returnStr += addVerticalLine(2578, "blue"); // POL RNA End
    	returnStr += addVerticalLine(3870, "dark green"); // POL p51 END RNase START
    	returnStr += addVerticalLine(4230, "black"); // p15 RNase END
    	returnStr += addVerticalLine(5041, "blue"); // VIF Start
    	returnStr += addVerticalLine(5559, "dark green"); // VPR Start
    	returnStr += addVerticalLine(5619, "black"); // VIF End
    	returnStr += addVerticalLine(5831, "blue"); // TAT Start
    	returnStr += addVerticalLine(5850, "dark green"); // VPR End
    	returnStr += addVerticalLine(6045, "black"); // TAT End
    	returnStr += addVerticalLine(6062, "blue"); // VPU Start
    	returnStr += addVerticalLine(6225, "dark green"); // ENV Start
    	returnStr += addVerticalLine(6310, "black"); // VPU End
    	returnStr += addVerticalLine(7719, "blue"); // RRE Start
    	returnStr += addVerticalLine(8055, "dark green"); // RRE End
    	returnStr += addVerticalLine(8379, "black"); // TAT2 Start
    	returnStr += addVerticalLine(8469, "blue"); // TAT2 End
    	returnStr += addVerticalLine(8797, "dark green"); // NEF Start
    	returnStr += addVerticalLine(9086, "black"); // LTR Start
    	returnStr += addVerticalLine(9417, "blue"); // NEF End
    	returnStr += addVerticalLine(9539, "dark green"); // TAR Start
    	returnStr += addVerticalLine(9599, "black"); // TAR End
    	
    	
    	return returnStr;
    }
    public static String addVerticalLine(int location, String colour) {
    	String returnStr = "abline(v=" + location + ", lty = 2, col=\"" + colour + "\");\n";
    	return returnStr;
    }
    /**
     * Based on an input file with highlight region will highlight the plot with yellow highlights
     * @param fileName
     * @return
     */
    public static String highlightRegion(String fileName) {
    	String returnStr = "";
    	try {
    		
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream din = new DataInputStream(fstream); 
			BufferedReader in = new BufferedReader(new InputStreamReader(din));
			while (in.ready()) {
				String str = in.readLine();
				String[] split = str.split("\t");
				if (split.length == 2) {
					int start = new Integer(split[0]);
					int end = new Integer(split[1]);
				
				    returnStr += highlightColor(start, end, "yellow");
				} else if (split.length >= 3) {
                                        int start = new Integer(split[0]);
                                        int end = new Integer(split[1]);

					String color = split[2];
                                    returnStr += highlightColor(start, end, color);
                                }
			}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return returnStr;
    }
    
    public static String highlightRegion() {
    	String returnStr = "";
    	returnStr += highlightColor(720, 727, "yellow");
    	returnStr += highlightColor(2063, 2225, "yelow");
    	returnStr += highlightColor(2246,2282, "yellow");
    	returnStr += highlightColor(2506,2540, "yellow");
    	returnStr += highlightColor(3228,3348, "yellow");
    	
    	returnStr += highlightColor(3771,3859, "yellow");
    	returnStr += highlightColor(4354,4499, "yellow");
    	returnStr += highlightColor(5284,5404, "yellow");
    	returnStr += highlightColor(5617,5768, "yellow");
    	returnStr += highlightColor(6331,6434, "yellow");
    	returnStr += highlightColor(7763,8120, "yellow");
    	returnStr += highlightColor(8440,8679, "yellow");
    	returnStr += highlightColor(8924,9078, "yellow");
    	returnStr += highlightColor(9102,9275, "yellow");
    	returnStr += highlightColor(9442,9606, "yellow");
    	
    	return returnStr;
    }
    public static String highlightColor(int start, int end, String color) {
    	String returnStr = "";
    	for (int i = start; i <= end; i++) {
    	    returnStr += "abline(v=" + i + ", lty = 2, col=\"" + color + "\");\n"; 
    	}
    	return returnStr;
    }
    public static String plotMultipleLines() {
    	
    	return "";
    }
    public static String createHIVLandMark() {
    	
    	String returnStr = "";
    	
    	returnStr += "plot(x, y, xlim=c(-1000, 11719), ylab = \"\", xlab = \"HXB2 Nucleotide Position\", ylim =c(-800,100), col = 0, yaxt='n', axes=FALSE, cex.lab = 2.4);\n";
    	returnStr += "Axis(side=1, axTicks(1), labels=TRUE, lty = 3, lwd = 2, cex.axis = 2, xaxp=c(0, 9720, 20));\n";

    	returnStr += highlightRegion(_HIGHLIGHTFILE);
    	returnStr += addVerticalLines();

    	returnStr += "fontsize = " + _LABEL_FONT_SIZE + ";\n";
    	

    	// draw RNA structures
    	returnStr += "rnaaxis = 100;\n";
    	// DIS
    	returnStr += "rect(694, rnaaxis, 733, rnaaxis - 100, lwd = 1.5);\n";
    	returnStr += "segments(704, rnaaxis-100, 704, rnaaxis -150);\n";
    	returnStr += "segments(570, rnaaxis-150, 704, rnaaxis-150);\n";
    	returnStr += "segments(570, rnaaxis-150, 570, rnaaxis-180);\n";
    	returnStr += "text(560, rnaaxis - 200, \"DIS\", cex = (fontsize + 1));\n";

    	// FE
    	returnStr += "rect(2085, rnaaxis, 2136, rnaaxis - 100, lwd = 1.5);\n";
    	returnStr += "text(2110, rnaaxis - 160, \"FE\", cex = (fontsize + 1));\n";

    	// GSL3
    	returnStr += "rect(854, rnaaxis, 937, rnaaxis - 100, lwd = 1.5);\n";
    	returnStr += "segments(937, rnaaxis-100, 1480, rnaaxis -100);\n";
    	returnStr += "segments(1480, rnaaxis-100, 1480, rnaaxis-180);\n";
    	returnStr += "text(1600, rnaaxis - 200, \"GSL3\", cex = (fontsize + 1));\n";

    	// PBS
    	returnStr += "rect(579, rnaaxis, 677, rnaaxis - 100, lwd = 1.5);\n";
    	returnStr += "segments(590, rnaaxis-100, 590, rnaaxis -130);\n";
    	returnStr += "segments(310, rnaaxis-130, 590, rnaaxis-130);\n";
    	returnStr += "segments(310, rnaaxis-130, 310, rnaaxis-180);\n";
    	returnStr += "text(300, rnaaxis - 200, \"PBS\", cex = (fontsize + 1));\n";

    	// PBS2
    	returnStr += "rect(9673, rnaaxis, 9717, rnaaxis - 100, lwd = 1.5);\n";
    	returnStr += "text(9820, rnaaxis - 160, \"PBS\", cex = (fontsize + 1));\n";

    	// POL
    	returnStr += "rect(2466, rnaaxis, 2578, rnaaxis - 100, lwd = 1.5);\n";
    	returnStr += "text(2520, rnaaxis - 160, \"POL\", cex = (fontsize + 1));\n";

    	// RRE
    	returnStr += "rect(7719, rnaaxis, 8055, rnaaxis - 100, lwd = 1.5);\n";
    	returnStr += "text(7875, rnaaxis - 160, \"RRE\", cex = (fontsize + 1));\n";

    	// SD
    	returnStr += "rect(736, rnaaxis, 754, rnaaxis - 100, lwd = 1.5);\n";
    	returnStr += "segments(744, rnaaxis-100, 744, rnaaxis -180);\n";
    	returnStr += "text(780, rnaaxis - 200, \"SD\", cex = (fontsize + 1));\n";

    	// SL3
    	returnStr += "rect(763, rnaaxis, 785, rnaaxis - 100, lwd = 1.5);\n";
    	returnStr += "segments(770, rnaaxis-100, 770, rnaaxis -130);\n";
    	returnStr += "segments(930, rnaaxis-130, 770, rnaaxis-130);\n";
    	returnStr += "segments(930, rnaaxis-130, 930, rnaaxis-180);\n";
    	returnStr += "text(1000, rnaaxis - 200, \"SL3\", cex = (fontsize + 1));\n";

    	// SL4
    	returnStr += "rect(791, rnaaxis, 810, rnaaxis - 100, lwd = 1.5);\n";
    	returnStr += "segments(800, rnaaxis-100, 800, rnaaxis -120);\n";
    	returnStr += "segments(1130, rnaaxis-120, 800, rnaaxis-120);\n";
    	returnStr += "segments(1130, rnaaxis-120, 1130, rnaaxis-180);\n";
    	returnStr += "text(1250, rnaaxis - 200, \"SL4\", cex = (fontsize + 1));\n";

    	// TAR
    	returnStr += "rect(454, rnaaxis, 514, rnaaxis - 100, lwd = 1.5);\n";
    	returnStr += "segments(460, rnaaxis-100, 460, rnaaxis - 120);\n";
    	returnStr += "segments(80, rnaaxis-120, 460, rnaaxis-120);\n";
    	returnStr += "segments(80, rnaaxis-120, 80, rnaaxis-180);\n";
    	returnStr += "text(1, rnaaxis - 200, \"TAR\", cex = (fontsize + 1));\n";

    	// TAR2
    	returnStr += "rect(9539, rnaaxis, 9599, rnaaxis - 100, lwd = 1.5);\n";
    	returnStr += "text(9520, rnaaxis - 160, \"TAR\", cex = (fontsize + 1));\n";

    	// draw 5'LTR
    	returnStr += "yaxisvloop = -200;\n";
    	returnStr += "yaxis = -350;\n";
    	returnStr += "yaxis2 = -500;\n";
    	returnStr += "yaxis3 = -650;\n";
    	returnStr += "rect(1, yaxis, 634, yaxis - 100, lwd = 1.5);\n";
    	returnStr += "text(340, yaxis - 40, \"5' LTR\", cex = fontsize + 1);\n";

    	// draw gag p17
    	returnStr += "rect(790, yaxis, 1186, yaxis - 100, lwd = 1);\n";
    	returnStr += "text(1000, yaxis - 40, \"p17\", cex = fontsize + 1);\n";

    	// draw gag p24
    	returnStr += "rect(1186, yaxis, 1879, yaxis - 100, lwd = 1);\n";
    	returnStr += "text(1550, yaxis - 40, \"p24\", cex = fontsize + 1);\n";

    	// draw gag p2
    	returnStr += "rect(1879, yaxis, 1921, yaxis - 100, lwd = 1);\n";

    	// draw gag p7
    	returnStr += "rect(1921, yaxis, 2086, yaxis - 100, lwd = 1);\n";
    	returnStr += "text(2000, yaxis - 40, \"p7\", cex = fontsize + 1);\n";

    	// draw gag p1
    	returnStr += "rect(2086, yaxis, 2134, yaxis - 100, lwd = 1);\n";

    	// draw gag p6
    	returnStr += "rect(2134, yaxis, 2292, yaxis - 100, lwd = 1);\n";
    	returnStr += "text(2200, yaxis - 40, \"p6\", cex = fontsize + 1);\n";

    	// draw gag
    	returnStr += "rect(790, yaxis, 2292, yaxis - 100, lwd = 1.5);\n";
    	returnStr += "text(1600, yaxis - 120, \"--------gag--------\", cex = (fontsize + 1));\n";

    	// draw pol prot
    	returnStr += "rect(2253, yaxis3, 2550, yaxis3 - 100, lwd = 1);\n";
    	returnStr += "text(2375, yaxis3 - 40, \"prot\", cex = fontsize + 1);\n";

    	// draw pol p51 RT
    	returnStr += "rect(2550, yaxis3, 3870, yaxis3 - 100, lwd = 1);\n";
    	returnStr += "text(3300, yaxis3 - 40, \"p51 RT\", cex = fontsize + 1);\n";

    	// draw pol p15 RNase
    	returnStr += "rect(3870, yaxis3, 4230, yaxis3 - 100, lwd = 1);\n";
    	returnStr += "text(4050, yaxis3 - 40, \" p15\nRNase\", cex = fontsize + 1);\n";

    	// draw pol p31 int
    	returnStr += "rect(4230, yaxis3, 5096, yaxis3 - 100, lwd = 1);\n";
    	returnStr += "text(4630, yaxis3 - 40, \"p31 int\", cex = fontsize + 1);\n";

    	// draw POL
    	returnStr += "rect(2085, yaxis3, 5096, yaxis3 - 100, lwd = 1.5);\n";
    	returnStr += "text(3600, yaxis3 - 120, \"--------------------pol--------------------\", cex = (fontsize + 1));\n";

    	// draw VIF
    	returnStr += "rect(5041, yaxis, 5619, yaxis - 100, lwd = 1.5);\n";
    	returnStr += "text(5350, yaxis - 40, \"vif\", cex = (fontsize + 1));\n";

    	// draw VPR
    	returnStr += "rect(5559, yaxis3, 5850, yaxis3 - 100, lwd = 1.5);\n";
    	returnStr += "text(5700, yaxis3 - 40, \"vpr\", cex = (fontsize + 1));\n";

    	// draw Tat
    	returnStr += "rect(5831, yaxis2, 6045, yaxis2 - 100, lwd = 1, col = \"grey\");\n";
    	returnStr += "rect(5831, yaxis2, 6045, yaxis2 - 100, lwd = 1.5);\n";
    	returnStr += "rect(8379, yaxis, 8469, yaxis - 100, lwd = 1, col = \"grey\");\n";
    	returnStr += "rect(8379, yaxis, 8469, yaxis - 100, lwd = 1.5);\n";
    	returnStr += "segments(6040, yaxis2, 6040, yaxis - 75);\n";
    	returnStr += "segments(6040, yaxis - 75, 8379, yaxis -75);\n";
    	returnStr += "text(7200, yaxis - 30, \"tat\", cex = (fontsize + 1));\n";

    	// draw vpu
    	returnStr += "rect(6062, yaxis2, 6310, yaxis2 - 100, lwd = 1.5);\n";
    	returnStr += "text(6200, yaxis2 - 40, \"vpu\", cex = (fontsize + 1));\n";

    	// draw REV
    	returnStr += "rect(5970, yaxis3, 6045, yaxis3 - 100, lwd = 1, col = \"grey\");\n";
    	returnStr += "rect(5970, yaxis3, 6045, yaxis3 - 100, lwd = 1.5);\n";
    	returnStr += "rect(8379, yaxis2, 8653, yaxis2 - 100, lwd = 1, col = \"grey\");\n";
    	returnStr += "rect(8379, yaxis2, 8653, yaxis2 - 100, lwd = 1.5);\n";

    	returnStr += "segments(6035, yaxis3 + 20, 6035, yaxis3);\n";
    	returnStr += "segments(6035, yaxis3 + 20, 8000, yaxis3 + 20);\n";
    	returnStr += "segments(8000, yaxis3 + 20, 8379, yaxis2 - 50);\n";
    	returnStr += "text(7000, yaxis3 + 50, \"rev\", cex = (fontsize + 1));\n";

    	// draw ENV
    	returnStr += "rect(6225, yaxis3, 7758, yaxis3 - 100, lwd = 1);\n";
    	returnStr += "text(7000, yaxis3 - 40, \"gp120\", cex = (fontsize + 1));\n";
    	returnStr += "rect(7758, yaxis3, 8795, yaxis3 - 100, lwd = 1);\n";
    	returnStr += "text(8300, yaxis3 - 40, \"gp41\", cex = (fontsize + 1));\n";
    	returnStr += "rect(6225, yaxis3, 8795, yaxis3 - 100, lwd = 1.5);\n";
    	returnStr += "text(7450, yaxis3 - 120, \"----------------env----------------\", cex = (fontsize + 1));\n";


        // draw Inter Protein Linker
        returnStr += "rect(1105, yaxisvloop, 1185, yaxisvloop - 100, lwd = 1, col = \"purple\");\n";
        returnStr += "text(1170, yaxisvloop - 120, \"IPL1\", cex = (fontsize + 0.5));\n";
        returnStr += "rect(1879, yaxisvloop, 1921, yaxisvloop - 100, lwd = 1, col = \"purple\");\n";
        returnStr += "text(1920, yaxisvloop + 40, \"IPL2\", cex = (fontsize + 0.5));\n";
        returnStr += "rect(2086, yaxisvloop, 2134, yaxisvloop - 100, lwd = 1, col = \"purple\");\n";
        returnStr += "text(2100, yaxisvloop - 120, \"IPL3\", cex = (fontsize + 0.5));\n";
        returnStr += "rect(2535, yaxisvloop, 2579, yaxisvloop - 100, lwd = 1, col = \"purple\");\n";
        returnStr += "text(2570, yaxisvloop + 40, \"IPL4\", cex = (fontsize + 0.5));\n";
        returnStr += "rect(3798, yaxisvloop, 3860, yaxisvloop - 100, lwd = 1, col = \"purple\");\n";
        returnStr += "text(3830, yaxisvloop - 120, \"IPL5\", cex = (fontsize + 0.5));\n";
        returnStr += "rect(4212, yaxisvloop, 4230, yaxisvloop - 100, lwd = 1, col = \"purple\");\n";
        returnStr += "text(4230, yaxisvloop + 40, \"IPL6\", cex = (fontsize + 0.5));\n";
        returnStr += "rect(6310, yaxisvloop, 6331, yaxisvloop - 100, lwd = 1, col = \"purple\");\n";
        returnStr += "text(6331, yaxisvloop - 120, \"IPL7\", cex = (fontsize + 0.5));\n";
        returnStr += "rect(7758, yaxisvloop, 7779, yaxisvloop - 100, lwd = 1, col = \"purple\");\n";
        returnStr += "text(7777, yaxisvloop + 40, \"IPL8\", cex = (fontsize + 0.5));\n";


        // draw Protein Domain Junction
        returnStr += "rect(1432, yaxisvloop, 1485, yaxisvloop - 100, lwd = 1, col = \"lightgreen\");\n";
        returnStr += "text(1470, yaxisvloop - 120, \"PDJ1\", cex = (fontsize + 0.5));\n";
        returnStr += "rect(1615, yaxisvloop, 1623, yaxisvloop - 100, lwd = 1, col = \"lightgreen\");\n";
        returnStr += "text(16200, yaxisvloop + 40, \"PDJ2\", cex = (fontsize + 0.5));\n";
        returnStr += "rect(3180, yaxisvloop, 3302, yaxisvloop - 100, lwd = 1, col = \"lightgreen\");\n";
        returnStr += "text(3230, yaxisvloop - 120, \"PDJ3\", cex = (fontsize + 0.5));\n";
        returnStr += "rect(3483, yaxisvloop, 3552, yaxisvloop - 100, lwd = 1, col = \"lightgreen\");\n";
        returnStr += "text(3520, yaxisvloop + 40, \"PDJ4\", cex = (fontsize + 0.5));\n";
        returnStr += "rect(4371, yaxisvloop, 4394, yaxisvloop - 100, lwd = 1, col = \"lightgreen\");\n";
        returnStr += "text(4390, yaxisvloop - 120, \"PDJ5\", cex = (fontsize + 0.5));\n";
        returnStr += "rect(4857, yaxisvloop, 4883, yaxisvloop - 100, lwd = 1, col = \"lightgreen\");\n";
        returnStr += "text(4880, yaxisvloop + 40, \"PDJ6\", cex = (fontsize + 0.5));\n";
        returnStr += "rect(6812, yaxisvloop, 6831, yaxisvloop - 100, lwd = 1, col = \"lightgreen\");\n";
        returnStr += "text(6870, yaxisvloop - 120, \"PDJ7\", cex = (fontsize + 0.5));\n";

    	// draw boxes for V1 - V5 loops

    	returnStr += "rect(6615, yaxisvloop, 6695, yaxisvloop - 100, lwd = 1);\n";
    	returnStr += "text(6600, yaxisvloop + 40, \"V1\", cex = (fontsize + 1));\n";
    	returnStr += "rect(6693, yaxisvloop, 6812, yaxisvloop - 100, lwd = 1);\n";
    	returnStr += "text(6770, yaxisvloop + 40, \"V2\", cex = (fontsize + 1));\n";
    	returnStr += "rect(7110, yaxisvloop, 7217, yaxisvloop - 100, lwd = 1);\n";
    	returnStr += "text(7150, yaxisvloop + 40, \"V3\", cex = (fontsize + 1));\n";
    	returnStr += "rect(7377, yaxisvloop, 7478, yaxisvloop - 100, lwd = 1);\n";
    	returnStr += "text(7430, yaxisvloop + 40, \"V4\", cex = (fontsize + 1));\n";
    	returnStr += "rect(7599, yaxisvloop, 7625, yaxisvloop - 100, lwd = 1);\n";
    	returnStr += "text(7600, yaxisvloop + 40, \"V5\", cex = (fontsize + 1));\n";

    	// draw nef
    	returnStr += "rect(8797, yaxis, 9417, yaxis - 100, lwd = 1.5);\n";
    	returnStr += "text(9100, yaxis - 40, \"nef\", cex = (fontsize + 1));\n";

    	// draw 3' LTR
    	returnStr += "rect(9086, yaxis2, 9719, yaxis2 - 100, lwd = 1.5);\n";
    	returnStr += "text(9440, yaxis2 - 40, \"3' LTR\", cex = (fontsize + 1));\n";


    	return returnStr;    	
    }
    /**
     * Runs the R script
     */
    public static void run_r_script() {
    	
    }
}


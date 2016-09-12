import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;


public class CreateInputFileAndParameter {
	/**
	 * Read in a folder containing files and create the input file for the R plot
	 * Choice of summarizing them or display them individually
	 * The input file assumes that the folder contain only HXB2 corrected sequences
	 * @param args
	 */
    public static void main(String[] args) {
    	System.out.println(args.length);
    	String folderName = args[0];
    	String summary_type = args[1].toUpperCase(); // AVG or MED or RAW
    	String outputFile = args[2]; // input file    	
    	String paramFile = args[3]; // parameter file name
        String title = args[4];
        String yaxis = args[5];
    	String colors = args[6];
        String plottype = args[7]; 

	if (title.equals("")) {
            title = "Graph Title";
        }
        if (colors.equals("")) {
            colors = "black";
        }
        if (yaxis.equals("")) {
            yaxis = "Y-axis";
        }
    	String optionOfSummarizeSubtype = "ALL"; // future argument for summarizing subtypes
    	
    	// we can have 9719 linked list
    	LinkedList[] HXB2_List = new LinkedList[9719];
    	
    	for (int i = 0; i < 9719; i++) {
    		HXB2_List[i] = new LinkedList<Double[]>();
    	}
    	LinkedList fileNames = new LinkedList();
    	try {
    		double min = Double.POSITIVE_INFINITY;
    		double max = Double.NEGATIVE_INFINITY;
    		File dir = new File(folderName);
    		for (File child : dir.listFiles()) {
    		    if (".".equals(child.getName()) || "..".equals(child.getName())) {
    			    continue;  // Ignore the self and parent aliases.
    			}
    		    fileNames.add(child.getName());
    		    // read the file and add to list
    			// Do something with child
    			FileInputStream fstreamFull = new FileInputStream(child.getPath());
    			DataInputStream dinFull = new DataInputStream(fstreamFull);
    			BufferedReader in = new BufferedReader(new InputStreamReader(dinFull));
    			int count_index = 0;
    			while (in.ready()) {
    				String str = in.readLine();
    				String[] split = str.split("\t");
    				int hxb2_index = new Integer(split[0]) - 1; // hxb2 numbering
    				while (count_index < hxb2_index) {
    					HXB2_List[count_index].add(Double.NaN);
    					count_index++;
    				}
    				double value = new Double(split[1]);
    				System.out.println(hxb2_index);
    				HXB2_List[hxb2_index].add(value);
    				
    				if (min > value) {
    					min = value;
    				}
    				if (max < value) {
    					max = value;
    				}
    				count_index++;
    			}
    			in.close();    		    
				while (count_index < 9719) {
					HXB2_List[count_index].add(Double.NaN);
					count_index++;
				}
    	    } // end for loop
    		System.out.println(HXB2_List.length);
    		FileWriter fwriter = new FileWriter(outputFile);
		    BufferedWriter out = new BufferedWriter(fwriter);
    		for (int i = 0; i < HXB2_List.length; i++) {
    			if (summary_type.equals("AVG")) {
    				out.write((i + 1) + "\t" + calculateAverage(HXB2_List[i]) + "\n");
    				
    			} else if (summary_type.equals("STDEV")) {
    				out.write((i + 1) + "\t" + calculateStandardDev(HXB2_List[i]) + "\n");
    			} else if (summary_type.equals("MED")) {
    				out.write((i + 1) + "\t" + calculateMedian(HXB2_List[i]) + "\n");
    			} else if (summary_type.equals("RAW")) {
    				out.write((i + 1) + "");
    				
    		    	Iterator itr = HXB2_List[i].iterator();    		    	
    		    	while (itr.hasNext()) {
    		    		out.write("\t" + (Double)itr.next());
    		    	}
    		    	out.write("\n");
    			} else if (summary_type.equals("PERCENTILE")) {
    				//for (int k = 0; k < HXB2_List[i].size(); k++) {
    				//    System.out.println("Peek Object: " + HXB2_List[i].get(k));
    				//}
    				double value_min = calculateMin(HXB2_List[i]);
    				double value_25percentile = calculate25Percentile(HXB2_List[i]);
    				double value_median = calculateMedian(HXB2_List[i]);
    				double value_75percentile = calculate75Percentile(HXB2_List[i]);
    				double value_max = calculateMax(HXB2_List[i]);
    				//out.write((i + 1) + "\t" + value_min + "\t" + value_max + "\t" + value_25percentile + "\t" + value_75percentile + "\t" + value_median + "\n");
    				out.write((i + 1) + "\t" + value_25percentile + "\t" + value_75percentile + "\t" + value_median + "\n");
    				
    			}
    		}    		    		
    		FileWriter fwriter2 = new FileWriter(paramFile);
		    BufferedWriter out2 = new BufferedWriter(fwriter2);
		    
    		if ((summary_type.equals("AVG") || summary_type.equals("MED") || summary_type.equals("STDEV")) && optionOfSummarizeSubtype.equals("ALL")) {
    			    			
    			out2.write("1" + "\t" + min + "\t" + max + "\t" + "0" + "\t" + "LEFT ONLY" + "\t" + "All_Subtype" + "\n");
    			out2.write("All_Subtype" + "\t" + min + "\t" + max + "\t" + colors + "\t" + "1" + "\t" + "LEFT" + "\t" + "ADD" + "\t" + yaxis + "\t" + plottype + "\t" + title + "\n");
    					
    		} else if (summary_type.equals("PERCENTILE")) {
    			out2.write("3" + "\t" + min + "\t" + max + "\t" + "0" + "\t" + "LEFT ONLY" + "\t" + "All_Subtype" + "\n");
    			//out2.write("MIN" + "\t" + min + "\t" + max + "\t" + "black" + "\t" + "1" + "\t" + "LEFT" + "\t" + "ADD" + "\t" + "Axis Name" + "\t" + plottype + "\tPercentiles\n");
    			//out2.write("Max" + "\t" + min + "\t" + max + "\t" + "black" + "\t" + "1" + "\t" + "LEFT" + "\t" + "ADD" + "\t" + "Axis Name" + "\t" + plottype + "\tPercentiles\n");
    			out2.write("25%" + "\t" + min + "\t" + max + "\t" + "red" + "\t" + "1" + "\t" + "LEFT" + "\t" + "ADD" + "\t" + "Axis Name" + "\t" + plottype + "\tPercentiles\n");    			
    			out2.write("75%" + "\t" + min + "\t" + max + "\t" + "red" + "\t" + "1" + "\t" + "LEFT" + "\t" + "ADD" + "\t" + "Axis Name" + "\t" + plottype + "\tPercentiles\n");
    			out2.write("Median" + "\t" + min + "\t" + max + "\t" + "blue" + "\t" + "1" + "\t" + "LEFT" + "\t" + "ADD" + "\t" + "Axis Name" + "\t" + plottype + "\tPercentiles\n");
    			
    		} else {
    			out2.write((HXB2_List.length - 1) + "\t" + min + "\t" + max + "\t" + "0" + "\t" + "LEFT ONLY" + "\t" + "ALL" + "\n");
    			int color_id = 0;
    			Iterator itr = fileNames.iterator();
    			String last_name = "";
    			while (itr.hasNext()) {
    				
    				String str = (String)itr.next();
    				String[] split_str = str.split("_");
    				if (split_str.length > 4) {
	    				str = str.split("_")[3];
	    				System.out.println("output type: " + str);
	    				if (!str.equals(last_name)) {    					
	        			    color_id++;
	        			    last_name = str;
	        			    if (color_id == 11) {
	        			    	color_id = 1;
	        			    }
	    				}
    				} else {
    					color_id = 1;
    				} 
                                if (colors.equals("NONE")) {
        			    out2.write(str + "\t" + min + "\t" + max + "\t" + convert_number_to_color(color_id) + "\t" + "1" + "\t" + "LEFT" + "\t" + "ADD" + "\t" + yaxis + "\t" + plottype + "\t" + title + "\n");    				

                                } else {
        			    out2.write(str + "\t" + min + "\t" + max + "\t" + colors + "\t" + "1" + "\t" + "LEFT" + "\t" + "ADD" + "\t" + yaxis + "\t" + plottype + "\t" + title + "\n");    				

                                }
    			}
    		}
    		out2.close();
    		out.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static String convert_number_to_color(int col_id) {
    	if (col_id == 0) {
    		return "white";
    	}
    	if (col_id == 1) {
    		return "black";
    	}
    	if (col_id == 2) {
    		return "dark blue";    		
    	}
    	if (col_id == 3) {
    		return "dark green";
    	}
    	if (col_id == 4) {
    		return "dark red";
    	}
    	if (col_id == 5) {
    		return "blue";
    	}
    	if (col_id == 6) {
    		return "green";
    	}
    	if (col_id == 7) {
    		return "red";
    	}
    	if (col_id == 8) {
    		return "purple";
    	}
    	if (col_id == 9) {
    		return "light blue";
    	}
    	if (col_id == 10) {
    		return "orange";
    	}
    	return "white";
    }
    public static double calculateAverage (LinkedList hxb2_list) {
    	double result = 0;
    	Iterator itr = hxb2_list.iterator();
    	int count = 0;
    	while (itr.hasNext()) {
    		double value = (Double) itr.next();
    		if (!Double.isNaN(value)) {
    		    result = result + value;
    		    count++;
    		}
    	}
    	return result / count;
    }
    
    public static double calculateMedian (LinkedList hxb2_list) {
    	
    	double result = 0;
    	Iterator itr = hxb2_list.iterator();
    	int count = 0;
    	while (itr.hasNext()) {
    		double value = (Double) itr.next();
    		if (!Double.isNaN(value)) {
    		    count++;
    		}
    	}
    	Double[] list = new Double[count];
    	itr = hxb2_list.iterator();
    	count = 0;
    	while (itr.hasNext()) {
    		double value = (Double) itr.next();
    		if (!Double.isNaN(value)) {
    			list[count] = value;
    		    count++;
    		}
    	}    	
    	
    	    	
    	Arrays.sort(list);
    	
    	int half = list.length / 2;
    	if (list.length == 0) {
    		return Double.NaN;
    	}    	
    	return list[half];
    }

    public static double calculateMin (LinkedList hxb2_list) {
    	double result = 0;
    	Iterator itr = hxb2_list.iterator();
    	int count = 0;
    	while (itr.hasNext()) {
    		double value = (Double) itr.next();
    		if (!Double.isNaN(value)) {
    		    count++;
    		}
    	}
    	Double[] list = new Double[count];
    	itr = hxb2_list.iterator();
    	count = 0;
    	while (itr.hasNext()) {
    		double value = (Double) itr.next();
    		if (!Double.isNaN(value)) {
    			list[count] = value;
    		    count++;
    		}
    	}    	
    	
    	    	
    	Arrays.sort(list);    	    	
    	if (list.length == 0) {
    		return Double.NaN;
    	}
    	return list[0];
    }
    public static double calculate25Percentile (LinkedList hxb2_list) {
    	double result = 0;
    	Iterator itr = hxb2_list.iterator();
    	int count = 0;
    	while (itr.hasNext()) {
    		double value = (Double) itr.next();
    		if (!Double.isNaN(value)) {
    		    count++;
    		}
    	}
    	Double[] list = new Double[count];
    	itr = hxb2_list.iterator();
    	count = 0;
    	while (itr.hasNext()) {
    		double value = (Double) itr.next();
    		if (!Double.isNaN(value)) {
    			list[count] = value;
    		    count++;
    		}
    	}    	
    	
    	
    	
    	Arrays.sort(list);    	    	
    	int percentile25 = list.length / 4;
    	if (list.length == 0) {
    		return Double.NaN;
    	}
    	return list[percentile25];
    }
    public static double calculate75Percentile (LinkedList hxb2_list) {
    	double result = 0;
    	Iterator itr = hxb2_list.iterator();
    	int count = 0;
    	while (itr.hasNext()) {
    		double value = (Double) itr.next();
    		if (!Double.isNaN(value)) {
    		    count++;
    		}
    	}
    	Double[] list = new Double[count];
    	itr = hxb2_list.iterator();
    	count = 0;
    	while (itr.hasNext()) {
    		double value = (Double) itr.next();
    		if (!Double.isNaN(value)) {
    			list[count] = value;
    		    count++;
    		}
    	}    	
    	
    	
    	
    	Arrays.sort(list);    	    	
    	int percentile75 = list.length / 4 * 3;
    	if (list.length == 0) {
    		return Double.NaN;
    	}
    	return list[percentile75];
    }
    public static double calculateMax (LinkedList hxb2_list) {
    	double result = 0;
    	Iterator itr = hxb2_list.iterator();
    	int count = 0;
    	while (itr.hasNext()) {
    		double value = (Double) itr.next();
    		if (!Double.isNaN(value)) {
    		    count++;
    		}
    	}
    	Double[] list = new Double[count];
    	itr = hxb2_list.iterator();
    	count = 0;
    	while (itr.hasNext()) {
    		double value = (Double) itr.next();
    		if (!Double.isNaN(value)) {
    			list[count] = value;
    		    count++;
    		}
    	}    	
    	
    	
    	
    	Arrays.sort(list);    	    	
    	if (list.length == 0) {
    		return Double.NaN;
    	}
    	return list[list.length - 1];
    }    
    
    
    public static double calculateStandardDev( LinkedList hxb2_list) {
    	//Double[] list = (Double[]) hxb2_list.toArray();
    	Double average = calculateAverage(hxb2_list);
    	double sum_squared = 0;
    	Iterator itr = hxb2_list.iterator();
    	int count = 0;
    	while (itr.hasNext()) {
    		double value = (Double) itr.next();
    		if (!Double.isNaN(value)) {
    		    sum_squared += (value - average) * (value - average);
    		    count++;
    		}
    	}
    	double standardDev = Math.sqrt(sum_squared / (count - 1));
    	return standardDev;    
    }
    
}


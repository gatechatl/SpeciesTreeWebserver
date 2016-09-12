import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;


public class CreateSNAFile {

    public static void main(String[] args) {
        
        try {
            
            String fileName = args[0];
            String specialChar = args[1];
            int num = 0;
            try {
                num = new Integer(args[2]) - 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (num < 0) {
                num = 0;
            }
            
            
            specialChar = specialChar.replaceAll("\"", "");
            HashMap map = new HashMap();
            HashMap norepeat = new HashMap();
            String outputFile = args[3];
            FileWriter fwriter = new FileWriter(outputFile);
            BufferedWriter out = new BufferedWriter(fwriter);
            
            FileInputStream fstream = new FileInputStream(fileName);
            DataInputStream din = new DataInputStream(fstream); 
            BufferedReader in = new BufferedReader(new InputStreamReader(din));         
            while (in.ready()) {
                String str = in.readLine();
                
                String[] split = str.split(specialChar);
                if (num < split.length) {
                    num = 0;
                }
            }
            in.close();
            LinkedList list = new LinkedList();
            fstream = new FileInputStream(fileName);
            din = new DataInputStream(fstream); 
            in = new BufferedReader(new InputStreamReader(din));            
            while (in.ready()) {
                String str = in.readLine();
                if (!norepeat.containsKey(str)) {
                    String[] split = str.split(specialChar);
                    if (map.containsKey(split[num])) {
                        String stuff = (String)map.get(split[num]);
                        stuff += "\t" + str;
                        map.put(split[num], stuff);
                    } else {
                        list.add(split[num]);
                        map.put(split[num], str);
                    }
                }
                norepeat.put(str, str);
            }
            in.close();
            norepeat.clear();
            Iterator itr = list.iterator();     
            while (itr.hasNext()) {
                String key = (String)itr.next();
                String str = (String)map.get(key);
                String[] split = str.split("\t");
                out.write(key + " " + split.length + " " + str.replaceAll("\t", " ") + "\n");
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


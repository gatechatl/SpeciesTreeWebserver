import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.*;

/**
 * Reads in Prefs file and stores prefs variables for rest of program
 * 
 * @author Ralph Pethica
 * @version 18/10/2008
 */
public class Preferences
{
    public ArrayList<String> preflist;
    public String svgfile = "";
    public String topologyfile = "";
    public String namestopologyfile = "";
    public String ncbitopologyfile = "";
    public String embedfile = "";
    public String embedout = "";
    public String xmlfile = "";
    public String treetype = "";
    public String treestyle = "";
    public String names = "none";
    public String link = "";
    public String css = "";
    public String js = "";
    public String buildlink = "";
    public boolean db = false;
    public boolean extra = false;
    public boolean svgtoscreen = false;
    public boolean pagetoscreen = false;
    public int minbrlen = 0;
    public int x = 500;
    public int y = 500;
    public int textsize = 100;
    public int firstrun = 1;
    
    /**
     * Constructor for objects of class Preferences
     */
    public Preferences()
    {
        //Set variables
        xmlfile = "";
        treestyle = "";
        treetype = "phylo";
        topologyfile = "../temp/output.tre";
        namestopologyfile = "";
        ncbitopologyfile = "";
        embedfile = "";
        css = "../scripts/tree.css";
        js = "../scripts/tree.js";
        embedout = "../temp/embed.txt";
        svgfile = "../temp/output.svg";
        xmlfile = "../temp/output.xml";
        names = "none";
        db = false;
        extra = false;
        svgtoscreen = false;
        pagetoscreen = false;
        minbrlen = 0;
        link = "";
        buildlink = "";
        preflist = new ArrayList<String>();
        x = 500;
        y = 500;
        textsize = 100;
        String preffile = "preferences.txt";
        firstrun = 1;
        
        //Run methods
        readPrefs(preffile);
        processPrefs();
        firstrun = 0;
    }

    /**
     * Read prefs file
     * 
     */
    public void readPrefs(String preffile)
    {
        //System.out.println("reading prefs" + preffile);
        try
            {
                BufferedReader in= new BufferedReader(new FileReader(preffile));
                String line;
                String prefs = new String();
                while ((line = in.readLine()) != null) {
                    if (!line.equals("")){
                        preflist.add(line);
                    }
                }
                if (firstrun ==0){
                    System.err.println("Preference file found, setting prefs now...");
                }
                in.close();
                //System.out.println(tree);
            } 
                        catch (Exception e)
            {
                if (firstrun ==1){
                    System.err.println("Setting initial default prefs...");
                }
                else{
                    System.err.println("Preferences file not found, using defaults...");
                }
            }
        
        
    }
    
    
    public void processPrefs(){
        
        if (!preflist.isEmpty()){           //if there are children in this node's arraylist
            Iterator it = preflist.iterator();
            while(it.hasNext()){
                    //System.out.println(it.next());
                    String prefline = (String) it.next();
                    StringTokenizer parser = new StringTokenizer(prefline);
                    
                    int tokens = parser.countTokens();
                    if (tokens < 2){
                        System.err.println("Preference file corrupt - should contain 2 words per line");
                    }
                    else{
                        String setting = parser.nextToken();
                        //System.out.println(setting);
                        String value = parser.nextToken();
                        //System.out.println(value);
                        
                        setPrefs(setting,value);
                    }
            }
        }
        
    }
    
    
    private void setPrefs(String setting, String value){
        
        if (setting.equals("svgfile")){
               svgfile = value;
        }
        else if (setting.equals("topologyfile")){
            namestopologyfile = value;
        }
        else if (setting.equals("namestopologyfile")){
            topologyfile = value;
        }
        else if (setting.equals("ncbitopologyfile")){
            ncbitopologyfile = value;
        }
        else if (setting.equals("xmlfile")){
            xmlfile = value;
        }
        else if (setting.equals("css")){
            css = value;
        }
        else if (setting.equals("js")){
            js = value;
        }
        else if (setting.equals("treetype")){
            treetype = value;
        }
        else if (setting.equals("treestyle")){
            treestyle = value;
        }
        else if (setting.equals("link")){
            link = value;
        }
        else if (setting.equals("embedfile")){
            embedfile = value;
        }
        else if (setting.equals("textsize")){
            textsize = Integer.parseInt(value);
        }
        else if (setting.equals("embedout")){
            embedout = value;
        }
        else if (setting.equals("minbrlen")){
            minbrlen = Integer.parseInt(value);
        }
        else if (setting.equals("extradata")){
            if (value.equals("on")){
                extra = true;
            }
            else{
             extra = false;   
            }
        }
        else if (setting.equals("svgtoscreen")){
            if (value.equals("on")){
                svgtoscreen = true;
            }
            else{
             svgtoscreen = false;   
            }
        }
        else if (setting.equals("pagetoscreen")){
            if (value.equals("on")){
                pagetoscreen = true;
            }
            else{
             pagetoscreen = false;   
            }
        }
        else if (setting.equals("buildlink")){
            buildlink = value;
        }
        else if (setting.equals("db")){
            if (value.equals("on")){
                db = true;
                //System.err.println("Database On!!");
            }
            else{
             db = false;   
            }
        }
        else{
            System.err.println("Error in preference file");
        }
        
    }
    
}

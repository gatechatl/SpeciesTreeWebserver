import java.io.*;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * Class to control the program from terminal using various commands.
 * 
 * @author Ralph Pethica 
 * @version 06/09/06
 */
public class TreeVector
{
    private static String style; // Tree style - square/triangle
    private static int x;
    private static int y;
    private static String treetopology;
    /**
     * Constructor for objects of class Phyloserve
     */
    public TreeVector()
    {
        x = 0;
        y = 0;
        treetopology = "";
    }

    /**
     * Main function to take commands and tailor tree output.
     * 
     * @param  various arguements from terminal
     * @return     void
     */
    public static void main(String[] args)
    {
        Preferences prefs = new Preferences();
        //System.out.println(prefs.svgfile);
        //makeTestFile();
        x = 0;
        y = 0;
        //Create new error log object
        Error error = new Error();
        
        if(null == args || args.length < 1) {
            System.err.println("Please enter a file name");
            System.exit(1);
        }
        System.err.println("Setting Preferences.....");
        processArgs(args,prefs);
        
        System.err.println("Parsing.....");
        NewickParser newick = new NewickParser();
        newick.readFile(args[0]);
        newick.parseTree();
        Node root = newick.getNode();
        
        DatabaseAccess db = new DatabaseAccess();
        if (prefs.db){
            //Register Database
            register();
        }
        
        //Creat new ExtraData array
        ArrayList<ExtraData> data = new ArrayList<ExtraData>();
        //if no args
        
        ProcessNames pn = new ProcessNames();
        if (!prefs.names.equals("none")){
            System.err.println("Checking Selected Names.....");
            System.err.println(prefs.names);
            if(pn.checkNames(root,prefs.names,error)){
                System.err.println("Selected Names Found.....");
            }
            else{
                System.out.println("Some of the specified names were not found in the tree");
                System.exit(1);
            }
        }
        //Extra array stuff for DB
        pn.addAllNames(root,data);
        
        //Print existing names
        //int n = data.size();
        //for(int i = 0; i < n ; i++) System.out.println( data.get(i).getNewickName() );
        
        //
        System.err.println("Exploring Tree.....");
        Explorer explorer = new Explorer();
        explorer.getPositions(root);
        System.err.println("Getting Coordinates.....");
        //System.err.println("Type is now: " + type);
        Coordinates coords;
        //Size for if below capped at bigger than 150 smaller than 10000
        if (x>150 && y>150  && x< 10000  && y<10000){    //If the size has been specified use it and override constructor only if bigger than 150
            coords = new Coordinates(x,y);
        }
        else{               //If no size specified use default constructor
            coords = new Coordinates();
        }
        if (prefs.treetype.equals("phylo")){
            coords.findPhylogram(root);
        }
        else if (prefs.treetype.equals("clad")){
            coords.findCladogram(root);
        }
        else{
            coords.findSimpleClad(root);
        }
        
        if (prefs.db){
            //SUPERFAMILY Get Real Names
            System.err.println("Connecting to DB.....");
            db.testMethod(root,data);
        }
        
        SVG svgfinder = new SVG();
        String svg;
        System.err.println("Producing SVG.....");
        if (prefs.treestyle.equals("triangle")){
            svg = svgfinder.getSVG(root, error, data, prefs);
        }
        else{
            svg = svgfinder.getSVG(root, error, data, prefs);
        }
        
        System.err.println("Creating SVG and XML Outputs.....");
        
        //Print new names
        //n = data.size();
        //for(int i = 0; i < n ; i++) System.out.println( data.get(i).getFullName() );
        
        //Update this soon
        
        if (!prefs.embedfile.equals("")){
            String embed = svgfinder.generateEmbed(prefs);
            stringToFile(embed,prefs.embedfile);
        }
        
        if (prefs.pagetoscreen){
            String page = svgfinder.generatePage(prefs);
            System.out.print(page);
        }
        
        
        xmlPrinter(root,prefs);
        String newicktree = root.getNewick();
        stringToFile(newicktree,prefs.topologyfile);
        
        if (prefs.namestopologyfile != ""){
            String namesnewicktree = root.getNewickExtra(data, "names");
            stringToFile(namesnewicktree,prefs.namestopologyfile);
        }
        if (prefs.ncbitopologyfile != ""){
            String ncbinewicktree = root.getNewickExtra(data, "ncbi");
            stringToFile(ncbinewicktree,prefs.ncbitopologyfile);
        }
        //System.err.println(svg);
        stringToFile(svg,prefs.svgfile);
        if (prefs.svgtoscreen){
            System.out.print(svg);
        }
        System.err.println("Done!");
    }
    
    /**
     * Prints the output.xml file from a Node object
     * 
     * @param  Node node
     * @return     void
     */
    public static void xmlPrinter(Node node, Preferences prefs)
    {
        String xml = node.getXML();
        //System.err.println(xml);
        FileOutputStream out;
        PrintStream print;
        try
           {
                out = new FileOutputStream(prefs.xmlfile);
                print = new PrintStream( out );
                print.println(xml); 
                print.println ();
                print.close();
            }
            catch (Exception e)
            {
                System.err.println("Error writing to file");
            }
        
    }
    
    
    /**
     * Prints the output.svg from a string
     * 
     * @param  String svg
     * @return     void
     */
    public static void stringToFile(String string, String filename)
    {
        FileOutputStream output;
        PrintStream print;
        try
           {
                output = new FileOutputStream(filename);
                print = new PrintStream( output );
                print.println(string); 
                print.println ();
                print.close();
            }
            catch (Exception e)
            {
                System.err.println("Error writing to file " + e);
            }
    }
    
    /**
     * Processes user commands
     * 
     * @param  String arguements
     * @return     void
     */    
    public static void processArgs(String[] args, Preferences prefs)
    {
        int i = 1;  //start at 1 as 0 should be filename
        
        while (i < args.length){
            //System.err.println(args[i]);
            
            //This is a bit weak, but the extra prefs file has to be specified first otherwise other command line selected prefs may not be saved
            if(args[i].equals("-prefs")){
                if (i+1 < args.length){
                    i++;
                    prefs.readPrefs(args[i]);
                    prefs.processPrefs();
                }
                else {
                    System.err.println("Please specify filename and path when using -prefs");
                    System.exit(1);
                }
            }
            
            else if(args[i].equals("-simpleclad")){      //Deal with the tree type commands
                prefs.treetype = "simpleclad";
            }
            else if(args[i].equals("-clad")){
                prefs.treetype = "clad";
            }
            else if(args[i].equals("-phylo")){
                prefs.treetype = "phylo";
            }
            else if(args[i].equals("-out")){
                if (i+1 < args.length){
                    i++;
                    prefs.svgfile = args[i];
                }
                else {
                    System.err.println("Please specify filename and path when using -out");
                    System.exit(1);
                }
            }
            
            else if(args[i].equals("-topology")){
                if (i+1 < args.length){
                    i++;
                    prefs.topologyfile = args[i];
                }
                else { 
                    System.err.println("Please specify filename and path when using -topology");
                    System.exit(1);
                }
            }
            else if(args[i].equals("-namestopology")){
                if (i+1 < args.length){
                    i++;
                    prefs.namestopologyfile = args[i];
                }
                else { 
                    System.err.println("Please specify filename and path when using -namestopology");
                    System.exit(1);
                }
            }
            else if(args[i].equals("-ncbitopology")){
                if (i+1 < args.length){
                    i++;
                    prefs.ncbitopologyfile = args[i];
                }
                else { 
                    System.err.println("Please specify filename and path when using -namestopology");
                    System.exit(1);
                }
            }
            else if(args[i].equals("-extradataoff")){
                    prefs.extra = false;
            }
            else if(args[i].equals("-embedfile")){
                if (i+2 < args.length){
                    i++;
                    prefs.embedfile = args[i];
                    i++;
                    prefs.embedout = args[i];
                }
                else { 
                    System.err.println("Please specify filename and path when using -embedfile, savepath first then link for html ");
                    System.exit(1);
                }
            }
            
            else if(args[i].equals("-size")){       //Deal with the proportions specified
                if (i+2 < args.length){
                    i++;
                    x = getInt(args[i]);
                    prefs.x = getInt(args[i]);
                    i++;
                    y = getInt(args[i]);
                    prefs.y = getInt(args[i]);
                    //System.err.println("X = " + x);
                    //System.err.println("Y = " + y);
                }
                else {
                    System.err.println("Please specify size X first in this way: -size 500 600");
                    System.exit(1);
                }
            }
            else if(args[i].equals("-square")){      //Deal with the style
                style = "square";
                prefs.treestyle = "square";
            }
            else if(args[i].equals("-triangle")){
                style = "triangle";
                prefs.treestyle = "triangle";
            }
            else if(args[i].equals("-selectnames")){       //Deal with the proportions specified
                if (i+1 < args.length){
                    i++;
                    prefs.names = args[i];
                    //namesnames = args[i]; = getInt(args[i]);                //CHANGE THIS TO STRING PROCESSING FUNCTION
                }
                else {
                    System.err.println("Please enter comma separated list of genome codes");
                    System.exit(1);
                }
            }
            else{                                   //Deal with any unrecongnised commands
                System.err.println("Command not recognised, make sure you leave no gap between - and command");
                System.err.println("Valid Commands: -selectnames - simpleclad, -clad, -phylo, - size int int, -square, -triangle -out");
                System.exit(1);
            }
            i++;
        }     
    }
    
    /**
     * Tests and converts a String number to an integer
     * 
     * @param  String arg
     * @return     int
     */
    public static int getInt(String arg)
    {
        int number = 0;
        try{
            number = Integer.valueOf(arg).intValue();
        }
        catch (Exception e){
            System.err.println("Please enter only integers after size command");
            System.exit(1);
        }
        return number;
        
    }
    
    public static void register() {
        try {
            // The newInstance() call is a work around for some
            // broken Java implementations

            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        }
    }
    

}

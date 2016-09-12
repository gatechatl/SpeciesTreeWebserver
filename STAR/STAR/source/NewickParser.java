import java.util.Stack;
import java.io.*;
/**
 * A Parser for the Newick format of tree topologies. 
 * Contains methods to parse to internal format or output as XML. 
 * Uses Node objects for internal data structure.
 * 
 * @author Ralph Pethica
 * @version 11/08/2006
 */
public class NewickParser
{
    private String newick;
    private Stack <Node> currentnode;
    private Node node;
    private int position;
    private boolean firstpass;
    /**
     * Constructor for objects of class NewickParser
     */
    public NewickParser()
    {
        newick = new String();
        position = 0;
        currentnode = new Stack<Node>();
        node = new Node();
        currentnode.push(node);
        firstpass = true;
    }
    
    /**
     * Main parser, should be used once the newick string is loaded.
     * Can be loaded using readFile or setString methods.
     * Possibly change this to several constructors later.
     * @param  void
     * @return     void(for now)
     */
    public void parseTree()
    {
        if (newick == null || newick.equals("")){   //Check tree variable has been filled
            System.out.println("Please load a tree before using the parsetree method");
            System.exit(1);
        }
        
        if (checkBrackets() == false){      //Check brackets for pre loaded newick tree
            System.out.println("There is a ')' in the wrong place in this tree");
            System.exit(1);   
        }

        String noblanks = newick.replace(" ", "");
        newick = noblanks;                  //Get rid of blanks between chars for easier parsing
        
        int i=newick.length();
        char c = newick.charAt(i-1);
            if(c != ';'){
                System.out.println("Tree does not end in a ';'");
                System.exit(1);
            }
                
        scanNode();
        
    }
    
    /**
     * Reads the next node in the newick string
     * 
     * @param  void
     * @return     void
     */
    private void scanNode()
    {
        if (getCurrentChar() == '('){       //if the char is a ( then we must have hit an internal node
            scanInternalNode();
            //System.out.println("Internal Node Found");
        }
        else if (getCurrentChar() != ';'){     //if char is anything else, then it must be a leaf node label
            getLeafLabel();
            //System.out.println("Leaf Label Found");
            
        }
        else {                                 //Else must be a ; so report error
            System.out.println("Found a ; in the wrong place in tree");
            System.exit(1);
        }
    }
    
    /**
     * Reads an internal node of the newick string
     * 
     * @param  void
     * @return     void
     */
    private void scanInternalNode()
    {
        position++;
        if (firstpass == true){     //This statement is here to make sure a node is not added as a descendant first time round
            firstpass = false;      //Once set to false a child node is always added to the stack
        }
        else{
            Node node = new Node();         //We now make a new node
            Node topnode = currentnode.pop();   //get the last node from stack
            topnode.addChild(node);     //add new node as child of last node
            currentnode.push(topnode);  //push lastnode back
            currentnode.push(node);     //push new node back
        }
        
        scanNode(); //Read the first node
        
        while (getCurrentChar() != ')'){
            if (getCurrentChar() == ','){   //if character is a , then move to next char
                position++;
                //System.out.println("Found a , in while loop and incremented position to: " + position + " char at pos = " + newick.charAt(position));
            }
            scanNode();
        }
        position++;
        if (getCurrentChar() != ',' && getCurrentChar() != ')' && getCurrentChar() != ';'){ //label found
            //System.out.println("About to get label in scanInternalNode method");
            Node tempnode = currentnode.pop();
            tempnode.setName(getLabel());
            currentnode.push(tempnode);
        }
       if (getCurrentChar() == ':'){   //length found
            position++;
            Node lengthnode = currentnode.pop();        //pop last node
            lengthnode.setLength(getLength());          //set the length to double from getlength method
            currentnode.push(lengthnode);               //push back on stack
        }
        currentnode.pop();
    }
    
    /**
     * Gets the label from a string using the next end character as a mark
     * 
     * @param  void
     * @return     String
     */
    private String getLabel()
    {
        String label = new String();

        int end = getNodeEnd();                         //find the next end character
        int colon = newick.indexOf( ':',position);
        
        if (colon >=0 && colon < end){                  //activate when length calculator sorted
            end = colon;
       }
        
        label = newick.substring(position, end);        // get all characters between current position and located end char
        position = end;                                 // once done, set the position to end

        return label;
    }
    
    
    private void getLeafLabel()
    {
        String label = getLabel();
        Node childnode = new Node();
        childnode.setName(label);
        childnode.setType("leaf");
 
        if (getCurrentChar() == ':'){
            position++;
            childnode.setLength(getLength());          //set the length to double from getlength method
        }
        
        Node tempnode = currentnode.pop();
        tempnode.addChild(childnode);
        currentnode.push(tempnode);
        //System.out.println("Leaf label found looks like: " + label);
        //System.out.println("Char at position now is: " + newick.charAt(position));
    }
        
    private double getLength()
    {
        int end = getNodeEnd();
        double length  = 0;
        String stringlength = newick.substring(position, end);
        try {
            length = Double.valueOf(stringlength);
        }
        
        catch (Exception e){
                System.out.println("Error reading branch length");
                System.exit(1);
        }
        position = end; ///May want to add check that end is valid before doing this
        //System.out.println("Length found is: " + length);
        return length;
    }
        
        
     /**
     * Uses the position int to calculate the int location in the newick char array(string) of a end of label character such as ,
     * 
     * @param  void
     * @return     int location
     */
    public int getNodeEnd()
    {
        int comma = newick.indexOf(',',position);   //if nothing found a -1 value is returned, and must be checked for later
        int semicolon = newick.indexOf(';',position);
        int rightbracket = newick.indexOf(')',position);
        int leftbracket = newick.indexOf('(',position);
        int location = newick.length();             //position of current end char
        
        if (comma >= 0){    //if there is a comma coming up
            location = comma;    //location of the comma is the current position in string plus distance of comma
        }
        
        if(semicolon >= 0 && semicolon < location){      //if there is a semicolon before comma, then use that as end
            location = semicolon;
        }
        
        if (rightbracket >= 0 && rightbracket < location){  //if there is a right bracket before semicolon, then use that as end
            location = rightbracket;
        }
        
        if (leftbracket >= 0 && leftbracket < location) {
               System.out.println("Found opening bracket in wrong place when detecting node end");
               System.exit(1);
        }
        return location;
    }
    
    /**
     * Gets the char in the newick string at the location of the position counter int
     * 
     * @param  void
     * @return     char
     */
    private char getCurrentChar()
    {
           if (position>newick.length()){
                System.out.println("Position counter off scale of tree string");
                System.exit(1);
            }
            char c = newick.charAt(position);
            return c;
    }
    /**
     * Reads a Newick Tree from a file and loads to main string variable
     * 
     * @param  String filename
     * @return     void(for now)
     */
    public void readFile(String filename)
    {
        try
            {
                BufferedReader in= new BufferedReader(new FileReader(filename));
                String line;
                String tree = new String();
                while ((line = in.readLine()) != null) {
                    tree = tree + line;
                }
                in.close();
                newick = tree;
                //System.out.println(tree);
            } 
                        catch (Exception e)
            {
                System.err.println("File input error, please make sure file name is the first arguement");
            }
        
    }
    
     /**
     * Loads main variable directly a with newick string
     * 
     * @param  String tree
     * @return     void(for now)
     */
    public void setString(String tree)
    {
        newick = tree;
    }
    
    private boolean checkBrackets()         //Checks the bracket numbers for a string returns true if OK
    {
        int bracketcounter = 0;
        for (int i=0; i<newick.length();i++){
            char c = newick.charAt(i);
            if(c == '('){
                bracketcounter++;
            }
            else if(c==')'){
                bracketcounter--;   
            }
            if (bracketcounter < 0){            //If there is an closing bracket before the next opening
                return false;
            }
        }
        if (bracketcounter > 0){          //If the number of brackets of each type is not equal
            return false;
        }
        return true;   
    }
    
    /**
     * Returns the main (parsed) node of the root of tree
     * 
     * @param  void
     * @return     Node node
     */
    public Node getNode()
    {
        return node;
    }
}

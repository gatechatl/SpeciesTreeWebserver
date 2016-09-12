import java.util.ArrayList;
import java.util.Iterator;
/**
 * Node class is a data structure and associated methods to store the nodes 
 * of a parsed Newick tree.
 * Each node also contains a method which allows it to print out relevant fields it contains as XML
 * 
 * @author Ralph Pethica 
 * @version 11/08/2006
 */
public class Node
{
    private String name;
    private String type;
    private Double length;
    public ArrayList<Node> children;    //Change back to private later
    private int level;
    private double treesize;
    private int depth;
    private double newicklength;
    private double startxcoord;
    private double startycoord;
    private double endxcoord;
    private double endycoord;
    private double standardy;
    private boolean selected;
    private double xmultiplier;
    private double ymultiplier;
    
    /**
     * Constructor for objects of class Node
     */
    public Node()
    {
        
        name = new String();
        type = new String("internal");  //Initial Node type set to internal
        length = new Double(0);        //Set initial branch length to zero
        children = new ArrayList<Node>();
        level = 0;
        treesize = new Double(0);
        depth = 0;
        newicklength = 0;
        startycoord = -1;
        startxcoord = -1;
        endxcoord = -1;
        endycoord = -1;
        standardy = -1;
        selected = false;
        xmultiplier = 0;
        ymultiplier = 0;
    }

    /**
     * Returns an XML tree as a string, representing this node and all children
     * 
     * @param  void
     * @return     String XML
     */
    public String getXML()      //Perhaps make a version of this where the header DTD can be manually specified
    {                           //Also allow changing of generated tags to user specified values
        
        String xml = new String();
        xml= "<?xml version = \"1.0\"?>\n";
        xml = xml + generateXML();
        return xml;
    }
    
    /**
     * Recursive function which produces an XML output of fields for each node
     * A string is built containing names and types from each node
     * 
     * @param  void
     * @return     String XML
     */
    public String generateXML()      
    {                         
        
        String xml = new String();
        
        String nodetype = new String();
        if (type.equals("internal")){
            nodetype = "INTERNAL";          //change this for change in XML node name
        }
        else {
               nodetype = "LEAF";           //change this for change in XML node name
        }
        
        xml = "<" + nodetype;
        if (!name.equals("")){
            xml = xml + " name = \"" + name + "\"";
        }
        if (length >0){
            xml = xml + " length = \"" + length + "\"";
        }
        //Section below tests tree size and depths
       // if (level > 0){
       //     xml = xml + " level = \"" + level + "\"";
       // }
        //if (depth > 0){
        //    xml = xml + " depth = \"" + depth + "\"";
        //}
        //if (newicklength > 0){
        //    xml = xml + " newicklength = \"" + newicklength + "\"";
        //}
       // if (treesize > 0){
        //    xml = xml + " treesize = \"" + treesize + "\"";
        //}
        //if (startxcoord >= 0){
        //    xml = xml + " startx = \"" + startxcoord + "\"";
        //}
        //if (endxcoord >= 0){
        //    xml = xml + " endx = \"" + endxcoord + "\"";
        //}
        //Test section done
        xml = xml + ">";
        
        if (!children.isEmpty()){           //if there are children in this node's arraylist
            Iterator it = children.iterator();
            while(it.hasNext()){
                Node child = (Node) it.next();
                xml = xml + child.generateXML();    //The recursive coolness happens here!
            }
        }
        xml = xml + "</" + nodetype + ">";
        
        return xml;
    }

    
    /**
     * Returns an Newick tree as a string, representing this node and all children
     * 
     * @param  void
     * @return     String Newick
     */
    public String getNewick()      //Perhaps make a version of this where the header DTD can be manually specified
    {                           //Also allow changing of generated tags to user specified values
        
        String newick = new String();
        newick = newick + generateNewick();
        newick = newick + ";";
        return newick;
    }
    
    /**
     * Recursive function which produces a Newick output of fields for each node
     * 
     * @param  void
     * @return     String Newick
     */
    public String generateNewick()      
    {                         
        
        String newick = new String();

        if (type.equals("internal")){
            newick = "(";
        }
        
        if (!name.equals("")){
            newick = newick + name;
        }
        
        if (!children.isEmpty()){           //if there are children in this node's arraylist
            Iterator it = children.iterator();
            while(it.hasNext()){
                Node child = (Node) it.next();
                newick = newick + child.generateNewick();
                if(it.hasNext()){
                    newick = newick + ",";
                }
            }
        }
        if (type.equals("internal")){
            newick = newick + ")";
        }
        if (length >0){
            newick = newick + ":" + length;
        }
        
        return newick;
    }
    
      /**
     * Returns an Newick tree as a string, representing this node and all children - uses extradata
     * 
     * @param  void
     * @return     String Newick
     */
    public String getNewickExtra(ArrayList<ExtraData> data, String sfextra)      //Perhaps make a version of this where the header DTD can be manually specified
    {                           //Also allow changing of generated tags to user specified values
        
        String newick = new String();
        newick = newick + generateNewickExtra(data, sfextra);
        newick = newick + ";";
        return newick;
    }
    
    /**
     * Recursive function which produces a Newick output of fields for each node
     * 
     * @param  void
     * @return     String Newick
     */
    public String generateNewickExtra(ArrayList<ExtraData> data,  String sfextra)      
    {                         
        
        String newick = new String();

        if (type.equals("internal")){
            newick = "(";
        }
        
        if (!name.equals("")){
            String fullname = name;
            String ncbi = "";
           //Roll through extra data and find this one
           
            int n = data.size();
            for(int i = 0; i < n ; i++){
                 //If the newickname is the same as this nodes newick name, and the full name isn't just null, 
                 //get this full name and make it the name
                 if (data.get(i).getNewickName().equals(name) && !data.get(i).getNewickName().equals(null)){
                     fullname = data.get(i).getFullName();
                     ncbi = data.get(i).getNCBI();
                 }
             }
             if (sfextra == "names"){
                fullname = fullname.replaceAll("[^a-zA-Z0-9]", ""); //strip non alphanumeric
                newick = newick + fullname;
                
            }
            if (sfextra == "ncbi"){
                newick = newick + ncbi;
            }
         }
        
        if (!children.isEmpty()){           //if there are children in this node's arraylist
            Iterator it = children.iterator();
            while(it.hasNext()){
                Node child = (Node) it.next();
                newick = newick + child.generateNewickExtra(data,sfextra);
                if(it.hasNext()){
                    newick = newick + ",";
                }
            }
        }
        if (type.equals("internal")){
            newick = newick + ")";
        }
        if (length >0){
            newick = newick + ":" + length;
        }
        
        return newick;
    }
    
   /**
     * Adds a child node to the descendants array of this node
     * 
     * @param  Node childnode
     * @return     void 
     */
    public void addChild(Node childnode)
    {
        children.add(childnode);
    }
    
   /**
     * Removes a child node from the list
     * 
     * @param  int index
     * @return     void 
     */
    public void removeChild(int index)
    {
        children.remove(index);
    }
    
    /**
     * Returns an arraylist of all child nodes for this node
     * 
     * @param  void
     * @return     ArrayList children 
     */
    public ArrayList getChildren()
    {
        return children;
    }
    
    
    
    /**
     * Returns the name of this node
     * 
     * @param  void
     * @return     String name 
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Sets the name of this node
     * 
     * @param  String name
     * @return     void
     */
    public void setName(String newname)
    {
        name = newname;
        
    }
    
    /**
     * Returns the type of this node
     * 
     * @param  void
     * @return     String type 
     */
    public String getType()
    {
        return type;
    }
    
    /**
     * Sets the type of this node
     * 
     * @param  String type
     * @return     void
     */
    public void setType(String newtype)
    {
        type = newtype;
    }
    
    /**
     * Returns the branch length for this node
     * 
     * @param  void
     * @return     Double length 
     */
    public Double getLength()
    {
        return length;
    }
    
    /**
     * Sets the branch length for this node
     * 
     * @param  Double length
     * @return     void
     */
    public void setLength(Double newlength)
    {
        length = newlength;
    }
    
    /**
     * Sets the level for this node
     * 
     * @param  int level
     * @return     void
     */
    public void setLevel(int newlevel)
    {
        level = newlevel;   
    }
    
    /**
     * Gets the level for this node
     * 
     * @param  void
     * @return     int level
     */
    public int getLevel()
    {
        return level;   
    }
    
    /**
     * Sets the depth for this node
     * 
     * @param  int depth
     * @return     void
     */
    public void setDepth(int newdepth)
    {
        depth = newdepth;   
    }
    
    /**
     * Gets the depth for this node
     * 
     * @param  void
     * @return     int depth
     */
    public int getDepth()
    {
        return depth;   
    }
    /**
     * Sets the treesize for this node
     * 
     * @param  Double treesize
     * @return     void
     */
    public void setTreeSize(Double newtreesize)
    {
        treesize = newtreesize;   
    }
    
    /**
     * Gets the treesize for this node
     * 
     * @param  void
     * @return     Double treesize
     */
    public double getTreeSize()
    {
        return treesize;   
    }
    /**
     * Sets the newicklength ( how long this node is in newick value total) for this node
     * 
     * @param  Double newicklength
     * @return     void
     */
    public void setNewickLength(Double newnewicklength)
    {
        newicklength = newnewicklength;   
    }
    
    /**
     * Gets the newicklength for this node
     * 
     * @param  void
     * @return     Double newicklength
     */
    public double getNewickLength()
    {
        return newicklength;   
    }
    
    /**
     * Sets the start X coordinate for this node
     * @param  Double x
     * @return     void
     */
    public void setStartX(double x)
    {
        startxcoord=x;   
    }
    
    /**
     * Gets the start X coordinate for this node
     * 
     * @param  void
     * @return     Double startxcoord
     */
    public double getStartX()
    {
        return startxcoord;   
    }
    
    /**
     * Sets the start Y coordinate for this node
     * @param  Double y
     * @return     void
     */
    public void setStartY(double y)
    {
        startycoord=y;   
    }
    
    /**
     * Gets the start Y coordinate for this node
     * 
     * @param  void
     * @return     Double startycoord
     */
    public double getStartY()
    {
        return startycoord;   
    }
        /**
     * Sets the end X coordinate for this node
     * @param  Double x
     * @return     void
     */
    public void setEndX(double x)
    {
        endxcoord=x;   
    }
    
    /**
     * Gets the end X coordinate for this node
     * 
     * @param  void
     * @return     Double endxcoord
     */
    public double getEndX()
    {
        return endxcoord;   
    }
    
    /**
     * Sets the start Y coordinate for this node
     * @param  Double y
     * @return     void
     */
    public void setEndY(double y)
    {
        endycoord=y;   
    }
    
    /**
     * Gets the start Y coordinate for this node
     * 
     * @param  void
     * @return     Double endycoord
     */
    public double getEndY()
    {
        return endycoord;   
    }
    
    /**
     * Sets the standard Y coordinate for this node
     * @param  Double y
     * @return     void
     */
    public void setStandardY(double y)
    {
        standardy=y;   
    }
    
    /**
     * Gets the standard Y coordinate for this node
     * 
     * @param  void
     * @return     Double standardy
     */
    public double getStandardY()
    {
        return standardy;   
    }
    
    /**
     * Sets the selected variable for this node
     * @param  boolean s
     * @return     void
     */
    public void setSelected(boolean s)
    {
        selected = s;   
    }
    
    /**
     * Gets the selected variable for this node
     * 
     * @param  void
     * @return     boolean selected
     */
    public boolean getSelected()
    {
        return selected;   
    }
    
    /**
     * Gets the y multiplier for this node - only useful for top node
     * 
     * @param  void
     * @return     double ymultiplier
     */
    public double getYmultiplier()
    {
        return ymultiplier;   
    }
    
   /**
     * Gets the x multiplier for this node - only useful for top node
     * 
     * @param  void
     * @return     double xmultiplier
     */
    public double getXmultiplier()
    {
        return xmultiplier;   
    }
    
   /**
     * Sets the x multiplier for this node - only useful for top node
     * 
     * @param   double xmultiplier
     * @return     void
     */
    public void setXmultiplier(double setx)
    {
        xmultiplier = setx;
    }
    
   /**
     * Sets the y multiplier for this node - only useful for top node
     * 
     * @param   double ymultiplier
     * @return     void
     */
    public void setYmultiplier(double sety)
    {
        ymultiplier = sety;
    }
    
    
    
    
    public String getAllNames(){
        
        String allnames = "";
        
        int size = children.size();
        //allnames = allnames + size;
        if (size > 0){ //If children
            
            for(int i = 0 ; i<size; i++){
                allnames = findAllNames(children.get(i),allnames); 
            }
        }
        
        if (!getName().equals("")){    //If the node has a name - could change this to use only internal nodes too
            if (allnames == ""){
                allnames = allnames + getName();
            }
            else{
                allnames = allnames + "," + getName();
            }
        }
        
        return allnames;
    }
    
    
    
    /**
     * This method gets an array of all leaf names below a node.
     * 
     * @param  Node
     * @return     something
     */
    public String findAllNames(Node node,String allnames)
    {
       //Scroll through tree and add all names to arraylist
       int size = node.children.size();
       
        if (size > 0){ //If children
            for(int i = 0 ; i<size; i++){
                allnames = findAllNames(node.children.get(i),allnames); 
            }
        }
        
        if (!node.getName().equals("")){    //If the node has a name - could change this to use only internal nodes too
            if (allnames == ""){
                allnames = allnames + node.getName();
            }
            else{
                allnames = allnames + "," + node.getName();
            }
        }
        
        return allnames;
    }
    
}

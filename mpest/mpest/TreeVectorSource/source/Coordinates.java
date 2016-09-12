
/**
 * Contains different algorithms to calculate the xy coordinates of a particular node
 * 
 * @author Ralph Pethica
 * @version 30/08/06
 */
public class Coordinates
{
    private double xmultiplier;
    private double ymultiplier;
    private int xcanvas;
    private int ycanvas;
    private double newickmultiplier;
    private double xstandard;
    private int ymin;
    private int maxdepth;
    private int startx;
    private boolean iscladogram;
    private boolean issimpleclad;
    
    /**
     * Constructor for objects of class Coordinates
     */
    public Coordinates()
    {
        xmultiplier = 70; //Calculated later using canvas size to work out scale of SVG image
        ymultiplier = 20;
        xcanvas = 500;  //Standard canvas size 500x500px
        ycanvas = 500;
        xstandard = 1;
        ymin = 15;   //minimum distance between leaves (in pixels)
        newickmultiplier = 50;
        startx = 20;    //Stores the x start position (default 20px - indented tree)
        iscladogram = false;
        issimpleclad = false;
    }
    
    /**
     * Constructor for objects of class Coordinates
     * This constructor allows canvas size to be specified
     */    
    public Coordinates(int x, int y)
    {
        xmultiplier = 70; //Calculated later using canvas size to work out scale of SVG image
        ymultiplier = 20;
        xcanvas = x;  //Standard canvas size 500x500px
        ycanvas = y;
        xstandard = 1;
        ymin = 15;   //minimum distance between leaves (in pixels)
        newickmultiplier = 50;
        startx = 20;
        iscladogram = false;
        issimpleclad = false;
    }
    
    /**
     * Method sets the main algorithm to produce a phylogram.
     * 
     * @param  Node node
     * @return     Node
     */
    public Node findPhylogram(Node node)
    {
        double xcurrent = startx;       //stores current x position
        double ycurrent = ycurrent = ycanvas/2;   //stores current y position start y is half tree total y
        iscladogram = false;            //Phylogram not cladogram so false
        issimpleclad = false;
        findXMultiplier(node);      //finds and sets the multiplier to scale X axis
        findYMultiplier(node);      //finds and sets the multiplier to scale Y axis
        findNewickMultiplier(node); //finds and sets the newick length multiplier
        
        findCoords(node, xcurrent, ycurrent);
        return node;
    }
    /**
     * Method sets the main algorithm to produce a cladogram.
     * 
     * @param  Node node
     * @return     Node
     */
    public Node findCladogram(Node node)
    {
        double xcurrent = startx;       //stores current x position
        double ycurrent = ycurrent = ycanvas/2;   //stores current y position start y is half tree total y
        maxdepth = node.getDepth() +2 ;
        iscladogram = true;         //Set Cladogram variable
        issimpleclad = false;
        findXMultiplier(node);      //finds and sets the multiplier to scale X axis
        findYMultiplier(node);      //finds and sets the multiplier to scale Y axis
        //Newick multiplier not needed for cladogram
        
        findCoords(node, xcurrent, ycurrent);
        return node;

    }
    
    /**
     * Method sets the main algorithm to produce a simple cladogram.
     * 
     * @param  Node node
     * @return     Node
     */
    public Node findSimpleClad(Node node)
    {
        double xcurrent = startx;       //stores current x position
        double ycurrent = ycurrent = ycanvas/2;   //stores current y position start y is half tree total y
        maxdepth = node.getDepth() +2 ;
        iscladogram = true;         //Set Cladogram variable
        issimpleclad = true;        //Set cladogram to simple
        findXMultiplier(node);      //finds and sets the multiplier to scale X axis
        findYMultiplier(node);      //finds and sets the multiplier to scale Y axis
        //Newick multiplier not needed for simple cladogram
        
        findCoords(node, xcurrent, ycurrent);
        return node;

    }
    
    /**
     * Main Coordinates search algorithm, stores coordinates in Node structures.
     * 
     * @param  Node node, double xcurrent, double ycurrent
     * @return     Node
     */
    private Node findCoords(Node node, double xcurrent, double ycurrent)
    {
        //Calculate the horixontal (x) line to the node - calculate using newick branch lengths if possible
        double x1 = xcurrent;
        double y1 = ycurrent;
        double x2 = 0;
        double y2 = ycurrent;
        
        if (node.getType().equals("leaf") && iscladogram == true && issimpleclad == false){    //Move leaf nodes so they line up
            int xunit = maxdepth - node.getLevel();
            x2 = xunit * xmultiplier;
        }
        else if (iscladogram == false){              //If there is a newick length then use it
            x2 = node.getLength() * newickmultiplier;   //Second X val calculated using newick multiplier
        }
        else{                                    //Else use the standard length
            x2 = xstandard * xmultiplier;        //Calculate second x using a standard length
        }
        x2 = x2 + xcurrent;                         //Second X is second + first X val.
        //System.out.println("Node type = " + node.getType());
       
        //Add Setter for X1 and X2 Vals here
        node.setStartX(x1);
        node.setEndX(x2);
        node.setStandardY(y1);
        
        x1 =x2;     //Now that X2 has been recorded, update the X1 value
       
        //If this is an internal node, then calculate the coordinates of its children
        if (node.children.isEmpty() == false){
                //System.out.println("Entering child of node loop with y coordinate = " + y1);
                double width = node.getTreeSize();    //Get the width of the tree from here on
               
                width = width /2;                   //width now = half treesize for this node
                width = 0 - width;                  //Change width to negative (start from top coord y first)
                double oldunit = 0;                 //Stores the tree size of the last child we looked at
                double firstpos = 0;                //Stores relative y pos of first child
                double lastpos = 0;                 //Stores relative y pos of last child
                double oldpos = 0;                  //Stores relative y pos of last child we looked at
                int size = node.children.size();    //Get number of child nodes
                
                for(int i = 0 ; i<size; i++){       //Look at each child in the array
                    
                    //System.out.println("Looping through arraylist of size = " + size);
                    //System.out.println("This loop number = " + i);
                    //System.out.println("Name for this node = " + node.children.get(i).getName());
                    //System.out.println("Tree width / 2= " + width);
                    
                    double unit = node.children.get(i).getTreeSize(); 
                    unit = unit/2;                  //Unit is half the size of this child node

                    //System.out.println("This unit size / 2 = " + unit);
                    //System.out.println("Old unit = " + oldunit);
                    
                    double position = 0;            //Holds relative position
                    
                    if (i==0){      //if this is the first time we need to calculate the negative end
                        position = width - unit; //If leaf then add appropriate amount if internal deduct
                        if (node.children.get(i).getType().equals("internal")){
                            position = position + unit;
                        }
                    }
                    else{           //If this is not the first child then use the position of the last child
                        position = oldpos;
                    }
                    
                    position = position + oldunit;  //Add on half the size of the last child

                    
                    if (i>0){                       //If this is not the first child then add a gap of 1
                       position = position + 1;
                        
                    }    
                    
                    position = position + unit;     //Add on half the treesize of this child
                    oldpos = position;              //Update the old position
                    oldunit = unit;                 //Update the old unit
                    
                    //System.out.println("Position = " + position);
                    
                    position = position * ymultiplier;  //Scale up relative positions according to the multipliers
                    
                    if (i == 0){
                        firstpos = position;            //Set first pos if necessary
                    }
                    lastpos = position;                 //Set last pos
                    
                    //System.out.println("Position after multiplier = " + position);
                    //System.out.println("Old unit set to = " + unit);

                    double ynew = y2 + position;        //Make a new y marker the product of old y + new position
                   
                    findCoords(node.children.get(i), x2, ynew);   //recursively call for children
                }
                
                //System.out.println("First y position = " + firstpos + "last y position = " + lastpos);
                y1 = y2 + firstpos;
                y2 = y2 + lastpos;
                //System.out.println("Y line calculation suceeded... with y1 = " + y1 + " y2 = " + y2);
                // Add y bar values for internal node here
                node.setStartY(y1);
                node.setEndY(y2);
            }
        
        return node;
    }   
    
    private void findXMultiplier(Node node)     //Calculates the multiplier using the depth of root node
    {
        int depth = node.getDepth();
        depth = depth + 1; //to allow for the entry node
        //System.out.println("Depth in XMulti method = " + depth);
        int xroom = xcanvas;
        xroom = xroom -20;  //indent the tree
        xroom = xroom - 80; //allow room for text
        xmultiplier = xroom/ depth; //find space for each branch i.e. multiplier
    }
    
    private void findYMultiplier(Node node)     //Calculates the multiplier using the depth of root node
    {
        double width = node.getTreeSize();
        //System.out.println("Width in YMulti method = " + width);
        int yroom = ycanvas;
        yroom = yroom -40;  //allow a little bit more space - Change to ypadding
        ymultiplier = yroom/ width; //find space for each branch i.e. multiplier
        if (ymultiplier < ymin){
            ymultiplier = ymin;
        }
        //System.out.println("Y Multiplier = " + ymultiplier);
        node.setYmultiplier(ymultiplier);
    }
    
    private void findNewickMultiplier(Node node)     //Calculates the newick multiplier using the max total length of root node
    {
        double maxnewick = node.getNewickLength();
        int xroom = xcanvas;
        xroom = xroom -20;  //indent the tree
        xroom = xroom - 80; //allow room for text
        newickmultiplier = xroom/ maxnewick; //find scale factor for each branch i.e. multiplier
    }
}

import java.util.Stack;
import java.io.*;
import java.util.ArrayList;

/**
 * Contains functions to extract leaves with selected names from a bigger tree.
 * 
 * @Ralph Pethica
 * @version 1
 */
public class ProcessNames
{

    int numberfound = 0;
    /**
     * Constructor for objects of class ProcessNames
     */
    public ProcessNames()
    {
       int numberfound = 0;
    }

    /**
     * This function takes in a pre explored Node object 
     * representing a newick string and an array of names.
     * It then checks the Node object for presence of names.
     * 
     * @param  Node, Array
     * @return     boolean 
     */
    public boolean checkNames(Node node, String nameslist, Error error)
    {
        String[] names = namesToArray(nameslist);
        int arraysize = names.length;
        ////System.err.println(arraysize);
        int numbernotfound = 0;
        
        String[] foundnames = new String[arraysize];
        
        findNames(node, names, foundnames);
        if (numberfound<foundnames.length){
            for(int i = 0 ; i<foundnames.length; i++){
                if(foundnames[i]==null){
                    error.addWarning(names[i] + " was not found in tree");
                    System.err.print(names[i] + " was not found in tree");
                    numbernotfound++;
                }
            }
            if ((arraysize - numbernotfound) < 3){
                System.out.println("Less than 3 names found in tree - you must select more than 3 names"); 
                System.exit(1);   
            }
            //return false;
        }
        
        purgeNodesFirst(node, names);
        purgeNodes(node, names);
        purgeNodes(node, names);
        //purgeNodes(node, names);
        return true;
    }
    
    /**
     * Recursive Search of tree for names in names array
     *
     * @param  y   Node
     * @return     boolean
     */
    public void findNames(Node node, String[] names, String[] foundnames)
    {
        int size = node.children.size();
        
        if (numberfound<foundnames.length){
            
            if (size > 0){
                for(int i = 0 ; i<size; i++){
                    findNames(node.children.get(i),names, foundnames); 
                }
            }
            
            if (!node.getName().equals("")){
                //System.err.println(node.getName());
                
                for(int i = 0 ; i<foundnames.length; i++){
                    if(names[i].equals(node.getName())){
                        foundnames[i] = "found";
                        //System.err.print("Just found ");
                        //System.err.println(node.getName());
                        node.setSelected(true);
                        numberfound ++;
                        
                    }
                }
            }
        }
        else{
            //System.err.println("All names found.....");
        }
        return;
    }
    
    
    
    
    /**
     * 
     * @param  String
     * @return     String[]
     */
    public String[] namesToArray(String nameslist)
    {
        int i = 0; //letter counter
        int j = 0; //word counter
        int c = 0; //comma counter
        String tempstring = "";
        //count commas
        while (i < nameslist.length()){
            if(nameslist.charAt(i)== ','){
                c++;
            }
            i++;
        }
        
        //Must have 3+ names 
        if (c<2){
            System.out.println("You must use more than 2 specified names"); 
            System.exit(1);   
        }
        
        //Split into words
        String[] names = new String[c+1];
        int end = nameslist.length();
        char endchar = nameslist.charAt(end-1);
        if (endchar == ','){
            //System.err.println(endchar);
            System.out.println("Last character of names list cannot be a comma"); 
            System.exit(1);
        }
        
        i = 0;
        while (i < nameslist.length()){
            if (j<c){
                int comma = nameslist.indexOf(',', i);
                String label = nameslist.substring(i, comma);
                //System.err.println(label);
                names[j]=label;
                j++;
                i=comma+1;
            }
            else{
                String label = nameslist.substring(i, end);
                //System.err.println(label); 
                i=end;
                names[j]=label;
            }
        }

        return names;
    }
    
    /**
     * First pass of purge nodes method
     *
     * @param  y   Node
     * @return     the sum of x and y
     */
    public void purgeNodesFirst(Node node, String[] names)
    {
        int size = node.children.size();
         
        if (size > 0){
            for(int i = 0 ; i<size; i++){
                purgeNodes(node.children.get(i),names); 
            }
        }
        
        if (node.getType().equals("internal")){
            if (size > 0){
                for(int i = 0 ; i<size; i++){
                    if(node.children.get(i).getSelected()==false && node.children.get(i).getType().equals("leaf")){
                        //System.err.println("found a remove node, name is..");
                        //System.err.println(node.children.get(i).getName());
                        //System.err.println(i);
                        node.children.remove(i);
                        //These are needed as array is shifted to the left!
                        i--;
                        size--;
                        //If the i'th child of this node is not selected
                        //remove it
                    }
                    else if(node.children.get(i).getSelected()==false && node.children.get(i).getType().equals("internal")){
                        //Remove internal nodes which do not go anywhere
                        if(node.children.get(i).children.size() == 0){
                            node.children.remove(i);
                            i--;
                            size--;
                        }
                        //Find nodes with just one child
                        if(node.children.get(i).children.size() == 1){
                            //Get length of useless node
                            //System.err.println("Found a node with just one child");
                            double length = node.children.get(i).getLength();
                            //System.err.println(length);
                            //Add length to child of useless node
                            double childlength = node.children.get(i).children.get(0).getLength();
                            //System.err.println(childlength);
                            node.children.get(i).children.get(0).setLength(length+childlength);
                            //System.err.println(node.children.get(i).children.get(0).getLength());
                            //Then add child node as a descendant of this node
                            node.addChild(node.children.get(i).children.get(0));
                            
                            //and remove the useless node
                            node.children.remove(i);
                        }
                    }
                    
                }
            
            }
        }
        return;
    }
    
    /**
     * Purge the nodes according to which ones have been selected
     *
     * @param  y   Node
     * @return     the sum of x and y
     */
    public int purgeNodes(Node node, String[] names)
    {
        int deletion = 0;
        int merge = 0;
        int size = node.children.size();
         
        if (size > 0){
            for(int i = 0 ; i<size; i++){
                int change = 1;
                while(change >0){
                    change =  purgeNodes(node.children.get(i),names); 
                    //System.err.println("change is" + change);
                }
                
            }
        }
        
        if (node.getType().equals("internal")){
            if (size > 0){
                for(int i = 0 ; i<size; i++){
                    //Remove unwanted leaf nodes
                    
                    if(node.children.get(i).getSelected()==false && node.children.get(i).getType().equals("leaf")){
                        //System.err.println("found a remove node, name is..");
                        //System.err.println(node.children.get(i).getName());
                        ////System.err.println(i);
                        node.children.remove(i);
                        //These are needed as array is shifted to the left!
                        //i--;
                        //size--;
                        deletion++;
                        //If the i'th child of this node is not selected
                        //remove it
                    }
                    else if(node.children.get(i).getSelected()==false && node.children.get(i).getType().equals("internal")){
                        //Remove internal nodes which do not go anywhere
                        if(node.children.get(i).children.size() == 0){
                            node.children.remove(i);
                            deletion++;
                            //i--;
                            //size--;
                        }
                        //Shorten nodes with just one child
                        else if(node.children.get(i).children.size() == 1){
                            //Get length of useless node
                            //System.err.println("Found a node with just one child");
                            double length = node.children.get(i).getLength();
                            //System.err.println(length);
                            //Add length to child of useless node
                            double childlength = node.children.get(i).children.get(0).getLength();
                            //System.err.println(childlength);
                            node.children.get(i).children.get(0).setLength(length+childlength);
                            //System.err.println(node.children.get(i).children.get(0).getLength());
                            //Then add child node as a descendant of this node
                            node.addChild(node.children.get(i).children.get(0));
                            //purgeNodes(node.children.get(i),names);
                            //and remove the useless node
                            node.children.remove(i);
                            merge++;
                        }
                    }
                    if (deletion>0){
                        //i--;
                        //System.err.println("size = " + size);
                        size = node.children.size();
                        //System.err.println("size = " + size);
                        
                    }
                    
                
                }
            
            }
        }
        deletion = deletion + merge;
        return deletion;
    }
    
    /**
     * This function finds all names in the tree and puts them into the extra data arraylist.
     * 
     * @param  Node, ArrayList
     * @return     void
     */
    public void addAllNames(Node node, ArrayList<ExtraData> data)
    {
       //Scroll through tree and add all names to arraylist
       int size = node.children.size();
         
        if (size > 0){ //If children
            for(int i = 0 ; i<size; i++){
                addAllNames(node.children.get(i),data); 
            }
        }
        
        if (!node.getName().equals("")){    //If the node has a name - could change this to use only internal nodes too
            ExtraData extra = new ExtraData();
            extra.setNewickName(node.getName());
            data.add(extra);
        }
        
        return;
    }
    
    
}

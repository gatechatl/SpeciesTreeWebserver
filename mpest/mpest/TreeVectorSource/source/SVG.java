import java.util.ArrayList;

/**
 * Contains Methods to print out SVG from Coordinates stored in nodes after Explorer and Coordinates carried out
 * 
 * @author Ralph Pethica
 * @version 26/03/06
 */
public class SVG
{
    private int textgap;
    private int ytextoffset;
    private int topnode;
    public double width;
    public double height;
    private boolean firsttime;

    /**
     * Constructor for objects of class SVG
     */
    public SVG()
    {
       textgap = 10;    //Set this to change text gap
       ytextoffset = 4;    //Set this to change text gap
       topnode = 0;
       width = 1600;
       height = 0;
       firsttime = true;
    }

    /**
     * Gets a string of svg lines and text from the coordinates stored in nodes
     * 
     * @param  Node node
     * @return     String svg
     */
    public String getSVG(Node node, Error error, ArrayList<ExtraData> data, Preferences prefs)
    {
        String svg = new String();
        
        double ymultiplier = node.getYmultiplier();
        double treesize = node.getTreeSize() + 2;
        double ytotal = ymultiplier * treesize;
        double ycentre = (prefs.y / 2); //CHANGE THIS TO BE DYNAMIC
        double ymin = 0 -((ytotal / 2) - ycentre);
        double ymax = (ytotal / 2) + ycentre;
        int textsize = 100;
        textsize = prefs.textsize;
        double totalwidth = prefs.x + textsize;
        width = totalwidth;
        topnode = 0;
        height = ytotal;
        
        //Embedded JavaScript
        svg = svg + "<?xml version=\"1.0\" standalone=\"no\"?>\n";
        svg = svg + "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n";
        svg = svg + "\"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">\n";
        svg = svg + "<?xml-stylesheet type=\"text/css\" href=\"" + prefs.css + "\" ?>\n";
        //svg = svg + "<?xml-stylesheet type=\"text/css\" href=\"../scripts/tree.css\" ?>\n";
        svg = svg + "<svg width=\"" + totalwidth + "\" height=\"" + ytotal + "\" viewBox=\"0 " + ymin +" " + totalwidth + " " + ytotal +"\" version=\"1.1\"\n";
        svg = svg + "xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" onload=\"startup(evt)\">\n";
        
        if (prefs.extra){
            svg = svg + "<script xlink:href=\"" + prefs.js + "\" type=\"text/ecmascript\"/>";
        }

        svg = svg + "<text x='10' y='10' font-family=\"Arial\" font-size=\"12\" stroke-width=\"0\" fill=\"red\">" + error.getWarnings() +"</text>\n";
        
        if (prefs.extra){
            svg = svg + "<g onclick=\"changeElement(evt)\">\n";
        }
        else{
            svg = svg + "<g>\n";
        }
        svg = findSVG(node, svg, data, prefs);
        
        svg = svg + "\n</g>\n</svg>";
        return svg;
    }
    
     
    
    private String findSVG(Node node, String svg, ArrayList<ExtraData> data, Preferences prefs)
    {
        //Draw the X line first for this node
        double y1 = node.getStandardY();
        double y2 = y1; //For the X line we only want the inital y value
        double x1 = node.getStartX();
        double x2 = node.getEndX();
        
        //THIS IS HALF OF THE LINE WIDTH AGAIN TO MAKE SURE NO GAPS
        x2 = x2 +1.5;
        //Print the variables in an svg line to the string -  newline added for these
       if (prefs.treestyle == "triangle"){
        }
        else{
            svg = svg + "<line style=\"stroke:rgb(99,99,99); stroke-width:3\" x1=\"" + x1 + "\"" + " y1=\"" + y1 + "\"" + " x2=\"" + x2 + "\"" + " y2=\"" + y2 + "\"" + "/>\n";
        }
            //TAKE THIS OUT
        if (node.getTreeSize() > 1 && topnode >1){
            //svg = svg + "<text  x=\"" + x1 + "\" y=\"" + y1 + "\"  >" + prefs.buildlink + node.getAllNames() + "</text>\n";
            double newx1 = x1 + 3;
            double linklength = 15;
            double newx2 = x1;
            if ((x2 - x1) < linklength){
                newx2 = newx2 = (x2);
            }
            else{
             newx2 = newx2 + linklength;   
            }
            if (prefs.extra){
                svg = svg + "<a target=\"_top\" xlink:href=\"" + prefs.buildlink + node.getAllNames() +"\">";
                svg = svg + "<line class=\"magnify\" x1=\"" + newx1 + "\" y1=\"" + y1 + "\" " + " x2=\"" + newx2 + "\"" + " y2=\"" + y2 + "\"" + "/>";
                svg = svg + "</a>\n";
            }
        }
        if (node.getTreeSize() > 1 && topnode <= 2){
         topnode ++;   
        }
        
        //Print the labels if a leaf node
        if (node.getType().equals("leaf") || node.getType().equals("internal")){
           double gap = textgap +x2;
           if (prefs.treestyle == "triangle"){
               gap = textgap +x1;
            }
           double gap2 = 15 +x2;
           double ytext = ytextoffset + y1;
           //Find extra data here
           
           String name = node.getName();
           String fullname = name;
           String sparestring = "";
           //Roll through extra data and find this one
           if (prefs.db == true){
            int n = data.size();
            for(int i = 0; i < n ; i++){
                 //If the newickname is the same as this nodes newick name, and the full name isn't just null, 
                 //get this full name and make it the name
                 if (data.get(i).getNewickName().equals(name) && !data.get(i).getNewickName().equals(null)){
                     fullname = data.get(i).getFullName();
                     sparestring = data.get(i).getSpare();
                    }
                }
            }
           if (!prefs.link.equals("")){
               svg = svg + "<a target=\"_top\" xlink:href=\"" + prefs.link + node.getName() +"\" >";
            }
               svg = svg + "<text  x=\"" + gap + "\" y=\"" + ytext + "\" id=\"" + name + "\" >" + fullname + "</text>\n";
               //make a conditional here
               if (prefs.extra){
                    svg = svg + "<circle cx=\"" + x2 + "\" cy=\"" + y1 + "\" r=\"4\" onmouseover=\"testmethod('" + name + "')\" onmouseout=\"testmethod('" + name + "')\" />";
                    svg = svg + "<g id=\"" + name +"extra\" visibility = \"hidden\">";
                    //svg = svg + "<rect x=\"" + gap + "\" y=\"" + y1 +"\" width=\"100\" height=\"100\" fill=\"gray\" visibility = \"inherit\" />";
                    svg = svg + "<text class=\"rollover\" x=\"" + gap + "\" y=\"" + ytext + "\" visibility = \"inherit\"  >" + sparestring + "</text>\n";
                    svg = svg + "</g>";
                }
               
               if (!prefs.link.equals("")){
               svg = svg + "</a>\n";
            }
           else{
               svg = svg + "\n";
            }
         }
        
        
        //If this node has children explore them depth first
        if (node.children.isEmpty() == false){
            svg = svg + "<g>";  //add an opening g to give the svg a tree structure
            int size = node.children.size();
            for(int i = 0 ; i<size; i++){
                svg = findSVG(node.children.get(i), svg, data, prefs);
                if (prefs.treestyle == "triangle"){
                    y1 = node.getStandardY();
                    y2 = node.children.get(i).getStandardY();
                    x2 = node.children.get(i).getStartX();
                    x1 = node.getStartX();
                    svg = svg + "<line style=\"stroke:rgb(99,99,99); stroke-width:3\" x1=\"" + x1 + "\"" + " y1=\"" + y1 + "\"" + " x2=\"" + x2 + "\"" + " y2=\"" + y2 + "\"" + "/>\n";  
                }
            }
            //Draw the y line for this internal node
            y1 = node.getStartY();    //Set y1 to the start y
            y2 = node.getEndY();    //Set y2 to the end y
            x1 = x2;                //Set x2 to the end x
            //svg = prefs.treetype;
            if (prefs.treestyle == "triangle"){
                    //svg = "";
                }
            else{
                svg = svg + "<line style=\"stroke:rgb(99,99,99); stroke-width:3\" x1=\"" + x1 + "\"" + " y1=\"" + y1 + "\"" + " x2=\"" + x2 + "\"" + " y2=\"" + y2 + "\"" + "/>\n";
            }
                svg = svg + "</g>"; //close the tree
        }
                
        return svg;
    }
    
    
    public String generateEmbed(Preferences prefs){
        String embed = "";
        //embed = "<EMBED SRC=\"" + prefs.embedout + "\" WIDTH=\"" + width +"\" HEIGHT=\"" + height +"\">\n";
        embed = "<object data=\"" + prefs.embedout + "\" WIDTH=\"" + width +"\" HEIGHT=\"" + height +"\" type=\"image/svg+xml\">\n";
        //embed = "<iframe src=\"" + prefs.embedout + "\" WIDTH=\"" + width +"\" HEIGHT=\"" + height +"\"></iframe>\n";
        return embed;
        
    }
    
    
    public String generatePage(Preferences prefs){
         String page = "";
         page = page + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n";
         page = page + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n";
         page = page + "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en-gb\" xml:lang=\"en\">\n";
         page = page + "<head><title>TreeVector</title>\n";
         page = page + "</head>\n";
         page = page + "<body>\n";

         
         page = page + generateEmbed(prefs);
         
         page = page + "</body>\n";
         page = page + "</html>\n";
         return page;
    }
    
    
}

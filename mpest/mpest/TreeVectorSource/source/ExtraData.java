
/**
 * This class is designed to hold extra information for each name in a newick tree
 * so that a database call can add more data to the tree
 * 
 * @author Ralph Pethica
 * @version 26/03/2008
 */
public class ExtraData
{
    
    private String newickname;
    private String fullname;
    private String sparestring;
    private String ncbi;

    /**
     * Constructor for objects of class ExtraData
     */
    public ExtraData()
    {
        // initialise instance variables
        newickname = "";
        fullname = "";
        sparestring = "testing with some text";
    }


    public void setNewickName(String name)
    {
        newickname = name;
    }
    
    public String getNewickName()
    {
        return newickname;
    }
    
    public void setFullName(String name)
    {
        fullname = name;
    }
    
    public String getFullName()
    {
        return fullname;
    }
    
    public String getSpare()
    {
        return sparestring;
    }
    
    public void setSpare(String name)
    {
        sparestring = name;
    }
    
    public String getNCBI()
    {
        return ncbi;
    }
    
    public void setNCBI(String name)
    {
        ncbi = name;
    }
}

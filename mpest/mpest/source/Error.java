
/**
 * Tracks errors in order to add warnings and embed in SVG 
 * 
 * @author Ralph Pethica    
 * @version 1
 */
public class Error
{
    // instance variables - replace the example below with your own
    private String warnings;

    /**
     * Constructor for objects of class Error
     */
    public Error()
    {
        // initialise instance variables
        warnings = "";
    }

    /**
     * Add warning to string
     * 
     * @param  y   String warning
     * @return     void
     */
    public void addWarning(String message)
    {
        warnings = warnings + " - " + message + " \n";
        return;
    }
    
    /**
     * Get warnings string
     * 
     * @param  y   void
     * @return     String warnings
     */
    public String getWarnings()
    {
        return warnings;
    }
}

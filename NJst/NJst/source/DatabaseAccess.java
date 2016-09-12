import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;


/**
 * Write a description of class DatabaseAccess here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DatabaseAccess
{


    /**
     * Constructor for objects of class DatabaseAccess
     */
    public DatabaseAccess()
    {
        // initialise instance variables
    }

    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y
     */
    public void testMethod(Node node, ArrayList<ExtraData> data)
    {
        
        Statement stmt = null;
        ResultSet rs = null;
        //System.err.println("in testmethod");
        try {
           Connection conn = //Change this for use with apache
           DriverManager.getConnection("jdbc:mysql://localhost/superfamily?" + "user=apache&password=");
           //Use Connection
           int size =data.size();
           for (int i =0;i<size;i++){
               
               String query = "select name,taxonomy,taxon_id from genome where genome = '" + data.get(i).getNewickName() + "'";
               
               try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
    
                // Now do something with the ResultSet ....
                //System.err.println("trying query");
                while (rs.next()) {            
                    // Get the data from the row using the column name
                    String result = rs.getString("name");
                    //System.err.println(result);
                    //System.err.println("next");
                    result = result.replaceAll("\\<.*?\\>", "");
                    data.get(i).setFullName(result);
                    
                    String result2 = rs.getString("taxonomy");
                    result2 = result2.replaceAll("\\<.*?\\>", "");
                    data.get(i).setSpare(result2);
                    
                    String result3 = rs.getString("taxon_id");
                    //result2 = result2.replaceAll("\\<.*?\\>", "");
                    data.get(i).setNCBI(result3);
                    
                }
                //Read Results
                
                } finally {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException sqlEx) { // ignore 
                            }
                        rs = null;
                    }
        
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (SQLException sqlEx) { // ignore 
                        }
                        stmt = null;
                    }
                }
            }//End of loop
           
           
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        
    }

}

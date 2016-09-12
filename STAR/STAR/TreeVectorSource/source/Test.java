import java.io.*;
/**
 * Test Class to create example newick strings
 * 
 * @author Ralph Pethica 
 * @version 1
 */
public class Test
{    
    private static int test;    // for test method
    /**
     * Constructor for objects of class Test
     */
    public Test()
    {

    }

    /**
     * Creates a file called testfile.tree when run, 
     * Tweak the algorithm to produce different trees
     * 
     * @param  void
     * @return     void
     */
     
    public static void makeTestFile()
    {
        String string = "";
        string = string + "(";
        int j = 5;
        for (int i=0; i<j; i++){
            string = recursiveTest(string, j);
            if (i+1<j){
                //string = string + ",";
            }
        }
        string = string + ")";
        string = string + ";";
        FileOutputStream out;
        PrintStream print;
        try
           {
                out = new FileOutputStream("testfile.tree");
                print = new PrintStream( out );
                print.println(string); 
                print.println ();
                print.close();
            }
            catch (Exception e)
            {
                System.out.println("Error writing to file");
            }
    }
    
    /**
     * Recursive method used by testfile method
     * 
     * @param  String, int
     * @return     String
     */
    public static String recursiveTest(String string, int j)
    {
        string = string + "(";
        for (int i=0; i<j; i++){
            string = string + test;
            test ++;
            //string = string + ':';
            //string = string + "0.";
            //string = string + i;
            //string = string + '2';
            if (i+1<j){
                string = string + ",";
            }
        }
        string = string + ")";
        string = string + ",";
        j --;
        if (j>0){
            string = string + "(";
            string = recursiveTest(string, j);
            string = string + ")";
            for (int i = 0; i<j; i++){
                string = string + ",";
                string = string + "(a,b,c)";
                string = string + ",";
            }
        }
        
        
        return string;   
    }
}

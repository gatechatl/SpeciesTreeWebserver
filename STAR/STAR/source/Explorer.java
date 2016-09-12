
/**
 * Calculates relationships between leaves and nodes in order to work out coordinates for plotting the tree.
 * Inspired by the RT algorithm
 * @author Ralph Pethica
 * @version 21/08/2006
 */
public class Explorer
{
    int level;
    int maxdepth;   //Stores max depth of tree useful for calculating the scale
    double newicklength;
    double maxnewick;
    /**
     * Constructor for objects of class Explorer
     */
    public Explorer()
    {
        level = 0;
        maxdepth = 0;
        newicklength = 0;
        maxnewick = 0;
    }

    /**
     * Finds the positions offsets and sizes of nodes by calling the recursive findPositions method
     * 
     * @param  Node node
     * @return     Node newnode
     */
    public Node getPositions(Node node)
    {
        level = 0;
        maxdepth = 0;
        newicklength = 0;
        maxnewick = 0;
        findPositions(node);
        //System.out.println("Treesize for main node = " + node.getTreeSize());
        findDepths(node);
        //System.out.println("MAX NEWICK = " + maxnewick);
        node.setNewickLength(maxnewick);
        return node;
    }
    
    /**
     * Finds the positions offsets and sizes of nodes recursively
     * 
     * @param  Node node
     * @return     void
     */
    public void findPositions(Node node)
    {
            level ++;   //Used to keep track of which level we are on and eventually calc depths
            if (node.getLength() !=0 && node.getLength() >0){
                newicklength = newicklength + node.getLength();
                //System.out.println("Adding newick length = " + node.getLength());
            }
            int size = node.children.size();
            double treesize = 0;
            if (size > 0){
                for(int i = 0 ; i<size; i++){
                    findPositions(node.children.get(i)); 
                    treesize = treesize + node.children.get(i).getTreeSize();
                }
            }
            if (size > 0){
                size = size -1;   //if bigger than 1!
            }
            treesize = treesize + size;
            node.setTreeSize(treesize);
            //System.out.println("Treesize for this node = " + node.getTreeSize());

            node.setLevel(level);   //set the level for this node
            if (maxdepth < level){
                maxdepth = level;   
            }
            level --;
            //node.setNewickLength(newicklength);
            if (maxnewick < newicklength){
                maxnewick = newicklength;
            }
            if (node.getLength() !=0){
                newicklength = newicklength - node.getLength();
            }
    }
    
    
    private void findDepths(Node node)
    {
        int size = node.children.size();
        node.setDepth(maxdepth - node.getLevel());
        if (size > 0){
            for(int i = 0 ; i<size; i++){
                findDepths(node.children.get(i)); 
            }
        }
    }
}

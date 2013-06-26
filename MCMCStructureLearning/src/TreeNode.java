
public class TreeNode {
	
	public TreeNode[] Children;
	public int value;
	public boolean isLeaf;
	public int[] diseaseCounts; //the number of times each disease state was seen with this parent configuration
	
	public TreeNode(int val, int diseaseStates, int numChildren)
	{
		value = val;
		isLeaf = false;
		Children = new TreeNode[numChildren];
		diseaseCounts = new int[diseaseStates];
	}

}

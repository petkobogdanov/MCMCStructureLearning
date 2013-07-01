import java.util.ArrayList;


public abstract class Scorer {
	
	public int[][] Data; //assumes: the last column is disease (states 0 through diseaseStates), snps are the columns, every entry is 0 or 1
	public int M; //the number of samples
	public int N; //the number of snps
	private int numConfigurations; //this is the number of parental configurations.  It is only updated after a call to getParentConfigurationCounts().
	public int AlleleStates;
	public int DiseaseStates;
	
	public Scorer(int[][] data, int alleleStates, int diseaseStates)
	{
		Data = data;
		Data = data;
		M=data.length; //we assume that each row is a sample
		N=data[0].length - 1; //we assume that each column is a SNP except the last column
		AlleleStates = alleleStates;
		DiseaseStates = diseaseStates;
	}
	
	public int getNumConfigurations()
	{
		return numConfigurations;
	}
	
	public double getLikelihood(ArrayList<Integer> parents)
	{
		double likelihood = 0;
		ArrayList<TreeNode> configCounts = getParentConfigurationCounts(parents);
		for(int j = 0; j < configCounts.size(); j++)
		{
			int nj = 0;
			for(int k = 0; k < DiseaseStates; k++)
			{
				nj = nj+configCounts.get(j).diseaseCounts[k];
			}
			for(int k = 0; k < DiseaseStates; k++)
			{
				double njk = (double)configCounts.get(j).diseaseCounts[k];//number of times parents takes jth configuration and X=k
				njk = njk+0.0000001; //add a very small epsilon in case njk=0.
				likelihood=likelihood+njk*Math.log(njk/nj);
			}
		}
		return likelihood;
	}
	
	private ArrayList<TreeNode> getParentConfigurationCounts(ArrayList<Integer> parents) {
		//this builds a tree where each level is a snp and each node is a value that snp can 
		//be (it assumes they can only have two values), so each parent configuration is a path 
		//from the root to a leaf.  The leaves contain counts of how many times the parent 
		//configuration appears for each disease state.
		ArrayList<TreeNode> leaves = new ArrayList<TreeNode>();
		TreeNode head = new TreeNode(-2, DiseaseStates, AlleleStates);
		for(int i=0; i < M; i++)
		{
			TreeNode current = head;
			for(int j=0; j < parents.size(); j++)
			{
				boolean foundChild = false;
				for(int k = 0; k < current.Children.length; k++)
				{
					if(current.Children[k] != null && current.Children[k].value == Data[i][parents.get(j).intValue()])
					{
						current = current.Children[k];
						foundChild = true;
						break;
					}
				}
				if(!foundChild)
				{
					for(int k = 0; k < current.Children.length; k++)
					{
						if(current.Children[k] == null)
						{
							current.Children[k] = new TreeNode(Data[i][parents.get(j).intValue()], DiseaseStates, AlleleStates);
							current = current.Children[k];
							break;
						}
					}
				}
			}
			current.isLeaf = true;
			current.diseaseCounts[Data[i][Data[0].length-1]]++;
			if(!leaves.contains(current))
			{
				leaves.add(current);
			}
		}
		numConfigurations = leaves.size();
		if(numConfigurations == 0)
		{
			numConfigurations = 1; //if there are no parents, then there is one configuration.
		}
		return leaves;
	}

	abstract double Score(ArrayList<Integer> parents);

}

import java.util.ArrayList;


public class SupMax extends Scorer{
	//this scoring function only works for binary disease states
	
	private double[][][][] frequencyTableCases;
	private double[][][][] frequencyTableControls; //first two indices are for snp, second two are for genotype of those snps.  So, frequency in controls of snps i and j with genotypes
	//0 and 2 respectively would be at frequencyTable[i][j][0][2]

	public SupMax(int[][] data, int alleleStates, int diseaseStates, double a) 
	{
		super(data, alleleStates, diseaseStates, a);
		frequencyTableCases = new double[N][N][AlleleStates][AlleleStates]; //this is a trade off of memory for time
		frequencyTableControls = new double[N][N][AlleleStates][AlleleStates];
	}

	@Override
	double calcScore(ArrayList<Integer> parents) 
	{ //returns the maximum value over all parent configurations of SupMaxPair, unless size of parents is 0 or 1
		//when it is 0, the score is 0
		//when it is 1, the score is max over all genotypes of abs(frequency in cases minus frequency in controls)
		if(parents.size() == 0)
		{
			return 0;
		}
		if(parents.size() == 1)
		{
			return supMax1(parents);			
		}
		double bestSupMaxPair = Double.NEGATIVE_INFINITY;
		TreeNode parentConfigs = getParentConfigurations(parents);
		//traverse tree, keeping track of genotype, and at every leaf check whether the frequency of the genotype is highest in the cases or controls, then calculate SupMaxPair accordingly
		int[] genotype = new int[parents.size()];
		//use dfs
		ArrayList<TreeNode> toExplore = new ArrayList<TreeNode>();
		ArrayList<Integer> level = new ArrayList<Integer>(); //the level in the tree of the node at the same position in toExplore (this is the same as the index of the snp in the parent set)
		toExplore.add(parentConfigs);
		level.add(Integer.valueOf(-1)); //the head is level -1, the first snp is at level 0
		while(toExplore.size() != 0)
		{
			TreeNode current = toExplore.get(toExplore.size()-1); //get the most recently pushed node
			int currentLevel = level.get(level.size()-1).intValue();
			if(currentLevel != -1)
			{
				genotype[currentLevel] = current.value;
			}
			toExplore.remove(toExplore.size()-1);
			level.remove(level.size()-1);
			if(current.isLeaf)
			{
				int timesInControls = current.diseaseCounts[0];
				int timesInCases = current.diseaseCounts[1];
				double smp;
				if(timesInControls > timesInCases)
				{
					smp = supMaxPair(parents, genotype, 0, current);
				}
				else
				{
					smp = supMaxPair(parents, genotype, 1, current);
				}
				if(smp > bestSupMaxPair)
				{
					bestSupMaxPair = smp;
				}
			}
			else
			{
				for(int i=0; i < current.Children.length; i++)
				{
					if(current.Children[i] != null)
					{
						toExplore.add(current.Children[i]);
						level.add(Integer.valueOf(currentLevel)+1);
					}
				}
			}
		}
		return bestSupMaxPair;
	}

	private double supMax1(ArrayList<Integer> parents) {
		double biggestDiff = Double.NEGATIVE_INFINITY;
		ArrayList<TreeNode> leaves = getParentConfigurationCounts(parents);
		int numCases = 0;
		int numControls = 0;
		for(int i = 0; i < leaves.size(); i++)
		{
			numCases+=leaves.get(i).diseaseCounts[1];
			numControls+=leaves.get(i).diseaseCounts[0];
		}
		for(int i = 0; i < leaves.size(); i++)
		{
			double difference = Math.abs((double)leaves.get(i).diseaseCounts[1]/numCases - (double)leaves.get(i).diseaseCounts[0]/numControls);
			if(difference > biggestDiff)
			{
				biggestDiff = difference;
			}
		}
		return biggestDiff;
	}

	@Override
	double getProbOfData(double score) 
	{ //this is not actually the probability of the data!!  Just a filler for now.
		return score;
	}
	
	private double supMaxPair(ArrayList<Integer> parents, int[] genotype, int casesHighFreq, TreeNode leaf)
	{ //casesHighFreq is 1 if the genotype is higher in the cases than the controls, otherwise 0
		double supAlpha = (double)leaf.diseaseCounts[casesHighFreq]/M;
		double maxSupIJ = 0; //the max frequency over all pairs of parent nodes
		for(int i = 0; i < parents.size(); i++)
		{
			for(int j = (i+1); j < parents.size(); j++)
			{
				double supIJ = supPair(parents.get(i).intValue(),parents.get(j).intValue(), genotype[i], genotype[j], casesHighFreq);
				if(supIJ > maxSupIJ)
				{
					maxSupIJ = supIJ;
				}
			}
		}
		double score = supAlpha - maxSupIJ;
		return score;
	}

	private double supPair(int snp1, int snp2, int genotype1, int genotype2, int casesHighFreq) {
		double sup;
		if (casesHighFreq == 1)
		{ //we want to look for the frequency in controls
			if(frequencyTableControls[snp1][snp2][genotype1][genotype2] != 0)
			{ //we've calculated this before
				sup = frequencyTableControls[snp1][snp2][genotype1][genotype2];
			}
			else
			{
				//calculate and save the frequency
				sup = calcFreq(snp1, snp2, genotype1, genotype2, 0);
				frequencyTableControls[snp1][snp2][genotype1][genotype2] = sup;
			}
		}
		else
		{ //we want to look for teh frequency in cases
			if(frequencyTableCases[snp1][snp2][genotype1][genotype2] != 0)
			{ //we've calculated this before
				sup = frequencyTableCases[snp1][snp2][genotype1][genotype2];
			}
			else
			{
				//calculate and save the frequency
				sup = calcFreq(snp1, snp2, genotype1, genotype2, 1);
				frequencyTableCases[snp1][snp2][genotype1][genotype2] = sup;
			}
		}
		return sup;
	}

	private double calcFreq(int snp1, int snp2, int genotype1, int genotype2, int cases) 
	{ //we look for frequency in the cases if cases is 1, otherwise we look in controls
		int occurrences = 0;
		int sampleSize = 0;
		for(int i = 0; i < M; i++)
		{
			if(Data[i][N] == cases)
			{
				sampleSize++;
				if((Data[i][snp1] == genotype1) && (Data[i][snp2] == genotype2))
				{
					occurrences++;
				}
			}
		}
		return (double)occurrences/sampleSize;
	}

}

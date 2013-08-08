import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math3.stat.descriptive.moment.Variance;


public class EpiScoreModified extends Scorer{

	private double emptyNetworkScore; //P(D|LDS0)
	private double assumeEmptyNetworkScore; //P(D|thetaLDS0,LDS0)
	private int numSampleTimes = 100; //the number of times to sample sets of individuals when estimating the variance of the G2 statistic
	private double fractionToSample = 0.1; //the fraction of individuals to include in each sampling when estimating the variance of the G2 statistic
	
	public EpiScoreModified(int[][] data, int alleleStates, int diseaseStates, double a) 
	{
		super(data, alleleStates, diseaseStates, a);
		emptyNetworkScore = scoreEmptyNetwork(data);
		assumeEmptyNetworkScore=emptyNetworkScore+(DiseaseStates-1);
	}
	
	//returns the AIC score of the empty network
	private double scoreEmptyNetwork(int[][] data)
	{
		int numSamples = data.length;
		int numSNPs=data[0].length-1;
		double score=0;
		for(int i=0; i<DiseaseStates;i++)
		{
			double nK=0;
			for(int j=0; j<numSamples; j++)
			{
				if(data[j][numSNPs]==i)
				{
					nK++;
				}
			}
			score+=nK*Math.log(nK/numSamples);
		}
		score-=(DiseaseStates-1);
		return score;
	}
	
	@Override
	/**
	 * This returns the EpiScore of the network, but the variance term is calculated differently.
	 * Instead of resampling sets of snps with size=parents.size(), it resamples the data and calculates the
	 * g2 statistic using the same set of parents.  So, the variance depends on teh particular parent
	 * set and not just the size of the parent set.
	 */
	double score(ArrayList<Integer> parents) 
	{
		return (0.5)*(gSquared(parents) - gSquaredVar(parents)) + emptyNetworkScore;
	}

	@Override
	double getProbOfData(double score) 
	{
		return Math.exp(score);
	}
	
	//returns the G2 statistic of the disease node and its parents
	private double gSquared(ArrayList<Integer> parents)
	{
		double likelihood = getLikelihood(parents);
		return 2*(likelihood - assumeEmptyNetworkScore);
	}
	
	//returns teh G2 statistic of the disease node, assuming that all the snps in data are in the parent set
	private double gSquared(int[][] data)
	{
		double emptyScore = scoreEmptyNetwork(data);
		double likelihood=getLikelihood(data);
		return 2*(likelihood-emptyScore);
	}
	
	private double getLikelihood(int[][] data)
	{
		double likelihood = 0;
		ArrayList<TreeNode> configCounts = getParentConfigurationCounts(data);
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
	
	private ArrayList<TreeNode> getParentConfigurationCounts(int[][] data)
	{
		int numSamples = data.length;
		int numSNPs=data[0].length-1;
		
		ArrayList<TreeNode> leaves = new ArrayList<TreeNode>();
		TreeNode head = new TreeNode(-2, DiseaseStates, AlleleStates);
		for(int i=0; i < numSamples; i++)
		{
			TreeNode current = head;
			for(int j=0; j < numSNPs; j++)
			{
				boolean foundChild = false;
				for(int k = 0; k < current.Children.length; k++)
				{
					if(current.Children[k] != null && current.Children[k].value == data[i][j])
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
							current.Children[k] = new TreeNode(data[i][j], DiseaseStates, AlleleStates);
							current = current.Children[k];
							break;
						}
					}
				}
			}
			current.isLeaf = true;
			current.diseaseCounts[data[i][numSNPs]]++;
			if(!leaves.contains(current))
			{
				leaves.add(current);
			}
		}
		return leaves;
	}
	
	//returns the estimated variance of the G2 statistic between the disease node and a parent set of size parentSize
	//uses numSampleTimes samples to estimate the variance
	private double gSquaredVar(ArrayList<Integer> parents)
	{
		int parentSize = parents.size();
		double gSquaredStats[]=new double[numSampleTimes];
		
		//Random randomGenerator = new Random();
		//create and populate the array of indices that we will shuffle in order to choose random sets of individuals
		int indices[] = new int[M];
		for(int j = 0; j < M; j++)
		{
			indices[j] = j;
		}
		
		for(int i=0; i<numSampleTimes;i++)
		{
			shuffleArray(indices);
			//create and populate the resampled data matrix
			int[][] resampled = new int[(int) (fractionToSample*M)][parentSize+1];
			for(int j = 0; j < resampled.length; j++)
			{
				for(int k = 0; k < resampled[0].length; k++)
				{
					if(k<parentSize)
					{
						resampled[j][k]=Data[indices[j]][parents.get(k)];
					}
					else
					{
						resampled[j][k]=Data[indices[j]][N]; //the phenotype value
					}
				}
			}
			gSquaredStats[i]= gSquared(resampled);
		}
		
		Variance v = new Variance();
		double var = v.evaluate(gSquaredStats);
		return var;
	}
	
	// Implementing Fisher–Yates shuffle
	  private void shuffleArray(int[] ar)
	  {
	    Random rnd = new Random();
	    for (int i = ar.length - 1; i >= 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      // Simple swap
	      int a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	  }
	
}

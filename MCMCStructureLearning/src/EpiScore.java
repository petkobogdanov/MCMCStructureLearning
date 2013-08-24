import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math3.stat.descriptive.moment.Variance;


public class EpiScore extends Scorer{

	private double emptyNetworkScore; //P(D|LDS0)
	private double assumeEmptyNetworkScore; //P(D|thetaLDS0,LDS0)
	private int numSampleTimes = 10000; //the number of times to sample sets of SNPs with size=(parent set size) when estimating the variance of the G2 statistic
	private double[] variances; //the array of variances of g2 tests for each size of parent set.  It is populated as needed and indexed by parent set size.
	
	public EpiScore(int[][] data, int alleleStates, int diseaseStates, double a) 
	{
		super(data, alleleStates, diseaseStates, a);
		variances = new double [N]; //we could have up to N snps in the parent set
		emptyNetworkScore = scoreEmptyNetwork();
		assumeEmptyNetworkScore=emptyNetworkScore+(DiseaseStates-1);
	}
	
	//returns the AIC score of the empty network
	private double scoreEmptyNetwork()
	{
		double score=0;
		for(int i=0; i<DiseaseStates;i++)
		{
			double nK=0;
			for(int j=0; j<M; j++)
			{
				if(Data[j][N]==i)
				{
					nK++;
				}
			}
			score+=nK*Math.log(nK/M);
		}
		score-=(DiseaseStates-1);
		return score;
	}
	
	@Override
	double calcScore(ArrayList<Integer> parents) 
	{
		double var = gSquaredVar(parents.size());
		if(parents.size() > 0 && var < gSquaredVar(parents.size()-1))
		{
			return Double.NEGATIVE_INFINITY;
		}
		double score=(0.5)*(gSquared(parents) - var) + emptyNetworkScore;
		return score;
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
	
	//returns the estimated variance of the G2 statistic between the disease node and a parent set of size parentSize
	//uses numSampleTimes samples to estimate the variance
	private double gSquaredVar(int parentSize)
	{
		if(parentSize == 0)
		{
			return 0;
		}
		if(variances[parentSize] != 0)
		{
			return variances[parentSize];
		}
		
		double gSquaredStats[]=new double[numSampleTimes];
		
		//Random randomGenerator = new Random();
		//create and populate the array of SNP indices that we will shuffle in order to choose random sets of SNPs
		int snpIndices[] = new int[N];
		for(int j = 0; j < N; j++)
		{
			snpIndices[j] = j;
		}
		
		for(int i=0; i<numSampleTimes;i++)
		{
			ArrayList<Integer> parentArray=new ArrayList<Integer>();
						
//			for(int j=0; j<parentSize; j++)
//			{
//				parentArray.add(Integer.valueOf(randomGenerator.nextInt(N)));			  
//			}
			shuffleArray(snpIndices);
			for(int j = 0; j < parentSize; j++)
			{
				parentArray.add(Integer.valueOf(snpIndices[j]));
			}
			gSquaredStats[i]= gSquared(parentArray);
		}
		
		Variance v = new Variance();
		double var = v.evaluate(gSquaredStats);
		//save the variance for the next time we need it.
		variances[parentSize] = var;
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



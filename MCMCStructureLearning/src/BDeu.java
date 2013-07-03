import java.util.ArrayList;

import org.apache.commons.math3.special.Gamma;


public class BDeu extends Scorer{

	public BDeu(int[][] data, int alleleStates, int diseaseStates, double a) {
		super(data, alleleStates, diseaseStates, a);
	}

	
	@Override
	
	// produces the BDeu score of the BN where q=number of configurations, r=DiseaseStates, 
	// NJK=number of times parents takes jth configuration and X=k, alpha= given alpha value

	
	double score(ArrayList<Integer> parents) {
		double score = 1;
		double term1=0, term2=1, NJK = 0;
		
		ArrayList<TreeNode> configCounts = getParentConfigurationCounts(parents);
		
		double alpha_numconfigs = alpha/getNumConfigurations();
		double alpha_disease_numconfigs = alpha/(DiseaseStates*getNumConfigurations());
		
		for (int j=0; j<getNumConfigurations(); j++)
		{
			double sumNJK=0;
			for (int k=0; k<DiseaseStates; k++)
			{
				NJK = configCounts.get(j).diseaseCounts[k];
				sumNJK = sumNJK + NJK;
			}
			
			double denominator_term1 = Gamma.gamma(alpha_numconfigs + sumNJK);
			if (Double.isInfinite(denominator_term1))
			{
				denominator_term1 = Double.MAX_VALUE;
			}
			term1 = Gamma.gamma(alpha_numconfigs)/denominator_term1;
			// term one of BDeu formula
			
			term2 = 1;
			
			for (int k=0; k<DiseaseStates; k++)
			{ 

				NJK = configCounts.get(j).diseaseCounts[k];
				
				double numerator_term2 = Gamma.gamma(alpha_disease_numconfigs+NJK);
				if (Double.isInfinite(numerator_term2))
				{
					numerator_term2 = Double.MAX_VALUE;
				}
				
				term2 = term2*(numerator_term2/Gamma.gamma(alpha_disease_numconfigs));
				// term2 of BDeu formula
			}
			
			//if (Double.isInfinite(term1))
			//{
			//	term1 = Double.MAX_VALUE;
			//}
			if (Double.isInfinite(term2))
			{
				term2 = Double.MAX_VALUE;
			}
			score = score * term1 * term2;
		}
		
		//System.out.println(alpha_numconfigs);
		//System.out.println(Gamma.gamma(1.7));
		//System.out.println(score);
		return score;
		
		
	}

	@Override
	double getProbOfData(double score) {
		return score;
	}

}

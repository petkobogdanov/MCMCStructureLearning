import java.util.ArrayList;

import org.apache.commons.math3.special.Gamma;


public class BDeu extends Scorer{

	public BDeu(int[][] data, int alleleStates, int diseaseStates, double a) {
		super(data, alleleStates, diseaseStates, a);
	}

	@Override
	
	// produces the BDeu score of the BN where q=number of configurations, r=DiseaseStates, 
	// s=number of times parents takes jth configuration and X=k
	
	double Score(ArrayList<Integer> parents) {
		double score = 1;
		double term1=0, term2=1, NJK = 0;
		
		ArrayList<TreeNode> configCounts = getParentConfigurationCounts(parents);

		for (int j=0; j<getNumConfigurations(); j++)
		{
			double sumNJK=0;
			for (int k=0; k<DiseaseStates; k++)
			{
				NJK = configCounts.get(j).diseaseCounts[k];
				sumNJK = sumNJK + NJK;
			}
			
			term1 = Gamma.gamma(alpha/getNumConfigurations())/Gamma.gamma((alpha/(getNumConfigurations()))+ sumNJK);
			term2 = 1;
			
			for (int k=0; k<DiseaseStates; k++)
			{ 
				NJK = configCounts.get(j).diseaseCounts[k];
				term2 = term2*(Gamma.gamma((alpha/(DiseaseStates*getNumConfigurations())+NJK)/Gamma.gamma(alpha/DiseaseStates*getNumConfigurations())));
			 
			}
			
			score = score * term1 * term2;
		}
		
		
		//System.out.println(score);
		//System.out.println(term2);
		return score;
		
		
	}

}

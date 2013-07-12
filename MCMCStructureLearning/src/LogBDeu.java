import java.util.ArrayList;

import org.apache.commons.math3.special.Gamma;


public class LogBDeu extends Scorer{

	public LogBDeu(int[][] data, int alleleStates, int diseaseStates, double a) {
		super(data, alleleStates, diseaseStates, a);
	}

	@Override
	double score(ArrayList<Integer> parents) 
	{ //computes the log of the BDeu score
		ArrayList<TreeNode> configCounts = getParentConfigurationCounts(parents);
		double alpha_numconfigs = Alpha/getNumConfigurations();
		double alpha_disease_numconfigs = Alpha/(DiseaseStates*getNumConfigurations());
		double score = 0;
		for (int j = 0; j < getNumConfigurations(); j++)
		{
			double sumNJK=0;
			for (int k=0; k<DiseaseStates; k++)
			{
				int NJK = configCounts.get(j).diseaseCounts[k];
				sumNJK = sumNJK + NJK;
			}
			double temp1 = Gamma.logGamma(alpha_numconfigs);
			double temp2 = Gamma.logGamma(alpha_numconfigs+sumNJK);
			double term1 = temp1-temp2;
			double term2 = 0;
			for(int k = 0; k < DiseaseStates; k++)
			{
				int sJK = configCounts.get(j).diseaseCounts[k];
				term2 = term2 + Gamma.logGamma(alpha_disease_numconfigs+sJK)-Gamma.logGamma(alpha_disease_numconfigs);
			}
			score = score + term1 + term2;
			
		}
		return score;
	}

	@Override
	double getProbOfData(double score) {
		return Math.exp(score);
	}

}

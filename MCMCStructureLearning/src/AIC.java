import java.util.ArrayList;


public class AIC extends Scorer{

	public AIC(int[][] data, int alleleStates, int diseaseStates, double a) {
		super(data, alleleStates, diseaseStates, a);
	}

	@Override
	public double calcScore(ArrayList<Integer> parents) {
		//returns the AIC score of the LDS: the disease status node and the edges to it.  The LDS does not include the snp nodes.
		double score = getLikelihood(parents);
		score = score-getNumConfigurations()*(DiseaseStates-1);
		return score;
	}

	@Override
	double getProbOfData(double score) {
		return Math.exp(score);
	}

}

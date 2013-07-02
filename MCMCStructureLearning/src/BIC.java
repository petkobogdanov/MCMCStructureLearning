import java.util.ArrayList;


public class BIC extends Scorer{

	public BIC(int[][] data, int alleleStates, int diseaseStates, double a) {
		super(data, alleleStates, diseaseStates, a);
	}

	@Override
	double Score(ArrayList<Integer> parents) {
		double score = getLikelihood(parents);
		return score-Math.log((double)M)*getNumConfigurations()*(DiseaseStates-1)/2;
	}

}

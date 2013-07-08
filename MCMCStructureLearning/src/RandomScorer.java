import java.util.ArrayList;


public class RandomScorer extends Scorer{

	public RandomScorer(int[][] data, int alleleStates, int diseaseStates, double a) {
		super(data, alleleStates, diseaseStates, a);
	}

	@Override
	double score(ArrayList<Integer> parents) {
		return Math.random();
	}

	@Override
	double getProbOfData(double score) {
		return score;
	}

}

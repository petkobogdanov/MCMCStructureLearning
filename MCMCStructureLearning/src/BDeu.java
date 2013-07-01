import java.util.ArrayList;

import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.stat.descriptive.summary.Product;


public class BDeu extends Scorer{

	public BDeu(int[][] data, int alleleStates, int diseaseStates, int alpha) {
		super(data, alleleStates, diseaseStates);
	}

	@Override
	double Score(ArrayList<Integer> parents) {
		double score = getLikelihood(parents);
		(Gamma.gamma(alpha * getNumConfigurations()))/Gamma.gamma(arg0)
		
		;
	}

}

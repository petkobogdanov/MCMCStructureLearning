import java.util.ArrayList;


public class PrecisionRecallCalc {

	
	public static void main(String[] args) {
		if(args.length < 2)
		{
			System.out.println("Arguments required: <learned network file> <gold standard network file>");
			return;
		}
		NetworkParser p = new NetworkParser();
		ArrayList<Integer> learned = p.parse(args[0]);
		ArrayList<Integer> standard = p.parse(args[1]);
		int truePositives = getNumTruePositives(learned, standard);
		double precision = truePositives/learned.size();
		double recall = truePositives/standard.size();
		double distance = Math.sqrt(Math.pow(1-precision, 2)+Math.pow(1-recall, 2));
		System.out.println("Precision: "+precision);
		System.out.println("Recall: "+recall);
		System.out.println("Distance from perfect precision and recall: "+distance);
	}
	
	public static int getNumTruePositives(ArrayList<Integer> learned, ArrayList<Integer> standard)
	{ //returns the number of SNPs that are in the learned network that are also in the gold standard network
		int counter = 0;
		for(int i = 0; i < standard.size(); i++)
		{
			if(learned.contains(standard.get(i)))
			{
				counter++;
			}
		}
		return counter;
	}

}

import java.io.File;


public class Experimenter {
	
	private static int MixingSteps = 10000;
	private static int RunningSteps = 100000;

	public static void main(String[] args) {
		//takes a path to a directory containing folders of data sets and a list of at least one file name
		//each folder should have a file with the given file name(s).
		//each folder represents a different trial where all of the named files are used in the same trial
		//for each trial, runs MCMC several times
		//each run of MCMC uses a different scoring method, and various alpha values may be given in the arguments
		//learned network naming convention: <path to network output directory>/<trial folder name>/<scoring method>_<alpha> where the "_<alpha>" is only included for BDeu scores
		//final output naming convention: <path to final output directory>/<trial folder name>/<scoring method>_<alpha> where the "_<alpha>" is only included for BDeu scores
		if(args.length < 8)
		{
			System.out.println("Arguments required: <number of disease states> <number of allele codes> <path to data folders> <number of data files> <data files> <path to network output directory> <path to correct network> <path to final output directory> <alpha values (optional)>");
			return;
		}
		//parse args
		String pathToData=args[2];
		int numDataFiles = 0;
		String[] dataFiles;
		String[] scoringMethods;
		double[] alphas = null;
		try{
			numDataFiles = Integer.parseInt(args[3]);
			dataFiles = new String[numDataFiles];
			for(int i = 0; i < numDataFiles; i++)
			{
				dataFiles[i] = args[i+4];
			}
			if(args.length > (7+dataFiles.length))
			{ //we have some alpha values
				alphas = new double[args.length-(7+dataFiles.length)];
				scoringMethods = new String[2+alphas.length];
				for(int i = 0; i < alphas.length; i++)
				{
					alphas[i] = Double.parseDouble(args[i+7+dataFiles.length]);
					scoringMethods[i+2] = "BDeu";
				}
			}
			else
			{
				scoringMethods = new String[2]; //just use AIC and BIC
			}
		}
		catch(NumberFormatException e)
		{
			System.out.println("Can not parse number argument.");
			return;
		}
		scoringMethods[0] = "AIC";
		scoringMethods[1] = "BIC";
		//loop through each folder and run one trial per scoring method on each folder's data
		File parent = new File(pathToData);
		for (File trialFolder : parent.listFiles())
		{
			//get the names to the files to use for this set of trials
			String[] trialDataFiles = new String[dataFiles.length];
			for(int i = 0; i < dataFiles.length; i++)
			{
				trialDataFiles[i] = trialFolder.getAbsolutePath() + "\\" + dataFiles[i];
			}
			//make the folders for output
			File networkOutputFolder = new File(args[4+dataFiles.length]+"\\"+trialFolder.getName());
			networkOutputFolder.mkdir();
			File finalOutputFolder = new File(args[args.length-1-alphas.length]+"\\"+trialFolder.getName());
			finalOutputFolder.mkdir();
			//run MCMC and PrecisionRecallCalc once for each scoring method
			for(int i = 0; i < scoringMethods.length; i++)
			{
				//run MCMC
				//args to MCMC: <scoring method> <mixing steps> <running steps> <number of disease states> <number of allele codes> <data files> <output file> <alpha>
				String[] mcmcArgs = new String[7+trialDataFiles.length];
				mcmcArgs[0] = scoringMethods[i];
				mcmcArgs[1] = Integer.toString(MixingSteps);
				mcmcArgs[2] = Integer.toString(RunningSteps);
				mcmcArgs[3] = args[0];
				mcmcArgs[4] = args[1];
				for(int j = 0; j < trialDataFiles.length; j++)
				{
					mcmcArgs[j+5] = trialDataFiles[j];
				}
				String networkOutputFile = networkOutputFolder.getAbsolutePath()+"\\"+scoringMethods[i];
				if(scoringMethods[i].equals("BDeu"))
				{
					networkOutputFile=networkOutputFile+"_"+Double.toString(alphas[i-2]);
					mcmcArgs[mcmcArgs.length-1] = Double.toString(alphas[i-2]);
				}
				else
				{
					mcmcArgs[6+trialDataFiles.length] = Integer.toString(-1);
				}
				mcmcArgs[5+trialDataFiles.length] = networkOutputFile;
				MCMC.main(mcmcArgs);
				//runPrecisionRecallCalc
				//args to PrecisionRecallCalc: <learned network file> <gold standard network file> <output file path>
				String[] prcArgs = new String[3];
				String finalOutputFile = finalOutputFolder.getAbsolutePath()+"\\"+scoringMethods[i];
				if(scoringMethods[i].equals("BDeu"))
				{
					finalOutputFile=finalOutputFile+"_"+Double.toString(alphas[i-2]);
				}
				prcArgs[0] = networkOutputFile;
				prcArgs[1] = args[args.length-2-alphas.length];
				prcArgs[2] = finalOutputFile;
				PrecisionRecallCalc.main(prcArgs);
			}
			
		}
	}

}

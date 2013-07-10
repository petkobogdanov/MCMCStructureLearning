import java.io.File;
import java.util.ArrayList;


public class Experimenter2 {

	private static int MixingSteps = 20000;
	private static int RunningSteps = 100000;
	private static boolean AIC = false;
	private static boolean BIC = false;
	private static boolean RandomScore = false;
	private static boolean UseFirstLine = true; //whether or not to use the first line of the Data files
	
	//don't set these--they're set in the code
	private static int NumDiseaseStates;
	private static int NumAlleleCodes;
	private static String[] ScoringMethods;
	private static double[] Alphas;
	private static String PathToCorrectNetwork;

	public static void main(String[] args) {
		//The program runs MCMCStructureLearning and PrecisionAndRecallCalc on every file contained in the current directory and any sub-directories (it keeps going 
		//into sub-directories until it has reached the bottom of the directory tree).
		//For every file it encounters, it creates a folder of the same name in the same directory as the file and puts all output for this file in this folder.
		//each run of MCMC uses a different scoring method, and various alpha values may be given in the arguments to this program
		//learned network naming convention: <scoring method>_<alpha>_LearnedNetwork where the "_<alpha>" is only included for BDeu scores
		//final output naming convention: <scoring method>_<alpha>_PrecisionAndRecall where the "_<alpha>" is only included for BDeu scores
		//all files are assumed to have the same correct network file, which is given as an argument to this program
		if(args.length < 3)
		{
			System.out.println("Arguments required: <number of disease states> <number of allele codes> <path to correct network> <alpha values (optional)>");
			return;
		}
		//parse args
		int numAlphas = args.length-3;
		try{
			int numScoringMethods = 0;
			if(AIC)
			{
				numScoringMethods++;
			}
			if(BIC)
			{
				numScoringMethods++;
			}
			if(RandomScore)
			{
				numScoringMethods++;
			}
			if(numAlphas > 0)
			{ //we have some alpha values
				Alphas = new double[numAlphas];
				numScoringMethods += numAlphas;
			}
			ScoringMethods = new String[numScoringMethods];
			for(int i = 0; i < numAlphas; i++)
			{
				Alphas[i] = Double.parseDouble(args[i+3]);
				ScoringMethods[i] = "BDeu";
			}
			NumDiseaseStates = Integer.parseInt(args[0]);
			NumAlleleCodes = Integer.parseInt(args[1]);
		}
		catch(NumberFormatException e)
		{
			System.out.println("Can not parse number argument.");
			return;
		}
		PathToCorrectNetwork = args[2];
		int nextIndex = Alphas.length; //this keeps track of where our next method goes in scoringMethods
		if(AIC)
		{
			ScoringMethods[nextIndex] = "AIC";
			nextIndex++;
		}
		if(BIC)
		{
			ScoringMethods[nextIndex] = "BIC";
			nextIndex++;
		}
		if(RandomScore)
		{
			ScoringMethods[nextIndex] = "Random";
			nextIndex++;
		}
		//explore each sub-directory and process all files
		String current = System.getProperty("user.dir");
		File currentDir = new File(current);
		ProcessDirectory(currentDir);
	}

	private static void ProcessFile(File file) 
	{
		//create new directory with same name as file
		File newDir = new File(file.getAbsolutePath());
		newDir.mkdir();
		//run MCMC and PrecisionRecallCalc once for each scoring method
		for(int i = 0; i < ScoringMethods.length; i++)
		{
			//run MCMC
			//args to MCMC: <scoring method> <mixing steps> <running steps> <number of disease states> <number of allele codes> <data files> <use first line: t/f> <output file> <alpha>
			String[] mcmcArgs = new String[9];
			mcmcArgs[0] = ScoringMethods[i];
			mcmcArgs[1] = Integer.toString(MixingSteps);
			mcmcArgs[2] = Integer.toString(RunningSteps);
			mcmcArgs[3] = Integer.toString(NumDiseaseStates);
			mcmcArgs[4] = Integer.toString(NumAlleleCodes);
			mcmcArgs[5] = file.getAbsolutePath();
			mcmcArgs[6] = Boolean.toString(UseFirstLine);
			String outputFilePrefix = newDir.getAbsolutePath()+"\\"+ScoringMethods[i];
			if(ScoringMethods[i].equals("BDeu"))
			{
				outputFilePrefix=outputFilePrefix+"_"+Double.toString(Alphas[i]);
				mcmcArgs[8] = Double.toString(Alphas[i]);
			}
			else
			{
				mcmcArgs[8] = Integer.toString(-1);
			}
			String networkOutputFile = outputFilePrefix + "_LearnedNetwork";
			mcmcArgs[7] = networkOutputFile;
			MCMC.main(mcmcArgs);
			//runPrecisionRecallCalc
			//args to PrecisionRecallCalc: <learned network file> <gold standard network file> <output file path>
			String[] prcArgs = new String[3];
			prcArgs[0] = networkOutputFile;
			prcArgs[1] = PathToCorrectNetwork;
			prcArgs[2] = outputFilePrefix + "_PrecisionAndRecall";
			PrecisionRecallCalc.main(prcArgs);
		}
		
	}

	private static void ProcessDirectory(File dir) {
		File[] contents = dir.listFiles();
		for (File file : contents) //we don't need to worry about avoiding the directories we make because we get the list of files to explore before making any directories
		{
	        if (file.isDirectory()) 
	        {
	            ProcessDirectory(file);
	        } 
	        else 
	        {
	            ProcessFile(file);
	        }
	    }
	}

}

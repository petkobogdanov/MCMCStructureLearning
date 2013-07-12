MCMCStructureLearning
=====================
This repository has one project which has four different main() methods.  How to run them and what they do is explained below.

1. MCMCStructureLearning
This runs the MCMC process on the input data using the given scoring method and creates an output file.  The output file prints the index (0-based indexing) of the snps that were in the parent set during the sampling process.  After the snp index it prints a colon and the frequency with which the snp was in the parent set.  The program also prints the total number of snps, the average parent size and the maximum parent size to stdout.

2. PrecisionAndRecallCalc
This calculates the precision, recall, and distance to perfect precision and recall of the output from MCMCStructureLearning, given the gold standard network.  It prints these to an output file.

3. Experimenter
This runs MCMCStructureLearning and then PrecisionAndRecallCalc successively on many data sets using the desired scoring methods (ie. it runs both programs once per scoring method on each dataset).  Each data set must be in its own folder, and all of these folders must be in one folder.  A file by each given name must be present in each data directory.  Directories with the same names as the data directories will be created (if not already present) in both output directories, and a file for each scoring method will be created in these directories.  The naming convention used for output files is <scoring method>[_<alpha value>], where the portion in square brackets only applies to BDeu scoring methods.  The number of mixing and running steps must be set in the code before running the program by changing the appropriate global variables.  The scoring methods used are set by setting the appropriate global booleans in the code.  The only exception is BDeu--to use this method, simply give the desired alpha values as arguments to the program.  To set whether or not to use the first line of the data files, change the appropriate global boolean in the code.

4. Experimenter2
The program runs MCMCStructureLearning and PrecisionAndRecallCalc on every .txt file contained in the current directory and any sub-directories (it keeps going into sub-directories until it has reached the bottom of the directory tree).  For every .txt file it encounters, it creates a folder with name <file name>_results in the same directory as the file and puts all output for this file in this folder.  It runs MCMCStructureLearning and PrecisionAndRecallCalc once per scoring method on each file it encounters.

There are two different BDeu scoring methods.  One of them tries to calculate the score directly using logGamma in only a few places.  When there is overflow or underflow it sets the value to Double.MAX_VALUE or Double.MIN_VALUE respectively.  This causes inaccuracies and the score sometimes still goes to 0.  LogBDeu calculates the log of the score and computes it as a sum of terms.  This removes the overflow and underflow problems.
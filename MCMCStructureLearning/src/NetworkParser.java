import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;


public class NetworkParser {
	
	public ArrayList<Integer> parse(String filePath)
	{ //returns the array of integers that are the indices of the SNPs in the parent set of a disease node.
		//assumes that the first integer on each line of the file is the SNP index and ignores everything else on the line.
		BufferedReader br = null;
		ArrayList<Integer> parents = null;
		try
		{
			br = new BufferedReader(new FileReader(filePath));
			parents = new ArrayList<Integer>();
			NumberFormat format = NumberFormat.getIntegerInstance( );
		    String strLine;
		    //Read File Line By Line
		    while ((strLine = br.readLine()) != null)   
		    {
		    	Number number = format.parse(strLine);
		    	parents.add(Integer.valueOf(number.intValue()));
		    }
		    //Close the input stream
		    br.close();
		}
		catch (Exception e)
		{//Catch exception if any
		    System.err.println("Error: " + e.getMessage());
		}
		finally
		{
		    	try 
		    	{
					br.close();
				} catch (IOException e) 
				{
				}
		}
		return parents;		
	}

}

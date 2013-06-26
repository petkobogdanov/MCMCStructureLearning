import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class Parser {
	
	public int[][] Parse(String filePath)
	{
		//assumes file format is: first row is SNP names, each row after is a sample, each column is a snp, except the last, 
		//which is the disease column, each entry separated by a single space
		int[][] finalData = null;
		BufferedReader br = null;
		try
		{

		    ArrayList<int[]> data = new ArrayList<int[]>();
			br = new BufferedReader(new FileReader(filePath));
		    String strLine;
		    //Read File Line By Line
		    boolean firstLine = true;
		    while ((strLine = br.readLine()) != null)   
		    {
		    	if(firstLine == false)
		    	{
		    		String stringArr[] = strLine.split(" ");
		    		int[] row = new int[stringArr.length];
		    		for(int j = 0; j < stringArr.length; j++)
		    		{
		    			row[j] = Integer.parseInt(stringArr[j]);
		    		}
		    		data.add(row);
		    	}
		    	else
		    	{
		    		firstLine = false;
		    	}
		    }
		    //Close the input stream
		    br.close();
		    //convert data to regular arrays
		    finalData = (int[][])data.toArray(new int[data.size()][]);
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
		return finalData;
	}

}

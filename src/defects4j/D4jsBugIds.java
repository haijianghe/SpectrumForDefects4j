/**
 * 
 */
package defects4j;

import java.util.List;

import common.ProjectConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @author ccsu
 *
 */
public class D4jsBugIds {
	private  static List<Integer> bugIdList=null; //a bug id list of one project
	
	private D4jsBugIds()
	{
		
	}
	
	/** read file , store to bugIdList;
	 * @param bugidFilename
	 * @return
	 */
	public static boolean readFile(String bugidFilename)
	{
		boolean result = true;
		
		bugIdList = new ArrayList<Integer>();
		String filename = ProjectConfiguration.DatasetWorkDirectory+"/"+bugidFilename;
		try {
			File file = new File(filename);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
				BufferedReader br = new BufferedReader(read);
				String lineTXT = null;
				while ((lineTXT = br.readLine()) != null) 
				{
					//String[] parsed = lineTXT.split(":");
					//String runStr = parsed[0].trim();
					int bugid = Integer.valueOf(lineTXT);
					bugIdList.add(bugid);
				}
				read.close();
			}
			else
				result = false;
		}//end of try. 
		catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	/** 
	 * @return bug id list
	 */
	public static List<Integer> getBugIdList()
	{
		return bugIdList;
	}
	
	/** 
	 * @return number of bug id 
	 */
	public static int getNumberOfBugId()
	{
		return bugIdList.size();
	}
	/** For test,first readFile is called.
	 *  show bug id list.
	 */
	public static void showBugIdList()
	{
		System.out.println("Bug id list ("+bugIdList.size()+") : ");
		for( int bugid : bugIdList)
		{
			System.out.print(bugid+",");
		}
		System.out.println(".");
	}
}//end of class

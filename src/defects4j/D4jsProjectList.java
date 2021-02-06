/**
 * 
 */
package defects4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import common.ProjectConfiguration;

/**
 * @author ccsu
 *
 */
public class D4jsProjectList {
	private  static List<String> projectIdList=null; // project id list of Defects4j
	
	private D4jsProjectList()
	{
		
	}
	
	/** read file , store to projectIdList;
	 * @param peojctIdFilename
	 * @return
	 */
	public static boolean readFile(String projctIdFilename)
	{
		boolean result = true;
		
		projectIdList = new ArrayList<String>();
		String filename = ProjectConfiguration.DatasetWorkDirectory+"/"+projctIdFilename;
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
					String projectId = lineTXT.trim();
					projectIdList.add(projectId);
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
	 * @return project id list
	 */
	public static List<String> getProjectIdList()
	{
		return projectIdList;
	}
	
	/** For test,first readFile is called.
	 *  show project id list.
	 */
	public static void showProjectIdList()
	{
		System.out.println("Bug id list ("+projectIdList.size()+") : ");
		for( String project : projectIdList)
		{
			System.out.print(project+",");
		}
		System.out.println(".");
	}
}

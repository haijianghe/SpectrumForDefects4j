/**
 * 
 */
package defects4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import common.ProjectConfiguration;

/**
 * @author ccsu
 *  当前操作对象及版本的配置属性
 */
public class D4jBuildProperties {
	private static int bugId = -1; //current bug id
	private static String projectId = null; //current project id
	private static String sourceDirectory = null; // "d4j.dir.src.classes=source"
	private static List<String> modifiedClassesList = null; //d4j.classes.modified
	private static List<String> relevantClassesList = null; //d4j.classes.relevant
	private static List<String> triggerTests = null; //d4j.tests.trigger

	private D4jBuildProperties()
	{
	}

	public static List<String> getModifiedClassesList() {
		return modifiedClassesList;
	}

	public static List<String> getRelevantClassesList() {
		return relevantClassesList;
	}

	public static List<String> getTriggerTests() {
		return triggerTests;
	}
	
	public static int getBugId()
	{
		return bugId;
	}
	
	/**
	 * @return  project ID
	 */
	public static String getProjectId()
	{
		return projectId;
	}
	
	/** 源代码所在的文件夹
	 * @return  sourceDirectory
	 */
	public static String getDirSource()
	{
		return sourceDirectory;
	}
	
	/** 读入文件夹(objectName)_buggy下的文件defects4j.build.properties； 
	 * 用于检查check out是否执行正确。并可用于后续操作正确性的检查。
	 * @param objname
	 * @return
	 */
	public static boolean readPropertyFile(String objname)
	{
		boolean result = true;
		
		modifiedClassesList = new ArrayList<String>();
		relevantClassesList = new ArrayList<String>();
		triggerTests = new ArrayList<String>();
		
		//检测这些数据是否都读入
		boolean foundBugId = false,foundModify = false,foundRelevant = false,foundSourceDir = false,
				foundProjectId = false, foundTrigger = false;
		
		String filename = ProjectConfiguration.DatasetWorkDirectory+"/"+objname+"_buggy/"+"defects4j.build.properties";
		try {
			File file = new File(filename);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
				BufferedReader br = new BufferedReader(read);
				
				String lineTxt = null;
				//运行过程中，会在文件中加入一些数据，不利于调试，所以改为此种方式。
				while ((lineTxt = br.readLine()) != null) 
				{
					String[] parsed = lineTxt.split("=");
					if( parsed[0].equalsIgnoreCase("d4j.bug.id") )
					{ //d4j.bug.id=
						foundBugId = true;
						bugId = Integer.valueOf(parsed[1]);;
					}
					else if( parsed[0].equalsIgnoreCase("d4j.classes.modified") )
					{//d4j.classes.modified=
						foundModify = true;
						String[] modifiedClasses = parsed[1].split(",");
						modifiedClassesList = Arrays.asList(modifiedClasses);
					}
					else if( parsed[0].equalsIgnoreCase("d4j.classes.relevant") )
					{//d4j.classes.relevant=
						foundRelevant = true;
						String[] relevantClasses = parsed[1].split(",");
						relevantClassesList = Arrays.asList(relevantClasses);
					}
					else if( parsed[0].equalsIgnoreCase("d4j.dir.src.classes") )
					{//d4j.dir.src.classes=
						foundSourceDir = true;
						sourceDirectory	= parsed[1];
					}
					else if( parsed[0].equalsIgnoreCase("d4j.project.id") )
					{//d4j.project.id=
						foundProjectId = true;
						projectId = parsed[1];
					}
					else if( parsed[0].equalsIgnoreCase("d4j.tests.trigger") )
					{//d4j.tests.trigger=
						foundTrigger = true;
						String[] testsTrigger = parsed[1].split(",");
						triggerTests = Arrays.asList(testsTrigger);
					}
					else
					{
						//#File automatically generated by Defects4J
						// or other
					}
				}//end of while
				read.close();
			}//end of if(file.isFile() && 
			else
				result = false;
		}//end of try. 
		catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		if( !foundBugId )
			System.out.println("The d4j.bug.id not found.");
		if( !foundModify )
			System.out.println("The d4j.classes.modified not found.");
		if( !foundRelevant )
			System.out.println("The d4j.classes.relevant not found.");
		if( !foundSourceDir )
			System.out.println("The d4j.dir.src.classes not found.");
		if( !foundProjectId )
			System.out.println("d4j.project.id.");
		if( !foundTrigger )
			System.out.println("The d4j.tests.trigger not found.");
		return result;
	}//end of readPropertyFile
	
	/**
	 * @param idProject  project id
	 * @param idBug   bug id
	 * @return
	 */
	public static boolean checkProjectidBugid(String idProject,int idBug)
	{
		if( projectId.equalsIgnoreCase(idProject) && (bugId==idBug) )
			return true;
		else
			return false;
	}
}

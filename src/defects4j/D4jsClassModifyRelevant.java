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

/**
 * @author ccsu
 *
 */
public class D4jsClassModifyRelevant {
	private static List<String> modifiedClassesList = null; //被修改的类
	private static List<String> relevantClassesList = null; //d4j.classes.relevant 所有测试用例执行JVM装入的类

	private D4jsClassModifyRelevant()
	{
		
	}
	
	
	/** read file , store to modifiedClassesList; 被修改的类
	 * @param modifyFilename  带目录的文件名。
	 * @return
	 */
	public static boolean readModifyClassFile(String modifyFilename)
	{
		boolean result = true;
		
		modifiedClassesList = new ArrayList<String>();
		try {
			File file = new File(modifyFilename);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
				BufferedReader br = new BufferedReader(read);
				String lineTXT = null;
				while ((lineTXT = br.readLine()) != null) 
				{
					modifiedClassesList.add(lineTXT);
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

	/** read file , store to relevantClassesList; 所有测试用例执行JVM装入的类
	 * @param relevantFilename  带目录的文件名。
	 * @return
	 */
	public static boolean readRelevantClassFile(String relevantFilename)
	{
		boolean result = true;
		
		relevantClassesList = new ArrayList<String>();
		try {
			File file = new File(relevantFilename);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
				BufferedReader br = new BufferedReader(read);
				String lineTXT = null;
				while ((lineTXT = br.readLine()) != null) 
				{
					relevantClassesList.add(lineTXT);
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

	/** itemList里搜索search项；
	 * @param search 搜索项
	 * @param itemList
	 * @return 如果找到，则返回true；否则返回false
	 */
	private static boolean isSearchInItemList(String search,List<String> itemList)
	{
		boolean found = false;
		for( String item : itemList )
			if( item.equals(search) )
			{ //found this search
				found = true;
				break;
			}
		return found;
	}
	
	/**  compare two string list,
	 * @param cmpList =modifiedClassesList, or relevantClassesList
	 * @param modrelList
	 * @return true:is same ; otherwise return false
	 */
	private static boolean compareStringList(List<String> cmpList,List<String> modrelList)
	{
		boolean result = true;
		if( cmpList.size()==modrelList.size() ) //length is equal
		{
			//item in cmpList must be in modrelList
			for( String item : modrelList )
			{
				if( isSearchInItemList(item,cmpList)==false )
				{ //this item have not found.
					result = false;
					break;		
				}
			}
			//item in modrelList must be in cmpList
			if( result==true )
			{
				for( String item : cmpList )
				{
					if( isSearchInItemList(item,modrelList)==false )
					{ //this item have not found.
						result = false;
						break;		
					}
				} //end of for...
			}//end of if...
		}
		else
			result = false;
		if( result==false )
			System.out.println("Two string list is not same.");
		return result;

	}
	
	/** 读入文件defects4j.build.properties，与defects4j export -p classes.modified命令比较，结果一定要相同。
	 * @param modifiedList
	 * @return
	 */
	public static boolean isModifySameAs(List<String> modifiedList)
	{
		return compareStringList(modifiedClassesList,modifiedList);
	}

	/** 读入文件defects4j.build.properties，与defects4j export -p classes.relevant命令比较，结果一定要相同。
	 * @param modifiedList
	 * @return
	 */
	public static boolean isRelevantSameAs(List<String> relevantList)
	{
		return compareStringList(relevantClassesList,relevantList);
	}
}

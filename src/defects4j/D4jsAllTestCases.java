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
public class D4jsAllTestCases {
	private static List<D4jsTestCase> allTestCases = null; //所有测试用例及结果。
	//注意，failingTestCases的内容全部包含在allTestCases里。failingTestCases是读入文件failing_tests的结果
	private static List<D4jsTestCase> failingTestCases = null; //未通过（测试失败）的测试用例及结果。

	private D4jsAllTestCases()
	{
		
	}
	
	/**
	 * @return all test cases
	 */
	public static List<D4jsTestCase> getTestCases()
	{
		return allTestCases;
	}
	
	/**  
	 * @return  number of all test cases
	 */
	public static int getNumberOfAllTestcases()
	{
		return allTestCases.size();
	}
	
	/**   /未通过（测试失败）的测试用例
	 * @return  number of failed test cases
	 */
	public static int getNumberOfFailedTestcases()
	{
		return failingTestCases.size();
	}

	/** 读入defects4j test ....的输出文件，并将结果保存到测试用例列表。
	 * @param testsFilename 文件名，不要加入目录信息。
	 * @return
	 */
	public static boolean readD4jsCommandFile(String testsFilename)
	{
		boolean result = false;
		int total = 0; //failing test case number. 
		
		allTestCases = new ArrayList<D4jsTestCase>();
		String filename = ProjectConfiguration.DatasetWorkDirectory+"/"+testsFilename;
		try {
			File file = new File(filename);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
				BufferedReader br = new BufferedReader(read);
				String lineTXT = null;
				while ((lineTXT = br.readLine()) != null) 
				{
					String[] parsed = lineTXT.split("\\s+"); //匹配空格
					if( parsed[0].equals("Failing") && parsed[1].equals("tests:") )
					{ //find "Failing tests:"
						result = true;
						total = Integer.valueOf(parsed[2]);
						break;
					}
				}
				if( false==result )
				{ //没有找到字符串"Failing tests:"，出错了。
					read.close();
					throw new Exception("Failing tests: is not found.");
				}
				while ((lineTXT = br.readLine()) != null) 
				{
					String[] parsed = lineTXT.split("\\s+"); //匹配空格
					parsed = lineTXT.split("\\s+");
					//String runStr = parsed[0].trim();
					String[] classMethod = parsed[2].split("::");
					D4jsTestCase dtc = new D4jsTestCase(false,classMethod[0],classMethod[1]);
					allTestCases.add(dtc);
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
		if( total!=allTestCases.size() )
			System.out.println("Failing tests="+total+",allTestCases.size()="+allTestCases.size()+"!!!!"); 
		else
			System.out.println("Failing tests= "+total);
		return result;
	}
	
	/**failingTestCases是读入文件failing_tests的结果;该文件由命令defects4j test -r ...产生 
	 * @param failFilename :failing_tests; 文件名，带目录信息。
	 * @return
	 */
	public static boolean readFailingTests(String failFilename)
	{
		boolean result = true;
		
		failingTestCases = new ArrayList<D4jsTestCase>();
		try {
			File file = new File(failFilename);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
				BufferedReader br = new BufferedReader(read);
				String lineTXT = null;
				while ((lineTXT = br.readLine()) != null) 
				{
					int index = lineTXT.indexOf("--- ");//--- 指出了测试类及方法。
					if( index<0 )
						continue;
					String substr = lineTXT.substring(4, lineTXT.length());
					String[] parsed = substr.split("::");
					D4jsTestCase dtc = new D4jsTestCase(false,parsed[0],parsed[1]);
					failingTestCases.add(dtc);
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
		if( result==true )
			result = compareFailingTestsList();
		return result;
	}
	
	/** itemList里搜索search项；
	 * @param search 搜索项
	 * @param itemList
	 * @return 如果找到，则返回true；否则返回false
	 */
	private static boolean isSearchInItemList(D4jsTestCase search,List<D4jsTestCase> itemList)
	{
		boolean found = false;
		for( D4jsTestCase item : itemList )
			if( item.isEqual(search) )
			{ //found this search
				found = true;
				break;
			}
		return found;
	}
	
	/** 当前的allTestCases是从defects4j test -r ...命令输出文件读出的失败测试用例集合
	 * failingTestCases是从文件failing_tests读出的失败测试用例集合 
	 * @return 两者类容完全相同，则返回true
	 */
	private static boolean compareFailingTestsList()
	{
		boolean result = true;
		if( failingTestCases.size()==allTestCases.size() ) //length is equal
		{
			//item in allTestCases must be in failingTestCases
			for( D4jsTestCase item : allTestCases )
			{
				if( isSearchInItemList(item,failingTestCases)==false )
				{ //this item have not found.
					result = false;
					break;		
				}
			}
			//fail in failingTestCases must be in allTestCases
			if( result==true )
			{
				for( D4jsTestCase fail : failingTestCases )
				{
					if( isSearchInItemList(fail,allTestCases)==false )
					{ //this fail have not found.
						result = false;
						break;		
					}
				} //end of for...
			}//end of if...
		}
		else
			result = false;
		if( result==false )
			System.out.println("The failingTestCases and allTestCases is not same.");
		return result;
	}
	
	/**读入文件all_tests的结果;该文件由命令defects4j test -r ...产生 
	 * @param allFilename :all_tests 文件名，带目录信息。
	 * @return
	 */
	public static boolean readAllTests(String allFilename)
	{
		boolean result = true;
		
		try {
			File file = new File(allFilename);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
				BufferedReader br = new BufferedReader(read);
				String lineTXT = null;
				while ((lineTXT = br.readLine()) != null) 
				{
					int parentheses = lineTXT.indexOf('(');//小括号
					String methodname = lineTXT.substring(0, parentheses);
					String classname = lineTXT.substring(parentheses+1,lineTXT.length()-1);//the last is ')'
					//针对通过的测试用例,后续执行过程addTestCase，会改变false->true;
					D4jsTestCase dtc = new D4jsTestCase(false,classname,methodname); //notice : false can't change to true.
					addTestCase(dtc);
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
	
	/**因为之前已经比较过failingTestCases和allTestCases，如果在failingTestCases中出现，那么它也一定出现在allTestCases中，此时不要再加入；
	 * 如果没有出现过，则，先将其测试结果改为true；在加入allTestCases列表。
	 * @param dtc
	 */
	private static void addTestCase( D4jsTestCase dtc )
	{
		boolean found = false;
		for( D4jsTestCase fail : failingTestCases )
		{
			if( fail.isEqual(dtc) )
			{
				found = true; //出现过的测试用例
				break;
			}
		}
		if( found==false )
		{ //没有在failingTestCases里出现过的测试用例，其执行结果一定是pass。
			dtc.setResult(true);//this test case is pass.
			allTestCases.add(dtc);
		}
	}
}

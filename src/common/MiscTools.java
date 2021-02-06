package common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/** 
 * @author ccsu
 *  misc tools
 */
public class MiscTools {
	/** 执行Shell命令 注意：有 > ，输出内容会保存到指定文件，而不会输出到console
	 * @param shellCommand
	 * @return 0 成功；otherwise 失败
	 */
	public static int runShellCommand(String shellCommand)
	{
		Process process;
        BufferedReader br;
        int returnCode = -1;
        try {
  	      	//执行命令
        	String[] command = {"/bin/bash","-c",shellCommand}; 
			process = Runtime.getRuntime().exec(command);
			//用缓冲器读行    
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String strLine=null;    
            //直到读完为止    
            while((strLine=br.readLine())!=null)    
            {    
                System.out.println(strLine);    
            }    
	        br.close();
	        returnCode = process.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return returnCode;    
	}
	
	/** 重定向输出  >&可以，而>通不过
	 * 该方法虽然调试通过，但是调用此方法后，导致runD4jsCommand失败。   失败原因，rtnCode=1；没有执行权限
	 * @param d4jsCommand
	 * @param shellFilename 存储SHELL命令的执行结果。重定向输出文件,不要加入目录信息。
	 * @return
	 */
	public static int runD4jsRedirect(String d4jsCommand,String shellFilename)
	{
		return runD4jsCommandRedirct(d4jsCommand,ProjectConfiguration.DatasetWorkDirectory+"/"+shellFilename);
	}

	/** 重定向输出  >&可以，而>通不过
	 * 该方法虽然调试通过，但是调用此方法后，导致runD4jsCommand失败。   失败原因，rtnCode=1；没有执行权限
	 * @param d4jsCommand
	 * @return
	 */
	public static int runD4jsRedirect(String d4jsCommand)
	{
		return runD4jsCommandRedirct(d4jsCommand,ProjectConfiguration.CommandResultTmpFilename);
	}

	
	/** 执行Defects4J命令 输出内容到文件resultFilename
	 * @param d4jsCommand  Defects4J command
	 * resultFilename 存储d4j命令执行的结果，不要加入目录信息。
	 * @return 0 成功；otherwise 失败
	 */
	public static int runD4jsCommand(String d4jsCommand,String resultFilename)
	{ 
		return runD4jsCommandStore(d4jsCommand,true,resultFilename);
	}
	
	/** 执行Defects4J命令 输出内容到console
	 * @param d4jsCommand  Defects4J command
	 * @return 0 成功；otherwise 失败
	 */
	public static int runD4jsCommand(String d4jsCommand)
	{ 
		return runD4jsCommandStore(d4jsCommand,false,null);
	}
	
	/** 执行带环境Defects4J命令 输出内容到console
	 * @param d4jsCommand  Defects4J command
	 * @return 0 成功；otherwise 失败
	 */
	public static int runD4jsCommandEnvironment(String d4jsCommand)
	{ 
		return runD4jsEnvironmentCommandBase(d4jsCommand,false,null);
	}

	/** 执行带环境Defects4J命令 输出内容到
	 * @param d4jsCommand  Defects4J command
	 * resultFilename 存储d4j命令执行的结果，不要加入目录信息。
	 * @return 0 成功；otherwise 失败
	 */
	public static int runD4jsCommandEnvironment(String d4jsCommand,String resultFilename)
	{ 
		return runD4jsEnvironmentCommandBase(d4jsCommand,true,ProjectConfiguration.DatasetWorkDirectory+"/"+resultFilename);
	}

	/**
	 * @return
	 */
	private static String getD4jsEnvironmentString()
	{
		String strEnvp = "export JAVA_HOME=/home/ccsu/jdk1.8.0 && "+
		         "export PATH=$JAVA_HOME/bin:$PATH && "+
		         "export CLASSPATH=$CLASSPATH:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar && ";
		return strEnvp;
	}
	
	/**  执行Defects4J command , and write result to file : filename.
	 * @param d4jsCommand  d4j命令
	 * @param redirect 是否保存d4j命令执行的结果到文件  重定向指令(存储shell命令执行的结果)
	 * @param redirectFilename 存储d4j命令执行的结果 d4j需要的执行结果文件名，要加入目录信息。
	 * @return
	 */
	private static int runD4jsEnvironmentCommandBase(String d4jsCommand,boolean redirect,String redirectFilename)
	{
		Process process;
        BufferedReader br;
        int returnCode = -1;

        try {
        	String command = getD4jsEnvironmentString() + ProjectConfiguration.Defects4jBinDir+"/defects4j"+" "+d4jsCommand;
        	if( redirect )
				command = command+" >& "+ redirectFilename;
        	String[] arycommand = {"/bin/bash","-c",command}; 
       		process = Runtime.getRuntime().exec(arycommand);
			
			//用缓冲器读行    
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String strLine=null;    
            //直到读完为止    
            while((strLine=br.readLine())!=null)    
            {    
        		System.out.println(strLine);
            }    
	        br.close();
	        returnCode = process.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return returnCode;    
	}
	
	/**  执行Defects4J command , and write result to file : filename.
	 * @param d4jsCommand  d4j命令
	 * @param redirectFilename 存储shell命令执行的结果 重定向文件名,要求提供目录信息。
	 * @return
	 */
	private static int runD4jsCommandRedirct(String d4jsCommand,String redirectFilename)
	{
		Process process;
        BufferedReader br;
        int returnCode = -1;
		
        try {
  	      	//执行命令
        	String command = ProjectConfiguration.Defects4jBinDir+"/defects4j"+" "+d4jsCommand; 
        	String[] aryCommand = {"/bin/bash","-c",command+" >& "+redirectFilename};
        	process = Runtime.getRuntime().exec(aryCommand);
			//用缓冲器读行    
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String strLine=null;    
            //直到读完为止    
            while((strLine=br.readLine())!=null)    
            {    
        		System.out.println(strLine);
            }    
	        br.close();
	        returnCode = process.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return returnCode;    
	}
	

	/**  执行Defects4J command , and write result to file : filename.
	 * @param d4jsCommand  d4j命令
	 * @param storeResult 是否保存d4j命令执行的结果到文件
	 * @param resultFilename 存储d4j命令执行的结果 d4j需要的执行结果文件名，不要加入目录信息。
	 * @return
	 */
	private static int runD4jsCommandStore(String d4jsCommand,boolean storeResult,String resultFilename)
	{
		Process process;
        BufferedReader br;
        int returnCode = -1;
    	FileWriter writer = null;
		BufferedWriter bw = null;
		
		File resultFile = null;
		if( storeResult )
			resultFile = new File(ProjectConfiguration.DatasetWorkDirectory+"/"+resultFilename);
        try {
  	      	//执行命令
        	String command = ProjectConfiguration.Defects4jBinDir+"/defects4j"+" "+d4jsCommand; 
    		process = Runtime.getRuntime().exec(command);
			if( storeResult )
			{//store result ro file.
				if( resultFile.exists() )
					resultFile.delete();  //delete old file.
				resultFile.createNewFile();
				writer = new FileWriter(resultFile, true);
				bw = new BufferedWriter(writer);
			}	
			//用缓冲器读行    
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String strLine=null;    
            //直到读完为止    
            while((strLine=br.readLine())!=null)    
            {    
            	if( storeResult )
            	{
            		bw.write(strLine+"\n");
            		bw.flush();
            	}
            	else
            		System.out.println(strLine);
            }    
	        br.close();
	        returnCode = process.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally 
		{
        	if( storeResult )
        	{
        		FileToolkit.CloseFile(bw);
        		FileToolkit.CloseFile(writer);
        	}
		}
        return returnCode;    
	}
	
	/** get project's bug work directory
	 * @param object object name
	 * @return
	 */
	public static String getBuggyWorkDir(String object)
	{
		return ProjectConfiguration.DatasetWorkDirectory+"/"+object+"_buggy";
	}

	/** get project's fixed work directory
	 * @param object object name
	 * @return
	 */
	public static String getFixedWorkDir(String object)
	{
		return ProjectConfiguration.DatasetWorkDirectory+"/"+object+"_fixed";
	}

	/**运行defects4j命令后，检查其运行结果文件command_result.tmp。
	 * 文件中checkItem项的结果是ok，说明该项命令运行结果达到要求。
	 * 如果找不到checkItem，或者找到后结果并非ok，则说明有问题。
	 * @param checkItem   检查项
	 * @return
	 */
	public static boolean checkCommandResult(String checkItem)
	{
		boolean result = false;
		String filename = ProjectConfiguration.CommandResultTmpFilename;
		try {
			File file = new File(filename);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
				BufferedReader br = new BufferedReader(read);
				String lineTXT = null;
				while ((lineTXT = br.readLine()) != null) 
				{
					if( lineTXT.startsWith(checkItem)==false )
						continue;
					if( lineTXT.endsWith("OK") ) //is OK not ok. 
					{//文件中checkItem项的结果是ok，说明该项命令运行结果达到要求。
						result = true;
						break;
					}
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
}


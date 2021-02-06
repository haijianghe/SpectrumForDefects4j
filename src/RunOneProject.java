import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.List;

import common.CoverageXMLParse;
import common.FileToolkit;
import common.LoggerFileConsole;
import common.MatrixOfClasses;
import common.MiscTools;
import common.ProfileFile;
import common.ProjectConfiguration;
import defects4j.D4jBuildProperties;
import defects4j.D4jInstrumentClass;
import defects4j.D4jNullLineClass;
import defects4j.D4jsAllTestCases;
import defects4j.D4jsBugIds;
import defects4j.D4jsClassModifyRelevant;
import defects4j.D4jsTestCase;

/**
 * 
 */

/**针对某个project，执行defects4j，收集数据，最终产生profile
 * @author ccsu
 *
 */
/**
 * @author ccsu
 *
 */
public class RunOneProject {
	private String projectName; //project id of defects4j
	private LoggerFileConsole fcLogger; //log info
	/**
	 * @param projectName
	 */
	public RunOneProject(String projectName) {
		//super();
		this.projectName = projectName;
		fcLogger = new LoggerFileConsole();
	}
	
	
	/**
	 * 针对某个project，执行defects4j，收集数据，最终产生profile
	 */
	/**
	 * 
	 */
	public void run()
	{
		fcLogger.setup(projectName);
		fcLogger.setClassName(RunOneProject.class);
		SimpleDateFormat dfStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		fcLogger.promptLN("Task is start."+dfStart.format(System.currentTimeMillis()));
		//show 项目的所有活动bug id，并将之读入列表。
		if( !bidsBugIdList() )
			return;
		List<Integer> bugIdList = D4jsBugIds.getBugIdList();
		int bugRunNumber = 0;
		
		for( int bugid : bugIdList )
		{
			bugRunNumber++; //生成文件XXX_bugid.verno，便于记载bugid和verno的对应关系。
			//if( bugRunNumber<=160 )//for test
				//continue;//for test
			if( bugid<=160 )//for test
				continue;//for test
			fcLogger.promptLN("***"+projectName+":"+bugid+" is start====>>>");
			if( !processBugidOrderFile(bugid,bugRunNumber) )
				break;
			if( !checkOutReadBuildInfo(bugid) )
				break;
			if( !compileTestReadPassFailing() )
				break;
			if( !compareClassModifyRelevant() )
				break;
			if( !mkdirSpectrum(bugid) )
				break;
			if( !processTestCaseFile(bugid) )
				break;
			//D4jBuildProperties.readPropertyFile(projectName);//####!!!!for test
			D4jInstrumentClass.run(projectName);
			if( !processSourceCode(bugid) )
				break;
			if( !executeTestSuitCoverage(bugid) )
				break;
			fcLogger.promptLN("*** bugid="+bugid+" is end. ***");
			//break; //for test.
		}
		//task is over
		fcLogger.down();
		SimpleDateFormat dfEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		fcLogger.promptLN("Task is over."+dfEnd.format(System.currentTimeMillis()));
	}
	
	/** 将来的科研任务，考察源代码对软件错误定位的影响。
	 * @param bugid
	 * @return
	 */
	private boolean processSourceCode(int bugid)
	{
		boolean result = true;
		List<String> buggyRelevantClass = D4jInstrumentClass.getInstrumentClass();
		for( String clazz : buggyRelevantClass )
			if( shellCopyFile(clazz,bugid,"buggy")==false )
			{
				result = false;
				break;
			}
		if( false==result )
		{
			fcLogger.promptLN("Copy file (buggy) is error.");
			return false;
		}
		List<String> fixedRelevantClass = D4jBuildProperties.getModifiedClassesList();
		for( String clazz : fixedRelevantClass )
			if( shellCopyFile(clazz,bugid,"fixed")==false )
			{
				result = false;
				break;
			}
		if( result )
			fcLogger.promptLN("Copy file  is ok.");
		else
			fcLogger.promptLN("Copy file (fixed) is error.");
		return result;
	}
	
	/** copy file by shell command
	 * @param clazz class name with package name
	 * @param bugid bug id
	 * @param version  version=buggy or fixed
	 * @return
	 */
	private boolean shellCopyFile(String clazz,int bugid,String version)
	{
		boolean result = true;
		//int dot = clazz.lastIndexOf('.');
		//String javaFile = clazz.substring(dot+1,clazz.length())+".java";
		String filename = clazz.replace('.', '/')+".java";
		String shellCommand = "cp "+ProjectConfiguration.DatasetWorkDirectory+"/"+projectName+"_"+version +
				"/"+D4jBuildProperties.getDirSource()+"/"+filename+"  " +
				ProjectConfiguration.MyWorkDir+"/"+projectName+"/SourceCode"+"/v"+bugid+"/"+version;
		int rtnCode = MiscTools.runShellCommand(shellCommand);
		if( 0!= rtnCode)
		{
			result = false;
			fcLogger.promptLN("Shell command  cp "+clazz+ " is error.");
		}
		return result;
	}
	
	/**1，若文件(project).testcase不存在，则生成它。并且添加表头信息。
	 * 2，将当前bugid对应的测试用例情况，存储到文件。
	 * @return
	 */
	private boolean processTestCaseFile(int bugid)
	{
		boolean result = true;
		String filename = ProjectConfiguration.MyWorkDir+"/"+projectName+"/"+projectName+".testcase";
		try {
			File file = new File(filename);
			if ( !file.exists() ) { //文件不存在
					StringBuilder sb = new StringBuilder();
					sb.append("ver_numbers      ");
					sb.append( D4jsBugIds.getNumberOfBugId() );
					sb.append( "\n" );
					sb.append("testcase   0000\n");
					sb.append("version    passed    failed\n");
					FileToolkit.OutputToFile(file,sb.toString(),true);
			}
			if ( file.exists() && file.isFile() ) { //文件存在
				int tcFailed = D4jsAllTestCases.getNumberOfFailedTestcases();
				int tcPassed = D4jsAllTestCases.getNumberOfAllTestcases()-tcFailed;
				String strInfo = "      v"+bugid+"      "+tcPassed+"      "	+tcFailed+"\n";
				//String strInfo = "      v"+bugid+"      "+0+"      "+0+"\n"; //for test.
				FileToolkit.OutputToFile(file, strInfo, true);
			}//end of 文件存在
			else
				result = false;
		}//end of try. 
		catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	/**1，若文件(project)_order.bugid不存在，则生成它。并且添加表头信息。
	 * 2，将当前bugid对应的order，存储到文件。
	 * @return
	 */
	private boolean processBugidOrderFile(int bugid,int order)
	{
		boolean result = true;
		String filename = ProjectConfiguration.MyWorkDir+"/"+projectName+"/"+projectName+"_order.bugid";
		try {
			File file = new File(filename);
			if ( !file.exists() ) { //文件不存在
					FileToolkit.OutputToFile(file,"This file lists the corresponding relation between bugid and order(natural number serial). \n",true);
					FileToolkit.OutputToFile(file,"bugid     order\n",true);
			}
			if ( file.exists() && file.isFile() ) { //文件存在
				String strInfo = "    "+bugid+"      "+order+"\n";
				FileToolkit.OutputToFile(file, strInfo, true);
			}//end of 文件存在
			else
				result = false;
		}//end of try. 
		catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	/** 创建<project>,info,profile,v(xxx),buggy,fixed等文件夹。存储生成的谱数据及错误定位相关的.java文件
	 * @return
	 */
	private boolean mkdirSpectrum(int bugid)
	{
		String dirProject = ProjectConfiguration.MyWorkDir+"/"+projectName;
		if( !createDirectory(dirProject) )
			return false;
		String directory = dirProject+"/info";
		if( !createDirectory(directory) )
			return false;
		directory = dirProject+"/profile";
		if( !createDirectory(directory) )
			return false;
		directory = dirProject+"/SourceCode";
		if( !createDirectory(directory) )
			return false;
		String version = "/SourceCode"+"/v"+bugid;
		directory = dirProject + version;
		if( !createDirectory(directory) )
			return false;
		directory = dirProject+version+"/buggy";
		if( !createDirectory(directory) )
			return false;
		directory = dirProject+version+"/fixed";
		if( !createDirectory(directory) )
			return false;
		return true;
	}
		
	/** 创建当个目录
	 * @param directory  绝对路径名
	 * @return true, 文件夹创建成功或者该文件夹已经存在
	 */
	private boolean createDirectory(String directory)
	{
		boolean result = true;
		File file = new File(directory);
		if ( !file.exists() )
		{	//directory is not exist
			String shellCommand = "mkdir "+directory;
			int rtnCode = MiscTools.runShellCommand(shellCommand);
			if( 0!= rtnCode)
			{
				result = false;
				fcLogger.promptLN("Shell command  "+shellCommand+ " is error.");
			}
			else
				fcLogger.promptLN("Shell command  "+shellCommand+ " is OK.");
		}
		return result;
	}
	
	/** show 项目的所有活动bug id，并将之读入列表备用。
	 * defects4j bids -p project
	 */
	private boolean bidsBugIdList()
	{
		boolean result = false;
		String bidsFilename = projectName+".bids";
		int rtnCode = MiscTools.runD4jsCommand("bids -p "+projectName,bidsFilename);
		if( 0== rtnCode)
		{
			fcLogger.promptLN("Defects4j command (bids) is sucessful.");
			if( true== D4jsBugIds.readFile(bidsFilename) )
				result = true;
			else
				fcLogger.promptLN("Read file "+ bidsFilename+" is error.");
		}
		else
			fcLogger.promptLN("Defects4j command (bids) is error: ",rtnCode);
		return result;
	}
	
	/**check out 对应bug id的buggy和fixed version；并读入文件夹(objectName)_buggy下的文件defects4j.build.properties；
	 * 当下的检查：d4j.bug.id=  ,  d4j.project.id
	 * 后续的检查：d4j.classes.modified=  ,   d4j.classes.relevant=,   d4j.tests.trigger=
	 * @param bugid  active bug id
	 */
	private boolean checkOutReadBuildInfo(int bugid)
	{
		boolean result = false;
		//buggy version
		String d4jsCommand = "checkout -p "+projectName+" -v "+String.valueOf(bugid)+"b -w "+MiscTools.getBuggyWorkDir(projectName);
		int rtnCode = MiscTools.runD4jsRedirect(d4jsCommand);
		if( 0!= rtnCode)
		{
			fcLogger.promptLN("Defects4j command (checkout buggy) is error: ",rtnCode);
			return false;
		}
		if( false==MiscTools.checkCommandResult("Check out program version:") )
		{
			fcLogger.promptLN("Check out program version:  .....................false");
			return false;
		}
		fcLogger.promptLN("Defects4j command (checkout buggy) is sucessful.");
		//fixed version
		d4jsCommand = "checkout -p "+projectName+" -v "+String.valueOf(bugid)+"f -w "+MiscTools.getFixedWorkDir(projectName);
		rtnCode = MiscTools.runD4jsRedirect(d4jsCommand);
		if( 0!= rtnCode)
		{
			fcLogger.promptLN("Defects4j command (checkout fixed)  is error: ",rtnCode);
			return false;
		}
		if( false==MiscTools.checkCommandResult("Check out program version:") )
		{
			fcLogger.promptLN("Check out program version:  .....................false");
			return false;
		}	
		fcLogger.promptLN("Defects4j command (checkout fixed) is sucessful.");
		//buggy fixed :两个都执行成功，才继续下面的操作。
		if( D4jBuildProperties.readPropertyFile(projectName) )
		{
			if( D4jBuildProperties.checkProjectidBugid(projectName, bugid) )
			{
				result = true;
				fcLogger.promptLN("Project Id and bug id are same.");
			}
			else
				fcLogger.promptLN("Project Id is error or  bug id is not same.");
		}
		else
			fcLogger.promptLN("Read file defects4j.build.properties is error.");
		return result;
	}
	
	/**
	 * compile a checked-out project version.
	 * executes test on  checked-out project version and reports failing test cases. 
	 * read file "all_tests" and "failing_tests" 
	 */
	private boolean compileTestReadPassFailing()
	{
		boolean result = true;
		//buggy version compile
		String d4jsCommand = "compile -w "+MiscTools.getBuggyWorkDir(projectName);
		int rtnCode = MiscTools.runD4jsCommandEnvironment(d4jsCommand); //maybe compile is redundant
		if( 0!= rtnCode)
		{
			fcLogger.promptLN("Defects4j command (compile buggy) is error: ",rtnCode);
			return false;
		}
		fcLogger.promptLN("Defects4j command (compile buggy) is sucessful.");
		
		//buggy version test
		d4jsCommand = "test -r -w "+MiscTools.getBuggyWorkDir(projectName);
		String testsFilename = projectName+".FailingTests"; //文件名，没有目录信息。
		rtnCode = MiscTools.runD4jsCommandEnvironment(d4jsCommand,testsFilename);
		if( 0!= rtnCode)
		{
			fcLogger.promptLN("Defects4j command (test buggy)  is error: ",rtnCode);
			return false;
		}
		fcLogger.promptLN("Defects4j command (test buggy) is sucessful.");
		//compile test :两个都执行成功，才继续下面的操作。	
		if( D4jsAllTestCases.readD4jsCommandFile(testsFilename)==false )
		{
			fcLogger.promptLN("Read file "+testsFilename+"  is error.");
			return false;
		}

		fcLogger.promptLN("Read file "+testsFilename+"  is ok.");
		String failFilename = MiscTools.getBuggyWorkDir(projectName)+"/failing_tests";
		if( D4jsAllTestCases.readFailingTests(failFilename)==false )
		{
			fcLogger.promptLN("Read file "+failFilename+"  is error.");
			return false;
		}
		fcLogger.promptLN("Read file "+failFilename+"  is ok.");

		String allFilename = MiscTools.getBuggyWorkDir(projectName)+"/all_tests";
		if( D4jsAllTestCases.readAllTests(allFilename)==false )
		{
			fcLogger.promptLN("Read file "+allFilename+"  is error.");
			result = false;
		}
		else
			fcLogger.promptLN("Read file "+allFilename+"  is ok.");
		return result;
	}
	
	/**使用export -p classes.modified命令读出该版本被修改的类。
	 * 使用export -p classes.relevant命令读出该版本执行所有测试用例导入JVM相关的类。
	 * 将他们的结果与文件defects4j.build.properties比较，若有不同，则说明有环节出错。
	 */
	private boolean compareClassModifyRelevant()
	{
		boolean result = false;
		//被修改的类
		String modifyFilename = ProjectConfiguration.DatasetWorkDirectory+"/"+projectName+".classModified";
		String d4jsCommand = "export -p classes.modified -o "+ modifyFilename + " -w "+MiscTools.getBuggyWorkDir(projectName);
		int rtnCode = MiscTools.runD4jsCommand(d4jsCommand); 
		if( 0!= rtnCode)
		{
			fcLogger.promptLN("Defects4j command (export -p classes.modified) is error: ", rtnCode);
			return false;
		}
		fcLogger.promptLN("Defects4j command (export -p classes.modified) is sucessful.");
		if( D4jsClassModifyRelevant.readModifyClassFile(modifyFilename)==false )
		{
			fcLogger.promptLN("Read "+ modifyFilename+" is error.");
			return false;
		}
		
		//所有测试用例执行JVM装入的类
		String relevantFilename = ProjectConfiguration.DatasetWorkDirectory+"/"+projectName+".classRelevant";
		d4jsCommand = "export -p classes.relevant -o "+ relevantFilename + " -w "+MiscTools.getBuggyWorkDir(projectName);
		rtnCode = MiscTools.runD4jsCommand(d4jsCommand); 
		if( 0!= rtnCode)
		{
			fcLogger.promptLN("Defects4j command (export -p classes.relevant) is error: ",rtnCode);
			return false;
		}
		fcLogger.promptLN("Defects4j command (export -p classes.relevant) is sucessful.");
		if( D4jsClassModifyRelevant.readRelevantClassFile(relevantFilename)==false )
		{
			fcLogger.promptLN("Read "+ modifyFilename+" is error.");
			return false;
		}

		//读入文件defects4j.build.properties
		if( D4jBuildProperties.readPropertyFile(projectName)==false )
		{
			fcLogger.promptLN("Read defects4j.build.properties is error.");
			return false;
		}
		
		//与defects4j命令比较，结果一定要相同。
		if( D4jsClassModifyRelevant.isModifySameAs(D4jBuildProperties.getModifiedClassesList())==false  )
			fcLogger.promptLN("Compare classes.modified is error.");
		else
		{
			fcLogger.promptLN("Compare classes.modified is ok.");
			if( D4jsClassModifyRelevant.isRelevantSameAs(D4jBuildProperties.getRelevantClassesList())==false  )
				fcLogger.promptLN("Compare classes.relevant is error.");
			else
			{
				result = true;
				fcLogger.promptLN("Compare classes.relevant is ok.");
			}
		}
		return result;
	}
	
	//remove char from string
	private String removeChar(String strIn)
	{
		String strOut = strIn;
		int pos = strIn.indexOf(')');
		if( pos>1 )
			strOut = strIn.substring(0,pos-1)+strIn.substring(pos+1);
		return strOut;
	}
	/**在 bugid 的版本上执行所有测试用例
	 * @param bugid
	 * @return
	 */
	private boolean executeTestSuitCoverage(int bugid)
	{
		boolean result = true;
		int tcFailed = D4jsAllTestCases.getNumberOfFailedTestcases();
		int tcPassed = D4jsAllTestCases.getNumberOfAllTestcases()-tcFailed;
		ProfileFile  matrixProfile = new ProfileFile(bugid,projectName,tcPassed,tcFailed);
		List<D4jsTestCase> dtcLst = D4jsAllTestCases.getTestCases();
		int totalTcases = dtcLst.size(); //该bugid总共有多少测试用例。
		fcLogger.promptLN("The bugid="+bugid+",his total testcases = "+totalTcases+".");
		if( totalTcases>=2500 )
		{//花费时间太多，放弃了。
			fcLogger.promptLN("The bugid="+bugid+",his total testcases is too much, abandon it.");
			return true;
		}
		int totalFailtcases = D4jsAllTestCases.getNumberOfFailedTestcases(); 
		if( totalFailtcases<1 )
		{//There have not failed testcases。
			fcLogger.promptLN("The bugid="+bugid+",his failed testcases  = 0, abandon it.");
			return true;
		}
		if( (totalTcases-totalFailtcases)<1 )
		{//There have not passed testcases。
			fcLogger.promptLN("The bugid="+bugid+",his passed testcases  = 0, abandon it.");
			return true;
		}
		
		int addup = 0;//累计完成的测试用例个数。
		String nowTestClassMethod = null;//将记录导致我程序出错的测试用例
		System.out.print("Now, ....");
		
		for( D4jsTestCase tcD4js : dtcLst )
		{
			//输出当前正在执行的测试用例
			addup++;
			//if( addup<=58 )//for test
			//	continue;
			String testClassMethod = tcD4js.getTestClass()+"::"+tcD4js.getTestMethod();
			nowTestClassMethod = testClassMethod;
			System.out.print(" "+addup+"#"+" ");
			if( addup%40 == 0 )
				System.out.println("       ");
			//some testcase will cause error.
			//Collections bugid=25: org.apache.commons.collections4.iterators.IteratorChainTest)::testEmptyChain
			//if( projectName.equals("Collections") )
			//	testClassMethod = removeChar(testClassMethod);
			//开始测试和解析
			if( !executeAtestForCoverage(testClassMethod) )
			{
				System.out.println("Now,object = "+projectName+", method= "+testClassMethod+", #="+addup);
				result = false;
				break;
			}
			CoverageXMLParse cxParse = new CoverageXMLParse();
			if( !parseCoverageXMLFile(cxParse) )
			{
				result = false;
				break;
			}
			MatrixOfClasses clazzMatrix = cxParse.getClazzMatrix();
			matrixProfile.assembleFromParsedCoverage(clazzMatrix, tcD4js.getResult());
		}
		System.out.println("  ");
		if( result )
		{
			fcLogger.promptLN("All test cases have been run, coverage report and parse is ok. bugid=",bugid);
			String nullLineClasses = D4jNullLineClass.getNullLineClasses();
			fcLogger.writeLogFile(nullLineClasses);
			if( matrixProfile.writeProfileFile()==false )
				fcLogger.promptLN("Write to profile file is error.");
		}
		else
			fcLogger.promptLN("bugid="+bugid+"   ,testcase "+nowTestClassMethod+" is error.");
		return result;
	}
	
	/**executes all test  on a checked-out project version
	 * @param testClassMethod
	 */
	private boolean executeAtestForCoverage(String testClassMethod)
	{
		boolean result = false;
		
		String d4jsCommand = "coverage -w "+MiscTools.getBuggyWorkDir(projectName) + " -t "+testClassMethod+" -i "+ProjectConfiguration.InstrumentClassesFilename;
		int rtnCode = MiscTools.runD4jsRedirect(d4jsCommand);//ProjectConfiguration.CommandResultTmpFilename
		if( 0!= rtnCode)
			fcLogger.promptLN("Defects4j command (coverage) "+testClassMethod+" is error: ",rtnCode);
		else
		{
			if( MiscTools.checkCommandResult("Running ant (coverage.report)") )
				result = true;
			else
				fcLogger.promptLN("Running ant (coverage.report) "+testClassMethod+"  .....................false");
		}
		return result;
	}
	
	/**
	 * parse file "coverage.xml"
	 */
	private  boolean parseCoverageXMLFile(CoverageXMLParse cxParse)
	{
		boolean result = true;
		
		String filename = MiscTools.getBuggyWorkDir(projectName)+"/"+ProjectConfiguration.CoverageXMLFilename;
		if( false==cxParse.parseFile(filename) )
		{
			result = false;
			fcLogger.promptLN("Parse XML file is fail.");
		}
		return result;
	}
}

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Scanner;

import common.CoverageXMLParse;
import common.MiscTools;
import common.ProfileFile;
import common.ProjectConfiguration;
import defects4j.D4jBuildProperties;
import defects4j.D4jInstrumentClass;
import defects4j.D4jsAllTestCases;
import defects4j.D4jsBugIds;
import defects4j.D4jsClassModifyRelevant;
import defects4j.D4jsProjectList;

/**
 * 
 */

/**
 * @author ccsu
 *Chart Cli Closure Codec Collections Compress Csv Gson
 *JacksonCore  JacksonDatabind  JacksonXml  Jsoup  JxPath
 *  Lang  Math Mockito  Time
 */
public class MainProcess {
	//已经完成的：Chart,Cli,Closure,Codec,Collections,Compress, Csv,Gson
	//          JacksonCore, JacksonXml,Jsoup, JxPath Lang,Time,Math,JacksonDatabind
	private static String objectName = "Closure"; //defects4j 2.0 have 17 object.   Mockito
	//protected static final Logger LOGGER  = Logger.getLogger(MainProcess.class.toString());
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int bugid = 5; //bug id

		System.out.println("1, get the project id list of defects4j.");
		System.out.println("2, get the list of active  bug IDs for a specific project.");
		System.out.println("3, for one bug id check out him and read file defects4j.build.properties.");
    	System.out.println("4, compile,execute all  developer-written tests,read failing_tests.");
    	System.out.println("5, export -p classes.modified  && export -p classes.relevant.");
    	System.out.println("6, executes a test on a checked-out project version and measures statement coverage.");
    	System.out.println("7, parse coverage.xml.");
    	System.out.println("8, run all bugid,collect information and generate profile.");
    	System.out.println("15, check profile can or not read.");
    	System.out.println("\r\n Others, exit............ .");
    	System.out.println("Please key your choice.");
    	Scanner sc=new Scanner(System.in);
    	int choice = sc.nextInt();
    	sc.close();
    	switch( choice )
    	{
    	case 1:  //pids 
    		pidsProjectList();
    		break;
    	case 2:  //bids -p 
    		bidsBugIdList();
    		break;
    	case 3: //check out -p XXX -v XXX -w ...  and read file defects4j.build.properties.
    		checkOutReadBuildInfo(bugid);
    		break;
    	case 4: //compile, test ,read file, ....
    		compileTestReadPassFailing();
    		break;
    	case 5: //export -p classes.modified  export -p classes.relevant
    		compareClassModifyRelevant();
    		break;
    	case 6:
    		executeAtestForCoverage("org.jfree.data.xy.junit.XYSeriesTests::testBug1955483");//this is for Chart-bug 5
    		break;
    	case 7:
    		parseCoverageXMLFile();
    		break;
		case 8:
			RunOneProject rop = new RunOneProject(objectName);
			rop.run();
			break;
		case 15:
			checkReadProfile(1);
		default:
			break;
    	}//end of switch
    	System.out.println("The task about (####"+objectName+"####)is over.");
	}
	
	
	/**
	 * 显示defects4j所有的项目（对象）。
	 */
	private static void pidsProjectList()
	{
		String pidsFilename = "defects4j.pids";
		int rtnCode = MiscTools.runD4jsCommand("pids ",pidsFilename);
		if( 0== rtnCode)
		{
			System.out.println("Defects4j command (pids) is sucessful.");
			if( true== D4jsProjectList.readFile(pidsFilename) )
				D4jsProjectList.showProjectIdList();
			else
				System.out.println("Read file "+ pidsFilename+" is error.");
		}
		else
			System.out.println("Defects4j command (pids) is error: "+ rtnCode+".");
		
	}
	
	/** 显示项目的所有活动bug id，并将之读入列表备用。
	 * defects4j bids -p project
	 */
	private static void bidsBugIdList()
	{
		String bidsFilename = objectName+".bids";
		int rtnCode = MiscTools.runD4jsCommand("bids -p "+objectName,bidsFilename);
		if( 0== rtnCode)
		{
			System.out.println("Defects4j command (bids) is sucessful.");
			if( true== D4jsBugIds.readFile(bidsFilename) )
				D4jsBugIds.showBugIdList();
			else
				System.out.println("Read file "+ bidsFilename+" is error.");
		}
		else
			System.out.println("Defects4j command (bids) is error: "+ rtnCode+".");
	}
	
	/**check out 对应bug id的buggy和fixed version；并读入文件夹(objectName)_buggy下的文件defects4j.build.properties；
	 * 当下的检查：d4j.bug.id=  ,  d4j.project.id
	 * 后续的检查：d4j.classes.modified=  ,   d4j.classes.relevant=,   d4j.tests.trigger=
	 * @param bugid  active bug id
	 */
	private static void checkOutReadBuildInfo(int bugid)
	{
		//buggy version
		String d4jsCommand = "checkout -p "+objectName+" -v "+String.valueOf(bugid)+"b -w "+MiscTools.getBuggyWorkDir(objectName);
		int rtnCode = MiscTools.runD4jsRedirect(d4jsCommand);
		if( 0!= rtnCode)
		{
			System.out.println("Defects4j command (checkout buggy) is error: "+ rtnCode+".");
			return;
		}
		if( false==MiscTools.checkCommandResult("Check out program version:") )
		{
			System.out.println("Check out program version:  .....................false");
			return;
		}
		System.out.println("Defects4j command (checkout buggy) is sucessful.");
		//fixed version
		d4jsCommand = "checkout -p "+objectName+" -v "+String.valueOf(bugid)+"f -w "+MiscTools.getFixedWorkDir(objectName);
		rtnCode = MiscTools.runD4jsRedirect(d4jsCommand);
		if( 0!= rtnCode)
		{
			System.out.println("Defects4j command (checkout fixed)  is error: "+ rtnCode+".");
			return;
		}
		if( false==MiscTools.checkCommandResult("Check out program version:") )
		{
			System.out.println("Check out program version:  .....................false");
			return;
		}	
		System.out.println("Defects4j command (checkout fixed) is sucessful.");
		//buggy fixed :两个都执行成功，才继续下面的操作。
		if( D4jBuildProperties.readPropertyFile(objectName) )
		{
			if( D4jBuildProperties.checkProjectidBugid(objectName, bugid) )
				System.out.println("Project Id and bug id are same.");
			else
				System.out.println("Project Id is error or  bug id is not same.");
		}
		else
			System.out.println("Read file defects4j.build.properties is error.");
	}
	
	/**
	 * compile a checked-out project version.
	 * executes test on  checked-out project version and reports failing test cases. 
	 * read file "all_tests" and "failing_tests" 
	 */
	private static void compileTestReadPassFailing()
	{
		//buggy version compile
		String d4jsCommand = "compile -w "+MiscTools.getBuggyWorkDir(objectName);
		int rtnCode = MiscTools.runD4jsCommandEnvironment(d4jsCommand); //maybe compile is redundant
		if( 0!= rtnCode)
		{
			System.out.println("Defects4j command (compile buggy) is error: "+ rtnCode+".");
			return;
		}
		System.out.println("Defects4j command (compile buggy) is sucessful.");
		
		//buggy version test
		d4jsCommand = "test -r -w "+MiscTools.getBuggyWorkDir(objectName);
		String testsFilename = objectName+".FailingTests"; //文件名，没有目录信息。
		rtnCode = MiscTools.runD4jsCommandEnvironment(d4jsCommand,testsFilename);
		//rtnCode = MiscTools.runD4jsCommandEnvironmentRedirect(d4jsCommand,testsFilename);
		if( 0!= rtnCode)
		{
			System.out.println("Defects4j command (test buggy)  is error: "+ rtnCode+".");
			return;
		}
		System.out.println("Defects4j command (test buggy) is sucessful.");
		//compile test :两个都执行成功，才继续下面的操作。	
		if( D4jsAllTestCases.readD4jsCommandFile(testsFilename)==false )
		{
			System.out.println("Read file "+testsFilename+"  is error.");
			return;
		}

		System.out.println("Read file "+testsFilename+"  is ok.");
		String failFilename = MiscTools.getBuggyWorkDir(objectName)+"/failing_tests";
		if( D4jsAllTestCases.readFailingTests(failFilename)==false )
		{
			System.out.println("Read file "+failFilename+"  is error.");
			return;
		}
		System.out.println("Read file "+failFilename+"  is ok.");

		String allFilename = MiscTools.getBuggyWorkDir(objectName)+"/all_tests";
		if( D4jsAllTestCases.readAllTests(allFilename)==false )
		{
			System.out.println("Read file "+allFilename+"  is error.");
			return;
		}
		System.out.println("Read file "+allFilename+"  is ok.");
	}
	
	/**使用export -p classes.modified命令读出该版本被修改的类。
	 * 使用export -p classes.relevant命令读出该版本执行所有测试用例导入JVM相关的类。
	 * 将他们的结果与文件defects4j.build.properties比较，若有不同，则说明有环节出错。
	 */
	private static void compareClassModifyRelevant()
	{
		//被修改的类
		String modifyFilename = ProjectConfiguration.DatasetWorkDirectory+"/"+objectName+".classModified";
		String d4jsCommand = "export -p classes.modified -o "+ modifyFilename + " -w "+MiscTools.getBuggyWorkDir(objectName);
		int rtnCode = MiscTools.runD4jsCommand(d4jsCommand); 
		if( 0!= rtnCode)
		{
			System.out.println("Defects4j command (export -p classes.modified) is error: "+ rtnCode+".");
			return;
		}
		System.out.println("Defects4j command (export -p classes.modified) is sucessful.");
		if( D4jsClassModifyRelevant.readModifyClassFile(modifyFilename)==false )
		{
			System.out.println("Read "+ modifyFilename+" is error.");
			return;
		}
		
		//所有测试用例执行JVM装入的类
		String relevantFilename = ProjectConfiguration.DatasetWorkDirectory+"/"+objectName+".classRelevant";
		d4jsCommand = "export -p classes.relevant -o "+ relevantFilename + " -w "+MiscTools.getBuggyWorkDir(objectName);
		rtnCode = MiscTools.runD4jsCommand(d4jsCommand); 
		if( 0!= rtnCode)
		{
			System.out.println("Defects4j command (export -p classes.relevant) is error: "+ rtnCode+".");
			return;
		}
		System.out.println("Defects4j command (export -p classes.relevant) is sucessful.");
		if( D4jsClassModifyRelevant.readRelevantClassFile(relevantFilename)==false )
		{
			System.out.println("Read "+ modifyFilename+" is error.");
			return;
		}

		//读入文件defects4j.build.properties
		if( D4jBuildProperties.readPropertyFile(objectName)==false )
		{
			System.out.println("Read defects4j.build.properties is error.");
			return;
		}
		
		//与defects4j命令比较，结果一定要相同。
		if( D4jsClassModifyRelevant.isModifySameAs(D4jBuildProperties.getModifiedClassesList())==false  )
			System.out.println("Compare classes.modified is error.");
		else
			System.out.println("Compare classes.modified is ok.");
		
		if( D4jsClassModifyRelevant.isRelevantSameAs(D4jBuildProperties.getRelevantClassesList())==false  )
			System.out.println("Compare classes.relevant is error.");
		else
			System.out.println("Compare classes.relevant is ok.");
	}
	//defects4j query -p Chart -q "bug.id,report.id,classes.modified,classes.relevant.src" -o Chart.query
	//defects4j checkout -p Chart -v 1b -w /media/ccsu/workspace/Defects4j/sbfldata/Chart_buggy
	//defects4j checkout -p Chart -v 1f -w /media/ccsu/workspace/Defects4j/sbfldata/Chart_fixed
	//defects4j test -r -w ./Chart_buggy  //运行完成后，会在工作文件夹下生成文件：all_tests(里面有所有测试类：测试方法) 和 failing_tests.
	//defects4j export -p tests.all  -w ./Chart_buggy
	//defects4j export -p classes.modified  -w ./Chart_buggy
	//defects4j export -p classes.relevant  -w ./Chart_buggy
	//defects4j coverage -w Chart_buggy -t org.jfree.chart.annotations.junit.XYBoxAnnotationTests::testDrawWithNullInfo -i instrument.classes
	/**executes a test  on a checked-out project version
	 * @param testClassMethod
	 */
	private static void executeAtestForCoverage(String testClassMethod)
	{
		String d4jsCommand = "coverage -w "+MiscTools.getBuggyWorkDir(objectName) + " -t "+testClassMethod+" -i "+ProjectConfiguration.InstrumentClassesFilename;
		int rtnCode = MiscTools.runD4jsRedirect(d4jsCommand);//ProjectConfiguration.CommandResultTmpFilename
		if( 0!= rtnCode)
		{
			System.out.println("Defects4j command (coverage) is error: "+ rtnCode+".");
			return;
		}
		if( false==MiscTools.checkCommandResult("Running ant (coverage.report)") )
		{
			System.out.println("Running ant (coverage.report)  .....................false");
			return;
		}
		System.out.println("Defects4j command (coverage) is sucessful.");
	}
	
	/**
	 * parse file "coverage.xml"
	 */
	private static void parseCoverageXMLFile()
	{
		String filename = MiscTools.getBuggyWorkDir(objectName)+"/"+ProjectConfiguration.CoverageXMLFilename;
		CoverageXMLParse cxParse = new CoverageXMLParse();
		if( cxParse.parseFile(filename) )
		{
			System.out.println("Parse XML file is ok.");
			cxParse.testMe();
		}
		else
			System.out.println("Parse XML file is fail.");
	}
	
	/** can or not read profile file.
	 * @param bugid
	 */
	private static void checkReadProfile(int bugid)
	{
		ProfileFile profile = new ProfileFile(objectName,bugid);
		profile.readProfileFile();
	}
}

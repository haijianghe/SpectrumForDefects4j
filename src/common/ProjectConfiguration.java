/**
 * 
 */
package common;

/**
 * @author ccsu
 * progject configuration. 
 */
public final class ProjectConfiguration {
	public static final String DatasetWorkDirectory = "/media/ccsu/workspace/Defects4j/sbfldata";   //object' work directory。
	public static final String MyWorkDir = "/media/ccsu/workspace/eclipseWorkspace/SpectrumForDefects4j";//this project work directory。
	public static final String Defects4jBinDir = "/media/ccsu/workspace/Defects4j/defects4j/framework/bin";//defects4j command directory。
	public static final String CommandResultTmpFilename = DatasetWorkDirectory+"/command_result.tmp"; //temp filename for d4j command result
	public static final String InstrumentClassesFilename = DatasetWorkDirectory+"/instrument.classes"; //instrument classes file in which all classes will be monitored.
	public static final String Defects4jEnviromentSH = MyWorkDir+"/defects4j_enviroment.sh"; //defects4j enviroment shell file
	public static final String CoverageXMLFilename = "coverage.xml";  
}

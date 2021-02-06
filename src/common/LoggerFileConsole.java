/**
 * 
 */
package common;

import java.io.File;

/**
 * @author ccsu
 *
 */
public final class LoggerFileConsole {
	private String  m_strClassInfo; //which class output info.
	private File logFile = null;
	
	//don't instance it.
	public  LoggerFileConsole()
	{
		m_strClassInfo = "():";
	}
	
	//setup,initialize
	public void setup(String objectName)
	{
		logFile = new File(ProjectConfiguration.MyWorkDir+"/"+objectName+".log");
		if( logFile.exists() )
			logFile.delete();  //delete old file.
	}

	//free resource.
	public void down()
	{
	}
	
	
	//set class name, which will be head of info.
	public void setClassName(Class<?> obj)
	{
		m_strClassInfo = obj.getTypeName() + ":  ";
	}
	
	//output to console, and write info to log file.
	public void writeLogConsole(String strInfo)
	{
		System.out.print(strInfo);
		FileToolkit.OutputToFile(logFile, strInfo, true);
	}
	
	// write info to log file.
	public void writeLogFile(String strInfo)
	{
		//System.out.print(strInfo);
		FileToolkit.OutputToFile(logFile, strInfo, true);
	}
	
	//Console  strInfo, new line 
	public void promptLN(String strInfo)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(m_strClassInfo);
		sb.append( strInfo );
		sb.append( "\n" );
		writeLogConsole(sb.toString());
	}
	
	//Console  strInfo, 
	public void prompt(String strInfo)
	{
		writeLogConsole("  "+strInfo);
	}
	
	//Console  strInfo , new line 
	public void promptLN(String strInfo, int number)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(m_strClassInfo);
		sb.append( strInfo +",");
		sb.append( number );
		sb.append( "\n" );
		writeLogConsole(sb.toString());
	}

	//Console  strInfo , new line 
	public void prompt(String strInfo, int number)
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "  "+strInfo +",");
		sb.append( number );
		writeLogConsole(sb.toString());
	}
	
}

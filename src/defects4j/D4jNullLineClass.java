/**
 * 
 */
package defects4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ccsu
 *文件里coverage.xml有该类，但是没有一条语句被覆盖，所以找不到语句信息。据观察，这些类基本上是接口。
 *记录这些类，进入监视文件instrument.classes，但是，它们并没有可执行语句。
 */
public class D4jNullLineClass {
	private static List<String> interfaceClasses = new ArrayList<String>();
	
	
	/**
	 * @param addClass 该类，没有语句进入覆盖数据
	 */
	public static void addOneClass(String addClass)
	{
		boolean found = false;
		for( String clazz : interfaceClasses )
			if( clazz.equals(addClass) )
			{
				found = true;
				break;
			}
		if( !found )
			interfaceClasses.add(addClass);
	}
	
	//write interfaceClasses to file.
	public static String getNullLineClasses()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("These classes cannot record line.\n");
		for( String instrument : interfaceClasses )
			sb.append(instrument+"\n");
		//保存完后一定要清空，免得累加。
		interfaceClasses.clear();
		return sb.toString();
	}
}

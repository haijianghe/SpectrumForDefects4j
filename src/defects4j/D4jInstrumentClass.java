/**
 * 产生应该获取程序谱的类，优先级如下：
1，首先获得被修改的类。
2，检查JVM载入相关类，若少于20个，则全部进入instrument; 跳过后面步骤，结束。
3, 若多于20个，则优先载入那些既是JVM相关类，又与被修改类处于同一文件夹下的。
4，如果满足第三条的超过20个，则随机取20个；跳过后面步骤，结束。
    如果不超过20个，全部进入instrument;
5，再在JVM相关类中随机取，直到满足20个。
 */
package defects4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import common.FileToolkit;
import common.ProjectConfiguration;

/** 优先级顺序： 被修改类，相关类并且与被修改类同一位置，相关类
 * @author ccsu
 * 产生应该获取程序谱的类，
 */
public class D4jInstrumentClass {
	private static List<String> clazzInstrument;//应该获取程序谱的类
	//文件里coverage.xml有类，但是没有一条语句被覆盖。据观察，这些类基本上是接口。
	private static int NumberOfCoverage = 25;   //原来是20，考虑到许多接口，coverage.xml不会有覆盖数据，改为25.
	
	public static List<String> getInstrumentClass()
	{
		return clazzInstrument;
	}
	
	/**
	 * @param projectName  defects4j project name.
	 * @return
	 */
	public static void run(String projectName)
	{
		clazzInstrument = new ArrayList<String>();
		List<String> modifiedClassesList = D4jBuildProperties.getModifiedClassesList();
		for( String clazz : modifiedClassesList)
			clazzInstrument.add(clazz);
		if(clazzInstrument.size()>=NumberOfCoverage )
		{//超过限制，结束。
			writeInstrumentFile();
			return;
		}
		List<String> relevantClassesList = D4jBuildProperties.getRelevantClassesList();
		List<String> excludeRelevant = excludeInstrumented(relevantClassesList);
		if( excludeRelevant.size()<=NumberOfCoverage )
		{//相关类不超过限制，结束。
			for( String clazz : excludeRelevant )
				clazzInstrument.add(clazz);
			writeInstrumentFile();
			return;
		}
		List<String> positionClassesList = getClassesListSameAsDirectoryOfModifyClass(excludeRelevant);
		if( positionClassesList.size()>=NumberOfCoverage )
		{
			int[] indexAry = getRandomoList(NumberOfCoverage,positionClassesList.size());
			for( int i=0;i<NumberOfCoverage;i++ )
				clazzInstrument.add(positionClassesList.get(indexAry[i]-1));
		}
		else
		{//不超过个数限制， 将这些类全部加入后，再在相关类中随机选取。
			for( String clazz : positionClassesList )
				clazzInstrument.add(clazz);
			excludeRelevant = excludeInstrumented(relevantClassesList);
			int want = NumberOfCoverage-positionClassesList.size();
			int[] indexAry = getRandomoList(want,excludeRelevant.size());
			for( int i=0;i<want;i++ )
				clazzInstrument.add(excludeRelevant.get(indexAry[i]-1));
		}
		writeInstrumentFile();
	}
	
	/** 获取与被修改类同一位置并且属于JVM相关类的所有类，注意：被修改类可能有多个。
	 * @param relevantNotInstrument  相关类中去除掉被修改类后的队列
	 * @return
	 */
	private static List<String> getClassesListSameAsDirectoryOfModifyClass(List<String> relevantNotInstrument)
	{
		List<String> positionClazz = new ArrayList<String>();
		List<String> modifiedClassesList = D4jBuildProperties.getModifiedClassesList();
		for( String modify : modifiedClassesList)
		{
			int dot = modify.lastIndexOf('.');
			String modifyPath =  modify.substring(0, dot);
			for( String relevant : relevantNotInstrument)
			{
				dot = relevant.lastIndexOf('.');
				String relevantPath =  relevant.substring(0, dot);
				if( modifyPath.equals(relevantPath) )
					positionClazz.add(relevant);
			}
		}
		return positionClazz;
	}
	
	/** 排除掉已经在等待获取程序谱类（clazzInstrument）中的类
	 * @param preExclude 
	 * @return preExclude排除掉后产生的新集合
	 */
	private static List<String> excludeInstrumented(List<String> preExclude)
	{
		List<String> postExclude = new ArrayList<String>();
		for( String clazz : preExclude )
		{
			boolean found = false;
			for( String instrument : clazzInstrument )
				if( instrument.equals(clazz) )
				{
					found = true;
					break;
				}
			if( !found )
				postExclude.add(clazz);
		}
		return postExclude;
	}
	
	/**从1 到 maxValue中随机取number个不重复的数   调试通过
	 * @param number
	 * @param maxValue
	 * @return number个值，处于1 到 maxValue中
	 */
	private static int[] getRandomoList(int number,int maxValue)
	{
		 List<Integer> candidateList = new ArrayList<>();
		 int[] indexList = new int[number]; 
		 for(int i = 0; i < maxValue; i ++)
			 candidateList.add(i + 1);
		
		 for(int i = 0; i < number; i ++){
			 Random random = new Random();
			 //return an int that between 0 and maxValue, 0 included, maxValue not included
			 int r = random.nextInt(maxValue-i);
			 indexList[i] = candidateList.get(r);
			 candidateList.remove(r);
		 }
		 return indexList;
	}
	
	/**
	 * save classes to instrument.classes
	 */
	private static void writeInstrumentFile()
	{
		StringBuilder sb = new StringBuilder();
		for( String instrument : clazzInstrument )
			sb.append(instrument+"\n");
		FileToolkit.OutputToFile(ProjectConfiguration.InstrumentClassesFilename, sb.toString(), false);
	}
}

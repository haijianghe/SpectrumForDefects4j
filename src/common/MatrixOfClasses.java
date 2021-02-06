/**
 * 
 */
package common;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ccsu
 *
 */
public class MatrixOfClasses {
	private List<ClassLinesHit> spectrumList;
	
	public MatrixOfClasses()
	{
		spectrumList = new ArrayList<ClassLinesHit>(); 
	}
	
	public List<ClassLinesHit> getSpectrums()
	{
		return spectrumList;
	}
	
	/** 增加一个类的代码行覆盖数据到队列。
	 * @param clh
	 */
	public void addClassLinesHit(ClassLinesHit clh)
	{
		spectrumList.add(clh);
	}
}

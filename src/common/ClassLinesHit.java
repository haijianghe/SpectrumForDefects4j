/**
 * 
 */
package common;

import java.util.ArrayList;
import java.util.List;

/** 一个类的行覆盖数据
 * @author ccsu
 *
 */
public class ClassLinesHit {
	private String className;//类名
	private String classFilename; //类名对应的文件名 
	private List<LineHit> lineCodes;//该类包含的行覆盖数据,其lineNo已经排好序。
	
	/**  	空的构造函数
	 */
	public ClassLinesHit() {
		this.className = "";
		this.classFilename = "";
		this.lineCodes = new ArrayList<LineHit>();
	}

	//增加一个
	public void addLineHit(LineHit lh)
	{
		lineCodes.add(lh);
	}
	
	//增加多个
	public void addLineCodes(List<LineHit> lineLst) {
		for( LineHit lh : lineLst )
			lineCodes.add(lh);
	}
	
	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return the classFilename
	 */
	public String getClassFilename() {
		return classFilename;
	}

	/**
	 * @return the lineCodes
	 */
	public List<LineHit> getLineCodes() {
		return lineCodes;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @param classFilename the classFilename to set
	 */
	public void setClassFilename(String classFilename) {
		this.classFilename = classFilename;
	}

	/**
	 * @param lineCodes the lineCodes to set
	 */
	public void setLineCodes(List<LineHit> lineCodes) {
		this.lineCodes = lineCodes;
	}
	
}

/**
 * 
 */
package common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ccsu
 *
 */
public class FileSpectrum {
	private String className;//类名
	private String classFilename; //类名对应的文件名 
	private List<SpectrumStruct> lineCodes;//该类包含的行覆盖数据。
	//private int execTotal; //该文件代码，可执行语句条数

	/**  	空的构造函数
	 */
	public FileSpectrum() {
		this.className = "";
		this.classFilename = "";
		//execTotal = 0;
		this.lineCodes = new ArrayList<SpectrumStruct>();
	}

	//增加一个
	public void addLineSpectrum(SpectrumStruct ss)
	{
		lineCodes.add(ss);
	}
	
	//增加多个
	public void addLineCodes(List<SpectrumStruct> lineLst) {
		for( SpectrumStruct lh : lineLst )
			lineCodes.add(lh);
	}
	
	/**
	 * @param lineno 可执行语句的行号
	 * @param hit  coverage.xml 覆盖次数
	 * @param passed 当前测试用例是否成功,true=通过
	 */
	public void assembleLineSpectrum(int lineno,int hit,boolean passed)
	{
		boolean found = false;
		for( SpectrumStruct ss : lineCodes )
		{
			if( ss.getLineNo()==lineno )
			{
				found = true;
				ss.incrementHit(hit, passed);
				break;
			}
		}
		if( !found )
		{//此处要慎重，为什么会出现以前没有的行号啊。
			SpectrumStruct adds = new SpectrumStruct(lineno,0,0);
			adds.incrementHit(hit, passed);
			lineCodes.add(adds);
			System.out.println("FileSpectrum: why?????????????????new lineno???????????????????????????????????");
		}
	}
	
	//该文件，可执行语句条数
	public int getTotalExec()
	{
		//return execTotal;
		return lineCodes.size();
	}
	
	//读入该文件的程序谱
	public void readFile(DataInputStream dis) throws IOException
	{
		//读入字符串
	    int len = dis.readInt();
	    byte []buf = new byte[len];
	    dis.read(buf);
	    className = new String(buf);   
	    //读入字符串
	    len = dis.readInt();
	    buf = new byte[len];
	    dis.read(buf);
	    classFilename = new String(buf);   
	    int totalLines = dis.readInt();
	    for(int k=0;k<totalLines;k++ )
        {
    		int lineno = dis.readInt();
    		int aep = dis.readInt();
    		int aef = dis.readInt();
    		SpectrumStruct item  = new SpectrumStruct(lineno,aep,aef);
    		lineCodes.add(item);
        }
	}
	 
    
	//保存该文件的程序谱
	public void writeFile(DataOutputStream dos) throws IOException
	{
        //写入字符串
		dos.writeInt(className.length());
		dos.writeBytes(className);
		dos.writeInt(classFilename.length());
		dos.writeBytes(classFilename);
		dos.writeInt(lineCodes.size());//该类包含的行个数。
		for( SpectrumStruct ss : lineCodes )
		{
			dos.writeInt(ss.getLineNo());
			dos.writeInt(ss.getAep());
			dos.writeInt(ss.getAef());
		}
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
	public List<SpectrumStruct> getLineCodes() {
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
	public void setLineCodes(List<SpectrumStruct> lineCodes) {
		this.lineCodes = lineCodes;
	}
}

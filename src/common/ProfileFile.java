/**
 * 
 */
package common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import defects4j.D4jNullLineClass;

/**
 * @author ccsu
 * 注意：只保存单个.profile数据，并非保存所有版本的。
 */
public class ProfileFile {
	private int thVer;  //第几个版本，注意不是版本个数verNo。
	private int tcPassed; //测试用例通过数
	private int tcFailed; //测试用例未通过数
	private int execTotal; //该版本所有检测文件代码，可执行语句条数
	private List<FileSpectrum> spectrumList; //程序谱
	private String objectName; //对象名。
	
	//构造函数，数据从文件读入。for : readProfileFile
	public ProfileFile(String object,int ver)
	{
		thVer = ver;
		objectName = object;
		execTotal = 0;
		spectrumList = null;
	}
	
	//某一个版本的程序谱。
	public ProfileFile(int ver,String object,int passed,int failed)
	{
		thVer = ver;
		objectName = object;
		tcPassed = passed;
		tcFailed = failed;
		execTotal = 0;
		spectrumList = new ArrayList<FileSpectrum> ();
	}
	
	//注意ver=1,...verNo,不是版本个数verNo。
	public ProfileFile(String object,int ver,int passed,int failed,int total,
			      List<FileSpectrum> ssList)
	{
		objectName = object;
		thVer = ver;
		tcPassed = passed;
		tcFailed = failed;
		execTotal = total;
		spectrumList = ssList;
	}
	
	
	public int getVerTh()
	{
		return thVer;
	}
	
	public int getPassed()
	{
		return tcPassed;
	}
	
	public int getFailed()
	{
		return tcFailed;
	}
	
	//获取总的可执行语句数目
	public int getTotalExec()
	{
		if( execTotal<=0 )
			execTotal = calTotalExec();
		return execTotal;
	}

	//计算总的可执行语句数目
	private int calTotalExec()
	{
		int total = 0;
		for( FileSpectrum fsp : spectrumList )
			total += fsp.getTotalExec();
		execTotal = total;
		return total;
	}

	public List<FileSpectrum> getSpectrumList()
	{
		return spectrumList;
	}
	
	/**
	 * @param clazzMatrix  解析单个coverage.xml文件形成的覆盖数据
	 * @param passed 当前测试用例是否成功,true=通过
	 */
	public void assembleFromParsedCoverage(MatrixOfClasses clazzMatrix,boolean passed)
	{
		List<ClassLinesHit> cLinesList = clazzMatrix.getSpectrums();
		for( ClassLinesHit clHit : cLinesList)
		{
			if( clHit.getLineCodes().size()<=0 )
			{
				D4jNullLineClass.addOneClass(clHit.getClassName());
				continue; //文件里coverage.xml有该类，但是没有一条语句被覆盖，所以找不到语句信息。据观察，这些类基本上是接口。
			}
			classIsExist(clHit,passed);
		}
	}
	
	/**
	 * @param clHit
	 * @param passed
	 */
	private void classIsExist(ClassLinesHit clHit,boolean passed)
	{
		boolean exist = false;
		for( FileSpectrum fspect : spectrumList)
		{
			if( fspect.getClassName().equals(clHit.getClassName()) )
			{
				exist = true;
				List<LineHit> lineCodes = clHit.getLineCodes();
				for( LineHit lh : lineCodes )
					fspect.assembleLineSpectrum(lh.getLineNo(),lh.getHit(),passed);
				break;
			}
		}
		//第一次装配，现要求初始化队列等
		if( !exist )
		{
			FileSpectrum assembling = new FileSpectrum();
			assembling.setClassName(clHit.getClassName());
			assembling.setClassFilename(clHit.getClassFilename());
			List<LineHit> lineCodes = clHit.getLineCodes();
			for( LineHit lh : lineCodes )
			{
				SpectrumStruct ss = new SpectrumStruct(lh.getLineNo(),0,0);
				assembling.addLineSpectrum(ss);
				assembling.assembleLineSpectrum(lh.getLineNo(),lh.getHit(),passed);
			}
			spectrumList.add(assembling);
		}
	}
	
	//从.profile文件读入程序谱
	public boolean readProfileFile()
	{
		boolean result = true;
		
		spectrumList = new ArrayList<FileSpectrum> ();
		FileInputStream fis = null;
	    DataInputStream dis = null;
	    File file;
	    try {
	    	 String filename = ProjectConfiguration.MyWorkDir+"/"+objectName +"/profile/"+
						objectName+"_v"+String.valueOf(thVer)+".profile";
	        file = new File(filename);
	        if( file.isFile()&& file.exists() )
	        {
	        	fis = new FileInputStream(file);
	        	dis = new DataInputStream(fis);
	 	        //读入字符串
	 	        int len = dis.readInt();
	            byte []buf = new byte[len];
	            dis.read(buf);
	            objectName = new String(buf);
	            
	        	thVer = dis.readInt();
	        	tcPassed = dis.readInt();
	        	tcFailed = dis.readInt();
	        	execTotal = dis.readInt();
	        	int numberOfFiles = dis.readInt();//总的文件个数。
		        //逐个读入文件的谱信息。
	        	for(int k=0;k<numberOfFiles;k++ )
		        {
	        		FileSpectrum item = new FileSpectrum();
	        		item.readFile(dis);
	        		spectrumList.add(item);
		        }
	        }//end of if
	        else
	        	result = false;
	    }
	    catch (Exception e) {
	    	result = false;
	        e.printStackTrace();
	    } 
	    finally {
	        try {
	            if (fis != null) {
	            	dis.close();
	            	fis.close();
	            }
	        } 
	        catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
		return result;
	}
	
	
	/**  将程序谱写入.profile文件
	 * @return
	 */
	public boolean writeProfileFile()
	{
		boolean result = true;
		FileOutputStream fos = null;
	    DataOutputStream dos = null;
	    File file;
	    try {
	        String filename = ProjectConfiguration.MyWorkDir+"/"+objectName +"/profile/"+
								objectName+"_v"+String.valueOf(thVer)+".profile";
	        file = new File(filename);
	        if( file.isFile()&& file.exists() )
	        	file.delete();
	        file.createNewFile();
	        fos = new FileOutputStream(file);
	        dos = new DataOutputStream(fos);
	        //写入字符串
			dos.writeInt(objectName.length());
			dos.writeBytes(objectName);
	        //dos.writeUTF(objectName);
	        dos.writeInt(thVer);
	        dos.writeInt(tcPassed);
	        dos.writeInt(tcFailed);
	        dos.writeInt(calTotalExec());
	        dos.writeInt(spectrumList.size());//总的文件个数。
	        //逐条语句的谱信息写入。
	        for( FileSpectrum fspect : spectrumList )
	        {
        		fspect.writeFile(dos);
	        }
	    }
	    catch (Exception e) {
	    	result = false;
	        e.printStackTrace();
	    } 
	    finally {
	        try {
	            if (fos != null) {
	            	dos.close();
	            	fos.close();
	            }
	        } 
	        catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
		return result;
	}
}

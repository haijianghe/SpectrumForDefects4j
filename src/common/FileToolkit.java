/**
 * 
 */
package common;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ccsu
 *文件工具包  工具包 ： 处理文件和文件夹
 */
public final class FileToolkit {
	
	//复制文件。
	//sourceFilename和destFilename包括目录和文件名。
	public static void copyFileUsingFileChannels(String sourceFilename, String destFilename)throws IOException 
	{
		File fileSource = new File(sourceFilename);
		//先生成目标文件。
		File fileDest = new File(destFilename);
		if (!fileDest.getParentFile().exists()) {
			fileDest.getParentFile().mkdirs();
		}
		if (!fileDest.exists()) {
			fileDest.createNewFile();
		}
		
		FileInputStream finputStream = null;
		FileOutputStream foutputStream = null;
	    FileChannel inputChannel = null;
	    FileChannel outputChannel = null;
	    try {
	    	finputStream = new FileInputStream(fileSource);
	        inputChannel = finputStream.getChannel();
	        foutputStream = new FileOutputStream(fileDest);
	        outputChannel = foutputStream.getChannel();
	        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
	    } 
	    finally {
	        inputChannel.close();
	        outputChannel.close();
	    	finputStream.close();
	    	foutputStream.close();
	    }
	}
	
	//删除文件夹下所有内容
	public static void DeleteDirectory(String directory) 
	{
		File file = new File(directory);
		
		if (file.exists()) {
			if (file.isDirectory()) { //如果是文件夹
				File[] files = file.listFiles();
				if (files.length > 0) {
					for (File f : files) {
						if (f.isFile()) 
						{
							DeleteFile(f.getAbsolutePath());
						} 
						else 
						{
							DeleteDirectory(f.getAbsolutePath());
						}
					}//end of for
				}
				file.delete();
			} 
			else {
				DeleteFile(directory);
			}
		}
	}//end of deleteDirectory
	
	//删除单个文件或目录，fileName：文件名或者目录名字。
	public static void DeleteFile(String fileName) 
	{
		File file = new File(fileName);
		
		if (file.exists()) {
			if (file.isFile()) //文件
			{
				file.delete();
			} 
			else //目录，文件夹
			{
				DeleteDirectory(fileName);
			}
		} 
	}//end of deleteFile
	
	//删除directory下 的所有文件及目录（文件夹）
	public static void DeleteFiles(String directory) 
	{
		File file = new File(directory);
		
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				if (files.length > 0) {
					for (File f : files) {
						if (f.isFile()) {
							DeleteFile(f.getAbsolutePath());
						} 
						else {
							DeleteFiles(f.getAbsolutePath());
						}
					}//end of for
				}
			} 
			else {
				DeleteFile(directory);
			}//end of else
		}
	}//end of deleteFiles
	
	/**
	 * fileName指定文件名，读它的内容。
	 * 返回一个字符串，包含文件内的所有内容。
	 * 调用ReadFile(File file)
	 */
	public static String ReadFile(String fileName)
	{
		return ReadFile(new File(fileName));
	}
	
	/**
	 *  file指定File文件类，读它的内容。
	  *   返回一个字符串，包含文件内的所有内容。.
	 */
	public static String ReadFile(File file) 
	{
		byte[] input = null;
		BufferedInputStream bis = null;
		
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			input = new byte[bis.available()];
			bis.read(input);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			CloseFile(bis);
		}
		
		String sourceCode = null;
		if (input != null) 
		{
			sourceCode = new String(input);
		}
		
		return sourceCode;
	}
	
	//关闭文件，释放资源。
	//writer，FileWriter 类从 OutputStreamWriter 类继承而来。该类按字符向流中写入数据。
	public static void CloseFile(FileWriter writer) 
	{
		try {
			if (writer != null) 
			{
				writer.close();
				writer = null;
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	//关闭文件，释放资源。
	// bw,BufferedWriter类，将文本写入字符输出流，缓冲字符，以便有效地写入单个字符，数组和字符串
	public static void CloseFile(BufferedWriter bw) 
	{
		try {
			if (bw != null) {
				bw.close();
				bw = null;
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//关闭文件，释放资源。
	//BufferedInputStream字节缓冲输入流
	private static void CloseFile(BufferedInputStream bis) 
	{
		try {
			if (bis != null) {
				bis.close();
				bis = null;
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	/*将data存入指定文件名fileName
	 * 调用OutputToFile(String fileName, String data, boolean append)
	 * append=true，添加到文件尾部。 =false，覆盖原文件。
	 */
	public static void OutputToFile(String fileName, StringBuilder sbData, boolean append) 
	{
		OutputToFile(fileName, sbData.toString(), append);
	}
	
	/*将data存入指定File文件类
	 * 调用OutputToFile(File file, String strData, boolean append)
	 * append=true，添加到文件尾部。 =false，覆盖原文件。
	 */
	public static void OutputToFile(File file, StringBuilder sbData, boolean append) 
	{
		OutputToFile(file, sbData.toString(), append);
	}
	
	
	 /*将data存入指定文件名fileName
	  * 调用OutputToFile(File file, String strData, boolean append)
	  * append=true，添加到文件尾部。 =false，覆盖原文件。
	  */
	public static void OutputToFile(String fileName, String strData, boolean append) 
	{
		File file = new File(fileName);
		OutputToFile(file, strData, append);
	}
	
	/**              
	 * 将字符串存入一个文件.
	 * @param file, File文件类.
	 * @param data, 存入的数据.
	 * @param append, 是否在文件内已有内容的后面添加data。
	 */
	public static void OutputToFile(File file, String strData, boolean append) 
	{
		FileWriter writer = null;
		BufferedWriter bw = null;

		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			writer = new FileWriter(file, append);
			bw = new BufferedWriter(writer);
			bw.write(strData);
			bw.flush();
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		finally 
		{
			CloseFile(bw);
			CloseFile(writer);
		}
	}//end of  static void OutputToFile(File file, 
	
	/** 
	 * 调用了 List<File> ListAllFiles(File file, String type)
	 * filePath，字符串，指定一个文件夹
	 * 将该文件夹下所有type文件扩展名的文件以File类形式，装入List
	 */
	public static List<File> GetAllFiles(String filePath, String type) 
	{
		return ListAllFiles(new File(filePath), type);
	}
	
	/**
	  * 递归调用，列出指定File（文件夹）.
	 * 比如 file是 ../EclipseWorkspace/MLDebug, type是.java；则将该目录下所有的.java文件以File形式
	 *         装入List<File>
	 * @param 指定文件夹
	 * @return  List<File> 
	 */
	private static List<File> ListAllFiles(File file, String type) 
	{
		List<File> fileList = new ArrayList<>();
		
		if (!file.exists()) {
			return null;
		}
		
		File[] files = file.listFiles();
		
		for (File f : files) {
			if (f.isFile()) {
				if (f.toString().endsWith(type)) {
					fileList.add(f);
				}
			} 
			else {
				List<File> fl = ListAllFiles(f, type); //閫掑綊璋冪敤
				if (fl != null && fl.size() > 0) {
					fileList.addAll(fl);
				}
			}
		}//end of for
		
		return fileList;
	}
	
	//关闭文件，释放资源。
	public static void CloseFile(FileReader reader) 
	{
		try {
			if (reader != null) 
			{
				reader.close();
				reader = null;
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	//关闭文件，释放资源。
	public static void CloseFile(BufferedReader buf) 
	{
		try {
			if (buf != null) 
			{
				buf.close();
				buf = null;
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	/** 
	 * strFile1,strFile2,strFile3三个文件合并到strOutput
	 * 按照行合并，三个文件的第X行合并到strOutput的第X行
	 * 要求，三个文件的行数相同。
	 */
	public static void mergeFilesByLine(String strOutput, String strFile1,String strFile2,String strFile3) 
	{
		FileReader fRead1 = null,fRead2 = null,fRead3 = null;
		FileWriter fileWriter = null;			
		BufferedReader buffer1 = null,buffer2 = null,buffer3 = null;
		BufferedWriter  bufWriter = null;
		try {
			//read string from file.
			fRead1 = new FileReader(strFile1);			
			buffer1 = new BufferedReader(fRead1);			
			fRead2 = new FileReader(strFile2);			
			buffer2 = new BufferedReader(fRead2);			
			fRead3 = new FileReader(strFile3);			
			buffer3 = new BufferedReader(fRead3);			
			String str1,str2,str3; //分别读三个文件的某一行
			// write string to file
            fileWriter = new FileWriter(strOutput);
            bufWriter = new BufferedWriter(fileWriter);
			String strMerge;//合并后的内容，以行为单位。
			// 按行读取字符串	
			while ((str1 = buffer1.readLine()) != null) 
			{		
				str2 = buffer2.readLine();
				str3 = buffer3.readLine();
				strMerge = str1+" "+str2+" "+str3+"\n";
				bufWriter.write(strMerge);			
			}			
			//先打开FileReader，再打开BufferedReader；先关闭BufferedReader,再关闭FileReader
			buffer1.close(); //1			
			fRead1.close();
			buffer2.close(); //2			
			fRead2.close();
			buffer3.close(); //3			
			fRead3.close();
			bufWriter.close(); //writer
			fileWriter.close();
		} 
		
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			//先打开FileReader，再打开BufferedReader；先关闭BufferedReader,再关闭FileReader
			CloseFile(buffer1); //1			
			CloseFile(fRead1);
			CloseFile(buffer2); //2			
			CloseFile(fRead2);
			CloseFile(buffer3); //3			
			CloseFile(fRead3);
			CloseFile(bufWriter); //writer
			CloseFile(fileWriter);
		}
	}
}//end of class FileTookkit


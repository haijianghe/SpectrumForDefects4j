/**
 * 
 */
package common;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author ccsu
 *
 */
public class CoverageXMLParse {
	private  MatrixOfClasses clazzMatrix;

	public CoverageXMLParse()
	{
		clazzMatrix = new MatrixOfClasses();
	}
	/** 将解析的结果存入clazzMatrix
	 * @param coverageXMLFilename   带目录信息的coverage.xml
	 * @return
	 */
	public boolean parseFile(String coverageXMLFilename)
	{
		boolean result = true;
		try {
			//1.创建SAXReader对象
			SAXReader saxReader=new SAXReader();
			//2.调用read的方法
			Document xmlDocument;
			xmlDocument = saxReader.read(new File(coverageXMLFilename));
			//3.获取根元素
			Element rootCoverage=xmlDocument.getRootElement();
			//4.使用迭代器遍历集合直接子节点
			for(Iterator<Element> iterRoot=rootCoverage.elementIterator();iterRoot.hasNext();) {
				Element covElement=iterRoot.next();//Coverage的直接子节点
				if( covElement.getName().equals("sources") )
					continue;
				if( covElement.getName().equals("packages") ) 
				{
					for(Iterator<Element> subIter=covElement.elementIterator();subIter.hasNext();) {
						Element packElement=subIter.next();//packages的直接子节点
						if( !packElement.getName().equals("package") )
							continue;
						//node name = "package"
						if( false==parsePackage(packElement) )
						{
							result = false;
							break;
						}
					}//end of for...
				}//end of if...
				if( false==result )
					break;
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			result = false;
			e.printStackTrace();
		}
		return result;
	}
	
	/**  将filename的属性值，取不带目录的文件名。
	 * @param dirFilename  filename="org/jfree/data/general/Series.java"
	 * @return
	 */
	private String getLastFilename(String dirFilename)
	{
		String[] strAry = dirFilename.split("/");
		return strAry[strAry.length-1];
	}

	/** parse XML node "package"  
	 * @param packElement 
	 * @return
	 */
	private  boolean parsePackage(Element packElement)
	{
		boolean result = true;
		for(Iterator<Element> packIter=packElement.elementIterator("classes");packIter.hasNext();) {
			Element clazzElement=packIter.next();//package的直接子节点
			//Element name = "classes"
			for(Iterator<Element> clsIter=clazzElement.elementIterator("class");clsIter.hasNext();) {
				Element clsElement = clsIter.next();//classes的直接子节点
				//Element name = "class"
				ClassLinesHit clzlHit = new ClassLinesHit();//###create
				//得到class的属性
				for(Iterator<Attribute> iteAtt=clsElement.attributeIterator();iteAtt.hasNext();) {
					Attribute attr=iteAtt.next();
					if( attr.getName().equals("name") ) //class name
						clzlHit.setClassName(attr.getValue());
					else if( attr.getName().equals("filename") ) //filename which include this class.
						clzlHit.setClassFilename(getLastFilename(attr.getValue()));
					else
						continue;
				}
				for (Iterator<Element> itLines = clsElement.elementIterator("lines"); itLines.hasNext();) 
				{
					Element linesElement = itLines.next();//class的直接子节点
				    //Element name = "methods"
				    List<LineHit> lHitLst = parseLines(linesElement);
				    clzlHit.addLineCodes(lHitLst);
				}
				clazzMatrix.addClassLinesHit(clzlHit);//###add
			}//end of for...clsIter
		}//end of for...packIter
		return result;
	}

	/** parse <methods name=.....  classes的子XML节点有两类: methods & lines
	 * @param eleMethod
	 * @return
	 */
	private List<LineHit> parseLines(Element linesElement)
	{
		List<LineHit> lHitLst = new ArrayList<LineHit>();
		for (Iterator<Element> itLine = linesElement.elementIterator("line"); itLine.hasNext();) 
		{
			Element oneLine = itLine.next();//lines的直接子节点
			int lineNo = Integer.valueOf(oneLine.attributeValue("number"));
			int hits = Integer.valueOf(oneLine.attributeValue("hits"));
			LineHit lHit = new LineHit(lineNo,hits);
			lHitLst.add(lHit);
		}//end of for...
		return lHitLst;
	}

	/** parse XML node "package"  该方法调试通过。 调用了parseMethods
	 * @param packElement 
	 * @return
	 */
	private  boolean parsePackage2(Element packElement)
	{
		boolean result = true;
		for(Iterator<Element> packIter=packElement.elementIterator("classes");packIter.hasNext();) {
			Element clazzElement=packIter.next();//package的直接子节点
			//Element name = "classes"
			for(Iterator<Element> clsIter=clazzElement.elementIterator("class");clsIter.hasNext();) {
				Element clsElement = clsIter.next();//classes的直接子节点
				//Element name = "class"
				ClassLinesHit clzlHit = new ClassLinesHit();//###create
				//得到class的属性
				for(Iterator<Attribute> iteAtt=clsElement.attributeIterator();iteAtt.hasNext();) {
					Attribute attr=iteAtt.next();
					if( attr.getName().equals("name") ) //class name
						clzlHit.setClassName(attr.getValue());
					else if( attr.getName().equals("filename") ) //filename which include this class.
						clzlHit.setClassFilename(getLastFilename(attr.getValue()));
					else
						continue;
				}
				for (Iterator<Element> itMethods = clsElement.elementIterator("methods"); itMethods.hasNext();) 
				{
					Element methodsElement = itMethods.next();//class的直接子节点
				    //Element name = "methods"
				    List<LineHit> lHitLst = parseMethods(methodsElement);
				    clzlHit.addLineCodes(lHitLst);
				}
				clazzMatrix.addClassLinesHit(clzlHit);//###add
			}//end of for...clsIter
		}//end of for...packIter
		return result;
	}
	
	/** parse <methods name=.....  该方法调试通过。
	 * @param eleMethod
	 * @return
	 */
	private List<LineHit> parseMethods(Element eleMethods)
	{
		List<LineHit> lHitLst = new ArrayList<LineHit>();
		for (Iterator<Element> itMethod = eleMethods.elementIterator("method"); itMethod.hasNext();) 
		{
			Element methodElement = itMethod.next();//methods的直接子节点
			for (Iterator<Element> itLines = methodElement.elementIterator("lines"); itLines.hasNext();) 
			{
				Element lesElement = itLines.next();//method的直接子节点
				for (Iterator<Element> lineIter = lesElement.elementIterator("line"); lineIter.hasNext();)
				{
					Element eleLine = lineIter.next();//lines的直接子节点
					int lineNo = Integer.valueOf(eleLine.attributeValue("number"));
					int hits = Integer.valueOf(eleLine.attributeValue("hits"));
					LineHit lHit = new LineHit(lineNo,hits);
					lHitLst.add(lHit);
				}
			}
		}//end of for...
		return lHitLst;
	}
	
	//print clazzMatrix
	public void testMe()
	{
		List<ClassLinesHit> spectList = clazzMatrix.getSpectrums();
		for( ClassLinesHit clh : spectList )
		{
			System.out.println(clh.getClassName()+"  ,  "+clh.getClassFilename());
			List<LineHit> lCodes = clh.getLineCodes();
			int carrnum = 0;
			for( LineHit lhit : lCodes )
			{
				System.out.print(lhit.getLineNo());
				System.out.print(",");
				System.out.print(lhit.getHit());
				System.out.print("  ");
				if( carrnum++>=10 )
				{
					carrnum = 0;
					System.out.println(" ");
				}
			}//end of for...
			System.out.println(" ");
		}//end of for( Class...
	}
	/**
	 * @return the clazzMatrix
	 */
	public  MatrixOfClasses getClazzMatrix() {
		return clazzMatrix;
	}

	/**
	 * @param clazzMatrix the clazzMatrix to set
	 */
	public  void setClazzMatrix(MatrixOfClasses clazzMatrix) {
		this.clazzMatrix = clazzMatrix;
	}
}

package defects4j;

/** 单个的测试用例，包括测试类及测试方法，以及测试结果。
 * @author ccsu
 *
 */
public class D4jsTestCase {
	private boolean pass; //test case result,true=pass, false=failing
	private String testClass; //test case test class
	private String testMethod; //test method of test case
	
	public D4jsTestCase(boolean pass, String testClass, String testMethod) {
		super();
		this.pass = pass;
		this.testClass = testClass;
		this.testMethod = testMethod;
	}

	public boolean getResult() {
		return pass;
	}
	
	public void setResult(boolean newResult) {
		pass = newResult;
	}
	
	public String getTestClass() {
		return testClass;
	}
	
	public String getTestMethod() {
		return testMethod;
	}
	
	/** me is equal dtc   测试类名：：测试方法名 测试结果 三者都相同才判定为相等。
	 * @param dtc 
	 * @return true: equal.
	 */
	public boolean isEqual(D4jsTestCase dtc)
	{
		if( (this.pass==dtc.getResult()) && (this.testClass.equals(dtc.getTestClass()))
				&& (this.testMethod.equals(dtc.getTestMethod())) )
			return true;
		else
			return false;
	}
}

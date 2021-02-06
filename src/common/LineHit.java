/**
 * 
 */
package common;

/**  行的覆盖
 * @author ccsu
 *
 */
public class LineHit {
	private int lineNo;//行号
	private int hit; //=0,no hit;=1,hit
	
	//don't need default constructor
	public LineHit()
	{
		lineNo = 0;
		hit = 0;
	}
	
	public LineHit(int lineNo, int hit) {
		//super();
		this.lineNo = lineNo;
		this.hit = hit;
	}

	public int getLineNo() {
		return lineNo;
	}

	public int getHit() {
		return hit;
	}
	
}

package a.m.a.s.os.linux;

public class ReadLineListener  {
	StringBuilder sb = new StringBuilder();
	/**
	 * @param n current line, start from 1
	 * @param l read listener
	 * @return return true to stop reading
	 */
	public boolean onHandleNewLine(int n, String l) {
		return false;
	}
	@Override
	public String toString() {
		return sb.toString();
	}
}
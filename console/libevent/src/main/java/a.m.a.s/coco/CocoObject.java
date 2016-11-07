package a.m.a.s.coco;

public class CocoObject {
	long itime = 0;
	long otime = 0;
	
	public void itime() {
		itime = System.currentTimeMillis();
	}
	
	public void otime() {
		otime = System.currentTimeMillis();
	}
	
	@Override
	public String toString() {
		return String.format("(:lagency %d)", otime - itime);
	}
}

package a.m.a.s.linux.os;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class proc {
	public static void readline(File f, ReadLineListener l) {
		try {
			BufferedReader r = new BufferedReader(new FileReader(f));
			String line = null;
			int n = 0;
			while ((line = r.readLine()) != null) {
				if (l.onHandleNewLine(++n, line)) {
					break;
				}
			}
			r.close();
		} catch (IOException e) {

		}
	}
	
	public static int getPid() {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		return Integer.valueOf(runtimeMXBean.getName().split("@")[0]).intValue();
	}
}

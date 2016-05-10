package a.m.a.s.coco.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CocoShell {

	public void run() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String s = null;
		try {
			while ((s = in.readLine()) != null) {
				System.out.println(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

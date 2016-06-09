package com.cm.kinfoc;

import com.cm.util.ArrayMap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;


public class KIniReader {

	protected ArrayMap<String, Properties> sections = new ArrayMap<String, Properties>();
	private transient String currentSecion;
	private transient Properties current;

	public KIniReader(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(filename)));
		read(reader);
		reader.close();
	}

	protected void read(BufferedReader reader) throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			parseLine(line);
		}
	}

	protected void parseLine(String line) {
		line = line.trim();
		if (line.matches("\\[.*\\]")) {
			currentSecion = line.replaceFirst("\\[(.*)\\]", "$1");
			current = new Properties();
		} else if (line.matches(".*=.*")) {
			int i = line.indexOf('=');
			String name = line.substring(0, i);
			String value = line.substring(i + 1);
			current.setProperty(name, value);
			sections.put(currentSecion, current);
		}
	}

	public int getPrivateProfileInt(String section, String variable,
			int defaultValue) {
		String str;
		str = getPrivateProfileString(section, variable,
				Integer.toString(defaultValue));
		int nReturn = defaultValue;
		try {
			nReturn = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return nReturn;
	}

	public String getPrivateProfileString(String section, String name,
			String defValue) {
		Properties p = (Properties) sections.get(section);

		if (p == null) {
			return defValue;
		}

		String value = p.getProperty(name);
		
		if (value == null)
			value = defValue;

		return value;
	}
}
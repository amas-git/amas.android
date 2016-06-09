package com.cm.kinfoc.base;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class InfocLog {
	private static InfocLog instance = null;
	long time = 0;
	
	private InfocLog(){
		time = System.currentTimeMillis()/1000;
	}
	
	public static InfocLog getLogInstance(){
		
		if(instance == null){
			instance = new InfocLog();
		}
		return instance;
	}
	
	public void log(String sLog){
		//WRITE TO FILE AND LOGCAT
		Log.e("InfocLog", "" + sLog);
	}

	//写入文件，供以后看客户的log
	public void logfile(String sLog){
//		log(sLog);
		write2File(sLog);
	}

	private void write2File( String sLog){
		sLog = sLog + "\r\n";
		String sPathString = Environment.getExternalStorageDirectory() + "/" + "auto_up_" + time + ".txt";
		File file = new File(sPathString);
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(file, true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			byte[] by = sLog.getBytes();
			stream.write(by);
			stream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

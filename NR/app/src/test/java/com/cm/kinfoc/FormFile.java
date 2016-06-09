package com.cm.kinfoc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FormFile {

	private File mFile;
	private String mFormName;

	public File getFile() {
		return mFile;
	}

	public void setFile(File file) {
		mFile = file;
	}

	public String getFormName() {
		return mFormName;
	}

	public void setFormName(String formName) {
		mFormName = formName;
	}
	
	public String getFileName(){
		return mFile.getName();
	}
	
	public byte[] getData(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FileInputStream reader = null;
		try {
			reader = new FileInputStream(mFile);
			byte[] bytes = new byte[1024];
			int read = 0;
			while((read = reader.read(bytes, 0, 1024)) != -1){
				out.write(bytes, 0, read);
			}
			return out.toByteArray();
        } catch (FileNotFoundException e) {
	        e.printStackTrace();
        } catch (IOException e) {
	        e.printStackTrace();
        } finally{
        	try {
				if (reader!=null){
					reader.close();
				}
            } catch (IOException e) {
	            e.printStackTrace();
            }
        }
		return null;
	}

}

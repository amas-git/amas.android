package lab.whitetree.bonny.box.ui.filemanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.os.FileUtils;


/**
 * Abstraction for file system node
 * @author amas
 */
public class FileSystemNode {	
	MimeInfo mMimeInfo       = null;
	File     mFile           = null;
	//boolean  mIsHiden        = false;
	
	private static final Comparator<FileSystemNode> sDefaultComparator = new Comparator<FileSystemNode>() {
		public int compare(FileSystemNode o1, FileSystemNode o2) {
			if(o1.isDirectory() && !o2.isDirectory()) {
				return -1;
			}
			
			if(!o1.isDirectory() && o2.isDirectory()) {
				return 1;
			}
			
			return o1.getName().compareTo(o2.getName());
		}
	};
	
	public File asFile() {
		return mFile.getAbsoluteFile();
	}
	
	public FileSystemNode(String path) {
		mFile     = new File(path);
		mMimeInfo = MimeUtils.getMimeInfo(mFile);
	}
	
	public FileSystemNode(File file) {
		mFile = file;
		mMimeInfo = MimeUtils.getMimeInfo(mFile);
	}
	
	public MimeInfo getMimeInfo() {
		return mMimeInfo;
	}
	
	/**
	 * The node is directory
	 * @return
	 */
	public boolean isDirectory() {
		return mFile.isDirectory();
	}
	
	/**
	 * Get node name
	 * @return
	 */
	public String getName() {
		return mFile.getName(); 
	}
	
	/**
	 * Get file length by byte
	 * @return
	 */
	public long getSize() {
		return mFile.length();
	}
	
	public ArrayList<FileSystemNode> _getChildren() {
		ArrayList<FileSystemNode> nodes = new ArrayList<FileSystemNode>();
		File[] files = mFile.listFiles();
		if(files != null) {
			for(File f: files) {
				if(onFilterNode(f)) {
					nodes.add(new FileSystemNode(f));
				}
			}
		}
		return nodes;
	}
	
	/**
	 * return true to added node
	 * @param file
	 * @return
	 */
	private boolean onFilterNode(File file) {
		return !file.isHidden();
	}
	
	public boolean isHidden() {
		return mFile.isHidden();
	}
	
	public ArrayList<FileSystemNode> getChildren() {
		ArrayList<FileSystemNode> chd = _getChildren();
		Collections.sort(chd, sDefaultComparator);
		return chd;
	}
	
	/**
	 * Get absolute path
	 * @return
	 */
	public String getAbsolutePath() {
		return mFile.getAbsolutePath();
	}
	
	/**
	 * Get parent node
	 * @return
	 */
	public FileSystemNode getParent() {
		return new FileSystemNode(new File(mFile.getParent()));
	}
	
	/**
	 * Rename
	 * @param target
	 */
	public boolean rename(File target) {
		boolean success = mFile.renameTo(target);
		if(success) {
			mFile = target;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Delete all
	 * @return
	 */
	public boolean delete() {
		System.out.println("~~~~~~~~~~~~~删除个泡");
		if(isDirectory() ) {
			return deleteDir(mFile);
		}
		return mFile.delete();
	}
	
	/**
	 * The file node existed
	 * @return
	 */
	public boolean existed() {
		return mFile.exists();
	}

	/**
	 * Clear all files in the specify dir
	 * @return false if the node is a file or failed to delete files
	 */
	public boolean clearFile() {
		if(mFile.isFile()) {
			return false;
		}
		
		File[] files  = mFile.listFiles();
		if(files == null) {
			// empty dir
			return true;
		}
		
		boolean success = true;
		for(int i=0; i<files.length; ++i) {
			if(!files[i].exists()) {
				continue;
			}
			
			if(!files[i].delete()) {
				success = false;
			}
		}
		return success;
	}
	
	/**
	 * @param name
	 * @return new dir node
	 */
	public FileSystemNode mkdir(String name) {
		File target = new File(mFile.getAbsoluteFile() + "/" + name);
		FileSystemNode node = null;
		if (!target.exists() && target.mkdir()) {
			node = new FileSystemNode(target);
		}
		return node;
	}

	@Override
	public String toString() {
		return String.format("(:PATH %s :MIME-TYPE: %s :IS-DIR %b)", 
				mFile.getAbsolutePath(), 
				getMimeInfo(), 
				isDirectory());
	}
	

	/**
	 * Delete all files under specify directory
	 * @param targetDir
	 * @return
	 */
	public static boolean clearDir(File targetDir) {
		File[]  files  = targetDir.listFiles();
		boolean result = true;
		
		for(File f : files) {
			if(f.exists()) {
				if(!f.delete()) {
					result = false;
				}
			}
		}
		return result;
	} 
	
    // If targetLocation does not exist, it will be created.
    public static void copyDirectory(File source , File target) throws IOException {
        if (source.isDirectory()) {
            if (!target.exists()) {
               if(!target.mkdir()) {
                  android.util.Log.e("xxx", "mkdir failed : " + target.getAbsolutePath());
               } else {
            	   
               }
            }
            
            String[] children = source.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(source, children[i]),
                        new File(target, children[i]));
            }
        } else {
            copyFile(source, target);
        }
    }
    
    /**
     * TODO: check duplicate !!!
     * @param source
     * @param target
     * @return
     * @throws IOException
     */
    public static int copyNode(FileSystemNode source, FileSystemNode target) throws IOException {
    	File s = new File(source.getAbsolutePath());
    	File t = null;
    	if(s.isDirectory()) {
    		t = new File(target.getAbsolutePath()+"/"+source.getName());
    		copyDirectory(s, t);
    	} else if (s.isFile()) {
    		t = new File(target.getAbsolutePath());
    		copyFile(s,new File(t.getAbsoluteFile()+"/"+s.getName()));
    	}
    	return 0;
    }
	
	/**
	 * Copy file from source to target.
	 * @param source
	 * @param target
	 * @throws IOException
	 */
	public static void copyFile(File source, File target) throws IOException {
		FileUtils.copyFile(source, target);
	}
	
	/**
	 * Recursive delete specify dir
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }
	    
	    // The directory is now empty so delete it
	    return dir.delete();
	}
}

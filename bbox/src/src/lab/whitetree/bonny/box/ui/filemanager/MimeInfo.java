package lab.whitetree.bonny.box.ui.filemanager;

public class MimeInfo {
	int mIconResId;
//	int mIconSelectedResId;
	String mMimeType;
	
	public MimeInfo(String mimeType, int iconId) {
		mIconResId = iconId;
		mMimeType  = mimeType;
	}
	
	public String getMimeType() {
		return mMimeType;
	}
	
	public int getIconId() {
		return mIconResId;
	}
}
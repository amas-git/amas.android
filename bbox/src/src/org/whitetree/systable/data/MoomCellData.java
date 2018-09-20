package org.whitetree.systable.data;

public class MoomCellData implements Cloneable {
	public static final int MOOM_CELL_TYPE_ENTRANCE = 1; // 是一个Activity的入口,点击后跳转到一个Activity
	public static final int MOOM_CELL_TYPE_SWITCH = 2; // 是一个开关，点击后触发一种操作

	public String mTag = null; // 此MoomCell的tag，应唯一，以区分不同的MoomCell
	public String mMoomViewCfg = null; // cfg的xml文件路径
	public String mMoomViewTag = null; // MoomView的tag
	public String mMoomCHID = null; // 对应的频道ID
	public int mTitleResId = 0; // 介绍性文字
	public int mCellType = MOOM_CELL_TYPE_ENTRANCE;

	public MoomCellData() {
		init(null, null, null, null, 0, MOOM_CELL_TYPE_ENTRANCE);
	}

	public MoomCellData(String tag, String moomCfg, String moomViewTag,
			String chid) {
		init(tag, moomCfg, moomViewTag, chid, 0, MOOM_CELL_TYPE_ENTRANCE);
	}

	public MoomCellData(String tag, String moomCfg, String moomViewTag,
			String chid, int titleResId) {
		init(tag, moomCfg, moomViewTag, chid, titleResId,
				MOOM_CELL_TYPE_ENTRANCE);
	}

	public MoomCellData(String tag, String moomCfg, String moomViewTag,
			String chid, int titleResId, int celltype) {
		init(tag, moomCfg, moomViewTag, chid, titleResId, celltype);
	}

	public Object clone() {
		MoomCellData mcd = null;
		try {
			mcd = (MoomCellData) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return mcd;
	}

	private void init(String tag, String moomCfg, String moomViewTag,
			String chid, int titleResId, int cellType) {
		mTag = tag;
		mMoomViewCfg = moomCfg;
		mMoomViewTag = moomViewTag;
		mMoomCHID = chid;
		mTitleResId = titleResId;
		mCellType = cellType;
	}
}

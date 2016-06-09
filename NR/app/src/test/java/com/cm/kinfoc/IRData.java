package com.cm.kinfoc;

public interface IRData {
	public class InfocDataItemBase {
		public static final int INFOC_DATA_ITEM_TYPE_UNKNOWN  = 0;
		public static final int INFOC_DATA_ITEM_TYPE_NORMAL   = 1;
		public static final int INFOC_DATA_ITEM_TYPE_ACTIVITY = 2;
		public static final int INFOC_DATA_ITEM_TYPE_SERVICE  = 3;
		public static final int INFOC_DATA_ITEM_TYPE_URGENT   = 4;
		public int itemType = INFOC_DATA_ITEM_TYPE_UNKNOWN;
		public Object dataItemObj = null;
	}
	
	public void reportData(KInfocClient infoc, InfocDataItemBase item);
}

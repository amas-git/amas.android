package com.cm.kinfoc.base;


public abstract class InfocServerControllerBase {
	protected AsyncConsumerTask<ConsumerItem> mAsyncConsumer = null;
	
	public static enum CONTROLLERTYPE{
		UNKNOWTYPE, MAINRECOMMEND, FUNCRECOMMEND, REP_PRIVATE_DATA
	}
	
	private static InfocServerControllerBase mInstance = null;
	
	public static InfocServerControllerBase getInstance(){
		if(null == mInstance){
			return null;
		}
		return mInstance;
	}
	
	public static void setInfocServerControllerInstance(InfocServerControllerBase instance){
		mInstance = instance;
	}
	

	public abstract void getInfocRepPrivateDataAval(final IResultCallback cb);

	public static interface IResultCallback{
		public void onResult(CONTROLLERTYPE type, boolean isSuccess, String result);
	}
	
	protected class ConsumerItem{
		public static final int CITEM_TYPE_UNKNOW 		 = 0;
		public static final int CITEM_TYPE_INFOCREPORT   = 2;
		
		public ConsumerItem(int type, IResultCallback cb){
			mCItemType = type;
			mCb = cb;
		}
		
		public IResultCallback mCb = null;
		public int mCItemType = CITEM_TYPE_UNKNOW;
	}
}

package s.a.m.a.sched.sched;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public abstract class SchedItem implements Serializable {
	public static int NO_REPEATE = -1;
	
	protected int mRangeMax = 0;
	protected int mRangeMin = 0;
	protected int mMin      = mRangeMin;
	protected String mDesc  = "";
	protected int cronFiledIndex = 0;
	
	public String text = "";
	
	protected HashSet<Integer> mDataSet = new HashSet<Integer>();
	private int mRepeatInterval = NO_REPEATE;
	private boolean mWhenever = false;

	public SchedItem(int rangeMin, int rangeMax, int cronIndex) {
		if(rangeMin < 0 || rangeMax < 0) {
			throw new IllegalArgumentException("rangeMin or rangeMax must be positive integer!!!");
		}
		
		if(rangeMin > rangeMax) {
			throw new IllegalArgumentException("rangeMin must be smaller than rangeMax!!!");
		}
		
		mRangeMin = rangeMin;
		mRangeMax = rangeMax;
		mMin      = Integer.MAX_VALUE;
		cronFiledIndex = cronIndex;
	}
	
	public void setWhenever(boolean isWhenever) {
		mWhenever = isWhenever;
	}
	
	public boolean isWhenerver() {
		return mWhenever;
	}
	
	public boolean contains(int element) {
		return mWhenever ? true : mDataSet.contains(element);
	}
	
	/**
	 * 
	 * 设置重复时间
	 * @param repeatInterval
	 * @return 设置成功返回true, 设置失败(repeatInterval取值不合法)返回false
	 */
	@Deprecated
	public boolean setRepeatInterval(int repeatInterval) {
		if(isValidate(repeatInterval)) {
			mRepeatInterval = repeatInterval;
			return true;
		}
		return false;
	}
	
	/**
	 * TODO(amas): 需要优化/使用二分法
	 * 
	 * @param element
	 * @return
	 */
	public int nearest(Calendar c, int element) {
		if (isWhenerver()) {
			return element % (mRangeMax+1); // 保证不会超过RangeMax
		} else {
			for (int i = element; i <= mRangeMax; ++i) {
				if (mDataSet.contains(i)) {
					return i;
				}
			}
		}
		return getMin();
	}
	
	public int getMin() {
		return mMin == Integer.MAX_VALUE ? mRangeMin : mMin;
	}
	/**
	 * @return 返回-1, 则说明不需要重复
	 */
	public int getRepeateInterval() {
		return mRepeatInterval;
	}
	
	public boolean add(int element) {
		if(isValidate(element)) {
			if(element < mMin) {
				mMin = element;
			}
			return mDataSet.add(element);
		}
		return false;
	}
	
	public boolean addAll(Collection<? extends Integer> elements) {
		boolean modified = false;
		Iterator<? extends Integer> e = elements.iterator();
		while (e.hasNext()) {
			int i = e.next();
			if(isValidate(i)) {
				mDataSet.add(i);
				modified = true;
			}
		}
		return modified;
	}
	
	protected boolean isValidate(int element) {
		return element >= mRangeMin && element <= mRangeMax;
	}
	
	public void addRange(int min, int max) {
		for(int i=min; i<=max; ++i) {
			add(i);
		}
	}
	
	/**
	 * @param min
	 * @param max
	 * @return
	 */
	public static ArrayList<Integer> range(int min, int max) {
		ArrayList<Integer> xs = new ArrayList<Integer>(max-min);
		for(int i=min; i<=max; ++i) {
			xs.add(i);
		}
		return xs;
	}
	
	@Override
	public String toString() {
		StringBuilder ds = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		
		if (!mDataSet.isEmpty()) {
			Integer[] array = new Integer[mDataSet.size()];
			mDataSet.toArray(array);
			for (int i = 0; i < array.length - 1; ++i) {
				ds.append(array[i]).append(",");
			}
			ds.append(array[array.length - 1]);
		}
		
		sb.append(String.format("[%s] : '%s' [Min = %d]",mDesc,text,mMin)).append("\n");
		sb.append(String.format("\t数据集合 = '%s'",ds.toString())).append("\n");
		sb.append(String.format("\t重复周期 = %d",getRepeateInterval())).append("\n");
		sb.append(String.format("\t何值均可 = '%s'",isWhenerver())).append("\n");
		
		return sb.toString();
	}

	public int getRangeMax() {
		return mRangeMax;
	}
	
	public int getRangeMin() {
		return mRangeMin;
	}
}

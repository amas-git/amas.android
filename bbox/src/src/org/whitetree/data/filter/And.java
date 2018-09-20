package org.whitetree.data.filter;

public class And<T> implements IFilter<T>{
	IFilter<T> mL , mR;
	
	public And(IFilter<T>  l , IFilter<T>  r) {
		mL = l;
		mR = r;
	}
	
	@Override
	public boolean onFilter(T target) {
		return mL.onFilter(target) && mR.onFilter(target);
	}
}

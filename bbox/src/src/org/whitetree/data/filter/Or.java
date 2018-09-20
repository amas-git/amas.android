package org.whitetree.data.filter;

public class Or<T> implements IFilter<T>{
	IFilter<T> mL , mR;
	
	public Or(IFilter<T>  l , IFilter<T>  r) {
		mL = l;
		mR = r;
	}
	
	@Override
	public boolean onFilter(T target) {
		return mL.onFilter(target) || mR.onFilter(target);
	}
}
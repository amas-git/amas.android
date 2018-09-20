package org.whitetree.data.filter;

public class Not<T> implements IFilter<T>{
	IFilter<T> mU;
	
	public Not(IFilter<T>  unary) {
		mU = unary;
	}
	
	@Override
	public boolean onFilter(T target) {
		return !mU.onFilter(target);
	}
}
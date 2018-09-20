package org.whitetree.data.filter;

public interface IFilter<T> {
	public boolean onFilter(T target);
}

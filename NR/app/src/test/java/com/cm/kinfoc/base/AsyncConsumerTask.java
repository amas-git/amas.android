package com.cm.kinfoc.base;

import java.util.LinkedList;
import java.util.Queue;


public class AsyncConsumerTask<E> {
	public static interface ConsumerCallback<E> {
		public void consumeProduct(E product);
	}
	
	public static class Builder<E> {
		public AsyncConsumerTask<E> build() {
			return new AsyncConsumerTask<E>(this);
		}
		
		public Builder<E> mWaitTime(int waitTime) {
			if (waitTime <= 0) {
				throw new IllegalArgumentException("The wait time should be positive integer.");
			}
			
			mWaitTime = waitTime;
			
			return this;
		}
		
		public Builder<E> mCallback(ConsumerCallback<E> callback) {
			mCallback = callback;
			return this;
		}

		private int mWaitTime = 1000 * 17;
		private ConsumerCallback<E> mCallback= null;
	}
	
	
	public void addProduct(E item) {
		if (null == item) {
			return;
		}
		
		synchronized (mProductQueue) {
			mProductQueue.offer(item);
			
			if (null == mConsumerThread) {
				createThread();
			}
			
			mProductQueue.notify();
		}
	}
	
	public int peekProductSize() {
		int size = 0;
		synchronized (mProductQueue) {
			size = mProductQueue.size();
		}
		return size;
	}
	
	
	private void createThread() {
		mConsumerThread = new Thread() {
			@Override
			public void run() {
				
				E item = null;
				while (true) {
					item = null;
					synchronized(mProductQueue) {
						if (mProductQueue.isEmpty()) {
							try {
								mProductQueue.wait(mWaitTime);
								if (mProductQueue.isEmpty()) {
									mConsumerThread = null;
									break;
								}
							} catch (InterruptedException e) {
								mConsumerThread = null;
								break;
							}
						}
						
						item = mProductQueue.poll();
					}
					
					if (null != mCallback) {
						mCallback.consumeProduct(item);
					}
				}
			}
		};
		
		mConsumerThread.start();
	}
	
	private AsyncConsumerTask(Builder<E> builder) {
		mWaitTime = builder.mWaitTime;
		mCallback = builder.mCallback;
	}
	
	
	private Thread mConsumerThread = null;
	private final Queue<E> mProductQueue = new LinkedList<E>();
	private final int mWaitTime;
	private final ConsumerCallback<E> mCallback;
}

package com.example.secondtestfan;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ValueAnimator;

public class MyAnimationListenerControll implements AnimatorListener{
	
	public MyAnimationListenerControll(ValueAnimator animation){
		animation.addListener(this);
	}

	@Override
    public void onAnimationCancel(Animator arg0) {
	    
    }

	@Override
    public void onAnimationEnd(Animator arg0) {
		onFinish();
    }

	@Override
    public void onAnimationRepeat(Animator arg0) {
		onAnimationRepeat();
    }

	@Override
    public void onAnimationStart(Animator arg0) {
    }
	
	public void onFinish(){
		
	}
	
	public void onAnimationRepeat(){
		
	}
	
}

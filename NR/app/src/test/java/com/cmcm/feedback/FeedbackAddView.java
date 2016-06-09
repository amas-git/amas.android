package com.cmcm.feedback;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cmcm.onews.R;


public class FeedbackAddView extends RelativeLayout{
	
	public ImageView mAddImage;
	public ImageView mDeleteImage;
	public ImageView mShowImage;
	
	private int mId;
	
	public void setId(int id){
		mId = id;
	}
	
	public int getId(){
		return mId;
	}
	
	public interface OnFeedbackOperListener{
		public void onAdd(int i);
		public void onDelete(int i);
		public void needShow();
		public void needHide();
	}
	
	private OnFeedbackOperListener mOnFeedbackOperListener;
	
	public void setOnFeedbackOperListener(OnFeedbackOperListener l){
		mOnFeedbackOperListener = l;
	}

	public FeedbackAddView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    LayoutInflater.from(context).inflate(R.layout.feedback_tag_feedback_add_view_layout, this);
	    mAddImage = (ImageView)findViewById(R.id.add_image);
	    mDeleteImage = (ImageView)findViewById(R.id.delete_img);
		mShowImage = (ImageView) findViewById(R.id.show_img);

	    mAddImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(null != mOnFeedbackOperListener){
					mOnFeedbackOperListener.onAdd(getId());
				}
			}
		});
		mDeleteImage.setVisibility(View.GONE);
	    
	    mDeleteImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(null != mOnFeedbackOperListener){
					mOnFeedbackOperListener.onDelete(getId());
					mDeleteImage.setVisibility(View.GONE);
					mAddImage.setImageDrawable(null);
					mAddImage.setClickable(true);

					mShowImage.setVisibility(View.GONE);
					mShowImage.setImageBitmap(null);
				}
				if(null != mOnFeedbackOperListener){
					mOnFeedbackOperListener.needHide();
				}
			}

		});
    }
	
	public void showImage(Bitmap bitmap){
		mAddImage.setClickable(false);

		mShowImage.setVisibility(View.VISIBLE);
		mShowImage.setImageBitmap(bitmap);

		mDeleteImage.setVisibility(View.VISIBLE);
		if(null != mOnFeedbackOperListener){
			mOnFeedbackOperListener.needShow();
		}
	}
	
	public boolean isEmpty(){
		return mDeleteImage.getVisibility() == View.GONE;
	}
	
	public boolean isHide(){
		return getVisibility() == View.GONE;
	}
	
/*	public void deleteImage(){
		
	}*/
	
	public void show(){
		if(getVisibility() != View.VISIBLE){
			setVisibility(View.VISIBLE);
		}
	}
	
	public void hide(){
		if(getVisibility() != View.GONE){
			setVisibility(View.GONE);
		}
	}
	
	
	

}

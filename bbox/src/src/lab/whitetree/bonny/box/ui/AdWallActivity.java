package lab.whitetree.bonny.box.ui;

import lab.whitetree.bonny.box.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;

//import com.pansi.cppagent.CPPAgent;

public class AdWallActivity extends Activity {
	private LinearLayout mLayout = null;
	
	public static void launchActivity(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, AdWallActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_adwall);
		mLayout = (LinearLayout) findViewById(R.id.layout);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onStart() {
		super.onStart();
		if (mLayout != null) {
			mLayout.removeAllViews();
			//View view = CPPAgent.getAdWallView(AdWallActivity.this);
			//view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
			//mLayout.addView(view);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (mLayout != null) {
			mLayout.removeAllViews();
		}
	}
}

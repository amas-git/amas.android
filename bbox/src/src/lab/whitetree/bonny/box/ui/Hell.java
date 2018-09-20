package lab.whitetree.bonny.box.ui;

import lab.whitetree.bonny.box.R;

import org.whitetree.bidget.moom.MoomView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * For artist
 * @author amas
 *
 */
public class Hell extends Activity {
	public static Intent getLaunchIntent(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, Hell.class);
		return intent;
	}
	
	MoomView moom = null;
	public static void startDefault(Context context) {
		context.startActivity(getLaunchIntent(context));
	}
	
	TextView mTvZoom = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hell);
		
		moom = (MoomView)findViewById(R.id.moom);
		Intent intent = getIntent();
		
		Uri uri = intent.getData();
		System.out.println("========================= URi = " + uri.toString());
		String moomUri = handleUrl(uri);
		moom.loadConfigure(this, moomUri);
		setTitle(moomUri);
		
		mTvZoom = (TextView)findViewById(R.id.tv1);
		_setZoom(moom.getZoom());
	}
	
	private String handleUrl(Uri uri) {
		if(uri.getScheme().equals("file")) {
			return "file://"+uri.toString();
		} else {
			return uri.toString();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
	}
	
	public void onZoomInc(View _) {
		moom.setZoom(moom.getZoom()+0.05f);
		_setZoom(moom.getZoom());
	}
	
	public void _setZoom(float z) {
		mTvZoom.setText("zoom="+z);
	}
	
	public void onZoomDec(View _) {
		moom.setZoom( moom.getZoom()-0.05f);
		_setZoom(moom.getZoom());
	}
	
}

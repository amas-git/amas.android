package lab.whitetree.bonny.box.ui;

import lab.whitetree.bonny.box.R;

import org.whitetree.systable.system.U;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class CpuTestPerformanceActivity extends Activity {
	private static final int PI_DECIMAL_PLACES_LEN = 4000;
	private static final int PI_LEN_DEFAULT = 4000;
	private static final String PI = "3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679821480865132823066470938446095505822317253594081284811174502841027019385211055596446229489549303819644288109756659334461284756482337867831652712019091456485669234603486104543266482133936072602491412737245870066063155881748815209209628292540917153643678925903600113305305488204665213841469519415116094330572703657595919530921861173819326117931051185480744623799627495673518857527248912279381830119491298336733624406566430860213949463952247371907021798609437027705392171762931767523846748184676694051320005681271452635608277857713427577896091736371787214684409012249534301465495853710507922796892589235420199561121290219608640344181598136297747713099605187072113499999983729780499510597317328160963185950244594553469083026425223082533446850352619311881710100031378387528865875332083814206171776691473035982534904287554687311595628638823537875937519577818577805321712268066130019278766111959092164201989380952572010654858632788659361533818279682303019520353018529689957736225994138912497217752834791315155748572424541506959508295331168617278558890750983817546374649393192550604009277016711390098488240128583616035637076601047101819429555961989467678374494482553797747268471040475346462080466842590694912933136770289891521047521620569660240580381501935112533824300355876402474964732639141992726042699227967823547816360093417216412199245863150302861829745557067498385054945885869269956909272107975093029553211653449872027559602364806654991198818347977535663698074265425278625518184175746728909777727938000816470600161452491921732172147723501414419735685481613611573525521334757418494684385233239073941433345477624168625189835694855620992192221842725502542568876717904946016534668049886272327917860857843838279679766814541009538837863609506800642251252051173929848960841284886269456042419652850222106611863067442786220391949450471237137869609563643719172874677646575739624138908658326459958133904780275900994657640789512694683983525957098258226205224894077267194782684826014769909026401363944374553050682034962524517493996514314298091906592509372216964615157098583874105978859597729754989301617539284681382686838689427741559918559252459539594310499725246808459872736446958486538367362226260991246080512438843904512441365497627807977156914359977001296160894416948685558484063534220722258284886481584560285060168427394522674676788952521385225499546667278239864565961163548862305774564980355936345681743241125150760694794510965960940252288797108931456691368672287489405601015033086179286809208747609178249385890097149096759852613655497818931297848216829989487226588048575640142704775551323796414515237462343645428584447952658678210511413547357395231134271661021359695362314429524849371871101457654035902799344037420073105785390621983874478084784896833214457138687519435064302184531910484810053706146806749192781911979399520614196634287544406437451237181921799983910159195618146751426912397489409071864942319615679452080951465502252316038819301420937621378559566389377870830390697920773467221825625996615014215030680384477345492026054146659252014974428507325186660021324340881907104863317346496514539057962685610055081066587969981635747363840525714591028970641401109712062804390397595156771577004203378699360072305587631763594218731251471205329281918261861258673215791984148488291644706095752706957220917567116722910981690915280173506712748583222871835209353965725121083579151369882091444210067510334671103141267111369908658516398315019701651511685171437657618351556508849099898599823873455283316355076479185358932261854896321329330898570642046752590709154814165498594616371802709819943099244889575712828905923233260972997120844335732654893823911932597463667305836041428138830320382490375898524374417029132765618093773444030707469211201913020330380197621101100449293215160842444859637669838952286847831235526582131449576857262433441893039686426243410773226978028073189154411010446823252716201052652272111660396665573092547110557853763466820653109896526918620564769312570586356";
	private static final float STANDARD_CPU_VALUE = 12000f ;
	
	private static final int MSG_WHAT_RESET_PI_VALUE = 1;
    private static final int MSG_WHAT_COMPUTE_PI_FINISH = 2;
    private static final int MSG_WHAT_SET_TEXTVIEW_MSG = 3;
    private static final int MSG_WHAT_COUNT_DOWN_FINISH = 4;

	private Button mbtConfirm = null;
	private TextView mtvPi = null;
	private ScrollView mScroll = null;
	
	private boolean mIsComputePIFinish = false;
	
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
        	case MSG_WHAT_COUNT_DOWN_FINISH:
        		mtvPi.setText("_");
        		scrollMoveToBottom();
        		break;
        		
        	case MSG_WHAT_SET_TEXTVIEW_MSG:
        		mtvPi.setText(msg.obj.toString());
        		scrollMoveToBottom();
        		break;
        		
        	case MSG_WHAT_RESET_PI_VALUE:
        		if (!mIsComputePIFinish) {
	        		mtvPi.setGravity(Gravity.LEFT);
	        		mtvPi.setText(PI.substring(0, msg.arg1) + "_");
	        		scrollMoveToBottom();
        		}
        		break;
        		
        	case MSG_WHAT_COMPUTE_PI_FINISH:
        		mIsComputePIFinish = true;
        		
        		mtvPi.setGravity(Gravity.LEFT);
                float score = getCpuScore(msg.arg1);
                
                String content = msg.obj.toString();
                String result = String.format(getString(R.string.cpu_performance_result), ((float)msg.arg1)/1000f, score);
               
                mtvPi.setText(content + "\n\n" + result + "\n");
        		scrollMoveToBottom();
                 
                mbtConfirm.setVisibility(View.VISIBLE);
        		break;
        		
    		default:
    			break;
        	}
        }
    };
    
	public static Intent getLaunchIntent(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, CpuTestPerformanceActivity.class);
		return intent;
	}
	
	public static void startDefault(Context context) {
		context.startActivity(getLaunchIntent(context));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_cpu_performance);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		mbtConfirm = (Button) findViewById(R.id.button);
		mtvPi = (TextView) findViewById(R.id.text);
		mScroll = (ScrollView) findViewById(R.id.scroll);
		
		Typeface face = Typeface.createFromAsset(getAssets(), "led.ttf"); 
		mtvPi.setTypeface(face);
		
		mbtConfirm.setVisibility(View.GONE);
		
		mbtConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mbtConfirm.setVisibility(View.GONE);
		onTestBegin();
	}
	
	private void onTestBegin() {
		new AsyncTask<Void, Void, Void>() {
	        protected void onPreExecute() {
	            super.onPreExecute();
	        }
	        protected Void doInBackground(Void... params) {
				android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
	        		Message msg = new Message();
	        		msg.what = MSG_WHAT_SET_TEXTVIEW_MSG;
	        		msg.obj = "3";
	        		mHandler.sendMessage(msg);
	        		sleepMilliSec(600);
	        		msg = new Message();
	        		msg.what = MSG_WHAT_SET_TEXTVIEW_MSG;
	        		msg.obj = "_";
	        		mHandler.sendMessage(msg);
	        		sleepMilliSec(400);
	        		msg = new Message();
	        		msg.what = MSG_WHAT_SET_TEXTVIEW_MSG;
	        		msg.obj = "2";
	        		mHandler.sendMessage(msg);
	        		sleepMilliSec(600);
	        		msg = new Message();
	        		msg.what = MSG_WHAT_SET_TEXTVIEW_MSG;
	        		msg.obj = "_";
	        		mHandler.sendMessage(msg);
	        		sleepMilliSec(400);
	        		msg = new Message();
	        		msg.what = MSG_WHAT_SET_TEXTVIEW_MSG;
	        		msg.obj = "1";
	        		mHandler.sendMessage(msg);
	        		sleepMilliSec(600);
	        		msg = new Message();
	        		msg.what = MSG_WHAT_COUNT_DOWN_FINISH;
	        		mHandler.sendMessage(msg);
	        		sleepMilliSec(400);
	        		
	        		mIsComputePIFinish = false;
	        		
					new Thread() {
						public void run() { 
							android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
							for (int i = 5; i <= PI_LEN_DEFAULT; ++i) {
	        	        		Message msg = new Message();
	        	        		msg.what = MSG_WHAT_RESET_PI_VALUE;
	        	        		msg.arg1 = i;
								if (!Thread.interrupted() && mHandler != null) {
									mHandler.sendMessage(msg);
								}
								sleepMilliSec(2);
	        	        	}
						}
					}.start();

					new Thread() {
						public void run() { 
							android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

	        	        	long time1 = System.currentTimeMillis();
	                		Message msg = new Message();
	                		msg.obj = U.computePi(PI_DECIMAL_PLACES_LEN);
	        	        	long time2 = System.currentTimeMillis();
	                		msg.what = MSG_WHAT_COMPUTE_PI_FINISH;
	                		msg.arg1 = (int) (time2 - time1);
	                		mHandler.sendMessage(msg);
						}
					}.start();
	        	return null;
	        }
	        protected void onPostExecute(Void result) {
	        }
	    }.execute();
	}
	
	@Override
	public void onBackPressed() {
		// 不能按回退 退出
		// 因为 所有计算都是另起线程执行的, 就算用户按了back键退出了, 计算也不会停止,仍会在后台继续
//		super.onBackPressed();
	}
	
	// 0~100分 100 最好
	private float getCpuScore(int timeconsuming) {
		float result = STANDARD_CPU_VALUE / (float) timeconsuming * 100;
		return result > 100 ? 100 : result;
	}
	
	private void sleepMilliSec(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (Exception e) {
		}
	}
	
	private void scrollMoveToBottom() {
		mScroll.post(new Runnable() {  
	        public void run() {  
	        	mScroll.fullScroll(ScrollView.FOCUS_DOWN);  
	        }  
		});  
	}
}

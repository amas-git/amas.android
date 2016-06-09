package com.cmcm.onews.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.cmcm.onews.BuildConfig;
import com.cmcm.onews.R;
import com.cmcm.onews.event.EventContentIdError;
import com.cmcm.onews.event.EventNetworkChanged;
import com.cmcm.onews.event.EventPrepareVideoPlayer;
import com.cmcm.onews.event.FireEvent;
import com.cmcm.onews.event.ONewsEvent;
import com.cmcm.onews.loader.LOAD_DETAILS;
import com.cmcm.onews.loader.ONewsDetailLoader;
import com.cmcm.onews.loader.ONewsLoadResult_LOAD_REMOTE;
import com.cmcm.onews.model.ONews;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.model.ONewsScenarioCategory;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.service.ReportDetailPercent;
import com.cmcm.onews.system.NET_STATUS;
import com.cmcm.onews.ui.item.NewsAlgorithmReport;
import com.cmcm.onews.ui.widget.CommonNewsDialog;
import com.cmcm.onews.ui.widget.INewsNotifyDialogClick;
import com.cmcm.onews.ui.widget.MareriaProgressBar;
import com.cmcm.onews.util.NetworkUtil;
import com.cmcm.onews.util.ReportThread;
import com.cmcm.onews.util.TimeAdder;
import com.cmcm.onews.util.NewsDebugConfigUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.cmcm.onews.model.ONewsScenario.getScenarioByCategory;

/**
 * Created by Jason.Su on 2015/12/8.
 * com.cmcm.onews.ui
 * des: 负责展示全屏的视频
 * email:suyanjiao@conew.com
 * 1553202383
 * 未解决的问题， 当视频播放发生error 的时候没有办法记录下该位置，所以导致不能续播
 */
public class NewsVideoFullScreenActivity extends NewsBaseUIActivity {
    private final static String URI_DETAIL = "uri_detail";
    private final static String URI_DETAIL_TIME = "uri_detail_time";
    private VideoView mVideoView;
    private MareriaProgressBar mProgressBar;
    private MediaController mController;
    private View mRetry;
    private View mRetryContainer;
    private String mUri;
    private final int mDelayMillis = 3000; // 快进快退按钮的默认消失时间是3s
    private View mBtnClose;
    private long mDelayRetryTime = 6000l;//加载重新加载界面时间是6s
    private static final int FROM_UNKWON = -1;
    private static int sFrom = FROM_UNKWON;

    private ONews mONews;
    private ONewsScenario mONewsScenario;
    private TimeAdder timer;

    private int position;
    private int duration;

    /**
     * 这里边跳转之前，为了安全应该先考虑是否有网络
     *
     * @param url
     * @param context
     */
    public static void on_HANDLESTARTACTIVITY(Context context, String url, long duration, ONews oNews, ONewsScenario oNewsScenario) {
        if (TextUtils.isEmpty(url)) {
        } else {
            Intent intent = new Intent();
            intent.setClass(context, NewsVideoFullScreenActivity.class);
            intent.putExtra(URI_DETAIL, url);
            intent.putExtra(NewsBaseActivity.KEY_NEWS, oNews.toContentValues());

            Bundle bundle = new Bundle();
            bundle.putParcelable(NewsBaseActivity.KEY_SCENARIO, oNewsScenario);
            intent.putExtra(NewsBaseActivity.KEY_BUNDLE, bundle);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();

        setContentView(R.layout.onews_activity_lower_video);
        init();
        timer = new TimeAdder();
        timer.zero();
    }

    private void initData() {
        try {
            Intent intent = getIntent();
            this.mONewsScenario = intent.getParcelableExtra(NewsBaseActivity.KEY_SCENARIO);
            android.content.ContentValues values = intent.getParcelableExtra(KEY_NEWS);
            this.mONews = com.cmcm.onews.model.ONews.fromContentValues(values);
            mUri = null;
            if (intent != null) {
                mUri = intent.getStringExtra(URI_DETAIL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            position = mVideoView.getCurrentPosition();
            duration = mVideoView.getDuration();
        }
        if (mVideoView != null) {
            if (mVideoView.isPlaying()) {
                mVideoView.pause();
            }
        }
        reportDuration(APP_TIME_VIDEO);
        if (null != adder && null != mONews && null != mONewsScenario) {
            reportAlgorithm();
            adder.zero();
        }
        timer.pause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initData();
        this.mFrom = intent.getIntExtra(NewsBaseActivity.KEY_FROM, FROM_HOME);
        ((TextView) findViewById(R.id.tv_content_id)).setText(mONews.contentid());
        setUri();
        isHasLoaded = false;
        setUIBasedNet(false);
    }

    private void setUri() {
        if (null == mONews || null == mONewsScenario) {
            return;
        }
        ArrayList<String> videoList = mONews.bodyvideosList();
        if (null != videoList && videoList.size() > 0) {
            String jsonArray = videoList.get(0);
            try {
                JSONObject jsonObject = new JSONObject(jsonArray);
                String url = jsonObject.optString("url");
                Long duration = jsonObject.optLong("duration");
                String description = jsonObject.optString("description");
                String thumbnail = jsonObject.optString("thumbnail");
                if (com.cmcm.onews.sdk.videoLib.BuildConfig.DEBUG) {
                    Log.e("suj", "url:" + url + ", duration:" + duration + ", description:" + description + ", thumbnail:" + thumbnail);
                }
                mUri = url;
                //url="http://ns.ibnlive.in.com/12_2015/30-12-2015/assam_poll_pkg.mp4";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        timer.resume();
    }

    @Override
    protected void onDestroy() {
        int percent = 0;
        if (duration != 0) {
            percent = (position * 100 / duration);
            percent = Math.min(100, Math.max(0, percent));
        }
        int total_Time = infoctime();
        L.videoplayer("onDestroy total time : " + total_Time);
        ReportThread.post(new ReportDetailPercent(mONews, mONewsScenario, DETAIL_FROM_LIST, percent, "", infocshare(), total_Time, "", ""));
        sFrom = -1;
        mVideoView.stopPlayback();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFromPush()) {
            startToList();
            finish();
        }
    }

    private void startToList() {
        if (null != com.cmcm.onews.sdk.NewsSdk.INSTAMCE.getDependence()) {
            try {
                Intent intent = com.cmcm.onews.sdk.NewsSdk.INSTAMCE.getDetailsBackTo();
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {
        mHandler = new Handler(getMainLooper());
        mVideoView = (VideoView) findViewById(R.id.news_video_detail);
        mProgressBar = (MareriaProgressBar) findViewById(R.id.progress);
        mBtnClose = findViewById(R.id.btn_close);
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishIfFromGCM();
            }
        });
        hideBtnClose();
        if (NewsDebugConfigUtil.getInstance().isShowNewsId()) {
            findViewById(R.id.tv_content_id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    com.cmcm.onews.ui.debug.NewsDebugDetailResultActivity.startDefault(NewsVideoFullScreenActivity.this,
                            mONews.contentid(),
                            mONewsScenario);
                }
            });
        }
        mRetryContainer = findViewById(R.id.retry);
        mRetry = mRetryContainer.findViewById(R.id.news_button_refresh);
        mErrorText1 = ((TextView) mRetryContainer.findViewById(R.id.onews__list_empty_t1));
        mErrorText2 = ((TextView) mRetryContainer.findViewById(R.id.onews__list_empty_t2));
        mErrorBack = ((TextView) mRetryContainer.findViewById(R.id.ii_btn_close));
        mIINetImage = (ImageView) mRetryContainer.findViewById(R.id.iv_no_net);
        ((TextView) mRetryContainer.findViewById(R.id.onews__list_empty_t1)).setTextColor(Color.WHITE);
        ((TextView) mRetryContainer.findViewById(R.id.onews__list_empty_t2)).setTextColor(Color.WHITE);
        mRetry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isVideoPrepared = false;
                setUIBasedNet(true);
                isHasRetry = true;
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                log("suj", "onPrepared");
                isVideoPrepared = true;
                mProgressBar.setVisibility(View.GONE);
            }
        });
        if (null != mUri) {
            mVideoView.setVideoURI(Uri.parse(mUri));
        } else {
            loadDetail();
        }
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                log("suj", "onError");
                if (extra == MediaPlayer.MEDIA_ERROR_IO && NetworkUtil.isNetworkAvailable(getBaseContext())) {
                    setSourceNotFoundState();
                    return true;
                }

                isVideoPrepared = false;
                if (!isHasRetry) {
                    setUIBasedNet(false);
                }
                return true;
            }
        });
        findViewById(R.id.root).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                log("suj", "ontouch");
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    showBtnClose();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideBtnClose();
                        }
                    }, mDelayMillis);
                }
                return false;
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                log("suj", "onCompletion");
                finishIfFromGCM();
            }
        });
        setUIBasedNet(false);
        mController = new MediaController(this);
        mVideoView.setMediaController(mController);
    }

    private void finishIfFromGCM() {
        if (isFromPush()) {
            startToList();
        }
        finish();
    }

    private void showBtnClose() {
        mBtnClose.setVisibility(View.VISIBLE);
        if (NewsDebugConfigUtil.getInstance().isShowNewsId()) {
            TextView tv_content_id = (TextView) findViewById(R.id.tv_content_id);
            tv_content_id.setText(mONews.contentid());
            tv_content_id.setVisibility(View.VISIBLE);
        }
    }

    private void hideBtnClose() {
        mBtnClose.setVisibility(View.GONE);
        if (NewsDebugConfigUtil.getInstance().isShowNewsId()) {
            findViewById(R.id.tv_content_id).setVisibility(View.GONE);
        }
    }

    private ImageView mIINetImage;
    private TextView mErrorText1;
    private TextView mErrorText2;
    private TextView mErrorBack;

    private void setSourceNotFoundState() {
        mProgressBar.setVisibility(View.GONE);
        mProgressBar.stop();
        mRetry.setVisibility(View.VISIBLE);
        mVideoView.setVisibility(View.GONE);
        mIINetImage.setImageResource(R.drawable.onews_source_not_found);
        mErrorText1.setText(R.string.onews_404);
        mErrorText2.setVisibility(View.GONE);
        mErrorBack.setText(R.string.onews_sdk_back);
        mRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishIfFromGCM();
            }
        });
    }

    private Handler mHandler = null;

    private boolean isVideoPrepared = false;

    private boolean isHasRetry = false;

    private void setContinueOrPrepare() {
        if (isHasLoaded) {
            continueLoad();
        } else {
            prepareLoading();
        }
    }

    private void setUIBasedNet(boolean forceUpdate) {
        boolean net = NetworkUtil.isNetworkUp(this);
        if (net || forceUpdate) {
            if ((NetworkUtil.isMobileNetworkUp(this) && !isHasNotified)) {
                show2gOr3gReminder();
            } else {
                setContinueOrPrepare();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        log("suj", "isVideoPrepared" + isVideoPrepared);
                        if (!isVideoPrepared) {
                            prepareRetry();
                        }
                        isHasRetry = false;
                    }
                }, mDelayRetryTime);
            }
        } else {
            if (mVideoView == null || mVideoView.getBufferPercentage() != 100) {
                prepareRetry();
            }
        }

    }


    private void show2gOr3gReminder() {
        if (isHasNotified) {
            return;
        }
        isHasNotified = true;
        // 经过3g 或者2g 的时间片延迟变为10
        mDelayRetryTime = 10000;
        final CommonNewsDialog dialog = new CommonNewsDialog(this, new INewsNotifyDialogClick() {
            @Override
            public void clickContinue() {
                setUIBasedNet(false);
            }

            @Override
            public void clickCancel() {
                finishIfFromGCM();

            }
        }, getString(R.string.onews__notify_not_wifi_reminder));
        dialog.setCanceledOnTouchOutside(false);
        dialog.showDialog();
    }


    private void prepareRetry() {
        mVideoView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mRetryContainer.setVisibility(View.VISIBLE);
        mVideoView.stopPlayback();
    }

    private boolean isHasLoaded = false;

    private void prepareLoading() {
        mVideoView.setVideoURI(Uri.parse(mUri));
        mVideoView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mRetryContainer.setVisibility(View.GONE);
        mProgressBar.start();
        if (!mVideoView.isPlaying()) {
            mVideoView.start();
        }
        isHasLoaded = true;
    }

    private void continueLoad() {
        log("suj", mVideoView.getCurrentPosition() + "");
        mVideoView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mRetryContainer.setVisibility(View.GONE);
        mProgressBar.start();
        if (!mVideoView.isPlaying()) {
            mVideoView.start();
        }
    }

    protected void showNotify(NET_STATUS current) {
        if (null == current) {
            return;
        }
        if (current == NET_STATUS.MOBILE) {
            showNotify2g();
        }
        if (current == NET_STATUS.NO) {
            showOffLineNotify();
        }
    }

    protected void showNotify2g() {
        if (isNeededNotify()) {
            if (mVideoView != null) {
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                }
            }
            show2gOr3gReminder();
        }
    }

    private void log(String tag, String content) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, content);
        }

    }

    /**
     * 判定是否已经提示过
     *
     * @return
     */
    private boolean isHasNotified = false;

    private boolean isNeededNotify() {
        if (mVideoView == null) {
            return false;
        }
        if (isHasNotified) {
            return false;
        }
        if (mVideoView.getBufferPercentage() != 100) {
            return false;
        }
        return true;
    }

    private void showOffLineNotify() {
    }

    @Override
    protected void onEventInUiThread(ONewsEvent event) {
        super.onEventInUiThread(event);
        if (isFinishing()) {
            return;
        }
        if (event instanceof EventNetworkChanged) {
            log("suj", "network change error");
            onHandleEvent_EventNetworkChanged((EventNetworkChanged) event);
        } else if (event instanceof EventContentIdError) {
            setSourceNotFoundState();
        } else if (event instanceof EventPrepareVideoPlayer) {
            mVideoView.setVideoURI(Uri.parse(mUri));
        }
    }

    /**
     * 算法上报
     */
    private void reportAlgorithm() {
        NewsAlgorithmReport.algorithmNewsReadTime_NORMAL(mONews, mONewsScenario, adder.end());
    }

    private int infocshare = 0;

    public int infocshare() {
        return infocshare;
    }

    public int infoctime() {
        if (null != timer) {
            return timer.end();
        }
        return 0;
    }

    private void loadDetail() {
        ONewsScenario scenario = getScenarioByCategory(ONewsScenarioCategory.SC_1C);
        LOAD_DETAILS DETAILS = new LOAD_DETAILS(scenario);
        DETAILS.contetnIds().add(mONews.contentid());

        //通知栏来源
        if (isFromPush()) {
            DETAILS.STATE_GCM();
        }

        new ONewsDetailLoader() {
            @Override
            protected void onLoadResultInBackground(ONewsLoadResult_LOAD_REMOTE r) {
                super.onLoadResultInBackground(r);
                try {
                    if (false == r.IS_RESULT_STATE_NO_NETWORK() && (r.response.newsList() != null && r.response.newsList().size() > 0)) {
                        L.newsDetail("[loadDetail onLoadResultInBackground] : " + r.response.newsList().toString());
                        mONews = r.response.newsList().get(0);
                        ArrayList<String> videoList = mONews.bodyvideosList();
                        if (null != videoList && videoList.size() > 0) {
                            String jsonArray = videoList.get(0);
                            try {
                                JSONObject jsonObject = new JSONObject(jsonArray);
                                mUri = jsonObject.optString("url");
                                if (isFromPush()) {
                                    String upack = r.response.header().upack();
                                    NewsAlgorithmReport.algorithmNewsClick_GCM(upack, mONews);
                                }
                                FireEvent.FIRE_Event_PREPARE_VIDEO_PLAYER();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                FireEvent.FIRE_Event_ContentId_Error();
                            }
                        }
                    } else {
                        FireEvent.FIRE_Event_ContentId_Error();
                    }
                } catch (Exception e) {
                    FireEvent.FIRE_Event_ContentId_Error();
                    e.printStackTrace();
                }
            }
        }.execute(DETAILS);
    }

    protected void onHandleEvent_EventNetworkChanged(EventNetworkChanged event){
        showNotify(event.curr());
    }
}

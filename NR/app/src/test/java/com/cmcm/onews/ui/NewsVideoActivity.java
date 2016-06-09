package com.cmcm.onews.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

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
import com.cmcm.onews.sdk.videoLib.BuildConfig;
import com.cmcm.onews.sdk.videoLib.components.SmoothStreamingTestMediaDrmCallback;
import com.cmcm.onews.sdk.videoLib.components.WidevineTestMediaDrmCallback;
import com.cmcm.onews.sdk.videoLib.components.builder.DashRendererBuilder;
import com.cmcm.onews.sdk.videoLib.components.builder.ExtractorRendererBuilder;
import com.cmcm.onews.sdk.videoLib.components.builder.HlsRendererBuilder;
import com.cmcm.onews.sdk.videoLib.components.builder.ONewsVideoPlayer;
import com.cmcm.onews.sdk.videoLib.components.builder.SmoothStreamingRendererBuilder;
import com.cmcm.onews.service.ReportDetailPercent;
import com.cmcm.onews.system.NET_STATUS;
import com.cmcm.onews.ui.item.NewsAlgorithmReport;
import com.cmcm.onews.ui.widget.CommonNewsDialog;
import com.cmcm.onews.ui.widget.INewsNotifyDialogClick;
import com.cmcm.onews.ui.widget.MareriaProgressBar;
import com.cmcm.onews.util.NetworkUtil;
import com.cmcm.onews.util.ReportThread;
import com.cmcm.onews.util.TimeAdder;
import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.exoplayer.metadata.GeobMetadata;
import com.google.android.exoplayer.metadata.PrivMetadata;
import com.google.android.exoplayer.metadata.TxxxMetadata;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.upstream.HttpDataSource;
import com.google.android.exoplayer.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.cmcm.onews.model.ONewsScenario.getScenarioByCategory;

import com.cmcm.onews.util.NewsDebugConfigUtil;
import com.cmcm.onews.ui.debug.NewsDebugDetailResultActivity;

/**
 * Created by Jason.Su on 2015/12/22.
 * com.cmcm.onews.ui
 * des: 负责详情页面的视频展示工作
 * email:suyanjiao@conew.com
 * 1553202383
 */
public class NewsVideoActivity extends NewsBaseUIActivity implements SurfaceHolder.Callback, View.OnClickListener,
        ONewsVideoPlayer.Listener, ONewsVideoPlayer.CaptionListener, ONewsVideoPlayer.Id3MetadataListener,
        AudioCapabilitiesReceiver.Listener {


    public static final String CONTENT_ID_EXTRA = "content_id";
    public static final String CONTENT_TYPE_EXTRA = "content_type";
    public static final int TYPE_DASH = 0;
    public static final int TYPE_SS = 1;
    public static final int TYPE_HLS = 2;
    public static final int TYPE_OTHER = 3;
    public static final int TYPE_UNSET = -1;

    // For use when launching the demo app using adb.
    private static final String CONTENT_EXT_EXTRA = "type";
    private static final String EXT_DASH = ".mpd";
    private static final String EXT_SS = ".ism";
    private static final String EXT_HLS = ".m3u8";

    private static final String TAG = "NewsVideoActivity";

    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private MediaController mMediaController;
    //  private View debugRootView;
    private View mShutterView;
    private AspectRatioFrameLayout mAspectRatioFrameLayout;
    private SurfaceView mSurfaceView;

    private ONewsVideoPlayer mONewsVideoPlayer;
    private boolean mPlayerNeedsPrepare;

    private long mPlayerPosition;
    private boolean mEnableBackgroundAudio = false;

    private Uri mContentUri;
    private int mContentType;
    private String mContentId;
    private String mContentTypeExtra;

    private AudioCapabilitiesReceiver mAudioCapabilitiesReceiver;

    private ONews mONews;
    private ONewsScenario mONewsScenario;
    private TimeAdder timer;
    private long position;
    private long duration;
    private CommonNewsDialog mCommonNewsDialog;

    // Activity lifecycle

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

        setContentView(R.layout.onews_activity_video);

        mShutterView = findViewById(R.id.shutter);
        View root = findViewById(R.id.root);

        mAspectRatioFrameLayout = (AspectRatioFrameLayout) findViewById(R.id.video_frame);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        mSurfaceView.getHolder().addCallback(this);
        mMediaController = new MediaController(this);
        mMediaController.setAnchorView(root);


        CookieHandler currentHandler = CookieHandler.getDefault();
        if (currentHandler != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        mAudioCapabilitiesReceiver = new AudioCapabilitiesReceiver(this, this);
        mAudioCapabilitiesReceiver.register();
        /*************为了不同网络做准备*****************************/
        init();
        /*****************************************/

        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE
                        || keyCode == KeyEvent.KEYCODE_MENU) {
                    return false;
                }
                return mMediaController.dispatchKeyEvent(event);
            }
        });
        timer = new TimeAdder();
        timer.zero();
    }

    private void initData() {
        try {
            Intent intent = getIntent();

            Bundle bundle  = intent.getBundleExtra(KEY_BUNDLE);
            if(null != bundle){
                bundle.setClassLoader(ONewsScenario.class.getClassLoader());
                this.mONewsScenario = bundle.getParcelable(KEY_SCENARIO);
            }

            android.content.ContentValues values = intent.getParcelableExtra(KEY_NEWS);
            this.mONews = com.cmcm.onews.model.ONews.fromContentValues(values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        releasePlayer();
        mPlayerPosition = 0;
        setIntent(intent);
        initData();
        this.mFrom = intent.getIntExtra(NewsBaseActivity.KEY_FROM, FROM_HOME);
        ((TextView) findViewById(R.id.tv_content_id)).setText(mONews.contentid());
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();

        mContentId = mONews.contentid();
        mContentUri = intent.getData();
        mContentTypeExtra = intent.getStringExtra(CONTENT_EXT_EXTRA);
        if (null != mContentUri) {
            mContentType = intent.getIntExtra(CONTENT_TYPE_EXTRA,
                    inferContentType(mContentUri, mContentTypeExtra));
            prepareVideoPlayer();
        } else {
            mContentType = intent.getIntExtra(CONTENT_TYPE_EXTRA, TYPE_UNSET);
            loadDetail();
            setLoadingState();
        }
    }

    private void prepareVideoPlayer() {

        if (mONewsVideoPlayer == null) {
            int netWorkStatus = getNetWorkStatus();
            if (netWorkStatus != NETWORK_3G || s3gNotified) {
                preparePlayer(true);
            }
        } else {
            mONewsVideoPlayer.setBackgrounded(false);
        }
        if (mPaused) {
            if (mONewsVideoPlayer != null) {
                mONewsVideoPlayer.pause();
            }
        }
        findViewById(R.id.root).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    toggleControlsVisibility();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.performClick();
                }
                return true;
            }
        });
        mPaused = false;
        timer.resume();
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
                                String url = jsonObject.optString("url");
                                mContentUri = Uri.parse(url);
                                if (TYPE_UNSET == mContentType) {
                                    mContentType = inferContentType(mContentUri, mContentTypeExtra);
                                }
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

    @Override
    public void onPause() {

        super.onPause();

        reportDuration(APP_TIME_VIDEO);
        if (null != adder && null != mONews && null != mONewsScenario) {
            reportAlgorithm();
            adder.zero();
        }
        if (mONewsVideoPlayer != null) {
            position = mONewsVideoPlayer.getCurrentPosition();
            duration = mONewsVideoPlayer.getDuration();
            mPaused = !mONewsVideoPlayer.getPlayWhenReady();
        }

        if (!mEnableBackgroundAudio) {
            releasePlayer();
        } else {
            mONewsVideoPlayer.setBackgrounded(true);
        }
        mShutterView.setVisibility(View.VISIBLE);
        timer.pause();
    }

    @Override
    public void onDestroy() {
        if (mCommonNewsDialog != null) {
            mCommonNewsDialog.dismissDialog();
        }
        int percent = 0;
        if (duration != 0) {
            percent = (int) (position * 100 / duration);
            percent = Math.min(100, Math.max(0, percent));
        }
        int total_Time = infoctime();
        L.videoplayer("onDestroy percent : " + percent);
        L.videoplayer("onDestroy total time : " + total_Time);
        ReportThread.post(new ReportDetailPercent(mONews, mONewsScenario, DETAIL_FROM_LIST, percent, "", infocshare(), total_Time, "", ""));
        mHandler.removeCallbacks(mPlayStateDelayed);
        super.onDestroy();
        mAudioCapabilitiesReceiver.unregister();
        releasePlayer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFromPush()) {
            if (mMediaController.isShowing()) {
                hideControls();
            } else {
                startToList();
                finish();
            }
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
    // OnClickListener methods

    @Override
    public void onClick(View view) {
    }


    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        if (mONewsVideoPlayer == null) {
            return;
        }
        boolean backgrounded = mONewsVideoPlayer.getBackgrounded();
        boolean playWhenReady = mONewsVideoPlayer.getPlayWhenReady();
        releasePlayer();
        preparePlayer(playWhenReady);
        mONewsVideoPlayer.setBackgrounded(backgrounded);
    }

    // Internal methods

    private ONewsVideoPlayer.RendererBuilder getRendererBuilder() {
        /**
         * 这一句话，必须要的，其他的还好说
         */
        String userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
        log("suj", mContentId);
        switch (mContentType) {
            case TYPE_SS:
                return new SmoothStreamingRendererBuilder(this, userAgent, mContentUri.toString(),
                        new SmoothStreamingTestMediaDrmCallback());
            case TYPE_DASH:
                return new DashRendererBuilder(this, userAgent, mContentUri.toString(),
                        new WidevineTestMediaDrmCallback(mContentId));
            case TYPE_HLS:
                return new HlsRendererBuilder(this, userAgent, mContentUri.toString());
            case TYPE_OTHER:
                return new ExtractorRendererBuilder(this, userAgent, mContentUri);
            default:
                throw new IllegalStateException("Unsupported type: " + mContentType);
        }
    }

    private boolean mPaused = false;

    private void preparePlayer(boolean playWhenReady) {
        if (mONewsVideoPlayer == null) {
            mONewsVideoPlayer = new ONewsVideoPlayer(getRendererBuilder());
            mONewsVideoPlayer.addListener(this);
            mONewsVideoPlayer.setCaptionListener(this);
            mONewsVideoPlayer.setMetadataListener(this);
            mONewsVideoPlayer.seekTo(mPlayerPosition);
            mPlayerNeedsPrepare = true;
            mMediaController.setMediaPlayer(mONewsVideoPlayer.getPlayerControl());
            mMediaController.setEnabled(true);
        }
        if (mPlayerNeedsPrepare) {
            mONewsVideoPlayer.prepare();
            mPlayerNeedsPrepare = false;
            updateButtonVisibilities();
        }
        mONewsVideoPlayer.setSurface(mSurfaceView.getHolder().getSurface());
        mONewsVideoPlayer.setPlayWhenReady(playWhenReady);
    }

    private void releasePlayer() {
        if (mONewsVideoPlayer != null) {
            mPlayerPosition = mONewsVideoPlayer.getCurrentPosition();
            mONewsVideoPlayer.release();
            mONewsVideoPlayer = null;

        }
    }

    // ONewsVideoPlayer.Listener implementation

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            showControls();
        }
        updateButtonVisibilities();
        log("suj", "playWhenReady" + playWhenReady + "--------------" + playbackState);
        if (playWhenReady && playbackState == ONewsVideoPlayer.STATE_ENDED) {
            onCompleted();
            return;
        }
        if (playWhenReady && playbackState == ONewsVideoPlayer.STATE_IDLE) {
            onError();
            return;
        }
        if (playWhenReady && playbackState == ONewsVideoPlayer.STATE_READY) {
            onPrepared();
            return;
        }
    }

    @Override
    public void onError(Exception e) {
        if (e instanceof UnsupportedDrmException) {
            // Special case DRM failures.
            UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
            int stringId = Util.SDK_INT < 18 ? R.string.drm_error_not_supported
                    : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                    ? R.string.drm_error_unsupported_scheme : R.string.drm_error_unknown;
            Toast.makeText(getApplicationContext(), stringId, Toast.LENGTH_LONG).show();
        }
        mPlayerNeedsPrepare = true;
        if (e != null && e.getCause() != null) {
            if (e.getCause() instanceof HttpDataSource.InvalidResponseCodeException) {
                setSourceNotFoundState();
            }
        }
    }

    private boolean mSourceNotFound = false;

    private void setSourceNotFoundState() {
        mSourceNotFound = true;
        mProgressBar.setVisibility(View.GONE);
        mProgressBar.stop();
        mRetryRoot.setVisibility(View.VISIBLE);
        if (mONewsVideoPlayer != null && mONewsVideoPlayer.isPlaying()) {
            mONewsVideoPlayer.pause();
        }
        mIINetImage.setImageResource(R.drawable.onews_source_not_found);
        mErrorText1.setText(R.string.onews_404);
        mErrorText2.setVisibility(View.GONE);
        mErrorBack.setText(R.string.onews_sdk_back);
        findViewById(R.id.root).setOnTouchListener(null);
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishIfFromGCM();
            }
        });
    }

    private void finishIfFromGCM() {
        if (isFromPush()) {
            startToList();
        }
        finish();
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                   float pixelWidthAspectRatio) {
        mShutterView.setVisibility(View.GONE);
        mAspectRatioFrameLayout.setAspectRatio(
                height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
    }

    // User controls
    private void updateButtonVisibilities() {

    }

    private void toggleControlsVisibility() {
        if (mMediaController == null) {
            return;
        }
        if (mMediaController.isShowing()) {
            hideControls();
        } else {
            showControls();
        }
    }

    private void hideControls() {
        mMediaController.hide();
        if (mBtnClose != null) {
            mBtnClose.setVisibility(View.GONE);
        }
        if (NewsDebugConfigUtil.getInstance().isShowNewsId()) {
            findViewById(R.id.tv_content_id).setVisibility(View.GONE);
        }
    }

    private void showControls() {
        mMediaController.show(0);
        if (mBtnClose != null) {
            mBtnClose.setVisibility(View.VISIBLE);
        }
        if (NewsDebugConfigUtil.getInstance().isShowNewsId()) {
            TextView tv_content_id = (TextView) findViewById(R.id.tv_content_id);
            tv_content_id.setText(mONews.contentid());
            tv_content_id.setVisibility(View.VISIBLE);
        }
    }

    // ONewsVideoPlayer.CaptionListener implementation

    @Override
    public void onCues(List<Cue> cues) {
    }

    // ONewsVideoPlayer.MetadataListener implementation

    @Override
    public void onId3Metadata(Map<String, Object> metadata) {
        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            if (TxxxMetadata.TYPE.equals(entry.getKey())) {
                TxxxMetadata txxxMetadata = (TxxxMetadata) entry.getValue();
                Log.i(TAG, String.format("ID3 TimedMetadata %s: description=%s, value=%s",
                        TxxxMetadata.TYPE, txxxMetadata.description, txxxMetadata.value));
            } else if (PrivMetadata.TYPE.equals(entry.getKey())) {
                PrivMetadata privMetadata = (PrivMetadata) entry.getValue();
                Log.i(TAG, String.format("ID3 TimedMetadata %s: owner=%s",
                        PrivMetadata.TYPE, privMetadata.owner));
            } else if (GeobMetadata.TYPE.equals(entry.getKey())) {
                GeobMetadata geobMetadata = (GeobMetadata) entry.getValue();
                Log.i(TAG, String.format("ID3 TimedMetadata %s: mimeType=%s, filename=%s, description=%s",
                        GeobMetadata.TYPE, geobMetadata.mimeType, geobMetadata.filename,
                        geobMetadata.description));
            } else {
                Log.i(TAG, String.format("ID3 TimedMetadata %s", entry.getKey()));
            }
        }
    }

    // SurfaceHolder.Callback implementation

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mONewsVideoPlayer != null) {
            mONewsVideoPlayer.setSurface(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Do nothing.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mONewsVideoPlayer != null) {
            mONewsVideoPlayer.blockingClearSurface();
        }
    }

    /**
     * Makes a best guess to infer the type from a media {@link Uri} and an optional overriding file
     * extension.
     *
     * @param uri           The {@link Uri} of the media.
     * @param fileExtension An overriding file extension.
     * @return The inferred type.
     */
    private static int inferContentType(Uri uri, String fileExtension) {
        String lastPathSegment = !TextUtils.isEmpty(fileExtension) ? "." + fileExtension
                : uri.getLastPathSegment();
        if (lastPathSegment == null) {
            return TYPE_OTHER;
        } else if (lastPathSegment.endsWith(EXT_DASH)) {
            return TYPE_DASH;
        } else if (lastPathSegment.endsWith(EXT_SS)) {
            return TYPE_SS;
        } else if (lastPathSegment.endsWith(EXT_HLS)) {
            return TYPE_HLS;
        } else {
            return TYPE_OTHER;
        }
    }

    /********************
     * 我是分割线，割---------------------------start--
     **********************/
    private View mRefresh;
    private MareriaProgressBar mProgressBar;
    private boolean mError = false;
    private View mRetryRoot = null;
    private View mBtnClose = null;
    private ImageView mIINetImage;
    private TextView mErrorText1;
    private TextView mErrorText2;
    private TextView mErrorBack;

    private void init() {
        mRetryRoot = findViewById(R.id.retry_root);
        View retry = findViewById(R.id.retry);
        mErrorText1 = ((TextView) retry.findViewById(R.id.onews__list_empty_t1));
        mErrorText1.setTextColor(Color.WHITE);
        mErrorText2 = ((TextView) retry.findViewById(R.id.onews__list_empty_t2));
        mErrorText2.setTextColor(Color.WHITE);
        mErrorBack = (TextView) findViewById(R.id.ii_btn_close);
        mRefresh = retry.findViewById(R.id.news_button_refresh);
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forceRefresh();
            }
        });
        mIINetImage = (ImageView) retry.findViewById(R.id.iv_no_net);
        mProgressBar = (MareriaProgressBar) findViewById(R.id.progress);
        setUIBasedTimer();
        mBtnClose = findViewById(R.id.btn_close);
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishIfFromGCM();
            }
        });
        if (NewsDebugConfigUtil.getInstance().isShowNewsId()) {
            findViewById(R.id.tv_content_id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewsDebugDetailResultActivity.startDefault(NewsVideoActivity.this,
                            mONews.contentid(),
                            mONewsScenario);
                }
            });
        }
    }

    private void onError() {
        if (mSourceNotFound) {
            return;
        }
        log("suj", "onError");
        setErrorState();
        mPlaystate = STATUS_ERROR;
    }

    private void onCompleted() {
        log("suj", "onCompleted");
        mPlaystate = STATUS_COMPLETED;
        finishIfFromGCM();
    }

    private final static int STATUS_ERROR = ONewsVideoPlayer.STATE_IDLE;
    private final static int STATUS_PREPARED = ONewsVideoPlayer.STATE_READY;
    private final static int STATUS_COMPLETED = ONewsVideoPlayer.STATE_ENDED;
    private int mPlaystate = ONewsVideoPlayer.STATE_IDLE;

    private void onPrepared() {
        log("suj", "onPrepared");
        setPlayState();
        mPlaystate = STATUS_PREPARED;
    }

    private void log(String tag, String content) {
        L.log(tag, content);
    }


    private Handler mHandler = new Handler(Looper.getMainLooper());

    private void setUIBasedTimer() {
        // first check out timer
        int netWork = getNetWorkStatus();
        if (netWork == NETWORK_ERROR) {
            setErrorState();
            return;
        }
        if (netWork == NETWORK_3G && !s3gNotified) {
            set3gState();
        } else {
            setLoadingState();
        }

    }

    private void forceRefresh() {
        setLoadingState();
        mHandler.postDelayed(mPlayStateDelayed, DELAYEDTIMES);
    }

    private Runnable mPlayStateDelayed = new Runnable() {
        @Override
        public void run() {
            if (mPlaystate != STATUS_PREPARED) {
                log("suj", "forceRefresh");
                setErrorState();
            }
        }
    };

    private final static int DELAYEDTIMES = 5000;
    private final static int MOBILE_DELAYED = 10000;

    private static final int NETWORK_ERROR = -1;
    private static final int NETWORK_3G = 1;
    private final static int NETWORK_WIFI = 2;

    // check play state ,if meet error

    /**
     * 关于play state
     * <p/>
     * http://google.github.io/ExoPlayer/doc/reference/
     * </>
     *
     * @return
     */
    private int getPlayerState() {
        if (mONewsVideoPlayer != null) {
            return ONewsVideoPlayer.STATE_BUFFERING;
        }
        return mONewsVideoPlayer.getPlaybackState();
    }


    private void setLoadingState() {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.start();
        mRetryRoot.setVisibility(View.GONE);
        if (mSurfaceView.getVisibility() != View.VISIBLE) {
            mSurfaceView.setVisibility(View.VISIBLE);
        }
        if (mONewsVideoPlayer != null) {
            preparePlayer(true);
        }
    }

    private void setPlayState() {
        if (mSurfaceView.getVisibility() != View.VISIBLE) {
            mSurfaceView.setVisibility(View.VISIBLE);
        }
        mProgressBar.setVisibility(View.GONE);
        mProgressBar.stop();
        mRetryRoot.setVisibility(View.GONE);
        preparePlayer(true);
    }

    private static boolean s3gNotified = false;

    private void set3gState() {
        if (s3gNotified) {
            return;
        }
        if (mONewsVideoPlayer != null) {
            mONewsVideoPlayer.pause();
        }
        show3gNotified();

    }

    private void show3gNotified() {
        mCommonNewsDialog = new CommonNewsDialog(this, new INewsNotifyDialogClick() {
            @Override
            public void clickContinue() {
                setPlayState();
                s3gNotified = true;
            }

            @Override
            public void clickCancel() {
                finishIfFromGCM();

            }
        }, getString(R.string.onews__notify_not_wifi_reminder));
        mCommonNewsDialog.setCanceledOnTouchOutside(false);
        mCommonNewsDialog.showDialog();
    }

    private void setErrorState() {
        mProgressBar.setVisibility(View.GONE);
        mProgressBar.stop();
        mRetryRoot.setVisibility(View.VISIBLE);
        if (mONewsVideoPlayer != null && mONewsVideoPlayer.isPlaying()) {
            mONewsVideoPlayer.pause();
        }

    }

    /**
     * {@link #NETWORK_ERROR} 不可用
     * {@link #NETWORK_WIFI} wifi
     * {@link #NETWORK_3G} mobie data 数据流量
     *
     * @return
     */
    private int getNetWorkStatus() {
        if (!NetworkUtil.isNetworkAvailable(this)) {
            return NETWORK_ERROR;
        }
        if (NetworkUtil.isWifiNetworkUp(this)) {
            return NETWORK_WIFI;
        }
        if (NetworkUtil.isMobileNetworkUp(this)) {
            return NETWORK_3G;
        }
        return NETWORK_ERROR;
    }

    protected void showNotify(NET_STATUS current) {
        if (null == current) {
            return;
        }
        judgeChangeState(current);
    }

    private void judgeChangeState(NET_STATUS current) {
        if (mONewsVideoPlayer != null && mONewsVideoPlayer.getBufferedPercentage() == 100) {
            return;
        }
        if (current == NET_STATUS.MOBILE) {
            log("suj", "mobile state");
            set3gState();
            return;
        }

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
            prepareVideoPlayer();
        }
    }

    public static void ON_START_VIDEOACTIVITY(Context context, ONews oNews, ONewsScenario oNewsScenario) {
        if (null == context || null == oNews || null == oNewsScenario) {
            return;
        }
        ArrayList<String> videoList = oNews.bodyvideosList();
        if (null != videoList && videoList.size() > 0) {
            String jsonArray = videoList.get(0);
            try {
                JSONObject jsonObject = new JSONObject(jsonArray);
                String url = jsonObject.optString("url");
                Long duration = jsonObject.optLong("duration");
                String description = jsonObject.optString("description");
                String thumbnail = jsonObject.optString("thumbnail");
                if (BuildConfig.DEBUG) {
                    Log.e("suj", "url:" + url + ", duration:" + duration + ", description:" + description + ", thumbnail:" + thumbnail);
                }
                //url="http://ns.ibnlive.in.com/12_2015/30-12-2015/assam_poll_pkg.mp4";
                NewsVideoActivity.ON_START_VIDEOACTIVITY(context, url, duration, oNews, oNewsScenario);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void ON_START_VIDEOACTIVITY(Context context, String url, long duration, ONews oNews, ONewsScenario oNewsScenario) {
        try {
            Log.e("suj", "url---------" + url);
            if (false || Build.VERSION.SDK_INT < 16) {
                NewsVideoFullScreenActivity.on_HANDLESTARTACTIVITY(context, url, duration, oNews, oNewsScenario);
                return;
            } else {


                Intent mpdIntent = new Intent(context, NewsVideoActivity.class)
                        .setData(Uri.parse(url))
                        .putExtra(NewsVideoActivity.CONTENT_ID_EXTRA, "video")
                        .putExtra(NewsBaseActivity.KEY_NEWS, oNews.toContentValues())
                        .putExtra(NewsVideoActivity.CONTENT_TYPE_EXTRA, TYPE_OTHER);

                Bundle bundle = new Bundle();
                bundle.putParcelable(NewsBaseActivity.KEY_SCENARIO, oNewsScenario);
                mpdIntent.putExtra(NewsBaseActivity.KEY_BUNDLE, bundle);

                mpdIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mpdIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /********************我是分割线，割---------------------------end--**********************/

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

    protected void onHandleEvent_EventNetworkChanged(EventNetworkChanged event){
        showNotify(event.curr());
    }
}


package com.cmcm.onews.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmcm.onews.C;
import com.cmcm.onews.R;
import com.cmcm.onews.infoc.newsindia_interest;
import com.cmcm.onews.model.ONewsInterest;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.ui.NewsActivity;
import com.cmcm.onews.util.DimenSdkUtils;
import com.cmcm.onews.util.UIConfigManager;

import java.util.ArrayList;
import java.util.List;

public class NewsCategoryView extends LinearLayout implements View.OnClickListener {

    private static final long ANIMATION_DURATION = 300L;

    private LinearLayout mTitleLL;
    private LinearLayout mMiddleLL;
    private LinearLayout mBottomLL;

    private RelativeLayout mStartRL;
    private LinearLayout mSkiptRL;

    private TextView mStartTV;
    private TextView mTitleTV;
    private TextView mDesTV;

    // 动画布局
    private RelativeLayout mOneRL;
    private RelativeLayout mTwoRL;
    private RelativeLayout mThreeRL;
    private RelativeLayout mFourRL;
    private RelativeLayout mFiveRL;
    private RelativeLayout mSixRL;
    private RelativeLayout mSevenRL;
    private RelativeLayout mEightRL;

    // 圆形背景色值
    private RelativeLayout mOneRLBg;
    private RelativeLayout mTwoRLBg;
    private RelativeLayout mThreeRLBg;
    private RelativeLayout mFourRLBg;
    private RelativeLayout mFiveRLBg;
    private RelativeLayout mSixRLBg;
    private RelativeLayout mSevenRLBg;
    private RelativeLayout mEightRLBg;

    // 圆形背景图片
    private RelativeLayout mOneRLIv;
    private RelativeLayout mTwoRLIv;
    private RelativeLayout mThreeRLIv;
    private RelativeLayout mFourRLIv;
    private RelativeLayout mFiveRLIv;
    private RelativeLayout mSixRLIv;
    private RelativeLayout mSevenRLIv;
    private RelativeLayout mEightRLIv;

    // 选中图标
    private ImageView mOneSelectedIV;
    private ImageView mTwoSelectedIV;
    private ImageView mThreeSelectedIV;
    private ImageView mFourSelectedIV;
    private ImageView mFiveSelectedIV;
    private ImageView mSixSelectedIV;
    private ImageView mSevenSelectedIV;
    private ImageView mEightSelectedIV;

    private Activity mContext;
    private List<String> mUserSelected = new ArrayList<>();// 参考 ONewsInterest
    private List<Boolean> selected = new ArrayList<Boolean>();
    private Handler mHandler = new Handler();

    public NewsCategoryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        selected.clear();
        for(int i=0;i<8;i++){
            selected.add(false);
        }
    }

    public NewsCategoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NewsCategoryView(Context context) {
        super(context);
        init();
    }

    public void initView(Activity context) {
        this.mContext = context;
        long start = System.currentTimeMillis();
        if (null == getContext()) {
            throw new NullPointerException("NewsCategoryView getContext() can't be null");
        }
        findView();
        setListener();
        L.news_category("dtiem = " + ((System.currentTimeMillis()) - start));
    }

    private void setListener() {
        mStartRL.setOnClickListener(this);
        mSkiptRL.setOnClickListener(this);

        mOneRL.setOnClickListener(this);
        mTwoRL.setOnClickListener(this);
        mThreeRL.setOnClickListener(this);
        mFourRL.setOnClickListener(this);
        mFiveRL.setOnClickListener(this);
        mSixRL.setOnClickListener(this);
        mSevenRL.setOnClickListener(this);
        mEightRL.setOnClickListener(this);
    }

    private void findView() {
        mTitleLL = (LinearLayout) findViewById(R.id.ll_title);
        mMiddleLL = (LinearLayout) findViewById(R.id.ll_middle);
        mBottomLL = (LinearLayout) findViewById(R.id.ll_bottom);

        mStartRL = (RelativeLayout) findViewById(R.id.rl_start);
        mSkiptRL = (LinearLayout) findViewById(R.id.ll_skip);

        mTitleTV = (TextView) findViewById(R.id.category_title);
        mStartTV = (TextView) findViewById(R.id.category_start);
        mDesTV = (TextView) findViewById(R.id.category_des);


        mOneRL = (RelativeLayout) findViewById(R.id.rl_one);
        mTwoRL = (RelativeLayout) findViewById(R.id.rl_two);
        mThreeRL = (RelativeLayout) findViewById(R.id.rl_three);
        mFourRL = (RelativeLayout) findViewById(R.id.rl_four);
        mFiveRL = (RelativeLayout) findViewById(R.id.rl_five);
        mSixRL = (RelativeLayout) findViewById(R.id.rl_six);
        mSevenRL = (RelativeLayout) findViewById(R.id.rl_seven);
        mEightRL = (RelativeLayout) findViewById(R.id.rl_eight);

        mOneRLBg = (RelativeLayout) findViewById(R.id.rl_one_bg);
        mTwoRLBg = (RelativeLayout) findViewById(R.id.rl_two_bg);
        mThreeRLBg = (RelativeLayout) findViewById(R.id.rl_three_bg);
        mFourRLBg = (RelativeLayout) findViewById(R.id.rl_four_bg);
        mFiveRLBg = (RelativeLayout) findViewById(R.id.rl_five_bg);
        mSixRLBg = (RelativeLayout) findViewById(R.id.rl_six_bg);
        mSevenRLBg = (RelativeLayout) findViewById(R.id.rl_seven_bg);
        mEightRLBg = (RelativeLayout) findViewById(R.id.rl_eight_bg);

        mOneRLIv = (RelativeLayout) findViewById(R.id.rl_one_iv);
        mTwoRLIv = (RelativeLayout) findViewById(R.id.rl_two_iv);
        mThreeRLIv = (RelativeLayout) findViewById(R.id.rl_three_iv);
        mFourRLIv = (RelativeLayout) findViewById(R.id.rl_four_iv);
        mFiveRLIv = (RelativeLayout) findViewById(R.id.rl_five_iv);
        mSixRLIv = (RelativeLayout) findViewById(R.id.rl_six_iv);
        mSevenRLIv = (RelativeLayout) findViewById(R.id.rl_seven_iv);
        mEightRLIv = (RelativeLayout) findViewById(R.id.rl_eight_iv);

        mOneSelectedIV = (ImageView) findViewById(R.id.iv_one_selected);
        mTwoSelectedIV = (ImageView) findViewById(R.id.iv_two_selected);
        mThreeSelectedIV = (ImageView) findViewById(R.id.iv_three_selected);
        mFourSelectedIV = (ImageView) findViewById(R.id.iv_four_selected);
        mFiveSelectedIV = (ImageView) findViewById(R.id.iv_five_selected);
        mSixSelectedIV = (ImageView) findViewById(R.id.iv_six_selected);
        mSevenSelectedIV = (ImageView) findViewById(R.id.iv_seven_selected);
        mEightSelectedIV = (ImageView) findViewById(R.id.iv_eight_selected);

        updateLayout();
        initItemBg();
        hideIVSelected();
    }

    // 开始播放动画
    public void startAnim() {
        // title 和 bottom 暂时先不要加动画
//        mTitleLL.setVisibility(View.INVISIBLE);
//        mBottomLL.setVisibility(View.INVISIBLE);

        mOneRL.setVisibility(View.INVISIBLE);
        mTwoRL.setVisibility(View.INVISIBLE);
        mThreeRL.setVisibility(View.INVISIBLE);
        mFourRL.setVisibility(View.INVISIBLE);
        mFiveRL.setVisibility(View.INVISIBLE);
        mSixRL.setVisibility(View.INVISIBLE);
        mSevenRL.setVisibility(View.INVISIBLE);
        mEightRL.setVisibility(View.INVISIBLE);

        // title and bottom
//        mTitleLL.startAnimation(getAnimation(0, 99));
//        mBottomLL.startAnimation(getAnimation(0, 100));
        // 1 4 6
        mOneRL.startAnimation(getAnimation(0, 0));
        mFourRL.startAnimation(getAnimation(0, 3));
        mSixRL.startAnimation(getAnimation(0, 5));
        // 2 5 7
        mTwoRL.startAnimation(getAnimation(150L, 1));
        mFiveRL.startAnimation(getAnimation(150L, 4));
        mSevenRL.startAnimation(getAnimation(150L, 6));
        // 3 8
        mThreeRL.startAnimation(getAnimation(250L, 2));
        mEightRL.startAnimation(getAnimation(250L, 7));

    }

    private Animation getAnimation(long startOffset, final int index){
        /*
        * 位移动画:
        * int fromXType, float fromXValue, int toXType, float toXValue,
        * int fromYType, float fromYValue, int toYType, float toYValue
        */
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 2f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        translateAnimation.setInterpolator(new DecelerateInterpolator());

        // 透明动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);

        animationSet.setDuration(ANIMATION_DURATION);
        animationSet.setStartOffset(startOffset);
//        animationSet.setInterpolator(new LinearInterpolator());

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                switch (index) {
                    // 0-7 为每个圆形item
                    case 0:
                        mOneRL.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        mTwoRL.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        mThreeRL.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        mFourRL.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        mFiveRL.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        mSixRL.setVisibility(View.VISIBLE);
                        break;
                    case 6:
                        mSevenRL.setVisibility(View.VISIBLE);
                        break;
                    case 7:
                        mEightRL.setVisibility(View.VISIBLE);
                        break;
                    // 99 和 100 为顶部和底部布局
                    case 99:
                        mTitleLL.setVisibility(View.VISIBLE);
                        break;
                    case 100:
                        mBottomLL.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        return animationSet;
    }

    // 默认隐藏选中的图标
    private void hideIVSelected() {
        mOneSelectedIV.setVisibility(View.GONE);
        mTwoSelectedIV.setVisibility(View.GONE);
        mThreeSelectedIV.setVisibility(View.GONE);
        mFourSelectedIV.setVisibility(View.GONE);
        mFiveSelectedIV.setVisibility(View.GONE);
        mSixSelectedIV.setVisibility(View.GONE);
        mSevenSelectedIV.setVisibility(View.GONE);
        mEightSelectedIV.setVisibility(View.GONE);
    }

    // 初始化每一个圆形的背景色
    private void initItemBg() {
        GradientDrawable oneGradientDrawable = (GradientDrawable) mOneRLBg.getBackground();
        oneGradientDrawable.setColor(Color.parseColor("#1E6BC4"));

        GradientDrawable TwoGradientDrawable = (GradientDrawable) mTwoRLBg.getBackground();
        TwoGradientDrawable.setColor(Color.parseColor("#00B1BC"));

        GradientDrawable threeGradientDrawable = (GradientDrawable) mThreeRLBg.getBackground();
        threeGradientDrawable.setColor(Color.parseColor("#0190CD"));

        GradientDrawable fourGradientDrawable = (GradientDrawable) mFourRLBg.getBackground();
        fourGradientDrawable.setColor(Color.parseColor("#39B3E9"));

        GradientDrawable fiveGradientDrawable = (GradientDrawable) mFiveRLBg.getBackground();
        fiveGradientDrawable.setColor(Color.parseColor("#00B1BC"));

        GradientDrawable sixGradientDrawable = (GradientDrawable) mSixRLBg.getBackground();
        sixGradientDrawable.setColor(Color.parseColor("#0190CD"));

        GradientDrawable sevenGradientDrawable = (GradientDrawable) mSevenRLBg.getBackground();
        sevenGradientDrawable.setColor(Color.parseColor("#1E6BC4"));

        GradientDrawable eightGradientDrawable = (GradientDrawable) mEightRLBg.getBackground();
        eightGradientDrawable.setColor(Color.parseColor("#39B3E9"));
    }

    // 动态设置控件间距
    private void updateLayout() {
        int screenHeightDp = (int) (DimenSdkUtils.getScreenHeight() / DimenSdkUtils.getDensity());

        // 动态设置标题大小
        mTitleTV.setTextSize((screenHeightDp / (640 / 30) ));

        // 每个item的宽高
        int itemSize = dp2px((int) (((float) 87 / 640) * screenHeightDp));
        DimenSdkUtils.updateLayout(mOneRLBg, itemSize, itemSize);
        DimenSdkUtils.updateLayout(mTwoRLBg, itemSize, itemSize);
        DimenSdkUtils.updateLayout(mThreeRLBg, itemSize, itemSize);
        DimenSdkUtils.updateLayout(mFourRLBg, itemSize, itemSize);
        DimenSdkUtils.updateLayout(mFiveRLBg, itemSize, itemSize);
        DimenSdkUtils.updateLayout(mSixRLBg, itemSize, itemSize);
        DimenSdkUtils.updateLayout(mSevenRLBg, itemSize, itemSize);
        DimenSdkUtils.updateLayout(mEightRLBg, itemSize, itemSize);

        // 每个item的上下边距
        int itemPadding = dp2px((int) (((float) 24 / 640) * screenHeightDp));
        int itemHalfPadding = itemPadding / 2;
        updateViewPadding(mOneRL, 0, itemHalfPadding, 0, itemHalfPadding);
        updateViewPadding(mTwoRL, 0, itemHalfPadding, 0, itemHalfPadding);
        updateViewPadding(mThreeRL, 0, itemHalfPadding, 0, itemHalfPadding);
        updateViewPadding(mFourRL, 0, itemHalfPadding, 0, itemHalfPadding);
        updateViewPadding(mFiveRL, 0, itemHalfPadding, 0, itemHalfPadding);
        updateViewPadding(mSixRL, 0, itemHalfPadding, 0, itemHalfPadding);
        updateViewPadding(mSevenRL, 0, itemHalfPadding, 0, itemHalfPadding);
        updateViewPadding(mEightRL, 0, itemHalfPadding, 0, itemHalfPadding);

        // 部分item的左右边距
        int itemMargin = dp2px((int) (((float) 17 / 640) * screenHeightDp));
        DimenSdkUtils.updateLayoutMargin(mOneRL, 0, 0, itemMargin, 0);
        DimenSdkUtils.updateLayoutMargin(mThreeRL, itemMargin, 0, 0, 0);
        DimenSdkUtils.updateLayoutMargin(mFiveRL, itemMargin, 0, 0, 0);
        DimenSdkUtils.updateLayoutMargin(mSixRL, 0, 0, itemMargin, 0);
        DimenSdkUtils.updateLayoutMargin(mEightRL, itemMargin, 0, 0, 0);

        // title 距离上边距
        DimenSdkUtils.updateLayoutMargin(mTitleLL, 0, dp2px((int) (((float) 56 / 640) * screenHeightDp)), 0, 0);
        // itemLayout 距离上边距
        DimenSdkUtils.updateLayoutMargin(mMiddleLL, 0, dp2px((int) (((float) 30 / 640) * screenHeightDp)), 0, 0);
        // start 距离上边距
        DimenSdkUtils.updateLayoutMargin(mStartRL, 0, dp2px((int) (((float) 32 / 640) * screenHeightDp)), 0, 0);
        // skip 距离上边距
        DimenSdkUtils.updateLayoutMargin(mSkiptRL, 0, dp2px((int) (((float) 11 / 640) * screenHeightDp)), 0, 0);
    }

    private void updateViewPadding(View view, int left, int top, int right, int bottom){
        view.setPadding(left, top, right, bottom);
    }

    private int dp2px(int value){
        return DimenSdkUtils.dp2px(value);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_start:
                // 如果没有选择一个兴趣标签，不允许点击start按钮
                if (mUserSelected.isEmpty()){
                    shakeDestv();
                    return;
                }
                action = 1;
                UIConfigManager.getInstanse(C.getAppContext()).setNEWS_USER_GUIDE();
                delayToNews(false);
                break;
            case R.id.ll_skip:
                action = 2;
                UIConfigManager.getInstanse(C.getAppContext()).setNEWS_USER_GUIDE();
                delayToNews(true);
                break;
            case R.id.rl_one:
                processOneRLClick();
                break;
            case R.id.rl_two:
                processTwoRLClick();
                break;
            case R.id.rl_three:
                processThreeRLClick();
                break;
            case R.id.rl_four:
                processFourRLClick();
                break;
            case R.id.rl_five:
                processFiveRLClick();
                break;
            case R.id.rl_six:
                processSixRLClick();
                break;
            case R.id.rl_seven:
                processSevenRLClick();
                break;
            case R.id.rl_eight:
                processEightRLClick();
                break;
        }
    }

    // 抖动描述文本
    private void shakeDestv() {
        mDesTV.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.onews__category_title_scale));
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDesTV.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.onews__category_title_scale_normal));
            }
        },150);
    }

    private void delayToNews(boolean isSkip) {
        final long time = (null == mUserSelected || mUserSelected.isEmpty() || isSkip) ? 0 : 300;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (time > 0) {
                    String content = mUserSelected.toString().replace("[", "").replace("]", "").replace(" ", "");
                    UIConfigManager.getInstanse(C.getAppContext()).setNEWS_CATEGORY_INTEREST(content);
                }
                NewsActivity.startActivity(mContext);
                mContext.finish();
            }
        }, time);
    }

    private void processEightRLClick() {
        mEightRL.startAnimation(getItemClickAnimation(7));
    }

    private void processSevenRLClick() {
        mSevenRL.startAnimation(getItemClickAnimation(6));
    }

    private void processSixRLClick() {
        mSixRL.startAnimation(getItemClickAnimation(5));
    }

    private void processFiveRLClick() {
        mFiveRL.startAnimation(getItemClickAnimation(4));
    }

    private void processFourRLClick() {
        mFourRL.startAnimation(getItemClickAnimation(3));
    }

    private void processThreeRLClick() {
        mThreeRL.startAnimation(getItemClickAnimation(2));
    }

    private void processTwoRLClick() {
        mTwoRL.startAnimation(getItemClickAnimation(1));
    }

    private void processOneRLClick() {
        mOneRL.startAnimation(getItemClickAnimation(0));
    }

    // 获取点击每个item的动画
    public Animation getItemClickAnimation(final int index){
        //  缩放动画：float fromX, float toX, float fromY, float toY
        final ScaleAnimation scaleAnimation = new ScaleAnimation(
                0.8f, 1.0f, 0.8f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnimation.setDuration(150L);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                boolean isSelected = false;
                switch (index) {
                    case 0:// ONewsInterest.NC_03+""
                        updateViewVisible(mOneSelectedIV, mOneRLIv, R.drawable.onews__category_cricket);
                        isSelected = !mUserSelected.contains(ONewsInterest.NC_03 + "");
                        updateUserSelected(ONewsInterest.NC_03 + "");
                        updateStartView();
                        break;
                    case 1:// ONewsInterest.NC_00+""
                        updateViewVisible(mTwoSelectedIV, mTwoRLIv, R.drawable.onews__category_world);
                        isSelected = !mUserSelected.contains(ONewsInterest.NC_00 + "");
                        updateUserSelected(ONewsInterest.NC_00 + "");
                        updateStartView();
                        break;
                    case 2:// ONewsInterest.NC_02+""
                        updateViewVisible(mThreeSelectedIV, mThreeRLIv, R.drawable.onews__category_sports);
                        isSelected = !mUserSelected.contains(ONewsInterest.NC_02 + "");
                        updateUserSelected(ONewsInterest.NC_02 + "");
                        updateStartView();
                        break;
                    case 3:// ONewsInterest.NC_01+""
                        updateViewVisible(mFourSelectedIV, mFourRLIv, R.drawable.onews__category_entertain);
                        isSelected = !mUserSelected.contains(ONewsInterest.NC_01 + "");
                        updateUserSelected(ONewsInterest.NC_01 + "");
                        updateStartView();
                        break;
                    case 4:// ONewsInterest.NC_05+""
                        updateViewVisible(mFiveSelectedIV, mFiveRLIv, R.drawable.onews__category_science);
                        isSelected = !mUserSelected.contains(ONewsInterest.NC_05 + "");
                        updateUserSelected(ONewsInterest.NC_05 + "");
                        updateStartView();
                        break;
                    case 5:// ONewsInterest.NC_04+""
                        updateViewVisible(mSixSelectedIV, mSixRLIv, R.drawable.onews__category_politics);
                        isSelected = !mUserSelected.contains(ONewsInterest.NC_04 + "");
                        updateUserSelected(ONewsInterest.NC_04 + "");
                        updateStartView();
                        break;
                    case 6:// ONewsInterest.NC_06 + ""
                        updateViewVisible(mSevenSelectedIV, mSevenRLIv, R.drawable.onews__category_commerce);
                        isSelected = !mUserSelected.contains(ONewsInterest.NC_06 + "");
                        updateUserSelected(ONewsInterest.NC_06 + "");
                        updateStartView();
                        break;
                    case 7:// ONewsInterest.NC_07 + ""
                        updateViewVisible(mEightSelectedIV, mEightRLIv, R.drawable.onews__category_movie);
                        isSelected = !mUserSelected.contains(ONewsInterest.NC_07 + "");
                        updateUserSelected(ONewsInterest.NC_07 + "");
                        updateStartView();
                        break;
                }

                if (selected.size() > index) {
                    selected.remove(index);
                    selected.add(index, isSelected);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        return scaleAnimation;
    }

    // 更新用户选择的兴趣项
    private boolean updateUserSelected(String interest){
        return mUserSelected.contains(interest) ? mUserSelected.remove(interest) : mUserSelected.add(interest);
    }

    // 更新控件状态
    private void updateViewVisible(ImageView imageView, RelativeLayout relativeLayout, int resId){
        if (imageView.getVisibility() == View.VISIBLE) {
            imageView.setVisibility(View.GONE);
            relativeLayout.setBackgroundColor(Color.TRANSPARENT);
        } else {
            imageView.setVisibility(View.VISIBLE);
            relativeLayout.setBackgroundDrawable(getRoundCornerDrawable(resId));

        }
    }

    // 更新start布局
    private void updateStartView(){
        GradientDrawable startBackground = (GradientDrawable) mStartRL.getBackground();
        if (mUserSelected.isEmpty()){
            startBackground.setColor(Color.parseColor("#d3d3d3"));
            mStartTV.setTextColor(Color.parseColor("#9a9a9a"));
        } else {
            startBackground.setColor(Color.parseColor("#F3F6FA"));
            mStartTV.setTextColor(Color.parseColor("#008EDF"));
        }
    }

    // 获取圆角图片
    private Drawable getRoundCornerDrawable(int resId){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
        Bitmap roundBtm = toRoundCorner(bitmap, bitmap.getWidth()/2);
        return new BitmapDrawable(getResources(), roundBtm);
    }
    /**
     * 把图片切成圆角
     *
     * @param bitmap
     *            原始图
     * @param pixels
     *            角度大小
     * @return 圆角图片
     */
    public static final Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private int ifclick = 2;//是否点击了选择1.是    2否
    private int clicknum = 0;//选择个数
    private int interesttype;//选择了哪些类别1-8位上报
    private int action = 3; //操作   1：正常进入    2：skip    3:退出

    public void initReport(){
        if(mUserSelected != null){
            clicknum = mUserSelected.size();
            interesttype = getInterestTypes();
        }
        // 首次上报
        reportCategoryInterest(ifclick, clicknum, interesttype,action);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                reportCategoryInterest(ifclick, clicknum, interesttype, action);//需要调用两次（注：InfoC的一个BUG）
            }
        }, 500);
    }

    private int getInterestTypes(){
        String type = "";
        for(int i=0;i<selected.size();i++){
            if(selected.get(i)){
                type += String.valueOf(i+1);
            }
        }
        if(!TextUtils.isEmpty(type)){
            return  Integer.parseInt(type);
        }
        return 0;
    }

    /**
     *上报分类爱好埋点
     * @param ifclick
     * @param clicknum
     * @param interesttype
     */
    private void reportCategoryInterest(int ifclick,int clicknum,int interesttype,int action){
        newsindia_interest interest = new newsindia_interest();
        interest.ifclick(ifclick);
        interest.clicknum(clicknum);
        interest.interesttype(interesttype);
        interest.action(action);
        interest.report();
    }

    public void changeLanguage() {
        ((TextView) findViewById(R.id.category_title)).setText(R.string.onews__guide_category_title);
        ((TextView) findViewById(R.id.category_des)).setText(R.string.onews__guide_category_des);

        ((TextView) findViewById(R.id.category_one)).setText(R.string.onews__guide_category_cricket);
        ((TextView) findViewById(R.id.category_two)).setText(R.string.onews__guide_category_global);
        ((TextView) findViewById(R.id.category_three)).setText(R.string.onews__guide_category_sports);

        ((TextView) findViewById(R.id.category_four)).setText(R.string.onews__guide_category_entertainment);
        ((TextView) findViewById(R.id.category_five)).setText(R.string.onews__guide_category_technology);

        ((TextView) findViewById(R.id.category_six)).setText(R.string.onews__guide_category_politics);
        ((TextView) findViewById(R.id.category_seven)).setText(R.string.onews__guide_category_busines);
        ((TextView) findViewById(R.id.category_eight)).setText(R.string.onews__guide_category_movie);

        ((TextView) findViewById(R.id.category_start)).setText(R.string.onews__guide_category_start);
        ((TextView) findViewById(R.id.category_skip)).setText(R.string.onews__guide_categoty_skip);
    }
}

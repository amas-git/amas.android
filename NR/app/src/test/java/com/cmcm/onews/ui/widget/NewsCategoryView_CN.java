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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmcm.onews.C;
import com.cmcm.onews.R;
import com.cmcm.onews.infoc.newsindia_interest;
import com.cmcm.onews.model.ONewsInterestCN;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.ui.NewsActivity;
import com.cmcm.onews.util.DimenSdkUtils;
import com.cmcm.onews.util.UIConfigManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 国内兴趣选择页
 * Created by Sam on 2016/2/22.
 */
public class NewsCategoryView_CN extends LinearLayout implements View.OnClickListener {

    // 动画布局
    private RelativeLayout mOneRL;
    private RelativeLayout mTwoRL;
    private RelativeLayout mThreeRL;
    private RelativeLayout mFourRL;
    private RelativeLayout mFiveRL;
    private RelativeLayout mSixRL;
    private RelativeLayout mSevenRL;
    private RelativeLayout mEightRL;
    private RelativeLayout mNineRL;
    private RelativeLayout mTenRL;
    private RelativeLayout mElevenRL;
    private RelativeLayout mTwelveRL;
    private RelativeLayout mThirteenRL;
    private RelativeLayout mFourteenRL;
    private RelativeLayout mFifteenRL;

    // 圆形背景色值
    private RelativeLayout mOneRLBg;
    private RelativeLayout mTwoRLBg;
    private RelativeLayout mThreeRLBg;
    private RelativeLayout mFourRLBg;
    private RelativeLayout mFiveRLBg;
    private RelativeLayout mSixRLBg;
    private RelativeLayout mSevenRLBg;
    private RelativeLayout mEightRLBg;
    private RelativeLayout mNineRLBg;
    private RelativeLayout mTenRLBg;
    private RelativeLayout mElevenRLBg;
    private RelativeLayout mTwelveRLBg;
    private RelativeLayout mThirteenRLBg;
    private RelativeLayout mFourteenRLBg;
    private RelativeLayout mFifteenRLBg;

    // 圆形背景图片
    private RelativeLayout mOneRLIv;
    private RelativeLayout mTwoRLIv;
    private RelativeLayout mThreeRLIv;
    private RelativeLayout mFourRLIv;
    private RelativeLayout mFiveRLIv;
    private RelativeLayout mSixRLIv;
    private RelativeLayout mSevenRLIv;
    private RelativeLayout mEightRLIv;
    private RelativeLayout mNineRLIv;
    private RelativeLayout mTenRLIv;
    private RelativeLayout mElevenRLIv;
    private RelativeLayout mTwelveRLIv;
    private RelativeLayout mThirteenRLIv;
    private RelativeLayout mFourteenRLIv;
    private RelativeLayout mFifteenRLIv;

    // 选中图标
    private ImageView mOneSelectedIV;
    private ImageView mTwoSelectedIV;
    private ImageView mThreeSelectedIV;
    private ImageView mFourSelectedIV;
    private ImageView mFiveSelectedIV;
    private ImageView mSixSelectedIV;
    private ImageView mSevenSelectedIV;
    private ImageView mEightSelectedIV;
    private ImageView mNineSelectedIV;
    private ImageView mTenSelectedIV;
    private ImageView mElevenSelectedIV;
    private ImageView mTwelveSelectedIV;
    private ImageView mThirteenSelectedIV;
    private ImageView mFourteenSelectedIV;
    private ImageView mFifteenSelectedIV;

    private LinearLayout mTitleLL;
    private LinearLayout mMiddleLL;
    private LinearLayout mBottomLL;

    private RelativeLayout mStartRL;
    private LinearLayout mSkiptRL;

    private TextView mStartTV;
    private TextView mTitleTV;
    private TextView mDesTV;

    private RelativeLayout mMiddleItemRL;


    private Activity mContext;
    private List<String> mUserSelected = new ArrayList<>();// 参考 ONewsInterestCN
    private List<Boolean> selected = new ArrayList<Boolean>();
    private Handler mHandler = new Handler();

    private void init() {
        selected.clear();
        for(int i=0; i<15; i++){
            selected.add(false);
        }
    }

    public NewsCategoryView_CN(Context context) {
        super(context);
        init();
    }

    public NewsCategoryView_CN(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NewsCategoryView_CN(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void initView(Activity context){
        this.mContext = context;
        findView();
        setListener();
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
        mNineRL.setOnClickListener(this);
        mTenRL.setOnClickListener(this);
        mElevenRL.setOnClickListener(this);
        mTwelveRL.setOnClickListener(this);
        mThirteenRL.setOnClickListener(this);
        mFourteenRL.setOnClickListener(this);
        mFifteenRL.setOnClickListener(this);
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

        mMiddleItemRL = (RelativeLayout) findViewById(R.id.rl_middle_item);

        mOneRL = (RelativeLayout) findViewById(R.id.rl_one);
        mTwoRL = (RelativeLayout) findViewById(R.id.rl_two);
        mThreeRL = (RelativeLayout) findViewById(R.id.rl_three);
        mFourRL = (RelativeLayout) findViewById(R.id.rl_four);
        mFiveRL = (RelativeLayout) findViewById(R.id.rl_five);
        mSixRL = (RelativeLayout) findViewById(R.id.rl_six);
        mSevenRL = (RelativeLayout) findViewById(R.id.rl_seven);
        mEightRL = (RelativeLayout) findViewById(R.id.rl_eight);
        mNineRL = (RelativeLayout) findViewById(R.id.rl_nine);
        mTenRL = (RelativeLayout) findViewById(R.id.rl_ten);
        mElevenRL = (RelativeLayout) findViewById(R.id.rl_eleven);
        mTwelveRL = (RelativeLayout) findViewById(R.id.rl_twelve);
        mThirteenRL = (RelativeLayout) findViewById(R.id.rl_thirteen);
        mFourteenRL = (RelativeLayout) findViewById(R.id.rl_fourteen);
        mFifteenRL = (RelativeLayout) findViewById(R.id.rl_fifteen);

        mOneRLBg = (RelativeLayout) findViewById(R.id.rl_one_bg);
        mTwoRLBg = (RelativeLayout) findViewById(R.id.rl_two_bg);
        mThreeRLBg = (RelativeLayout) findViewById(R.id.rl_three_bg);
        mFourRLBg = (RelativeLayout) findViewById(R.id.rl_four_bg);
        mFiveRLBg = (RelativeLayout) findViewById(R.id.rl_five_bg);
        mSixRLBg = (RelativeLayout) findViewById(R.id.rl_six_bg);
        mSevenRLBg = (RelativeLayout) findViewById(R.id.rl_seven_bg);
        mEightRLBg = (RelativeLayout) findViewById(R.id.rl_eight_bg);
        mNineRLBg = (RelativeLayout) findViewById(R.id.rl_nine_bg);
        mTenRLBg = (RelativeLayout) findViewById(R.id.rl_ten_bg);
        mElevenRLBg = (RelativeLayout) findViewById(R.id.rl_eleven_bg);
        mTwelveRLBg = (RelativeLayout) findViewById(R.id.rl_twelve_bg);
        mThirteenRLBg = (RelativeLayout) findViewById(R.id.rl_thirteen_bg);
        mFourteenRLBg = (RelativeLayout) findViewById(R.id.rl_fourteen_bg);
        mFifteenRLBg = (RelativeLayout) findViewById(R.id.rl_fifteen_bg);

        mOneRLIv = (RelativeLayout) findViewById(R.id.rl_one_iv);
        mTwoRLIv = (RelativeLayout) findViewById(R.id.rl_two_iv);
        mThreeRLIv = (RelativeLayout) findViewById(R.id.rl_three_iv);
        mFourRLIv = (RelativeLayout) findViewById(R.id.rl_four_iv);
        mFiveRLIv = (RelativeLayout) findViewById(R.id.rl_five_iv);
        mSixRLIv = (RelativeLayout) findViewById(R.id.rl_six_iv);
        mSevenRLIv = (RelativeLayout) findViewById(R.id.rl_seven_iv);
        mEightRLIv = (RelativeLayout) findViewById(R.id.rl_eight_iv);
        mNineRLIv = (RelativeLayout) findViewById(R.id.rl_nine_iv);
        mTenRLIv = (RelativeLayout) findViewById(R.id.rl_ten_iv);
        mElevenRLIv = (RelativeLayout) findViewById(R.id.rl_eleven_iv);
        mTwelveRLIv = (RelativeLayout) findViewById(R.id.rl_twelve_iv);
        mThirteenRLIv = (RelativeLayout) findViewById(R.id.rl_thirteen_iv);
        mFourteenRLIv = (RelativeLayout) findViewById(R.id.rl_fourteen_iv);
        mFifteenRLIv = (RelativeLayout) findViewById(R.id.rl_fifteen_iv);

        mOneSelectedIV = (ImageView) findViewById(R.id.iv_one_selected);
        mTwoSelectedIV = (ImageView) findViewById(R.id.iv_two_selected);
        mThreeSelectedIV = (ImageView) findViewById(R.id.iv_three_selected);
        mFourSelectedIV = (ImageView) findViewById(R.id.iv_four_selected);
        mFiveSelectedIV = (ImageView) findViewById(R.id.iv_five_selected);
        mSixSelectedIV = (ImageView) findViewById(R.id.iv_six_selected);
        mSevenSelectedIV = (ImageView) findViewById(R.id.iv_seven_selected);
        mEightSelectedIV = (ImageView) findViewById(R.id.iv_eight_selected);
        mNineSelectedIV = (ImageView) findViewById(R.id.iv_nine_selected);
        mTenSelectedIV = (ImageView) findViewById(R.id.iv_ten_selected);
        mElevenSelectedIV = (ImageView) findViewById(R.id.iv_eleven_selected);
        mTwelveSelectedIV = (ImageView) findViewById(R.id.iv_twelve_selected);
        mThirteenSelectedIV = (ImageView) findViewById(R.id.iv_thirteen_selected);
        mFourteenSelectedIV = (ImageView) findViewById(R.id.iv_fourteen_selected);
        mFifteenSelectedIV = (ImageView) findViewById(R.id.iv_fifteen_selected);

        updateLayout();
        initItemBg();
        hideIVSelected();
    }

    // 动态设置控件间距
    private void updateLayout() {
        int screenHeightDp = (int) (DimenSdkUtils.getScreenHeight() / DimenSdkUtils.getDensity());
        L.news_category("screenHeightDp = " + screenHeightDp);

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
        DimenSdkUtils.updateLayout(mNineRLBg, itemSize, itemSize);
        DimenSdkUtils.updateLayout(mTenRLBg, itemSize, itemSize);
        DimenSdkUtils.updateLayout(mElevenRLBg, itemSize, itemSize);
        DimenSdkUtils.updateLayout(mTwelveRLBg, itemSize, itemSize);
        DimenSdkUtils.updateLayout(mThirteenRLBg, itemSize, itemSize);
        DimenSdkUtils.updateLayout(mFourteenRLBg, itemSize, itemSize);
        DimenSdkUtils.updateLayout(mFifteenRLBg, itemSize, itemSize);

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
        updateViewPadding(mNineRL, 0, itemHalfPadding, 0, itemHalfPadding);
        updateViewPadding(mTenRL, 0, itemHalfPadding, 0, itemHalfPadding);
        updateViewPadding(mElevenRL, 0, itemHalfPadding, 0, itemHalfPadding);
        updateViewPadding(mTwelveRL, 0, itemHalfPadding, 0, itemHalfPadding);
        updateViewPadding(mThirteenRL, 0, itemHalfPadding, 0, itemHalfPadding);
        updateViewPadding(mFourteenRL, 0, itemHalfPadding, 0, itemHalfPadding);
        updateViewPadding(mFifteenRL, 0, itemHalfPadding, 0, itemHalfPadding);

        // 部分item的左右边距
        int itemMargin = dp2px((int) (((float) 17 / 640) * screenHeightDp));
        DimenSdkUtils.updateLayoutMargin(mTwoRL, itemMargin, 0, 0, 0);
        DimenSdkUtils.updateLayoutMargin(mThreeRL, itemMargin, 0, 0, 0);
        DimenSdkUtils.updateLayoutMargin(mFourRL, itemMargin, 0, 0, 0);
        DimenSdkUtils.updateLayoutMargin(mFiveRL, itemMargin, 0, 0, 0);

        DimenSdkUtils.updateLayoutMargin(mSevenRL, itemMargin, 0, 0, 0);
        DimenSdkUtils.updateLayoutMargin(mEightRL, itemMargin, 0, 0, 0);
        DimenSdkUtils.updateLayoutMargin(mNineRL, itemMargin, 0, 0, 0);
        DimenSdkUtils.updateLayoutMargin(mTenRL, itemMargin, 0, 0, 0);

        DimenSdkUtils.updateLayoutMargin(mTwelveRL, itemMargin, 0, 0, 0);
        DimenSdkUtils.updateLayoutMargin(mThirteenRL, itemMargin, 0, 0, 0);
        DimenSdkUtils.updateLayoutMargin(mFourteenRL, itemMargin, 0, 0, 0);
        DimenSdkUtils.updateLayoutMargin(mFifteenRL, itemMargin, 0, 0, 0);

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
        mNineSelectedIV.setVisibility(View.GONE);
        mTenSelectedIV.setVisibility(View.GONE);
        mElevenSelectedIV.setVisibility(View.GONE);
        mTwelveSelectedIV.setVisibility(View.GONE);
        mThirteenSelectedIV.setVisibility(View.GONE);
        mFourteenSelectedIV.setVisibility(View.GONE);
        mFifteenSelectedIV.setVisibility(View.GONE);
    }

    // 初始化每一个圆形的背景色
    private void initItemBg() {
        setColorBg(mOneRLBg, "#17C4CF");
        setColorBg(mTwoRLBg, "#19C1EB");
        setColorBg(mThreeRLBg, "#37B8F0");
        setColorBg(mFourRLBg, "#38B0F5");
        setColorBg(mFiveRLBg, "#17C4CF");
        setColorBg(mSixRLBg, "#37B8F0");
        setColorBg(mSevenRLBg, "#329AFA");
        setColorBg(mEightRLBg, "#19C1EB");
        setColorBg(mNineRLBg, "#17C4CF");
        setColorBg(mTenRLBg, "#19C1EB");
        setColorBg(mElevenRLBg, "#329AFA");
        setColorBg(mTwelveRLBg, "#38B0F5");
        setColorBg(mThirteenRLBg, "#17C4CF");
        setColorBg(mFourteenRLBg, "#37B8F0");
        setColorBg(mFifteenRLBg, "#329AFA");
    }

    public void setColorBg(RelativeLayout relativeLayout, String colorStr){
        GradientDrawable eightGradientDrawable = (GradientDrawable) relativeLayout.getBackground();
        eightGradientDrawable.setColor(Color.parseColor(colorStr));
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
                mOneRL.startAnimation(getItemClickAnimation(0));
                break;

            case R.id.rl_two:
                mTwoRL.startAnimation(getItemClickAnimation(1));
                break;

            case R.id.rl_three:
                mThreeRL.startAnimation(getItemClickAnimation(2));
                break;

            case R.id.rl_four:
                mFourRL.startAnimation(getItemClickAnimation(3));
                break;

            case R.id.rl_five:
                mFiveRL.startAnimation(getItemClickAnimation(4));
                break;

            case R.id.rl_six:
                mSixRL.startAnimation(getItemClickAnimation(5));
                break;

            case R.id.rl_seven:
                mSevenRL.startAnimation(getItemClickAnimation(6));
                break;

            case R.id.rl_eight:
                mEightRL.startAnimation(getItemClickAnimation(7));
                break;

            case R.id.rl_nine:
                mNineRL.startAnimation(getItemClickAnimation(8));
                break;

            case R.id.rl_ten:
                mTenRL.startAnimation(getItemClickAnimation(9));
                break;

            case R.id.rl_eleven:
                mElevenRL.startAnimation(getItemClickAnimation(10));
                break;

            case R.id.rl_twelve:
                mTwelveRL.startAnimation(getItemClickAnimation(11));
                break;

            case R.id.rl_thirteen:
                mThirteenRL.startAnimation(getItemClickAnimation(12));
                break;

            case R.id.rl_fourteen:
                mFourteenRL.startAnimation(getItemClickAnimation(13));
                break;

            case R.id.rl_fifteen:
                mFifteenRL.startAnimation(getItemClickAnimation(14));
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
                    /* 第一排 */
                    case 0:// 社会
                        updateViewVisible(mOneSelectedIV, mOneRLIv, R.drawable.onews__category_society_cn);
                        isSelected = !mUserSelected.contains(ONewsInterestCN.NC_00 + "");
                        updateUserSelected(ONewsInterestCN.NC_00 + "");
                        updateStartView();
                        break;
                    case 1:// 娱乐
                        updateViewVisible(mTwoSelectedIV, mTwoRLIv, R.drawable.onews__category_movie);
                        isSelected = !mUserSelected.contains(ONewsInterestCN.NC_01 + "");
                        updateUserSelected(ONewsInterestCN.NC_01 + "");
                        updateStartView();
                        break;
                    case 2:// 军事
                        updateViewVisible(mThreeSelectedIV, mThreeRLIv, R.drawable.onews__category_military_cn);
                        isSelected = !mUserSelected.contains(ONewsInterestCN.NC_02 + "");
                        updateUserSelected(ONewsInterestCN.NC_02 + "");
                        updateStartView();
                        break;
                    case 3:// 房产
                        updateViewVisible(mFourSelectedIV, mFourRLIv, R.drawable.onews__category_estate_cn);
                        isSelected = !mUserSelected.contains(ONewsInterestCN.NC_03 + "");
                        updateUserSelected(ONewsInterestCN.NC_03 + "");
                        updateStartView();
                        break;
                    case 4:// 历史
                        updateViewVisible(mFiveSelectedIV, mFiveRLIv, R.drawable.onews__category_history_cn);
                        isSelected = !mUserSelected.contains(ONewsInterestCN.NC_04 + "");
                        updateUserSelected(ONewsInterestCN.NC_04 + "");
                        updateStartView();
                        break;
                    /* 第二排 */
                    case 5:// 汽车
                        updateViewVisible(mSixSelectedIV, mSixRLIv, R.drawable.onews__category_car_cn);
                        isSelected = !mUserSelected.contains(ONewsInterestCN.NC_05 + "");
                        updateUserSelected(ONewsInterestCN.NC_05 + "");
                        updateStartView();
                        break;
                    case 6:// 时尚
                        updateViewVisible(mSevenSelectedIV, mSevenRLIv, R.drawable.onews__category_fashion_cn);
                        isSelected = !mUserSelected.contains(ONewsInterestCN.NC_06 + "");
                        updateUserSelected(ONewsInterestCN.NC_06 + "");
                        updateStartView();
                        break;
                    case 7:// 体育
                        updateViewVisible(mEightSelectedIV, mEightRLIv, R.drawable.onews__category_sports);
                        isSelected = !mUserSelected.contains(ONewsInterestCN.NC_07 + "");
                        updateUserSelected(ONewsInterestCN.NC_07 + "");
                        updateStartView();
                        break;
                    case 8:// 趣味
                        updateViewVisible(mNineSelectedIV, mNineRLIv, R.drawable.onews__category_taste_cn);
                        isSelected = !mUserSelected.contains(ONewsInterestCN.NC_08 + "");
                        updateUserSelected(ONewsInterestCN.NC_08 + "");
                        updateStartView();
                        break;
                    case 9:// 游戏
                        updateViewVisible(mTenSelectedIV, mTenRLIv, R.drawable.onews__category_game_cn);
                        isSelected = !mUserSelected.contains(ONewsInterestCN.NC_09 + "");
                        updateUserSelected(ONewsInterestCN.NC_09 + "");
                        updateStartView();
                        break;
                    /* 第三排 */
                    case 10:// 科技
                        updateViewVisible(mElevenSelectedIV, mElevenRLIv, R.drawable.onews__category_technology_cn);
                        isSelected = !mUserSelected.contains(ONewsInterestCN.NC_10 + "");
                        updateUserSelected(ONewsInterestCN.NC_10 + "");
                        updateStartView();
                        break;
                    case 11:// 财经
                        updateViewVisible(mTwelveSelectedIV, mTwelveRLIv, R.drawable.onews__category_commerce);
                        isSelected = !mUserSelected.contains(ONewsInterestCN.NC_11 + "");
                        updateUserSelected(ONewsInterestCN.NC_11 + "");
                        updateStartView();
                        break;
                    case 12:// 健康
                        updateViewVisible(mThirteenSelectedIV, mThirteenRLIv, R.drawable.onews__category_health_cn);
                        isSelected = !mUserSelected.contains(ONewsInterestCN.NC_12 + "");
                        updateUserSelected(ONewsInterestCN.NC_12 + "");
                        updateStartView();
                        break;
                    case 13:// 国际
                        updateViewVisible(mFourteenSelectedIV, mFourteenRLIv, R.drawable.onews__category_world);
                        isSelected = !mUserSelected.contains(ONewsInterestCN.NC_13 + "");
                        updateUserSelected(ONewsInterestCN.NC_13 + "");
                        updateStartView();
                        break;
                    case 14:// 减肥
                        updateViewVisible(mFifteenSelectedIV, mFifteenRLIv, R.drawable.onews__category_lose_weight_cn);
                        isSelected = !mUserSelected.contains(ONewsInterestCN.NC_14 + "");
                        updateUserSelected(ONewsInterestCN.NC_14 + "");
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

    // 更新start布局
    private void updateStartView(){
        GradientDrawable startBackground = (GradientDrawable) mStartRL.getBackground();
        if (mUserSelected.isEmpty()){
            startBackground.setColor(Color.parseColor("#d3d3d3"));
            mStartTV.setTextColor(Color.parseColor("#9a9a9a"));
        } else {
            startBackground.setColor(Color.parseColor("#1976D2"));
            mStartTV.setTextColor(Color.parseColor("#FFFFFF"));
        }
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
    private int interesttype;//选择了哪些类别1-15位上报
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
            try {
                return  Integer.parseInt(type);
            } catch (Exception e){
                L.news_category(e.getMessage());
                return 0;
            }
        }
        return 0;
    }

    /**
     * 上报分类爱好埋点
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

}

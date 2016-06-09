package com.cmcm.onews.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmcm.onews.C;
import com.cmcm.onews.R;
import com.cmcm.onews.util.LanguageCountry;
import com.cmcm.onews.util.UIConfigManager;

public class NewsLanguageDialog {
    private NewsAlertDialog.Builder builder;
    private NewsAlertDialog mDialog;
    private Context mContext;
    private TextView mTextEn;
    private ImageView mEn;
    private TextView mTextHi;
    private ImageView mHi;
    private TextView mTitle;
    private INewsDialogLanguage iClick;

    public NewsLanguageDialog(Context context, INewsDialogLanguage click) {
        this.mContext = context;
        this.iClick = click;
        initDialog();
    }

    private void initDialog(){
        View root = LayoutInflater.from(mContext).inflate(R.layout.onews__language_dialog, null);
        builder = new NewsAlertDialog.Builder(mContext);

        mTextEn = (TextView) root.findViewById(R.id.language_text_en);
        mEn = (ImageView) root.findViewById(R.id.language_img_en);
        root.findViewById(R.id.language_en).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClick.clickCount(new LanguageCountry(LanguageCountry.LANGUAGE_OPTION_EN));
                dismissDialog();
            }
        });

        mTextHi = (TextView) root.findViewById(R.id.language_text_hi);
        mHi = (ImageView) root.findViewById(R.id.language_img_hi);
        root.findViewById(R.id.language_hi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClick.clickCount(new LanguageCountry(LanguageCountry.LANGUAGE_OPTION_HI));
                dismissDialog();
            }
        });
        mTitle = (TextView) root.findViewById(R.id.onews__language_title);

        builder.setView(root);
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);
    }

    public void showDialog() {
        if(null != mDialog && !mDialog.isShowing()){
            mTitle.setText(R.string.onews__dialog_language_title);
            LanguageCountry languageCountry = UIConfigManager.getInstanse(C.getAppContext()).getLanguageSelected(C.getAppContext());
            if(LanguageCountry.LANGUAGE_OPTION_EN.equals(languageCountry.getLanguage())){
                mEn.setVisibility(View.VISIBLE);
                mHi.setVisibility(View.INVISIBLE);
                mTextEn.setTextColor(Color.parseColor("#FF2196F3"));
                mTextHi.setTextColor(Color.parseColor("#FF212121"));
            }else if(LanguageCountry.LANGUAGE_OPTION_HI.equals(languageCountry.getLanguage())){
                mEn.setVisibility(View.INVISIBLE);
                mHi.setVisibility(View.VISIBLE);
                mTextEn.setTextColor(Color.parseColor("#FF212121"));
                mTextHi.setTextColor(Color.parseColor("#FF2196F3"));
            }
            mDialog.show();
        }
    }

    public void dismissDialog() {
        if(null != mDialog && mDialog.isShowing()){
            mDialog.dismiss();
        }
    }
}

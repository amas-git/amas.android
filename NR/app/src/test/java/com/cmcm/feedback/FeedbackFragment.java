package com.cmcm.feedback;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cmcm.feedback.service.EvFeedbackResult;
import com.cmcm.feedback.service.FeedBackDataBean;
import com.cmcm.onews.MainEntry;
import com.cmcm.onews.R;
import com.cmcm.onews.event.ONewsEvent;
import com.cmcm.onews.fragment.NewsBaseFragment;
import com.cmcm.onews.loader.AsyncTaskEx;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.service.LocalService;
import com.cmcm.onews.ui.widget.CommonSingleDialog;
import com.cmcm.onews.ui.widget.INewsNotifyDialogClick;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class FeedbackFragment extends NewsBaseFragment {

    private static final int SHOW_IMAGE_LIMIT_WIDTH = 200;
    private static final int UPLOAD_IMAGE_LIMIT_WIDTH = 600;
    private EditText mEditContent;
    private EditText mEditContact;
    private ProgressBar mProgress;
    private FeedbackAddView mFeed_0;
    private FeedbackAddView mFeed_1;
    private FeedbackAddView mFeed_2;
    private boolean mIsFeedbackForAppFrequence = false;
    private static final String TMP_SAVE_FEEDBACK_IMAGE_FILE_PRFIX = "cm_feedback_tmp";

    public static final int TOTAL_CACHE_FILE_NUM = 1;

    private static final int MSG_UPLOAD_OUT_OF_TIME = 0;

    public final static int FROM_SETTINGS = 1;
    public static final int FROM_DEFALUT = FROM_SETTINGS;
    public static final int FROM_CONTENT = 2;

    private String mEditorName;
    private String mIconUrl;
    private int mFrom = FROM_DEFALUT;
    private String mTag = "";


    public final static String FROM = "pciks_editor";
    public final static String NAME = "pciks_name";
    public final static String URL = "pciks_url";
    public final static String EXTRA_TAG = "extra_tag";

    public final static String PHOTO_PATH1 = ":path1";
    public final static String PHOTO_PATH2 = ":path2";
    private String inputEmail;
    private String inputContent;
    private FeedBackActivity mActivity;
    public FeedbackFragment() {
    }

    public static FeedbackFragment setArgument(FeedbackFragment fragment, String editorName, String iconUrl, int from) {
        Bundle bundle = new Bundle();
        bundle.putInt(FROM, from);
        bundle.putString(URL, iconUrl);
        bundle.putString(NAME, editorName);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static FeedbackFragment newInstance() {
        return new FeedbackFragment();
    }

    public static FeedbackFragment newInstance(int from) {
        FeedbackFragment instance = new FeedbackFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FROM, from);
        instance.setArguments(bundle);
        return instance;
    }

    public static FeedbackFragment newInstance(int from, String tag) {
        FeedbackFragment instance = new FeedbackFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FROM, from);
        bundle.putString(EXTRA_TAG, tag);
        instance.setArguments(bundle);
        return instance;
    }

    public static FeedbackFragment newInstance(int from, String path1, String path2) {
        FeedbackFragment instance = new FeedbackFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FROM, from);
        bundle.putString(PHOTO_PATH1, path1);
        bundle.putString(PHOTO_PATH2, path2);
        instance.setArguments(bundle);
        return instance;
    }

    public static FeedbackFragment newInstance(String editorName, String iconUrl, int from) {
        FeedbackFragment instance = new FeedbackFragment();
        return setArgument(instance, editorName, iconUrl, from);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (null != bundle) {
            mEditorName = bundle.getString(NAME);
            mIconUrl = bundle.getString(URL);
            mFrom = bundle.getInt(FROM, FROM_DEFALUT);
            if (bundle.containsKey(EXTRA_TAG)) {
                mTag = bundle.getString(EXTRA_TAG);
            }
        }
        deleteAllCachFiles(getActivity());
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FeedBackActivity) context;
    }

    public void setPhotoFile(String photoPath) {
        if (TextUtils.isEmpty(photoPath)) {
            return;
        }
        final File file = new File(photoPath);
        AsyncTaskEx task = new AsyncTaskEx() {
            @Override
            protected Bitmap doInBackground(Object[] params) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    saveCacheFile(fis, 0);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                Bitmap bitmap = ImageUtil.getResizedBitmap(file, SHOW_IMAGE_LIMIT_WIDTH);
                if (null == bitmap) {
                    return;
                }
                if (!bitmap.isRecycled()) {
                    mFeed_0.showImage(bitmap);
                }
            }
        };
        task.execute();

    }


    public static void deleteAllCachFiles(Context context) {
        for (int i = 0; i < TOTAL_CACHE_FILE_NUM; i++) {
            deleteCurrentFile(context, i);
        }
    }

    public static void deleteCurrentFile(Context context, int i) {
        File file = new File(context.getCacheDir(), getFileName(i));
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    public static String getFileName(int i) {
        return TMP_SAVE_FEEDBACK_IMAGE_FILE_PRFIX + "_" + i + ".jpg";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.feedback_tag_fragment_feedback, container, false);
        mProgress = (ProgressBar) root.findViewById(R.id.progress);
        L.feedback(mFrom + "-----------");
        initCommonView(root);
        initArguments();
        if (mFrom == FROM_SETTINGS) {
            initNormalView(root);
        }
        if (mFrom == FROM_CONTENT) {
            initFromContentView(root);
        }
        return root;
    }

    private void initCommonView(View view) {
        mFeed_0 = (FeedbackAddView) view.findViewById(R.id.feed_add_0);
        mFeed_0.setOnFeedbackOperListener(mOnFeedbackOperListener);
        view.findViewById(R.id.editor_layout).setVisibility(View.VISIBLE);
        view.findViewById(R.id.scroll_view).setVisibility(View.VISIBLE);
        mEditContent = (EditText) view.findViewById(R.id.editor_edit_des);
        mEditContact = (EditText) view.findViewById(R.id.editor_edit_connect);
        mEditContact.addTextChangedListener(new EMailEditChangedListener());
        mEditContent.addTextChangedListener(new ContentEditChangedListener());
    }

    private void initArguments() {
        final Bundle arguments = getArguments();
        if (arguments == null) {
            return;
        }
        mIconUrl = arguments.getString(URL);
        setPhotoFile(mIconUrl);
    }


    private void initFromContentView(View view) {


    }


    private void initNormalView(View root) {


    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startFloatCheckingThread();
    }


    private void startFloatCheckingThread() {

    }
    
    FeedbackAddView.OnFeedbackOperListener mOnFeedbackOperListener = new FeedbackAddView.OnFeedbackOperListener() {

        @Override
        public void onDelete(int i) {
            deleteCurrentFile(getActivity(), i);
        }

        @Override
        public void onAdd(int i) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            try {
                startActivityForResult(intent, i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void needShow() {

            for (int j = 0; j < TOTAL_CACHE_FILE_NUM; j++) {
                FeedbackAddView view = getById(j);
                FeedbackAddView viewNext = getById(j + 1);
                if (null != view && !view.isEmpty() && null != viewNext && viewNext.isHide()) {
                    viewNext.show();
                }
            }

        }

        @Override
        public void needHide() {
        }
    };

    private FeedbackAddView getById(int id) {
        switch (id) {
            case 0:
                return mFeed_0;
            case 1:
                return mFeed_1;
            case 2:
                return mFeed_2;
            default:
                return null;
        }
    }



    class EMailEditChangedListener implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            inputEmail = s.toString();
            if(null!=mActivity){
                mActivity.sendImgChanged(inputEmail,inputContent);
            }
//            L.feedback("正在输入Email="+s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class ContentEditChangedListener implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            inputContent = s.toString();
            inputEmail = s.toString();
            if(null!=mActivity){
                mActivity.sendImgChanged(inputEmail,inputContent);
            }
//            L.feedback("正在输入内容 content="+s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }



    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_UPLOAD_OUT_OF_TIME:
                    if (!isDetached() && isVisible()) {
                        mProgress.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), getString(R.string.feedback_tag_feedback_fail), Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }

    };

    public void startCommit() {
        String content = mEditContent.getText().toString().trim();
        String contact = mEditContact.getText().toString().trim();
        if (TextUtils.isEmpty(content) && TextUtils.isEmpty(contact)) {
            if (mEditContact.hasFocus()) {
                showDialogContactEmpty();
                return;
            }
            if (mEditContent.hasFocus()) {
                showDialogContentEmpty();
                return;
            }else {
                showDialogContactEmpty();
                return;
            }

        }
        if (TextUtils.isEmpty(content)) {
            showDialogContentEmpty();
            return;
        }
        if (TextUtils.isEmpty(contact)) {
            showDialogContactEmpty();
            return;
        }
        mHandler.removeMessages(MSG_UPLOAD_OUT_OF_TIME);
        mHandler.sendEmptyMessageDelayed(MSG_UPLOAD_OUT_OF_TIME, 39 * 1000);
        mProgress.setVisibility(View.VISIBLE);
        List<String> list = new ArrayList<>();
        final String[] uploadPaths = getUploadPaths(MainEntry.getAppContext());
        for (String path : uploadPaths) {
            if (!TextUtils.isEmpty(path)) {
                list.add(path);
            }
        }
        LocalService.start_ACTION_UPLOAD_LOGS(MainEntry.getAppContext(), FeedBackDataBean.formDataBean(content, contact, null, list));
    }

    private void showDialogContactEmpty() {
        showDialog(getString(R.string.feedback_valid_emial));

    }

    private void showDialogContentEmpty() {
        showDialog(getString(R.string.feedback_valid_des));
    }

    private void showDialog(String des) {
        CommonSingleDialog dialog = new CommonSingleDialog(getContext(), new INewsNotifyDialogClick() {
            @Override
            public void clickContinue() {

            }

            @Override
            public void clickCancel() {

            }
        }, des);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setIfExitWhitBack(false);
        dialog.showDialog();


    }

    public static String[] getUploadPaths(Context context) {
        String paths[] = new String[3];
        for (int i = 0; i < TOTAL_CACHE_FILE_NUM; i++) {
            File file = new File(context.getCacheDir(), getFileName(i));
            if (null != file && file.exists() && file.length() > 0) {
                paths[i] = file.getAbsolutePath();
            }
        }
        return paths;
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && data.getData() != null) {
            Uri uri = data.getData();
            ContentResolver cr = getActivity().getContentResolver();
            try {
                if ((requestCode >= 0 && requestCode < 3) && saveCacheFile(cr.openInputStream(uri), requestCode)) {
                    File file = new File(getActivity().getCacheDir(), getFileName(requestCode));
                    FileInputStream input = new FileInputStream(file);
                    Bitmap bitmap = ImageUtil.getResizedBitmap(input, SHOW_IMAGE_LIMIT_WIDTH);
                    if (null == bitmap) {
                        return;
                    }
                    FeedbackAddView view = getById(requestCode);
                    if (null != view) {
                        view.showImage(bitmap);
                    }
                } else {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.feedback_tag_feedback_load_image_fail), Toast.LENGTH_SHORT).show();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), getActivity().getString(R.string.feedback_tag_feedback_load_image_fail), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean saveCacheFile(InputStream openInputStream, int i) {
        Bitmap map = ImageUtil.getResizedBitmap(openInputStream, UPLOAD_IMAGE_LIMIT_WIDTH);
        if (null == map) {
            return false;
        }
        deleteCurrentFile(getActivity(), i);
        File file;
        FileOutputStream out = null;
        try {
            file = new File(getActivity().getCacheDir(), getFileName(i));
            file.createNewFile();
            out = new FileOutputStream(file);
            map.compress(CompressFormat.JPEG, 90, out);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (map != null) {
                if (!map.isRecycled()) {
                    map.recycle();
                }
            }
            try {
                if (null != out) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        deleteAllCachFiles(getActivity());
        super.onDestroy();
    }

    public void onEventInUiThread(ONewsEvent event) {
        if (event instanceof EvFeedbackResult) {
            onHandleFeedbackResult((EvFeedbackResult) event);
        }
    }

    private void onHandleFeedbackResult(EvFeedbackResult event) {
        if (event.isOutOfTime()) {
            return;
        } else {
            if (null != mHandler) {
                mHandler.removeMessages(MSG_UPLOAD_OUT_OF_TIME);
            }
        }
        if (isVisible() && null != getActivity()) {
            mProgress.setVisibility(View.GONE);
            final boolean result = event.getFeedbackResult();
            if (result) {
                if (mIsFeedbackForAppFrequence) {
                    Toast.makeText(getActivity(), getString(R.string.feedback_tag_uninst_feedback_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.feedback_tag_feedback_success), Toast.LENGTH_SHORT).show();
                }
                ((FeedBackActivity) getActivity()).hideInput();
                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), getString(R.string.feedback_tag_feedback_fail), Toast.LENGTH_SHORT).show();
            }
        }

    }


}

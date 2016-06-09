package com.cmcm.feedback.service;

import java.io.Serializable;
import java.util.List;

public class FeedBackDataBean implements Serializable {

    private static final long serialVersionUID = -3754836845709146023L;

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public List<String> getPicPaths() {
        return mPicPaths;
    }

    public void setPicPaths(List<String> picPaths) {
        mPicPaths = picPaths;
    }

    public List<String> getLogPaths() {
        return mLogPaths;
    }

    public void setLogPaths(List<String> logPaths) {
        mLogPaths = logPaths;
    }

    private String mContent = "";
    private String mContact = "";
    private List<String> mPicPaths;
    private List<String> mLogPaths;
    public static FeedBackDataBean formDataBean(String content,String contact,List<String> logs,List<String>pics){
        FeedBackDataBean bean = new FeedBackDataBean();
        bean.setContent(content);
        bean.setContact(contact);
        bean.setLogPaths(logs);
        bean.setPicPaths(pics);
        return bean;
    }



    @Override
    public String toString() {
        return "";
    }
    
    public String getContact() {
        return mContact;
    }

    public void setContact(String contact) {
        mContact = contact;
    }
}

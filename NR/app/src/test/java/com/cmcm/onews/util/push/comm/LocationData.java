
package com.cmcm.onews.util.push.comm;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 天气实体类
 */
public class LocationData implements Serializable, Parcelable {
    
    private static final long serialVersionUID = -5071237264858062489L;
    
    private String mCountry;
    private String mProvince;
    private String mCity;
    private String mCounty;
    private String mLocale;
    private String mTimeZone;
    private String mCityCode;
    private boolean mIsUserSel ;
    
    public LocationData() {
    }
    
    public LocationData(Parcel pl) {
        mCountry = pl.readString();
        mProvince = pl.readString();
        mCity = pl.readString();
        mCounty = pl.readString();
        mLocale = pl.readString();
        mTimeZone = pl.readString();
        mCityCode = pl.readString();
        mIsUserSel = pl.readByte()==1;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        this.mCountry = country;
    }

    public String getProvince() {
        return mProvince;
    }

    public void setProvince(String province) {
        this.mProvince = province;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        this.mCity = city;
    }

    public String getCounty() {
        return mCounty;
    }

    public void setCounty(String county) {
        this.mCounty = county;
    }

    public String getLocale() {
        return mLocale;
    }

    public void setLocale(String locale) {
        this.mLocale = locale;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        this.mTimeZone = timeZone;
    }

    public String getCityCode() {
        return mCityCode;
    }

    public void setCityCode(String cityCode) {
        this.mCityCode = cityCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    

    public boolean isUserSel() {
        return mIsUserSel;
    }

    public void setUserSel(boolean isUserSel) {
        this.mIsUserSel = isUserSel;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCountry);
        dest.writeString(mProvince);
        dest.writeString(mCity);
        dest.writeString(mCounty);
        dest.writeString(mLocale);
        dest.writeString(mTimeZone);
        dest.writeString(mCityCode);
        dest.writeByte((byte) (mIsUserSel?1:0));
    }
    
    public static final Creator<LocationData> CREATOR = new Creator<LocationData>() {

        @Override
        public LocationData createFromParcel(Parcel source) {
            return new LocationData(source); 
        } 

        @Override
        public LocationData[] newArray(int size) { 
            return new LocationData[size]; 
        } 

    };
}

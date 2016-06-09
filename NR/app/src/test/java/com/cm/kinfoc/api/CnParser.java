package com.cm.kinfoc.api;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.cm.CH;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by yzx on 2015/5/29.
 */
public class CnParser {

    private AssetManager assertManager = null;
    private Context ctx;

    static private CnParser mCnParser = null;
    static public CnParser getInstance(Context ctx){
        if(mCnParser == null){
            synchronized (CnParser.class){
                if(mCnParser == null){
                    mCnParser = new CnParser(ctx);
                }
            }
        }
        return mCnParser;
    }

    private CnParser(Context ctx){
        this.ctx = ctx.getApplicationContext();
        assertManager = ctx.getAssets();
    }

    public int getCN(){
        int cn;
        //boolean isCnVersion = ConflictCommons.isCNVersion();
        //if(isCnVersion){
            try {
                InputStream in = assertManager.open("cn");
                String cn_infile = new BufferedReader(new InputStreamReader(in)).readLine().trim();
                cn = Integer.valueOf(cn_infile);

            } catch (IOException e) {
                cn = CH.getDefaultChannelId();

                Log.e("err", e.getLocalizedMessage());
            } catch (NumberFormatException e){
                cn = CH.getDefaultChannelId();

                Log.e("err", Log.getStackTraceString(e));
            }
            
//        } else {
//            cn = CampaignTrackingReceiver_cm.getChannel(this.ctx);
//        }
        
        return cn;
    }

    public String getCN2(){
//        if(getCN() == CH.CHANNEL_ID_GOOGLE){
//            return CampaignTrackingReceiver_cm.getCN2(ctx);
//        }
        try {
            InputStream in = assertManager.open("cn2");
            String cn = new BufferedReader(new InputStreamReader(in)).readLine();
            return cn;
        } catch (IOException e) {
            Log.e("err", e.getLocalizedMessage());
        }
        return "N/A";
    }
}

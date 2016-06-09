package com.cmcm.onews;

import android.text.TextUtils;

import com.cmcm.onews.model.ONewsInterest;
import com.cmcm.onews.model.ONewsInterestCN;
import com.cmcm.onews.model.ONewsScenario;
import com.cmcm.onews.model.ONewsScenarioCategory;
import com.cmcm.onews.sdk.L;
import com.cmcm.onews.sdk.NewsSdk;
import com.cmcm.onews.util.ConflictCommons;
import com.cmcm.onews.util.LanguageUtils;
import com.cmcm.onews.util.UIConfigManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cmcm.onews.model.ONewsScenario.getScenarioByCategory;

public class ONewsScenarios {
    /**
     * 1.默认首页的分类tab顺序调整一下： 热点、娱乐、生活、国际(world)、商业、时政、视频、板球、体育
     * 2.会根据用户设置修改顺序
     */
    private volatile static ONewsScenarios instance;
    private static final List<ONewsScenario> scenarios = Collections.synchronizedList(new ArrayList<ONewsScenario>());

    private ONewsScenarios(){
        initScenarios(false);
    }

    public void initScenarios(boolean clear){
        if(ConflictCommons.isCNVersion()){
            initCNScenarios();
        }else {
            initINScenarios(clear);
        }
    }

    public static ONewsScenarios getInstance() {
        if (instance == null) {
            synchronized (ONewsScenarios.class) {
                if (instance == null) {
                    instance = new ONewsScenarios();
                }
            }
        }
        return instance;
    }

    public synchronized void initINScenarios(boolean clear) {
        if(null != scenarios && !scenarios.isEmpty() && !clear){
            return;
        }

        scenarios.clear();

        String interests = UIConfigManager.getInstanse(NewsSdk.INSTAMCE.getAppContext()).getNEWS_CATEGORY_INTEREST();
        if(TextUtils.isEmpty(interests)){
            scenarios.addAll(defaultScenarios(true));
        }else {
            scenarios.addAll(userScenarios(interests));
        }
    }

    public synchronized List<ONewsScenario> scenarios(){
        return scenarios;
    }

    private List<ONewsScenario> userScenarios(String interests){
        List<ONewsScenario> userScenarios = new ArrayList<ONewsScenario>();
        List<ONewsScenario> defaultScenarios = defaultScenarios(false);
        userScenarios.add(getScenarioByCategory(ONewsScenarioCategory.SC_1D));
        userScenarios.add(getScenarioByCategory(ONewsScenarioCategory.SC_20));
        userScenarios.add(getScenarioByCategory(ONewsScenarioCategory.SC_2D));
        if(L.DEBUG) L.scenarios(interests);

        String[] strings = interests.split(",");

        for(int i=0;i<strings.length;i++){
            if(L.DEBUG) L.scenarios(strings[i]);
            ONewsScenario scenario = filterScenario(strings[i].trim(),defaultScenarios);
            if(null != scenario){
                defaultScenarios.remove(scenario);
                userScenarios.add(scenario);
            }
        }

        if(!defaultScenarios.isEmpty()){
            userScenarios.addAll(defaultScenarios);
        }
        return userScenarios;
    }

    private List<ONewsScenario> defaultScenarios(boolean isDefault){
        List<ONewsScenario> defaultScenarios = new ArrayList<ONewsScenario>();
        ONewsScenario  SC_1D = getScenarioByCategory(ONewsScenarioCategory.SC_1D);
        ONewsScenario  SC_2D = getScenarioByCategory(ONewsScenarioCategory.SC_2D);

        ONewsScenario  SC_03 = getScenarioByCategory(ONewsScenarioCategory.SC_03);
        ONewsScenario  SC_1F = getScenarioByCategory(ONewsScenarioCategory.SC_1F);
        ONewsScenario  SC_0F = getScenarioByCategory(ONewsScenarioCategory.SC_0F);
        ONewsScenario  SC_2A = getScenarioByCategory(ONewsScenarioCategory.SC_2A);
        ONewsScenario  SC_06 = getScenarioByCategory(ONewsScenarioCategory.SC_06);
        ONewsScenario  SC_01 = getScenarioByCategory(ONewsScenarioCategory.SC_01);
        ONewsScenario  SC_1C = getScenarioByCategory(ONewsScenarioCategory.SC_1C);
        ONewsScenario  SC_20 = getScenarioByCategory(ONewsScenarioCategory.SC_20);
        ONewsScenario  SC_04 = getScenarioByCategory(ONewsScenarioCategory.SC_04);

        if(isDefault){
            defaultScenarios.add(SC_1D);
            defaultScenarios.add(SC_20);
            defaultScenarios.add(SC_2D);
        }

        defaultScenarios.add(SC_03);
        defaultScenarios.add(SC_1F);
        defaultScenarios.add(SC_0F);
        defaultScenarios.add(SC_2A);
        defaultScenarios.add(SC_06);
        defaultScenarios.add(SC_01);
        if(LanguageUtils.isEnglish(UIConfigManager.getInstanse(C.getAppContext()).getLanguageSelected(C.getAppContext()))){
            defaultScenarios.add(SC_1C);
        }
        defaultScenarios.add(SC_04);
        return defaultScenarios;
    }

    private ONewsScenario filterScenario(String interest,List<ONewsScenario> scenarios){
        if(String.valueOf(ONewsInterest.NC_00).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_0F,scenarios);

        }else if(String.valueOf(ONewsInterest.NC_01).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_03,scenarios);

        }else if(String.valueOf(ONewsInterest.NC_02).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_04,scenarios);

        }else if(String.valueOf(ONewsInterest.NC_04).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_01,scenarios);

        }else if(String.valueOf(ONewsInterest.NC_05).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_06,scenarios);

        }else if(String.valueOf(ONewsInterest.NC_06).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_2A,scenarios);

        }else if(String.valueOf(ONewsInterest.NC_07).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_03,scenarios);
        }else {
            return null;
        }
    }

    private ONewsScenario obtainScenario(byte category,List<ONewsScenario> scenarios){
        for (ONewsScenario scenario : scenarios){
            if(scenario.getCategory() == category){
                return scenario;
            }
        }
        return null;
    }

    public synchronized boolean removeVideo() {
        initScenarios(true);
        return true;
    }

    public synchronized boolean addVideo() {
        initScenarios(true);
        return true;
    }

    public boolean removeTest() {
        initScenarios(true);
        return true;
    }

    public boolean addTest() {
        initScenarios(true);
        scenarios.add(0, ONewsScenario.getTestScenario());
        return true;
    }

    public void initCNScenarios(){

        scenarios.clear();

        String interests = UIConfigManager.getInstanse(NewsSdk.INSTAMCE.getAppContext()).getNEWS_CATEGORY_INTEREST();
        if(TextUtils.isEmpty(interests)){
            scenarios.addAll(cnScenarios(true, true));
        }else {
            scenarios.addAll(userScenariosCN(interests));
        }
    }

    private List<ONewsScenario> userScenariosCN(String interests) {
        if(L.DEBUG) L.scenarios(interests);
        List<ONewsScenario> userScenariosCN = new ArrayList<ONewsScenario>();
        List<ONewsScenario> defaultScenariosCN = cnScenarios(false, false);
        userScenariosCN.add(getScenarioByCategory(ONewsScenarioCategory.SC_00));// 推荐
        userScenariosCN.add(getScenarioByCategory(ONewsScenarioCategory.SC_1D));// 热门
        userScenariosCN.add(getScenarioByCategory(ONewsScenarioCategory.SC_2D));// 热门

        String[] strings = interests.split(",");
        for (int i = 0; i < strings.length; i++){
            if(L.DEBUG) L.scenarios(strings[i]);

            ONewsScenario scenario = filterScenarioCN(strings[i].trim(), defaultScenariosCN);
            if(null != scenario){
                defaultScenariosCN.remove(scenario);
                userScenariosCN.add(scenario);
            }
        }

        if(!defaultScenariosCN.isEmpty()){
            userScenariosCN.addAll(defaultScenariosCN);
        }

        return userScenariosCN;
    }

    private ONewsScenario filterScenarioCN(String interest, List<ONewsScenario> scenarios) {
        if(String.valueOf(ONewsInterestCN.NC_00).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_02,scenarios);

        }else if(String.valueOf(ONewsInterestCN.NC_01).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_03,scenarios);

        }else if(String.valueOf(ONewsInterestCN.NC_02).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_05,scenarios);

        }else if(String.valueOf(ONewsInterestCN.NC_03).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_09,scenarios);

        }else if(String.valueOf(ONewsInterestCN.NC_04).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_12,scenarios);

        }else if(String.valueOf(ONewsInterestCN.NC_05).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_08,scenarios);

        }else if(String.valueOf(ONewsInterestCN.NC_06).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_0A,scenarios);

        }else if(String.valueOf(ONewsInterestCN.NC_07).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_04,scenarios);

        }else if(String.valueOf(ONewsInterestCN.NC_08).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_19,scenarios);

        }else if(String.valueOf(ONewsInterestCN.NC_09).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_10,scenarios);

        }else if(String.valueOf(ONewsInterestCN.NC_10).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_06,scenarios);

        }else if(String.valueOf(ONewsInterestCN.NC_11).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_07,scenarios);

        }else if(String.valueOf(ONewsInterestCN.NC_12).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_0C,scenarios);

        }else if(String.valueOf(ONewsInterestCN.NC_13).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_0F,scenarios);

        }else if(String.valueOf(ONewsInterestCN.NC_14).equals(interest)){

            return obtainScenario(ONewsScenarioCategory.SC_16,scenarios);
        }else {
            return null;
        }
    }

    /**
     * 国内版栏目
     * [{"id":"0","name":"推荐","show":"1","order":"0"},
     * {"id":"29","name":"热点","show":"1","order":"1"},
     * {"id":"2","name":"社会","show":"1","order":"3"},
     * {"id":"3","name":"娱乐","show":"1","order":"4"},
     * {"id":"5","name":"军事","show":"1","order":"5"},
     * {"id":"8","name":"汽车","show":"1","order":"6"},
     * {"id":"10","name":"时尚","show":"1","order":"7"},
     * {"id":"6","name":"科技","show":"1","order":"8"},
     * {"id":"7","name":"财经","show":"1","order":"9"},
     * {"id":"12","name":"健康","show":"1","order":"10","feature":"1"},
     * {"id":"4","name":"体育","show":"1","order":"11"},
     * {"id":"9","name":"房产","show":"1","order":"12"},
     * {"id":"26","name":"趣味","show":"1","order":"13"},
     * {"id":"15","name":"国际","show":"1","order":"14"},
     * {"id":"18","name":"历史","show":"1","order":"15"},
     * {"id":"16","name":"游戏","show":"1","order":"16"},
     * {"id":"22","name":"减肥","show":"1","order":"17"}]
     */
    public List<ONewsScenario> cnScenarios(boolean hasRecommend, boolean hasHot){
        List<ONewsScenario> cns = new ArrayList<ONewsScenario>();
        if (hasRecommend){
            cns.add(getScenarioByCategory(ONewsScenarioCategory.SC_00));
        }
        if (hasHot) {
            cns.add(getScenarioByCategory(ONewsScenarioCategory.SC_1D));
            cns.add(getScenarioByCategory(ONewsScenarioCategory.SC_2D));
        }
        cns.add(getScenarioByCategory(ONewsScenarioCategory.SC_02));
        cns.add(getScenarioByCategory(ONewsScenarioCategory.SC_03));
        cns.add(getScenarioByCategory(ONewsScenarioCategory.SC_05));
        cns.add(getScenarioByCategory(ONewsScenarioCategory.SC_08));
        cns.add(getScenarioByCategory(ONewsScenarioCategory.SC_0A));
        cns.add(getScenarioByCategory(ONewsScenarioCategory.SC_06));
        cns.add(getScenarioByCategory(ONewsScenarioCategory.SC_07));
        cns.add(getScenarioByCategory(ONewsScenarioCategory.SC_0C));
        cns.add(getScenarioByCategory(ONewsScenarioCategory.SC_04));
        cns.add(getScenarioByCategory(ONewsScenarioCategory.SC_09));
        cns.add(getScenarioByCategory(ONewsScenarioCategory.SC_19));
        cns.add(getScenarioByCategory(ONewsScenarioCategory.SC_0F));
        cns.add(getScenarioByCategory(ONewsScenarioCategory.SC_12));
        cns.add(getScenarioByCategory(ONewsScenarioCategory.SC_10));
        cns.add(getScenarioByCategory(ONewsScenarioCategory.SC_16));
        return cns;
    }
}

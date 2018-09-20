package lab.whitetree.bonny.box.ui;

import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.service.LocalService;
import lab.whitetree.bonny.box.service.NotificationService;
import lab.whitetree.bonny.box.storage.LocalStorage;

import org.whitetree.systable.system.U;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class SettingActivity extends PreferenceActivity 
							 implements OnPreferenceClickListener, OnSharedPreferenceChangeListener {

	public static final String BONNY_BOX_SETTING_FILE_SUFFIX = "_preferences";
	public static final String SP_KEY_ENABLE_NOTIFICATION_BAR = "pref_key_enable_notification_bar";
	public static final String SP_KEY_AD_WALL = "pref_key_ad_wall";
	public static final String SP_KEY_FEEDBACK = "pref_key_feedback";
	public static final String SP_KEY_TEMPERATURE_UNITS = "pref_key_temperature_unit";
	public static final String SP_KEY_NOTIFICATION_BAR_STYLE = "pref_key_set_notification_bar_style";

	private static final int TEMPERATURE_UNIT_CELSIUS    = 1;
	private static final int TEMPERATURE_UNIT_FAHRENHEIT = 2;
	
	private ListPreference mlpTempUnit = null;
	
	public static Intent getLaunchIntent(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, SettingActivity.class);
		return intent;
	}
	
	public static void startDefault(Context context) {
		context.startActivity(getLaunchIntent(context));
	}
	
	public static String getSettingSharedPrefFileName(Context context) {
		return context.getPackageName() + BONNY_BOX_SETTING_FILE_SUFFIX;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences_setting);
		
		initPreferences();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}

	@Override
	public boolean onPreferenceClick(Preference p) {
		if (SP_KEY_ENABLE_NOTIFICATION_BAR.equals(p.getKey())) {
			boolean isChecked = ((CheckBoxPreference) p).isChecked();  
			if (isChecked) {
				NotificationService.start(SettingActivity.this);
			} else {
				NotificationService.disable(SettingActivity.this);
			}
		}
		
		else if (SP_KEY_AD_WALL.equals(p.getKey())) {
//			AdWallActivity.launchActivity(SettingActivity.this);
			U.startViewOurApp(SettingActivity.this);
		}
		
		else if (SP_KEY_FEEDBACK.equals(p.getKey())) {
			//U.launchContactUs(SettingActivity.this);
		}
		
		else if (SP_KEY_NOTIFICATION_BAR_STYLE.equals(p.getKey())) {
			NotificationStyleActivity.startDefault(SettingActivity.this);
		}
		
		return false;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (SP_KEY_TEMPERATURE_UNITS.equals(key)) { 
			int value = TEMPERATURE_UNIT_CELSIUS;
			try {
				value = Integer.valueOf(mlpTempUnit.getValue()).intValue();
			} catch (Exception e) {
			}
			LocalStorage.getInstance().setTemperatureUnitAsCelsius(value != TEMPERATURE_UNIT_FAHRENHEIT);
			mlpTempUnit.setSummary(mlpTempUnit.getEntry());
			LocalService.startReregistTemperatureListener(SettingActivity.this);
		}
	}
	

	@SuppressWarnings("deprecation")
	private void initPreferences() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this); 
        prefs.registerOnSharedPreferenceChangeListener(this); 
		  
		findPreference(SP_KEY_ENABLE_NOTIFICATION_BAR).setOnPreferenceClickListener(this);
		findPreference(SP_KEY_AD_WALL).setOnPreferenceClickListener(this);
		findPreference(SP_KEY_FEEDBACK).setOnPreferenceClickListener(this);
		findPreference(SP_KEY_NOTIFICATION_BAR_STYLE).setOnPreferenceClickListener(this);
		
		mlpTempUnit = (ListPreference) findPreference(SP_KEY_TEMPERATURE_UNITS);
		if (TextUtils.isEmpty(mlpTempUnit.getValue())) {
			mlpTempUnit.setValue(String.valueOf(TEMPERATURE_UNIT_CELSIUS));
		}
		mlpTempUnit.setSummary(mlpTempUnit.getEntry());
	}
}

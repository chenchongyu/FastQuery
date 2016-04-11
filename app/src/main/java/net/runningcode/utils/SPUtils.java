package net.runningcode.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import net.runningcode.RunningCodeApplication;

public class SPUtils {
	private static final String DEFAULT_NAME = "share_data";
	private static SharedPreferences sp;
	public static SharedPreferences getInstance(String name){
		if (TextUtils.isEmpty(name))
			name = DEFAULT_NAME;
		sp = RunningCodeApplication.getInstance().getSharedPreferences(name,Context.MODE_MULTI_PROCESS);
		return sp;
	}

	private static String getString(String key,
			final String defaultValue) {
		return sp.getString(key, defaultValue);
	}

	private static void setString(final String key,final String value) {
		sp.edit().putString(key, value).commit();
	}

	private static boolean getBoolean(final String key,final boolean defaultValue) {
		return sp.getBoolean(key, defaultValue);
	}

	private static boolean hasKey(final String key) {
		return sp.contains(key);
	}

	private static void setBoolean(final String key,
			final boolean value) {
		sp.edit().putBoolean(key, value).commit();
	}

	private static void setInt(final String key,
			final int value) {
		sp.edit().putInt(key, value).commit();
	}

	private static int getInt(final String key,
			final int defaultValue) {
		return sp.getInt(key, defaultValue);
	}

	private static void setFloat(final String key,
			final float value) {
		sp.edit().putFloat(key, value).commit();
	}

	private static float getFloat(final String key,
			final float defaultValue) {
		return sp.getFloat(key, defaultValue);
	}

	private static void setSettingLong(final String key,
			final long value) {
		sp.edit().putLong(key, value).commit();
	}

	private static long getLong(final String key,
			final long defaultValue) {
		return sp.getLong(key, defaultValue);
	}

	private static void clearPreference(Context context) {
		final Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}
}

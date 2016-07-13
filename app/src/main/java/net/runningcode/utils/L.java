package net.runningcode.utils;

import android.util.Log;

import net.runningcode.BuildConfig;

import java.io.FileWriter;
import java.io.IOException;

//Logcat统一管理类
public class L
{

	private L()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	public static boolean isDebug = BuildConfig.DEBUG;
	private static final String TAG = "fastquery";

	// 下面四个是默认tag的函数
	public static void i(String msg)
	{
		System.out.println("是否degbu："+isDebug);
		if (isDebug)
			Log.i(TAG, msg);
//		writeLog(msg);

	}

	private static void writeLog(String msg) {
		try {
			FileWriter writer = new FileWriter(PathUtil.getInstance().getCacheRootPath("log")+"/log.txt", true);
			writer.write(msg);
			writer.write("\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void d(String msg)
	{
		if (isDebug)
			Log.d(TAG, msg);
//		writeLog(msg);
	}

	public static void e(final String msg)
	{
		if (isDebug)
			Log.e(TAG, msg);
		ThreadPool.submit(new Runnable() {
			@Override
			public void run() {
				writeLog(msg);
			}
		});

	}

	public static void v(String msg)
	{
		if (isDebug)
			Log.v(TAG, msg);
	}

	// 下面是传入自定义tag的函数
	public static void i(String tag, String msg)
	{
		if (isDebug)
			Log.i(tag, msg);
	}

	public static void d(String tag, String msg)
	{
		if (isDebug)
			Log.i(tag, msg);
	}

	public static void e(String tag, String msg)
	{
		if (isDebug)
			Log.i(tag, msg);
		writeLog(msg);
	}

	public static void v(String tag, String msg)
	{
		if (isDebug)
			Log.i(tag, msg);
	}
}
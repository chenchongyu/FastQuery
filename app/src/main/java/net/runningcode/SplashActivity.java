package net.runningcode;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sixth.adwoad.ErrorCode;
import com.sixth.adwoad.NativeAdListener;
import com.sixth.adwoad.NativeAdView;

import net.runningcode.constant.Constants;
import net.runningcode.utils.L;
import net.runningcode.utils.ThreadPool;

/**
 * @author ADWO 此实例为adwo全屏广告展示。
 *         adwo全屏为了能使广告更快，更好的展示，采用了外部存储卡存储机制。所有的广告资源都存放在手机外部存储卡的adwo目录下。
 *         adwo全屏广告从请求到销毁完全按照Android程序构架机制设计
 *         。有初始化，设置参数，请求，加载，请求加载成功失败回调接口，展示和销毁等接口。
 */
public class SplashActivity extends Activity implements NativeAdListener {

	private String LOG_TAG = "Adwo full-screen ad";
	private NativeAdView ad;
	private ImageView mImageView;
	private FrameLayout root;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		mImageView = (ImageView) findViewById(R.id.v_img);
		root = (FrameLayout) findViewById(R.id.splash_holder);
		//全屏广告实例   2ff473948b2d4dca80b845d368789e53
		// new InterstitialAd(Context,ID,isTesting,InterstitialAdListener);
//		ad = new InterstitialAd(this, Constants.ANWO_PUBLISHER_ID,false,this);

		ad = new NativeAdView(this,Constants.ANWO_PUBLISHER_ID,true,this);
		ad.prepareAd();
//		ad = new InterstitialAd(this, "2b8dbd92edd74a97b3ba6b0189bef125",false,this);
		// 设置全屏格式
//		ad.setDesireAdForm(InterstitialAd.ADWO_INTERSTITIAL);
		// 设置请求广告类型 可选。
//		ad.setDesireAdType((byte) 0);
		// 开始请求全屏广告
//		ad.prepareAd();
		Log.e(LOG_TAG, "onCreate");

	}
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		// 开始请求广告
		Log.e(LOG_TAG, "onAttachedToWindow");
	}



	private void dojump() {
//		startActivity(new Intent(SplashActivity.this,IndexActivity.class));
		ThreadPool.submitDelay(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(SplashActivity.this,IndexActivity.class));
				finish();
			}
		},3);
	}


	@Override
	public void onReceiveAd(String s) {
		L.i("get ad:"+s);
		JSONObject adJson = JSON.parseObject(s);
		String title = adJson.getString("title");
		String img = adJson.getString("img");

		RunningCodeApplication.loadImg(mImageView,img);
		final ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
				mImageView.getMeasuredWidth(),
				mImageView.getMeasuredHeight());
		ad.setLayoutParams(layoutParams);
		root.addView(ad);
		dojump();

	}

	@Override
	public void onFailedToReceiveAd(View view, ErrorCode errorCode) {
		dojump();
	}

	@Override
	public void onPresentScreen() {

	}

	@Override
	public void onDismissScreen() {

	}
}

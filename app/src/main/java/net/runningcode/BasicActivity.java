package net.runningcode;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xiaomi.mistatistic.sdk.MiStatInterface;
import com.zhy.autolayout.AutoLayoutActivity;

import net.runningcode.utils.AnimationFactory;
import net.runningcode.utils.L;
import net.runningcode.utils.TransitionHelper;


public abstract class BasicActivity extends AutoLayoutActivity {
	public static final String ACTION_EXIT = RunningCodeApplication.getInstance().getPackageName() + ".EXIT";
	public TextView baseTitle;//返回
	public LinearLayout baseHome;
	public TextView baseSubject;
	public TextView baseActionBtn;//actionbar右上角按按钮
	protected Toolbar toolbar;
	protected View shareTarget;
	protected Interpolator interpolator;
	protected ViewDataBinding binding;
	private SystemBarTintManager tintManager;

	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
			setupWindowAnimations();
		}

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

		binding = DataBindingUtil.setContentView(this, getContentViewID());

		//设置FitsSystemWindows
		ViewGroup contentFrameLayout = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
		View parentView = contentFrameLayout.getChildAt(0);
		if (parentView != null && Build.VERSION.SDK_INT >= 14) {
			parentView.setFitsSystemWindows(true);
		}

		if(showActionbar())
			baseInitActionBar();

	    IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_EXIT);
		registerReceiver(mExitReceiver, filter);
//	    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	protected void setupWindowAnimations() {
		L.i("setupWindowAnimations");
		Slide slideTransition = new Slide();
		slideTransition.setSlideEdge(Gravity.LEFT);
		slideTransition.setDuration(500);
		getWindow().setReenterTransition(slideTransition);
		getWindow().setExitTransition(slideTransition);

	}

	public void baseInitActionBar() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		L.i("获取toolbar:"+toolbar);
		if (toolbar != null) {
			toolbar.setTitle("");
			setSupportActionBar(toolbar);
			tintManager=new SystemBarTintManager(this);
//			setStatusBarColor(R.color.colorPrimaryDark);
			// 激活状态栏设置
			tintManager.setStatusBarTintEnabled(true);
			// 激活导航栏设置
			tintManager.setNavigationBarTintEnabled(true);

			baseSubject = (TextView) toolbar.findViewById(R.id.title);
			toolbar.setVisibility(View.VISIBLE);
			toolbar.setNavigationIcon(R.drawable.icon_back);
			toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						finishAfterTransition();
					}else
						finish();
				}
			});
			shareTarget = toolbar.findViewById(R.id.shared_target);
		}

	}
	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}


	/***
	 * 不想显示actionbar的activity需要重写此方法
	 * 返回false即可
	 * @return
	 */
	protected boolean showActionbar() {
		return true;
	}
	
	public void setTitle(int title) {
		setTitle(getString(title));
	}
	public void setTitle(String title){
		if (toolbar == null)return;
		baseSubject.setText(title);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	protected void setupEnterAnimations(final int color) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

			Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.changebounds_with_arcmotion);
			getWindow().setSharedElementEnterTransition(transition);
			transition.addListener(new Transition.TransitionListener() {
				@Override
				public void onTransitionStart(Transition transition) {
				}

				@Override
				public void onTransitionEnd(Transition transition) {
					// Removing listener here is very important because shared element transition is executed again backwards on exit. If we don't remove the listener this code will be triggered again.
					AnimationFactory.animateRevealShow(toolbar);
					setToolBarColor(color);
//                shareTarget.setBackgroundResource(R.color.red);
					shareTarget.setAlpha(0L);
//                shareTarget.setVisibility(View.GONE);
//                animateButtonsIn();
					transition.removeListener(this);
				}

				@Override
				public void onTransitionCancel(Transition transition) {
				}

				@Override
				public void onTransitionPause(Transition transition) {
				}

				@Override
				public void onTransitionResume(Transition transition) {
				}
			});
		}else {
			setToolBarColor(color);
		}
    }

	protected void setupExitAnimations() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			Fade returnTransition = new Fade();
			getWindow().setReturnTransition(returnTransition);
			returnTransition.setDuration(getResources().getInteger(R.integer.anim_duration_medium));
			returnTransition.setStartDelay(getResources().getInteger(R.integer.anim_duration_medium));
			returnTransition.addListener(new Transition.TransitionListener() {
				@Override
				public void onTransitionStart(Transition transition) {
					shareTarget.setAlpha(0.6F);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
						transition.removeListener(this);
					}
//                animateButtonsOut();
					AnimationFactory.animateRevealHide(toolbar);
				}

				@Override
				public void onTransitionEnd(Transition transition) {
				}

				@Override
				public void onTransitionCancel(Transition transition) {
				}

				@Override
				public void onTransitionPause(Transition transition) {
				}

				@Override
				public void onTransitionResume(Transition transition) {
				}
			});
		}
    }

	public abstract int getContentViewID();

	protected void initToolbar(int color,int drawble){
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
			setToolBarColor(color);
		}else {
			shareTarget.setBackgroundResource(drawble);
			shareTarget.setVisibility(View.VISIBLE);
		}
	}

	protected void setToolBarColor(int color){
		shareTarget.setVisibility(View.INVISIBLE);
		toolbar.setBackgroundResource(color);
		setStatusBarColor(color);
	}
	protected void setStatusBarColor(int color){
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//			getWindow().setStatusBarColor(getResources().getColor(color));
//			tintManager.setStatusBarTintResource(color);
//		}else
			tintManager.setStatusBarTintResource(color);
	}

	protected void setActionImage(int drawable,boolean hideText) {
		Drawable background = getResources().getDrawable(drawable);
		background.setBounds(0, 0, background.getMinimumWidth(), background.getMinimumHeight());
		baseActionBtn.setCompoundDrawables(background, null, null, null);
		baseActionBtn.setVisibility(View.VISIBLE);
		if(hideText)
			baseActionBtn.setText("");
	}
	
	public void setDrawbleTopImage(TextView view,int drawable) {
		setDrawbleTopImage(view, drawable, Color.GRAY);
	}
	
	public void setDrawbleTopImage(TextView view,int drawable,int color) {
		Drawable background = getResources().getDrawable(drawable);
		background.setBounds(0, 0, background.getMinimumWidth(), background.getMinimumHeight());
		view.setCompoundDrawables(null, background, null, null);
		view.setVisibility(View.VISIBLE);
		view.setTextColor(color);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		MobclickAgent.onResume(this);
		MiStatInterface.recordPageStart(this, getClass().getSimpleName());
	}

	@Override
	protected void onPause() {
		super.onPause();
//		MobclickAgent.onPause(this);
		MiStatInterface.recordPageEnd();
	}

	@Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	unregisterReceiver(mExitReceiver);
    }
 

	private BroadcastReceiver mExitReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
		  finish();
		}
	};
	
	public void appExit() {
	  Intent intent = new Intent();
	  intent.setAction(ACTION_EXIT);
	  sendBroadcast(intent);
	}

	protected <T extends View>T $(int id){
		return (T)findViewById(id);
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
//		final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(this, true);
//		ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairs);
//		startActivity(intent, transitionActivityOptions.toBundle());
	}

	public void startActivity(Intent intent,View view) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

			final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(this, false,
					new Pair<>(view, "express"));
//		final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(this, true);
			ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairs);
			startActivity(intent, transitionActivityOptions.toBundle());
		}else {
			startActivity(intent);
		}
	}
}

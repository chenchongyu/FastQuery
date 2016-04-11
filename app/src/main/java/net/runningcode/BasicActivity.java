package net.runningcode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.LinearLayout;
import android.widget.TextView;

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

//	private SystemBarTintManager tintManager;

	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setupWindowAnimations();

		super.setContentView(R.layout.layout_base);
		setContentView(getContentViewID());

		if(showActionbar())
			baseInitActionBar();

	    IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_EXIT);
		registerReceiver(mExitReceiver, filter);
//	    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

	}

	protected void setupWindowAnimations() {
		L.i("setupWindowAnimations");
//		Transition slide =  TransitionInflater.from(this).inflateTransition(R.transition.activity_slide_out);
//		Transition fade = TransitionInflater.from(this).inflateTransition(R.transition.activity_fade_in);
//		getWindow().setEnterTransition(fade);
//		getWindow().setExitTransition(slide);
		Slide slideTransition = new Slide();
		slideTransition.setSlideEdge(Gravity.LEFT);
		slideTransition.setDuration(500);
		getWindow().setReenterTransition(slideTransition);
		getWindow().setExitTransition(slideTransition);
	}

	public void baseInitActionBar() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			toolbar.setTitle("");
			setSupportActionBar(toolbar);
//			tintManager=new SystemBarTintManager(this);
			setStatusBarColor(R.color.colorPrimaryDark);
//			tintManager.setStatusBarTintEnabled(true);

			baseSubject = (TextView) toolbar.findViewById(R.id.title);
			toolbar.setVisibility(View.VISIBLE);
			toolbar.setNavigationIcon(R.drawable.icon_back);
			toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finishAfterTransition();
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

	@Override
	public void setContentView(int layoutResID) {
		setContentView(View.inflate(this, layoutResID, null));
	}

	@Override
	public void setContentView(View view) {
		LinearLayout root = (LinearLayout) findViewById(R.id.v_root);
		if(root == null) return;
		root.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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

	protected void setupEnterAnimations(final int color) {
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
    }

	protected void setupExitAnimations() {
        Fade returnTransition = new Fade();
        getWindow().setReturnTransition(returnTransition);
        returnTransition.setDuration(getResources().getInteger(R.integer.anim_duration_medium));
        returnTransition.setStartDelay(getResources().getInteger(R.integer.anim_duration_medium));
        returnTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                shareTarget.setAlpha(0.6F);
                transition.removeListener(this);
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

	public abstract int getContentViewID();

	protected void setToolBarColor(int color){
		toolbar.setBackgroundResource(color);
		setStatusBarColor(color);
	}
	protected void setStatusBarColor(int color){
		getWindow().setStatusBarColor(getResources().getColor(color));
//		tintManager.setStatusBarTintResource(color);
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
//		super.startActivity(intent);
		final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(this, true);
		ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairs);
		startActivity(intent, transitionActivityOptions.toBundle());
	}

	public void startActivity(Intent intent,View view) {

		final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(this, false,
				new Pair<>(view, "express"));
//		final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(this, true);
		ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairs);
		startActivity(intent, transitionActivityOptions.toBundle());
	}
}

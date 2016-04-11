package net.runningcode.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * Created by Administrator on 2016/4/8.
 */
public class AnimationFactory {
    public interface OnAnimationFinishListener{
        void onFinished();
    }

    public static TranslateAnimation getTranslateAnimation(float x, float toX, float y, float toY, final OnAnimationFinishListener animationFinishListener){
        TranslateAnimation translateAnimation = new TranslateAnimation(x,toX,y,toY);
        translateAnimation.setDuration(800);
        translateAnimation.setFillAfter(true);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animationFinishListener.onFinished();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        return translateAnimation;
    }

    public static AnimationSet getParabolaAnimation(float x, float toX, float y, float toY, final OnAnimationFinishListener animationFinishListener){
        AnimationSet set=new AnimationSet(false);
        TranslateAnimation translateAnimationX=new TranslateAnimation(x, toX, 0, 0);
        translateAnimationX.setInterpolator(new LinearInterpolator());

        TranslateAnimation translateAnimationY=new TranslateAnimation(0, 0, y, toY);
        translateAnimationY.setInterpolator(new AccelerateInterpolator());

        set.addAnimation(translateAnimationY);
        set.addAnimation(translateAnimationX);
        set.setDuration(500);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animationFinishListener.onFinished();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        return set;
    }

    public static void animateRevealShow(View viewRoot) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
        viewRoot.setVisibility(View.VISIBLE);
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.start();
    }

    public static void animateRevealHide(final View viewRoot) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int initialRadius = viewRoot.getWidth();

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, initialRadius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                viewRoot.setVisibility(View.INVISIBLE);
            }
        });
        anim.setDuration(500);
        anim.start();
    }
}

package com.han.citynews_n.citynews_n;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.han.citynews_n.citynews_n.utils.CacheUtils;

public class SplashActivity extends Activity {

    public static final String IS_FIRST_OPEN = "is_first_open";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        init();
    }

    private void init() {
        ImageView imageView = findViewById(R.id.iv_splash);

        AnimationSet mAnimationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0, 1.0f,
                0, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        //scaleAnimation.setDuration(5000);
        mAnimationSet.addAnimation(scaleAnimation);

        RotateAnimation rotateAnimation = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimationSet.addAnimation(rotateAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 0.5f);
        mAnimationSet.addAnimation(alphaAnimation);


        mAnimationSet.setDuration(5000);
        mAnimationSet.setAnimationListener(new MyAnimationListener());
        imageView.setAnimation(mAnimationSet);
    }

    class MyAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (CacheUtils.getBoolean(SplashActivity.this, IS_FIRST_OPEN, true)) {
                startActivity(new Intent(SplashActivity.this, GuideActivity.class));
            }else{
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

}

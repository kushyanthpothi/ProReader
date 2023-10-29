package com.magiccode.proreader;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class splash_activity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        getSupportActionBar().hide();

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Apply the fade-in animation to your view (e.g., an ImageView)
        // Replace R.id.yourViewId with the actual ID of the view you want to animate
        findViewById(R.id.imageView).startAnimation(fadeIn);

        Intent i=new Intent(splash_activity.this, MainActivity.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(i);
                finish();
            }
        },1000);

    }
}

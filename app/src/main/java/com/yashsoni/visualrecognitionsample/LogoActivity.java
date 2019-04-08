package com.yashsoni.visualrecognitionsample;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.yashsoni.visualrecognitionsample.activities.HomeActivity;

public class LogoActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT=4000;
    ImageView imgLogo;
    ConstraintLayout my_layout;
    AnimationDrawable animationDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        imgLogo= findViewById(R.id.logo);

        //Inicio con animacion en la imagen
        Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.logo_anim);
        imgLogo.startAnimation(animation);
        new Handler().postDelayed(() -> {
            Intent HomeIntent= new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(HomeIntent);
            finish();
        },SPLASH_TIME_OUT);
    }
}

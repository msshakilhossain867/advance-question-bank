package com.example.questionbank_practiceexam.Activity;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.presentation.main.MainActivity;
import com.example.questionbank_practiceexam.presentation.onboarding.Activity_Wecome_Screen;
import com.example.questionbank_practiceexam.utils.SharePreferenceManager;

//static import
import static com.example.questionbank_practiceexam.utils.Utils.*;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class Splash_Screen extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 3000;
    private ImageView imgLogo;
    private TextView  txtTagline;

    private LinearLayout brandingContainer;
    private LinearProgressIndicator progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        brandingContainer = findViewById(R.id.brandingContainer);
        txtTagline = findViewById(R.id.txtTagline);
        progressBar = findViewById(R.id.progressBar);

        // ১. প্রাথমিক লেআউট এনিমেশন (Fade & Scale In)
        brandingContainer.setAlpha(0f);
        brandingContainer.setScaleX(0.8f);
        brandingContainer.setScaleY(0.8f);
        progressBar.setAlpha(0f);

        brandingContainer.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        progressBar.animate()
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(400)
                .start();

        // 🌟 ২. "Learn • Practice • Success" এর অলটাইম লাফানোর এনিমেশন (Infinite Bounce)
        startContinuousBounceAnimation(txtTagline);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if (SharePreferenceManager.isFirstTime(Splash_Screen.this)) {

                 moveToNextActivity(Splash_Screen.this, Activity_Wecome_Screen.class);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                } else {

               moveToNextActivity(Splash_Screen.this, MainActivity.class);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                }

                finish();

            }
        }, SPLASH_TIME_OUT);
    }

    private void startContinuousBounceAnimation(TextView textView) {
        // Y-অক্ষ বা ওপর-নিচে ২০ পিক্সেল লাফালাফি করবে
        ObjectAnimator bounceAnim = ObjectAnimator.ofFloat(textView, "translationY", 0f, -18f);
        bounceAnim.setDuration(600); // প্রতিটি লাফের সময় (Milliseconds)
        bounceAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        bounceAnim.setRepeatCount(ValueAnimator.INFINITE); // অলটাইম চলবে
        bounceAnim.setRepeatMode(ValueAnimator.REVERSE); // ওপরে গিয়ে আবার মসৃণভাবে নিচে নামবে
        bounceAnim.start();
    }

}
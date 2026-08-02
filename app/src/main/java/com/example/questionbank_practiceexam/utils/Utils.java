package com.example.questionbank_practiceexam.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class Utils {

    public static void moveToNextActivity(Context context, Class<?> activity)
    {

       Intent intent = new Intent(context,activity);
       context.startActivity(intent);

    }

    public static void startContinuousFloatingAnimation(View view) {
        ObjectAnimator floatAnim = ObjectAnimator.ofFloat(view, "translationY", 0f, -15f);
        floatAnim.setDuration(1200); // গতি (১২০০ms খুব স্মুথ অনুভূতি দেয়)
        floatAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnim.setRepeatCount(ValueAnimator.INFINITE); // অলটাইম চলবে
        floatAnim.setRepeatMode(ValueAnimator.REVERSE); // ওপরে উঠে আবার মসৃণভাবে নিচে নামবে
        floatAnim.start();
    }

}

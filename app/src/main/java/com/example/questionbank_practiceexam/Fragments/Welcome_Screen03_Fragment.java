package com.example.questionbank_practiceexam.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.questionbank_practiceexam.Activity.MainActivity;
import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.utils.SharePreferenceManager;
import com.example.questionbank_practiceexam.utils.Utils;


public class Welcome_Screen03_Fragment extends Fragment {
    Button btnNext;
    private ImageView imageView5;
    private TextView textView5, textView9, textView6, textView11;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome__screen03_, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnNext = view.findViewById(R.id.btnNext);
        imageView5 = view.findViewById(R.id.imageView5);
        textView5 = view.findViewById(R.id.textView5);
        textView9 = view.findViewById(R.id.textView9);
        textView6 = view.findViewById(R.id.textView6);
        textView11 = view.findViewById(R.id.textView11);

        // ThirdFragment (শেষ ফ্র্যাগমেন্ট)-এর onViewCreated মেথডে:
        btnNext.setOnClickListener(v -> {
            // ১. প্রথমবার দেখার কাজ সম্পন্ন হয়েছে তা সেভ করে দেওয়া
            SharePreferenceManager.setFirstTimeCompleted(requireContext());

            // ২. HomeActivity-তে চলে যাওয়া
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            startActivity(intent);

            // ৩. OnboardingActivity বন্ধ করে দেওয়া
            requireActivity().finish();
        });


        imageView5.setAlpha(0f);
        imageView5.setScaleX(0.7f);
        imageView5.setScaleY(0.7f);

        textView5.setAlpha(0f);
        textView5.setTranslationY(40f);

        textView9.setAlpha(0f);
        textView9.setTranslationY(40f);

        textView6.setAlpha(0f);
        textView6.setTranslationY(40f);

        textView11.setAlpha(0f);
        textView11.setTranslationY(40f);

        btnNext.setAlpha(0f);
        btnNext.setTranslationY(50f);

        // ৩. স্ক্রিনে আসার সাথে সাথেই টেক্সট, বাটন ও ইমেজের এনিমেশন ফায়ার করা
        imageView5.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(600)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    // 🌟 ইমেজ বড় হওয়া শেষ হলে অলটাইম ফ্লোটিং এনিমেশন শুরু হবে
                    Utils.startContinuousFloatingAnimation(imageView5);
                })
                .start();

        textView5.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(200).start();
        textView9.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(300).start();
        textView6.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(400).start();
        textView11.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(500).start();

        // 🌟 Get Started বাটনটি সুন্দরভাবে নিচ থেকে ভেসে উঠবে
        btnNext.animate().alpha(1f).translationY(0f).setDuration(600).setStartDelay(650).start();


    }
}
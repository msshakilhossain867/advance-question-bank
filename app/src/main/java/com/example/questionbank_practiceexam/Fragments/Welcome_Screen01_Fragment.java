package com.example.questionbank_practiceexam.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.questionbank_practiceexam.presentation.main.MainActivity;
import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.utils.SharePreferenceManager;
import com.example.questionbank_practiceexam.utils.Utils;


public class Welcome_Screen01_Fragment extends Fragment {

    private TextView skipOnBoarding;
    private ImageView imageView4;
    private TextView textView5, textView7, textView6, txtNext;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_welcome__screen01_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        skipOnBoarding=view.findViewById(R.id.txtSkip);
        txtNext=view.findViewById(R.id.txtNext);
        imageView4 = view.findViewById(R.id.imageView4);
        textView5 = view.findViewById(R.id.textView5);
        textView7 = view.findViewById(R.id.textView7);
        textView6 = view.findViewById(R.id.textView6);
        txtNext = view.findViewById(R.id.txtNext);


        txtNext.setOnClickListener(v -> {
            ViewPager2 viewPager = requireActivity().findViewById(R.id.viewPager); // আপনার ViewPager এর ID দিন
            if (viewPager != null) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });

        skipOnBoarding.setOnClickListener(v -> {
            SharePreferenceManager.setFirstTimeCompleted(requireContext());
            startActivity(new Intent(requireActivity(), MainActivity.class));
            requireActivity().finish();
        });



        imageView4.setAlpha(0f);
        imageView4.setScaleX(0.7f);
        imageView4.setScaleY(0.7f);

        textView5.setAlpha(0f);
        textView5.setTranslationY(40f);

        textView7.setAlpha(0f);
        textView7.setTranslationY(40f);

        textView6.setAlpha(0f);
        textView6.setTranslationY(40f);

        // ৩. স্ক্রিনে ঢোকার সময় একবারের এনিমেশন
        imageView4.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(600)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    // 🌟 ইমেজ বড় হওয়া শেষ হলে অলটাইম ফ্লোটিং/লাফানোর এনিমেশন শুরু হবে
                    Utils.startContinuousFloatingAnimation(imageView4);
                })
                .start();

        textView5.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(200).start();
        textView7.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(350).start();
        textView6.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(500).start();
    }

    // 🚀 ইমেজের অলটাইম স্মুথ নড়াচড়া/লাফানোর (Continuous Floating) লজিক

    }




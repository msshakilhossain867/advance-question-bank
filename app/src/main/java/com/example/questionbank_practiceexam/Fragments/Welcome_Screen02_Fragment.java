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

import com.example.questionbank_practiceexam.Activity.MainActivity;
import com.example.questionbank_practiceexam.R;
import com.example.questionbank_practiceexam.utils.SharePreferenceManager;
import com.example.questionbank_practiceexam.utils.Utils;


public class Welcome_Screen02_Fragment extends Fragment {
    private TextView txtNext;
    private TextView skipOnBoarding;

    private ImageView imageView5;
    private TextView textView5, textView9, textView6;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome__screen02_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        skipOnBoarding=view.findViewById(R.id.txtSkip);
        txtNext=view.findViewById(R.id.txtNext);
        imageView5 = view.findViewById(R.id.imageView5);
        textView5 = view.findViewById(R.id.textView5);
        textView9 = view.findViewById(R.id.textView9);
        textView6 = view.findViewById(R.id.textView6);




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



        // ২. শুরুতেই এনিমেশনের প্রাথমিক রূপ (Hide & Scale Down)
        imageView5.setAlpha(0f);
        imageView5.setScaleX(0.7f);
        imageView5.setScaleY(0.7f);

        textView5.setAlpha(0f);
        textView5.setTranslationY(40f);

        textView9.setAlpha(0f);
        textView9.setTranslationY(40f);

        textView6.setAlpha(0f);
        textView6.setTranslationY(40f);

        // ৩. স্ক্রিনে ঢোকার সময়ে টেক্সট ও ইমেজের এনিমেশন
        imageView5.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(600)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    // 🌟 ইমেজ বড় হওয়া শেষ হলে অলটাইম ফ্লোটিং এনিমেশন শুরু হবে
                   Utils. startContinuousFloatingAnimation(imageView5);
                })
                .start();

        textView5.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(200).start();
        textView9.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(350).start();
        textView6.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(500).start();


    }
}

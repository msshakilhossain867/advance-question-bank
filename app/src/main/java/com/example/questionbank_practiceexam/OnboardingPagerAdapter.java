package com.example.questionbank_practiceexam;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.questionbank_practiceexam.Fragments.Welcome_Screen01_Fragment;
import com.example.questionbank_practiceexam.Fragments.Welcome_Screen02_Fragment;
import com.example.questionbank_practiceexam.Fragments.Welcome_Screen03_Fragment;

public class OnboardingPagerAdapter extends FragmentStateAdapter {

    public OnboardingPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // পজিশন অনুযায়ী সঠিক ফ্রেগমেন্ট রিটার্ন করবে
        switch (position) {
            case 0:
                return new Welcome_Screen01_Fragment();  // 👈 আপনার ১ম ফ্রেগমেন্টের নাম
            case 1:
                return new Welcome_Screen02_Fragment(); // 👈 আপনার ২য় ফ্রেগমেন্টের নাম
            case 2:
                return new Welcome_Screen03_Fragment();  // 👈 আপনার ৩য় ফ্রেগমেন্টের নাম
            default:
                return new Welcome_Screen01_Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // মোট ৩টি ফ্রেগমেন্ট
    }
}
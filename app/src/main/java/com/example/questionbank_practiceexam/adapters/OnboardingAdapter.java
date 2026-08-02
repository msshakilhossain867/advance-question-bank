package com.example.questionbank_practiceexam.adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.questionbank_practiceexam.Fragments.Welcome_Screen01_Fragment;
import com.example.questionbank_practiceexam.Fragments.Welcome_Screen02_Fragment;
import com.example.questionbank_practiceexam.Fragments.Welcome_Screen03_Fragment;

public class OnboardingAdapter extends FragmentStateAdapter {

    public OnboardingAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Welcome_Screen01_Fragment();  // আপনার ১ নম্বর ফ্র্যাগমেন্ট
            case 1:
                return new Welcome_Screen02_Fragment(); // আপনার ২ নম্বর ফ্র্যাগমেন্ট
            case 2:
                return new Welcome_Screen03_Fragment();  // আপনার ৩ নম্বর ফ্র্যাগমেন্ট
            default:
                return new Welcome_Screen01_Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // মোট ৩টি ফ্র্যাগমেন্ট
    }
}

package com.example.questionbank_practiceexam.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.questionbank_practiceexam.Fragments.Welcome_Screen01_Fragment;
import com.example.questionbank_practiceexam.Fragments.Welcome_Screen02_Fragment;
import com.example.questionbank_practiceexam.Fragments.Welcome_Screen03_Fragment;
import com.example.questionbank_practiceexam.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Activity_Wecome_Screen extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wecome_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        // অ্যাডাপ্টার সেট করা
        OnboardingPagerAdapter adapter = new OnboardingPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // onCreate মেথডে TabLayoutMediator ডট তৈরি করার পর:
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // ডটে টেক্সট থাকবে না
        }).attach();

// 🌟 ডটগুলোকে স্ট্রেচ হওয়া থেকে বাঁচাতে এবং ফাকা ফাকা প্রিমিয়াম লুক দিতে:
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();

            // ডটের ডানে-বামে স্পেসিং (Margin) এবং নির্দিষ্ট সাইজ ফিক্সড করা
            p.setMargins(10, 0, 10, 0);
            tab.requestLayout();
        }

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position == 2) {
                    // ৩ নম্বর পেজে আসলে TabLayout হাইড হয়ে যাবে
                    tabLayout.setVisibility(View.GONE);
                } else {
                    // ১ম ও ২য় পেজে TabLayout দেখা যাবে
                    tabLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // ViewPager2-এর জন্য Fragment Adapter Class
    private static class OnboardingPagerAdapter extends FragmentStateAdapter {

        public OnboardingPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new Welcome_Screen01_Fragment(); // ১ম ফ্রেগমেন্ট
                case 1:
                    return new Welcome_Screen02_Fragment(); // ২য় ফ্রেগমেন্ট
                case 2:
                    return new Welcome_Screen03_Fragment(); // ৩য় ফ্রেগমেন্ট
                default:
                    return new Welcome_Screen01_Fragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3; // মোট ৩টি ফ্রেগমেন্ট
        }
    }
}
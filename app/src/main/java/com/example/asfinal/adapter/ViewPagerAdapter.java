package com.example.asfinal.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.asfinal.fragment.ImageFragment;
import com.example.asfinal.fragment.InforFragment;
import com.example.asfinal.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private User user;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior, User user) {
        super(fm, behavior);
        this.user = user;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                InforFragment inforFragment = new InforFragment();
                Bundle bundle = new Bundle();

                bundle.putSerializable("user", user);
                inforFragment.setArguments(bundle);
                return inforFragment;
            case 1:
                ImageFragment imagesFragment = new ImageFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("user", user);
                imagesFragment.setArguments(bundle1);
                return imagesFragment;
        }
        return new InforFragment();
    }
    @Override
    public int getCount() {
        return 2;
    }
}

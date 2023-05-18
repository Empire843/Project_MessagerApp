package com.example.asfinal.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asfinal.R;
import com.example.asfinal.adapter.ImageAdapter;
import com.example.asfinal.model.User;

import java.util.ArrayList;
import java.util.List;

public class InforFragment extends Fragment {
    private User user = new User();
    private TextView tvEmail, tvPhone, tvAddress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
        } else {
            Toast.makeText(getContext(), "Lỗi: Không có user", Toast.LENGTH_SHORT).show();
        }
        return inflater.inflate(R.layout.fragment_infor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvEmail.setText(user.getEmail());
        tvPhone.setText(user.getPhone_number());
        tvAddress.setText(user.getAddress());
    }
}
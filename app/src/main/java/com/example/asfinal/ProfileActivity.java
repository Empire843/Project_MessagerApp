package com.example.asfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.asfinal.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView txtFullName, txtEmail, txtPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        setData();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        String uid = currentUser.getUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    txtFullName.setText(user.getFull_name());
                    txtEmail.setText("Email: " + user.getEmail());
                    txtPhone.setText("Phone Number: " + user.getPhone_number());
                    // Nếu có hình ảnh, có thể tải ảnh từ URL và hiển thị lên ImageView trong NavigationView Header
//                    if (user.getPhotoUrl() != null) {
//                        ImageView avatarImageView = navigationView.getHeaderView(0).findViewById(R.id.nav_header_avatar);
//                        Glide.with(context).load(user.getPhotoUrl()).into(avatarImageView);
//                    }
                }
                // Xử lý danh sách người dùng tại đây
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi tại đây
            }
        });
    }

    public void init() {
        txtFullName = findViewById(R.id.full_name_profile);
        txtEmail = findViewById(R.id.user_email);
        txtPhone = findViewById(R.id.user_phone);
//        toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        content
    }
}
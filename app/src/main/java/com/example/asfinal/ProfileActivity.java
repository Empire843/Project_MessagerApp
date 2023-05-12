package com.example.asfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.asfinal.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView txtFullName, txtEmail, txtPhone, txtAddress;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        receiveDataFromIntent();

//        setData();
        displayInfor();
        ImageView editButton = findViewById(R.id.toolbarEditButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("user", user);
                Toast.makeText(ProfileActivity.this, user.getFull_name(), Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private void receiveDataFromIntent() {
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        if(user == null){
            Toast.makeText(this, "user bị null;", Toast.LENGTH_SHORT).show();
        }
    }
    private void displayInfor(){
        txtFullName.setText(user.getFull_name());
        txtEmail.setText("Email: " + user.getEmail());
        txtAddress.setText("Address: " + user.getAddress());
        txtPhone.setText("Phone Number: " + user.getPhone_number());
        if (user.getAvatar() != null) {
            CircleImageView profileImageView = findViewById(R.id.user_avatar);
            Glide.with(ProfileActivity.this)
                    .load(user.getAvatar())
                    .placeholder(R.drawable.loading_image)
//                                .error(R.drawable.error) // Ảnh hiển thị khi có lỗi xảy ra trong quá trình tải ảnh
                    .into(profileImageView);
        }
    }
    public void init() {
        txtFullName = findViewById(R.id.full_name_profile);
        txtEmail = findViewById(R.id.user_email);
        txtPhone = findViewById(R.id.user_phone);
        txtAddress = findViewById(R.id.user_address);
//        toolbar
        toolbar = findViewById(R.id.toolbar_edit);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        content
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Xử lý khi click vào icon back
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
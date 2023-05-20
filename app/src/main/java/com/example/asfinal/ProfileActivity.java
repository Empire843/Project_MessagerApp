package com.example.asfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.asfinal.adapter.ViewPagerAdapter;
import com.example.asfinal.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView txtFullName, txtEmail, txtPhone, txtAddress;
    private User user;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageView editButton;
    private ViewPagerAdapter viewPagerAdapter;
    private List<String> listImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();

        receiveDataFromIntent();
        int colorA = Color.parseColor("#FF0000"); // Màu đỏ
        int colorB = Color.parseColor("#0000FF"); // Màu xanh
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{colorA, colorB});
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        View view = findViewById(R.id.background_dimmer);
        view.setBackground(gradientDrawable);
//        display;
        displayInfor();
        tabLayoutInformation();
        if (user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            editButton.setVisibility(View.VISIBLE);
        } else {
            editButton.setVisibility(View.GONE);
        }
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("user", user);
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
        if (user == null) {
            Toast.makeText(this, "user bị null;", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayInfor() {
        txtFullName.setText(user.getFull_name());
        txtEmail.setText("Email: " + user.getEmail());
        txtAddress.setText("Address: " + user.getAddress());
        txtPhone.setText("Phone Number: " + user.getPhone_number());
        CircleImageView profileImageView = findViewById(R.id.user_avatar);
        if (user.getAvatar() != null) {
            Glide.with(ProfileActivity.this)
                    .load(user.getAvatar())
                    .placeholder(R.drawable.loading_image)
                    .into(profileImageView);
        }
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageDialog(ProfileActivity.this, user.getAvatar());
            }
        });
    }

    private void showImageDialog(Context context, String imageUrl) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_image_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView imageView = dialog.findViewById(R.id.imageView);
        Glide.with(getApplicationContext())
                .load(imageUrl)
//                .placeholder(R.drawable.ic_user)
                .into(imageView);
        dialog.show();
    }

    private void tabLayoutInformation() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, user);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab tab1 = tabLayout.getTabAt(0);
        tab1.setText("Information");
        TabLayout.Tab tab2 = tabLayout.getTabAt(1);
        tab2.setText("Images");
    }

    public void init() {
        txtFullName = findViewById(R.id.full_name_profile);
        txtEmail = findViewById(R.id.user_email);
        txtPhone = findViewById(R.id.user_phone);
        txtAddress = findViewById(R.id.user_address);
        editButton = findViewById(R.id.toolbarEditButton);
//        toolbar
        toolbar = findViewById(R.id.toolbar_edit);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabLayout);

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
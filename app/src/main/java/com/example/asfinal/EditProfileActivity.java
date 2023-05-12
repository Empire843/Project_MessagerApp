package com.example.asfinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

import com.bumptech.glide.Glide;
import com.example.asfinal.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1;
    private CircleImageView avatar;
    private User user;
    private User userUpdate;
    private Toolbar toolbar;
    private EditText fullName, address, phone, old_password, new_password, confirm_password;
    private Button btnChangePassword;
    private ImageView toolbarSaveButton;
    private String imageUrlUpdate;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();
        receiveDataFromIntent();
        displayOldInformation();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                old_password.setVisibility(View.VISIBLE);
                new_password.setVisibility(View.VISIBLE);
                confirm_password.setVisibility(View.VISIBLE);
            }
        });
        toolbarSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingBar.show();
                saveInforUserDatabase(null);
                loadingBar.dismiss();
                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
            }
        });
    }

    public void init() {
//        toolbar
        toolbar = findViewById(R.id.toolbar_save);
        imageUrlUpdate = "";
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbarSaveButton = findViewById(R.id.toolbarSaveButton);
//        content
        avatar = findViewById(R.id.edit_user_avatar);
        fullName = findViewById(R.id.edit_fullname_profile);
        address = findViewById(R.id.edit_address_profile);
        phone = findViewById(R.id.edit_phone_profile);
        old_password = findViewById(R.id.old_password);
        new_password = findViewById(R.id.new_password);
        confirm_password = findViewById(R.id.confirm_password);

        btnChangePassword = findViewById(R.id.btn_change_password);
        loadingBar = new ProgressDialog(this);
    }

    private void displayOldInformation() {
        if (user.getAvatar() != null) {
            CircleImageView profileImageView = findViewById(R.id.edit_user_avatar);
            Glide.with(EditProfileActivity.this)
                    .load(user.getAvatar())
                    .placeholder(R.drawable.loading_image)
//                                .error(R.drawable.error) // Ảnh hiển thị khi có lỗi xảy ra trong quá trình tải ảnh
                    .into(profileImageView);
        }
        fullName.setText(user.getFull_name());
        address.setText(user.getAddress());
        phone.setText(user.getPhone_number());
        old_password.setVisibility(View.GONE);
        new_password.setVisibility(View.GONE);
        confirm_password.setVisibility(View.GONE);
    }

    private void receiveDataFromIntent() {
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void uploadImageToStorage(Uri imageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setView(R.layout.loading_layout)
                .setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        String pathString = "images/" + user.getUid() + "/" + imageUri.getLastPathSegment();
        StorageReference storageRef = storage.getReference().child(pathString);
        UploadTask uploadTask = storageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        String imageUrl = downloadUrl.toString();
                        imageUrlUpdate = imageUrl;
                        saveInforUserDatabase(imageUrl);
                        alertDialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Xử lý khi tải lên thất bại
                Toast.makeText(EditProfileActivity.this, "Tải ảnh lên thất bại, hãy thử lại!", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
    }

    private void saveInforUserDatabase(String imageUrl) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        Map<String, Object> updateInfo = new HashMap<>();
        if (fullName.getText().toString().isEmpty() || phone.getText().toString().isEmpty() || address.getText().toString().isEmpty()) {
            Toast.makeText(this, "Full name is empty!", Toast.LENGTH_SHORT).show();
        } else {
            updateInfo.put("full_name", fullName.getText().toString());
            updateInfo.put("address", address.getText().toString());
            updateInfo.put("phone_number", phone.getText().toString());
            user.setFull_name(fullName.getText().toString());
            user.setPhone_number(phone.getText().toString());
            user.setAddress(address.getText().toString());
        }
        if (imageUrl != null) {
            updateInfo.put("avatar", imageUrl);
            user.setAvatar(imageUrl);
        }
        if (!old_password.getText().toString().isEmpty() && !new_password.getText().toString().isEmpty() && !confirm_password.getText().toString().isEmpty()) {
            if (old_password.getText().toString().equals(user.getPassword())) {
                if (new_password.getText().toString().equals(confirm_password.getText().toString())) {
                    updateInfo.put("password", new_password.getText().toString());
                    user.setPassword(new_password.getText().toString());
                } else {
                    Toast.makeText(this, "Confirm password is not correct!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Old password is not correct!", Toast.LENGTH_SHORT).show();
            }
        }
        userRef.updateChildren(updateInfo, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Toast.makeText(EditProfileActivity.this, "Update Success", Toast.LENGTH_SHORT).show();
                    // Cập nhật thành công
                } else {
                    // Xảy ra lỗi khi cập nhật
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            avatar.setImageURI(imageUri);
            loadingBar.show();
            uploadImageToStorage(imageUri);
            loadingBar.dismiss();
        }
    }
}
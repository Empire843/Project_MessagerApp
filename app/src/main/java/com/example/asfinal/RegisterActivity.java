package com.example.asfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private TextView txtLogin;
    private EditText editEmail, editPassword, editConfirmPassword, editFullName, editPhoneNumber;
    private Spinner spinnerGender;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        setEventClick();
    }

    private void init() {
        txtLogin = findViewById(R.id.login_text);
        editEmail = findViewById(R.id.email);
        editPassword = findViewById(R.id.password);
        editConfirmPassword = findViewById(R.id.confirm_password);
        editFullName = findViewById(R.id.full_name);
        editPhoneNumber = findViewById(R.id.phone_number);
        mAuth = FirebaseAuth.getInstance();
        btnRegister = findViewById(R.id.register_button);
        loadingBar = new ProgressDialog(this);
    }

    private void setEventClick() {
        btnRegister.setOnClickListener(view -> {
            createUser();
        });
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(com.example.asfinal.RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createUser() {
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();
        String full_name = editFullName.getText().toString();
        String phone_number = editPhoneNumber.getText().toString();
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Email can not be empty!");
            editEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            editPassword.setError("Password can not be empty!");
            editPassword.requestFocus();
        } else if (!TextUtils.equals(password, confirmPassword)) {
            editConfirmPassword.setError("Confirm password does not match!");
            editConfirmPassword.requestFocus();
        } else if (TextUtils.isEmpty(phone_number)) {
            editFullName.setError("Phone number can not be empty!");
            editFullName.requestFocus();
        } else if (TextUtils.isEmpty(full_name)) {
            editFullName.setError("Full name can not be empty!");
            editFullName.requestFocus();
        } else {
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    loadingBar.dismiss();
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                        Map<String, Object> user = new HashMap<>();
                        user.put("email", email);
                        user.put("password", password);
                        user.put("phone_number", phone_number);
                        user.put("full_name", full_name);
                        currentUserDb.setValue(user);
                        Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
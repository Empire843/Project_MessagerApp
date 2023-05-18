package com.example.asfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.asfinal.adapter.UserAdapter;
import com.example.asfinal.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddMessageActivity extends AppCompatActivity implements UserAdapter.UserListener {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private List<User> userList;
    private UserAdapter adapter;
    private User userCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message);
        initView();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uidCurrent = mAuth.getCurrentUser().getUid();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String fullName) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty() || newText.equals("") || newText == null) {
                    userList.clear();
                    adapter.setList(userList);
                    recyclerView.setAdapter(adapter);
                    return false;
                }
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userList.clear();
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            User user = userSnapshot.getValue(User.class);
                            if (user != null) {
                                if (user.getFull_name().toLowerCase(Locale.ROOT).contains(newText.toLowerCase()) || user.getEmail().toLowerCase(Locale.ROOT).contains(newText.toLowerCase())) {
                                    String uid = userSnapshot.getKey();
                                    if (!uidCurrent.equals(uid)) {
                                        user.setUid(uid);
                                        userList.add(user);
                                    }
                                }
                            }
                            adapter.setList(userList);
                            LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                            recyclerView.setLayoutManager(manager);
                            recyclerView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý lỗi tại đây
                    }
                });
                return false;
            }
        });
        adapter.setUserListener(this);
    }

    private void initView() {
        userCurrent = new User();
        Intent intent = getIntent();
        userCurrent = (User) intent.getSerializableExtra("userCurrent");
        if (userCurrent != null) {
        } else {
            Toast.makeText(this, "Lỗi không nhận được user current", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddMessageActivity.this, MainActivity.class));
            finish();
        }

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Looking for new chats!");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        userList = new ArrayList<>();
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new UserAdapter();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddMessageActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onClickItem(View view, int position) {
        Intent intent = new Intent(AddMessageActivity.this, ChatActivity.class);
        intent.putExtra("user2", userList.get(position));
        intent.putExtra("user1", userCurrent);
//        Toast.makeText(getApplicationContext(), userCurrent.getUid() + ":uid1", Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(), userList.get(position).getUid() + ":uid2", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
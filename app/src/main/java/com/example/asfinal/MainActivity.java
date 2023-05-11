package com.example.asfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asfinal.adapter.ConversationAdapter;
import com.example.asfinal.adapter.MessageAdapter;
import com.example.asfinal.model.Message;
import com.example.asfinal.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ConversationAdapter.ConversationListener {
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private RecyclerView recyclerView;
    private ActionBarDrawerToggle toggle;
    private FloatingActionButton fab;
    private User user;
    private List<User> userList;
    private ConversationAdapter adapter;
    private List<Message> messageList;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddMessageActivity.class));
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_item_chat:
//                        Intent intent_chat = new Intent(MainActivity.this, ChatActivity.class);
//                        startActivity(intent_chat);
                        break;
                    case R.id.nav_item_profile:
                        Intent intent_profile = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent_profile);
                        break;
                    case R.id.nav_item_setting:
                        Intent intent_setting = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent_setting);
                        break;
                    case R.id.nav_item_logout:
                        logout(); // gọi phương thức đăng xuất
                        break;
                }
                menuItem.setChecked(true);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        loadingBar.show();
        getConversationFromFirebase();
        adapter.setConversationListener(this);
    }

    public void init() {
        adapter = new ConversationAdapter();

        loadingBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Messages");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.recycler_view_main);
        fab = findViewById(R.id.fab);
        userList = new ArrayList<>();


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            getDataUserFromFirebase();
        }
    }

    private void getConversationFromFirebase() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        String uidCurrent = currentUser.getUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(uidCurrent);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userUid = userSnapshot.getKey();
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUid);
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                user.setUid(userUid);
                                userList.add(user);
                            }
                            Message lastMessage = new Message();
                            lastMessage.setContent("test");
                            lastMessage.setSenderId(uidCurrent);
                            lastMessage.setTimestamp(System.currentTimeMillis());
                            loadingBar.dismiss();
                            adapter.setList(userList, lastMessage, currentUser.getDisplayName());
                            LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                            recyclerView.setLayoutManager(manager);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDataUserFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        String uid = currentUser.getUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    TextView nameTextView = navigationView.getHeaderView(0).findViewById(R.id.text_name);
                    nameTextView.setText(user.getFull_name());
                    TextView emailTextView = navigationView.getHeaderView(0).findViewById(R.id.text_email);
                    emailTextView.setText(user.getEmail());
                    // Nếu có hình ảnh, có thể tải ảnh từ URL và hiển thị lên ImageView trong NavigationView Header
//                    if (user.getPhotoUrl() != null) {
//                        ImageView avatarImageView = navigationView.getHeaderView(0).findViewById(R.id.nav_header_avatar);
//                        Glide.with(context).load(user.getPhotoUrl()).into(avatarImageView);
//                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        Toast.makeText(MainActivity.this, "Logout successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onClickConversation(View view, int position) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra("user", userList.get(position));
//        Toast.makeText(this, "ChatActivity" + userList.get(position).getFull_name(), Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}
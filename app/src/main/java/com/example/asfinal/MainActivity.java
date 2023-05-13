package com.example.asfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;

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

        getConversationFromFirebase();
        adapter.setConversationListener(this);
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
                        intent_profile.putExtra("user", user);
                        startActivity(intent_profile);
                        break;
                    case R.id.nav_item_setting:
                        Intent intent_setting = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent_setting);
                        break;
                    case R.id.nav_item_logout:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Notification!"); // Tiêu đề của thông báo
                        builder.setMessage("Are you sure you want to sign out of your account?"); // Nội dung của thông báo
                        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                                dialog.dismiss(); // Đóng thông báo
                            }
                        });
                        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        break;
                }
                menuItem.setChecked(true);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

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
        loadingBar.show();
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
                            loadingBar.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                loadingBar.dismiss();
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
                    user.setUid(uid);
                    Toast.makeText(MainActivity.this, user.getUid() + "", Toast.LENGTH_SHORT).show();
                    TextView nameTextView = navigationView.getHeaderView(0).findViewById(R.id.text_name);
                    nameTextView.setText(user.getFull_name());
                    TextView emailTextView = navigationView.getHeaderView(0).findViewById(R.id.text_email);
                    emailTextView.setText(user.getEmail());
                    // Nếu có hình ảnh, có thể tải ảnh từ URL và hiển thị lên ImageView trong NavigationView Header
                    if (user.getAvatar() != null) {
                        CircleImageView profileImageView = findViewById(R.id.image_avatar);
                        Glide.with(MainActivity.this)
                                .load(user.getAvatar())
                                .placeholder(R.drawable.loading_image)
//                                .error(R.drawable.error) // Ảnh hiển thị khi có lỗi xảy ra trong quá trình tải ảnh
                                .into(profileImageView);
                    }
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
        intent.putExtra("user2", userList.get(position));
        intent.putExtra("user1", user);
//        Toast.makeText(this, "ChatActivity" + userList.get(position).getFull_name(), Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    @Override
    public void onLongClickConversation(View view, int position) {
//        adapter.deleteItem(position);

    }
}
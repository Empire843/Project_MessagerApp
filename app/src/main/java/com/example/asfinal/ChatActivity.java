package com.example.asfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.asfinal.adapter.MessageAdapter;
import com.example.asfinal.model.Message;
import com.example.asfinal.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText editChat;
    private ImageView sendButton;
    private User userSend;
    private User userReceive;

    private List<User> userList;
    private List<Message> messageList;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        Intent intent = getIntent();
        if (intent.hasExtra("user")) {
            userReceive = (User) intent.getSerializableExtra("user");
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editChat.getText().toString();
                if (messageText.isEmpty()) {
                    return;
                }
                sendMessage(uid, userReceive.getUid(), messageText);
                editChat.setText("");
            }
        });

    }

    public void init() {
//        toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Message");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sendButton = findViewById(R.id.button_send);
        editChat = findViewById(R.id.edit_chat);
        recyclerView = findViewById(R.id.recyclerView_chat);
//        content
        userReceive = new User();
        userSend = new User();
        userList = new ArrayList<>();
        messageList = new ArrayList<>();
    }

    private void sendMessage(String senderId, String receiverId, String messageText) {
        LocalDateTime now = LocalDateTime.now();
        String times = now.getDayOfMonth() + "/" + now.getMonthValue() + "/" + now.getYear() + " " + now.getHour() + ":" + now.getMinute();
        Message message = new Message(senderId, receiverId, messageText, times);
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("Messages");
        String messageId = messagesRef.child(senderId).child(receiverId).push().getKey();
        messagesRef.child(senderId).child(receiverId).child(messageId).setValue(message);
    }

    private void displayMessage() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        DatabaseReference currentUserRef = messagesRef.child(currentUser.getUid()).child(userReceive.getUid());
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Node tồn tại, không cần đảo ngược
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        Message message = messageSnapshot.getValue(Message.class);
                        if (message != null) {
                            messageList.add(message);
                        }
                    }
                    MessageAdapter adapter = new MessageAdapter(messageList, currentUser.getUid());
                    LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                    recyclerView.setLayoutManager(manager);
                    recyclerView.setAdapter(adapter);
                } else {
                    // Node không tồn tại, đảo ngược currentUser và userReceive
                    DatabaseReference userReceiveRef = messagesRef.child(userReceive.getUid()).child(currentUser.getUid());
                    userReceiveRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                Message message = messageSnapshot.getValue(Message.class);
                                if (message != null) {
                                    messageList.add(message);
                                }
                            }
                            MessageAdapter adapter = new MessageAdapter(messageList, currentUser.getUid());
                            LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                            recyclerView.setLayoutManager(manager);
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayMessage();
    }
}
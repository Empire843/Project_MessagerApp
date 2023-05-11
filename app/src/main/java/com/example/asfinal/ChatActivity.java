package com.example.asfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.example.asfinal.adapter.MessageAdapter;
import com.example.asfinal.model.Message;
import com.example.asfinal.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText editChat;
    private ImageView sendButton;
    private User userReceive = new User();

    private List<Message> messageList = new ArrayList<>();
    private List<String> textList = new ArrayList<>();
    private Toolbar toolbar;
    private String uidCurrent;
    private String uidReceive;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        receiveDataFromIntent();
        init();
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
                sendMessage(uidCurrent, uidReceive, messageText);
                editChat.setText("");
            }
        });
//        showMessage();
    }

    private void receiveDataFromIntent() {
        Intent intent = getIntent();
        userReceive = (User) intent.getSerializableExtra("user");
        if (userReceive != null) {
            // Sử dụng đối tượng User nhận được
            uidReceive = userReceive.getUid();
        } else {
            uidReceive = null;
        }
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(userReceive.getFull_name());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sendButton = findViewById(R.id.button_send);
        editChat = findViewById(R.id.edit_chat);
        recyclerView = findViewById(R.id.recyclerView_chat);
//        content

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        uidCurrent = mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
    }

    private void sendMessage(String senderId, String receiverId, String messageContent) {
        Message message = new Message(senderId, messageContent, System.currentTimeMillis());
        DatabaseReference messagesRef = database.getReference("Messages");
        String messageId = messagesRef.child(senderId).child(receiverId).push().getKey();
        messagesRef.child(senderId).child(receiverId).child(messageId).setValue(message);
        messagesRef.child(receiverId).child(senderId).child(messageId).setValue(message);
    }

    private void sortListByTimestamp(List<Message> list) {
        Collections.sort(list, new Comparator<Message>() {
            @Override
            public int compare(Message message1, Message message2) {
                long timestamp1 = message1.getTimestamp();
                long timestamp2 = message2.getTimestamp();

                if (timestamp1 > timestamp2) {
                    return 1;
                } else if (timestamp1 < timestamp2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDataMessageOnFirebase(uidCurrent, uidReceive);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getDataMessageOnFirebase(String uidUser1, String uidUser2) {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("Messages");
        messagesRef.child(uidUser1).child(uidUser2).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    messageList.add(message);
                    textList.add(message.getContent());
                }
                MessageAdapter adapter = new MessageAdapter(messageList, uidCurrent);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                recyclerView.scrollToPosition(messageList.size() - 1);
                RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
                if (animator instanceof SimpleItemAnimator) {
                    ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}
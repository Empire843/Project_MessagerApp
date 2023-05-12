package com.example.asfinal;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.example.asfinal.adapter.MessageAdapter;
import com.example.asfinal.model.Message;
import com.example.asfinal.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private final int REQUEST_IMAGE_PICK = 1;
    private CircleImageView avatar;
    private RecyclerView recyclerView;
    private EditText editChat;
    private ImageView sendButton;
    private ImageView btnSelectImages;
    private User userReceive = new User();
    private User userCurrent = new User();

    private List<Message> messageList = new ArrayList<>();
    private List<String> textList = new ArrayList<>();
    private Toolbar toolbar;
    private String uidCurrent;
    private String uidReceive;
    private FirebaseDatabase database;
    private ProgressDialog loadingBar;

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
                sendMessage(uidCurrent, uidReceive, null, messageText);
                editChat.setText("");
            }
        });
        btnSelectImages.setOnClickListener(view -> {
            chooseImage();
        });
//        showMessage();
    }

    private void receiveDataFromIntent() {
        Intent intent = getIntent();
        userReceive = (User) intent.getSerializableExtra("user2");
        userCurrent = (User) intent.getSerializableExtra("user1");
        if (userReceive != null) {
            // Sử dụng đối tượng User nhận được
            uidReceive = userReceive.getUid();
        } else {
            uidReceive = null;
        }
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar_options);
        toolbar.setTitle(userReceive.getFull_name());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sendButton = findViewById(R.id.button_send);
        editChat = findViewById(R.id.edit_chat);
        recyclerView = findViewById(R.id.recyclerView_chat);
        btnSelectImages = findViewById(R.id.choose_image);
//        content

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        uidCurrent = mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();

    }

    private void sendMessage(String senderId, String receiverId, String images, String messageContent) {
        Message message = new Message(senderId, messageContent, images, System.currentTimeMillis());
        DatabaseReference messagesRef = database.getReference("Messages");
        String messageId = messagesRef.child(senderId).child(receiverId).push().getKey();
        messagesRef.child(senderId).child(receiverId).child(messageId).setValue(message);
        messagesRef.child(receiverId).child(senderId).child(messageId).setValue(message);
    }

    //    private void sortListByTimestamp(List<Message> list) {
//        Collections.sort(list, new Comparator<Message>() {
//            @Override
//            public int compare(Message message1, Message message2) {
//                long timestamp1 = message1.getTimestamp();
//                long timestamp2 = message2.getTimestamp();
//
//                if (timestamp1 > timestamp2) {
//                    return 1;
//                } else if (timestamp1 < timestamp2) {
//                    return -1;
//                } else {
//                    return 0;
//                }
//            }
//        });
//    }
    public void showOptionsMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_options, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item1:
                        // Xử lý cho menu item 1
                        Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                        intent.putExtra("user", userReceive);
                        startActivity(intent);
                        return true;
//                    case R.id.menu_item2:
//                        // Xử lý cho menu item 2
//                        return true;
                    // Thêm các trường hợp xử lý khác (nếu cần)
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
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
                MessageAdapter adapter = new MessageAdapter(messageList, uidCurrent, userCurrent.getAvatar(), userReceive.getAvatar());
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

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void uploadImageToStorage(Uri imageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setView(R.layout.loading_layout)
                .setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
//        String pathString = "images/" + userCurrent.getUid() + "/" + imageUri.getLastPathSegment();
        StorageReference storageRef = storage.getReference().child("images").child(userCurrent.getUid()).child(imageUri.getLastPathSegment());

        UploadTask uploadTask = storageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        String imageUrl = downloadUrl.toString();
                        Toast.makeText(ChatActivity.this, imageUrl + "", Toast.LENGTH_SHORT).show();
                        sendMessage(userCurrent.getUid(), userReceive.getUid(), imageUrl, "");
                        saveMessageToDatabase(imageUrl);
                        alertDialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Xử lý khi tải lên thất bại
                alertDialog.dismiss();
            }
        });
    }

    private void saveMessageToDatabase(String imageUrl) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(userCurrent.getUid());
        Map<String, Object> updateInfo = new HashMap<>();
        if (imageUrl != null) {
            updateInfo.put("avatar", imageUrl);
        }
        userRef.updateChildren(updateInfo, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
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
//            avatar.setImageURI(imageUri);

            uploadImageToStorage(imageUri);
        }
    }
}
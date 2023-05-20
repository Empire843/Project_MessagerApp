package com.example.asfinal;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.content.ClipData;
import android.content.ClipboardManager;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.example.asfinal.adapter.MessageAdapter;
import com.example.asfinal.model.Message;
import com.example.asfinal.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements MessageAdapter.MessageListener {
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
    private MessageAdapter adapter;

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
        adapter.setMessageListener(this);
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

        adapter = new MessageAdapter();

    }

    private void sendMessage(String senderId, String receiverId, String images, String messageContent) {
        Message message = new Message(senderId, messageContent, images, System.currentTimeMillis());
        DatabaseReference messagesRef = database.getReference("Messages");
        String messageId = messagesRef.child(senderId).child(receiverId).push().getKey();
        message.setId(messageId);
        messagesRef.child(senderId).child(receiverId).child(messageId).setValue(message);
        messagesRef.child(receiverId).child(senderId).child(messageId).setValue(message);
    }

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
//                MessageAdapter adapter = new MessageAdapter(messageList, uidCurrent, userCurrent.getAvatar(), userReceive.getAvatar());
                adapter.setMessageList(getApplicationContext(), messageList, uidCurrent, userCurrent.getAvatar(), userReceive.getAvatar());
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
        builder.setView(R.layout.loading_layout).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("chat").child(userCurrent.getUid()).child(imageUri.getLastPathSegment());

        UploadTask uploadTask = storageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        String imageUrl = downloadUrl.toString();
//                        Toast.makeText(ChatActivity.this, imageUrl + "", Toast.LENGTH_SHORT).show();
                        sendMessage(userCurrent.getUid(), userReceive.getUid(), imageUrl, "");
                        alertDialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                alertDialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            uploadImageToStorage(imageUri);
        }
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

    @Override
    public void onClickMessage(View view, int position) {
        if (messageList.get(position).getImages() != null) {
            Context context = view.getContext();
            showImageDialog(context, messageList.get(position).getImages());
        }
    }

    private void showAlertDialogMenu(int position) {
        Message message = messageList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] menuItems = {"Delete", "Unsend", "Copy"};
        builder.setItems(menuItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedItem = menuItems[which];
                if (selectedItem.equals("Delete")) {
                    deleteItemMessage(message);
                } else if (selectedItem.equals("Copy")) {
                    copyTextToClipboard(message.getContent());
                } else if (selectedItem.equals("Unsend")) {
                    unSendItemMessage(message);
                }
            }
        });
        builder.show();
    }

    private void deleteItemMessage(Message message) {
        DatabaseReference messagesRef = database.getReference("Messages");
        messagesRef.child(userCurrent.getUid()).child(userReceive.getUid()).child(message.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Message has been deleted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed message deletion." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void unSendItemMessage(Message message) {
        if (userCurrent.getUid().equals(message.getSenderId())) {
            DatabaseReference messagesRef = database.getReference("Messages");
            messagesRef.child(userCurrent.getUid()).child(userReceive.getUid()).child(message.getId()).removeValue();
            messagesRef.child(userReceive.getUid()).child(userCurrent.getUid()).child(message.getId()).removeValue();
            Toast.makeText(this, "Unsend successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "You can't unsend this message", Toast.LENGTH_SHORT).show();
        }
    }
    private void copyTextToClipboard(String text) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Copied Text", text);
        clipboardManager.setPrimaryClip(clipData);
    }
    @Override
    public void onLongClickMessage(View view, int position) {
        showAlertDialogMenu(position);
    }
}
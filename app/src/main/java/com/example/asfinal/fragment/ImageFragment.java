package com.example.asfinal.fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.asfinal.R;
import com.example.asfinal.adapter.ImageAdapter;
import com.example.asfinal.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ImageFragment extends Fragment {
    private User user = new User();
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<String> images = new ArrayList<>();
    private List<String> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
        } else {
            Toast.makeText(getContext(), "fragment", Toast.LENGTH_SHORT).show();
        }
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        getDataFromFirebaseStorage();
    }

    private void getDataFromFirebaseStorage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imagesRef = storage.getReference().child("images/" + user.getUid());
        imagesRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        int itemCount = listResult.getItems().size();
                        for (StorageReference item : listResult.getItems()) {
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    list.add(imageUrl);
//                                    Toast.makeText(getContext(), "list: " + list, Toast.LENGTH_SHORT).show();
                                    if (list.size() == itemCount) {
                                        imageAdapter.updateData(list);
                                        recyclerView.setAdapter(imageAdapter);
                                    }
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xảy ra lỗi khi lấy danh sách tệp tin
                        Toast.makeText(getContext(), "Lỗi khi lấy tập tin", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView_image);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(manager);
        imageAdapter = new ImageAdapter(new ArrayList<>());
        recyclerView.setAdapter(imageAdapter);
    }
}
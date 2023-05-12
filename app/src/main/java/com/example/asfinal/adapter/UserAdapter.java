package com.example.asfinal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.asfinal.R;
import com.example.asfinal.model.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<User> resultList;
    private UserListener userListener;

    public UserAdapter() {
        this.resultList = new ArrayList<>();
    }

    public void setUserListener(UserListener userListener) {
        this.userListener = userListener;
        notifyDataSetChanged();
    }

    public void setList(List<User> resultList) {
        this.resultList = resultList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_users_search, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User result = resultList.get(position);
        holder.resultTextView.setText(result.getFull_name());
        holder.resultTextViewEmail.setText(result.getEmail());
        if(result.getAvatar() != null){
            Glide.with(holder.itemView.getContext())
                    .load(result.getAvatar())
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView resultTextView;
        TextView resultTextViewEmail;
        CircleImageView imageView;

        //        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            resultTextView = itemView.findViewById(R.id.sender_name);
            resultTextViewEmail = itemView.findViewById(R.id.sender_email);
            imageView = itemView.findViewById(R.id.profile_image);
//            imageView = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
//            Toast.makeText(view.getContext(), "test adapter", Toast.LENGTH_SHORT).show();
            if (userListener != null) {
                userListener.onClickItem(view, getAdapterPosition());
            }
        }
    }

    public interface UserListener {
        void onClickItem(View view, int position);
    }
}

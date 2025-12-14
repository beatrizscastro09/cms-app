package com.netflix_plus_plus.cms.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.netflix_plus_plus.cms.R;
import com.netflix_plus_plus.cms.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onDeleteUser(User user, int position);
    }

    public UserAdapter(List<User> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        System.out.println("USER" + user);

        holder.tvUsername.setText(user.getUsername());
        holder.tvEmail.setText(user.getEmail());

        // Role badge - only show if role exists
        if (user.getRole() != null && !user.getRole().isEmpty()) {
            holder.tvRole.setText(user.getRole());
            holder.tvRole.setVisibility(View.VISIBLE);
        } else {
            holder.tvRole.setVisibility(View.GONE);
        }

        // Status indicator
        if (user.isActive() != null && user.isActive()) {
            holder.tvStatus.setText("● Active");
            holder.tvStatus.setTextColor(0xFF00FF00);  // Green
        } else {
            holder.tvStatus.setText("● Inactive");
            holder.tvStatus.setTextColor(0xFFFF0000);  // Red
        }

        // Delete button click
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteUser(user, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // Remove user from list (after successful delete)
    public void removeUser(int position) {
        userList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, userList.size());
    }

    // Update entire list (after refresh)
    public void updateUsers(List<User> newUsers) {
        this.userList = newUsers;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvEmail, tvRole, tvStatus;
        Button btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnDelete = itemView.findViewById(R.id.btnDeleteUser);
        }
    }
}
package com.example.rgbpicker.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rgbpicker.Model.UserSelection;
import com.example.rgbpicker.R;

import java.util.List;

public class UserSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private List<UserSelection> userSelectionList;
    private OnItemClickListener listener;
    private String selectedUserElement;

    public interface OnItemClickListener {
        void onItemClick(UserSelection userSelection);
    }

    public UserSelectionAdapter(List<UserSelection> userSelectionList, OnItemClickListener listener) {
        this.userSelectionList = userSelectionList;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_selection, parent, false);
            return new UserSelectionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.headerTextView.setText(selectedUserElement == null ? "Please select a user item" : selectedUserElement);
        } else {
            UserSelectionViewHolder userSelectionViewHolder = (UserSelectionViewHolder) holder;
            UserSelection userSelection = userSelectionList.get(position - 1); // Adjust for header
            userSelectionViewHolder.selectionTextView.setText(userSelection.getSelection());
            userSelectionViewHolder.itemView.setVisibility(selectedUserElement == null ? View.VISIBLE : View.GONE);
            userSelectionViewHolder.itemView.setOnClickListener(v -> {
                selectedUserElement = userSelection.getSelection();
                notifyDataSetChanged();
                listener.onItemClick(userSelection);
            });
        }
    }

    @Override
    public int getItemCount() {
        return selectedUserElement == null ? userSelectionList.size() + 1 : 2; // Add one for the header, and one for the selected item
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTextView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTextView = itemView.findViewById(R.id.header_text_view);
        }
    }

    static class UserSelectionViewHolder extends RecyclerView.ViewHolder {
        TextView selectionTextView;

        public UserSelectionViewHolder(@NonNull View itemView) {
            super(itemView);
            selectionTextView = itemView.findViewById(R.id.user_selection_value);
        }
    }
}
package com.example.rgbpicker.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rgbpicker.Model.Smartphone;
import com.example.rgbpicker.R;

import java.util.List;

public class SmartphoneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private List<Smartphone> smartphoneList;
    private OnItemClickListener listener;
    private String selectedSmartphoneName;

    public interface OnItemClickListener {
        void onItemClick(Smartphone smartphone);
    }

    public SmartphoneAdapter(List<Smartphone> smartphoneList, OnItemClickListener listener) {
        this.smartphoneList = smartphoneList;
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_smartphone, parent, false);
            return new SmartphoneViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.headerTextView.setText(selectedSmartphoneName == null ? "Please select a smartphone" : selectedSmartphoneName);
        } else {
            SmartphoneViewHolder smartphoneViewHolder = (SmartphoneViewHolder) holder;
            Smartphone smartphone = smartphoneList.get(position - 1); // Adjust for header
            smartphoneViewHolder.nameTextView.setText(smartphone.getName());
            smartphoneViewHolder.itemView.setVisibility(selectedSmartphoneName == null ? View.VISIBLE : View.GONE);
            smartphoneViewHolder.itemView.setOnClickListener(v -> {
                selectedSmartphoneName = smartphone.getName();
                notifyDataSetChanged();
                listener.onItemClick(smartphone);
            });
        }
    }

    @Override
    public int getItemCount() {
        return selectedSmartphoneName == null ? smartphoneList.size() + 1 : 2; // Add one for the header, and one for the selected item
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTextView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTextView = itemView.findViewById(R.id.header_text_view);
        }
    }

    static class SmartphoneViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public SmartphoneViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.smartphone_name);
        }
    }
}
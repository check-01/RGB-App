package com.example.rgbpicker.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rgbpicker.R;

import java.util.List;

public class CameraAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private List<String> cameraList;
    private OnItemClickListener listener;
    private String selectedCameraValue;

    public interface OnItemClickListener {
        void onItemClick(String cameraValue);
    }

    public CameraAdapter(List<String> cameraList, OnItemClickListener listener) {
        this.cameraList = cameraList;
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_camera, parent, false);
            return new CameraViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.headerTextView.setText(selectedCameraValue == null ? "Please select a camera" : selectedCameraValue);
        } else {
            CameraViewHolder cameraViewHolder = (CameraViewHolder) holder;
            String cameraValue = cameraList.get(position - 1); // Adjust for header
            cameraViewHolder.cameraTextView.setText(cameraValue);
            cameraViewHolder.itemView.setVisibility(selectedCameraValue == null ? View.VISIBLE : View.GONE);
            cameraViewHolder.itemView.setOnClickListener(v -> {
                selectedCameraValue = cameraValue;
                notifyDataSetChanged();
                listener.onItemClick(cameraValue);
            });
        }
    }

    @Override
    public int getItemCount() {
        return selectedCameraValue == null ? cameraList.size() + 1 : 2; // Add one for the header, and one for the selected item
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTextView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTextView = itemView.findViewById(R.id.header_text_view);
        }
    }

    static class CameraViewHolder extends RecyclerView.ViewHolder {
        TextView cameraTextView;

        public CameraViewHolder(@NonNull View itemView) {
            super(itemView);
            cameraTextView = itemView.findViewById(R.id.camera_value);
        }
    }
}
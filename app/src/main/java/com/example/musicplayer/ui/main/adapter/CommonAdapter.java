package com.example.musicplayer.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.data.model.CommonItem;

import java.util.List;

public class CommonAdapter extends RecyclerView.Adapter<CommonAdapter.CommonViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(CommonItem item);
    }

    private List<CommonItem> items;
    private final OnItemClickListener listener;

    public CommonAdapter(List<CommonItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_common, parent, false);
        return new CommonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonViewHolder holder, int position) {
        CommonItem item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.subtitle.setText(item.getSubtitle());

        if (item.getImageResId() != 0) {
            holder.icon.setImageResource(item.getImageResId());
        }

        // ✅ Bind your click listener here
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class CommonViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        ImageView icon;

        public CommonViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitle);
            subtitle = itemView.findViewById(R.id.itemSubtitle);
            icon = itemView.findViewById(R.id.itemIcon);
        }
    }

    public void updateList(List<CommonItem> newList) {
        this.items = newList;
        notifyDataSetChanged();
    }
}


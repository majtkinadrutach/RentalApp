package com.example.rentalapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentalapp.model.Asset;

import java.util.List;

public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.AssetViewHolder> {

    /**
     * Fragment implementuje ten interfejs i reaguje na akcje przycisków.
     * Adapter nie wie nic o ViewModelu ani nawigacji — tylko deleguje.
     */
    public interface OnActionListener {
        void onRent(Asset asset);
        void onReturn(Asset asset);
        void onRepair(Asset asset);
    }

    private final List<Asset>      assetList;
    private final OnActionListener listener;

    public AssetAdapter(List<Asset> assetList, OnActionListener listener) {
        this.assetList = assetList;
        this.listener  = listener;
    }

    @NonNull
    @Override
    public AssetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_asset_row, parent, false);
        return new AssetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssetViewHolder holder, int position) {
        Asset asset = assetList.get(position);

        holder.textItemId.setText(asset.getId());
        holder.textItemName.setText(asset.getName());
        holder.textItemCategory.setText("Kategoria: " + asset.getCategory());
        holder.textItemStatus.setText(asset.getStatus());

        switch (asset.getStatus()) {
            case "Dostępny":
                holder.textItemStatus.setTextColor(Color.parseColor("#008000"));
                holder.buttonAction.setText("Wypożycz");
                holder.buttonAction.setEnabled(true);
                holder.buttonAction.setOnClickListener(v -> {
                    if (listener != null) listener.onRent(asset);
                });
                break;

            case "Uszkodzony":
                holder.textItemStatus.setTextColor(Color.parseColor("#D32F2F"));
                holder.buttonAction.setText("Napraw");
                holder.buttonAction.setEnabled(true);
                holder.buttonAction.setOnClickListener(v -> {
                    if (listener != null) listener.onRepair(asset);
                });
                break;

            default: // "Wypożyczony"
                holder.textItemStatus.setTextColor(Color.parseColor("#FFA500"));
                holder.buttonAction.setText("Zwróć");
                holder.buttonAction.setEnabled(true);
                holder.buttonAction.setOnClickListener(v -> {
                    if (listener != null) listener.onReturn(asset);
                });
                break;
        }
    }

    @Override
    public int getItemCount() { return assetList.size(); }

    static class AssetViewHolder extends RecyclerView.ViewHolder {
        TextView textItemId, textItemName, textItemCategory, textItemStatus;
        Button   buttonAction;

        AssetViewHolder(@NonNull View itemView) {
            super(itemView);
            textItemId       = itemView.findViewById(R.id.textItemId);
            textItemName     = itemView.findViewById(R.id.textItemName);
            textItemCategory = itemView.findViewById(R.id.textItemCategory);
            textItemStatus   = itemView.findViewById(R.id.textItemStatus);
            buttonAction     = itemView.findViewById(R.id.buttonItemAction);
        }
    }
}

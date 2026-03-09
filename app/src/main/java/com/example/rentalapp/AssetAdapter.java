package com.example.rentalapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.AssetViewHolder> {

    private List<Asset> assetList;

    public AssetAdapter(List<Asset> assetList) {
        this.assetList = assetList;
    }

    @NonNull
    @Override
    public AssetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_asset_row, parent, false);
        return new AssetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssetViewHolder holder, int position) {
        Asset asset = assetList.get(position);

        holder.textItemId.setText(asset.getId());
        holder.textItemName.setText(asset.getName());
        holder.textItemCategory.setText("Kategoria: " + asset.getCategory());
        holder.textItemStatus.setText(asset.getStatus());

        if (asset.getStatus().equals("Dostępny")) {
            holder.buttonAction.setText("Wypożycz");
            holder.textItemStatus.setTextColor(Color.parseColor("#008000"));
        } else if (asset.getStatus().equals("Uszkodzony")) {
            holder.buttonAction.setText("Napraw");
            holder.textItemStatus.setTextColor(Color.parseColor("#FF0000"));
        } else {
            holder.buttonAction.setText("Zwróć");
            holder.textItemStatus.setTextColor(Color.parseColor("#FFA500"));
        }

        holder.buttonAction.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Kliknięto: " + holder.buttonAction.getText() + " dla " + asset.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return assetList.size();
    }

    static class AssetViewHolder extends RecyclerView.ViewHolder {
        TextView textItemId, textItemName, textItemCategory, textItemStatus;
        Button buttonAction;

        public AssetViewHolder(@NonNull View itemView) {
            super(itemView);
            textItemId = itemView.findViewById(R.id.textItemId);
            textItemName = itemView.findViewById(R.id.textItemName);
            textItemCategory = itemView.findViewById(R.id.textItemCategory);
            textItemStatus = itemView.findViewById(R.id.textItemStatus);
            buttonAction = itemView.findViewById(R.id.buttonItemAction);
        }
    }
}
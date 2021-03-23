package edu.neu.madcourse.sticker_app;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RviewHolder extends RecyclerView.ViewHolder {

    public TextView stickerIcon;
    public TextView sender;

    public RviewHolder(@NonNull View itemView) {
        super(itemView);
        sender = itemView.findViewById(R.id.sender);
        stickerIcon = itemView.findViewById(R.id.sticker_icon);
    }
}

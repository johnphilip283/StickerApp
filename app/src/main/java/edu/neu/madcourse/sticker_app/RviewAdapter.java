package edu.neu.madcourse.sticker_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RviewAdapter extends RecyclerView.Adapter<RviewHolder> {

    private final List<StickerCard> stickers;

    public RviewAdapter(List<StickerCard> stickers) { this.stickers = stickers; }

    @NonNull
    @Override
    public RviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_card, parent, false);
        return new RviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RviewHolder holder, int position) {
        StickerCard card = stickers.get(position);
        holder.sender.setText(String.format("Sent by: %s", card.getSender()));
        holder.stickerIcon.setText(this.getEmojiByUnicode(0x1F975));
    }

    @Override
    public int getItemCount() {
        return stickers.size();
    }

    private String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}

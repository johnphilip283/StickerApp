package edu.neu.madcourse.sticker_app;

public class StickerCard {

    private String img;
    private String sender;

    public StickerCard() {
    }

    public StickerCard(String img, String sender) {
        this.img = img;
        this.sender = sender;
    }

    public String getImage() {
        return img;
    }

    public void setImage(String img) {
        this.img = img;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return this.img + " " + this.sender;
    }
}

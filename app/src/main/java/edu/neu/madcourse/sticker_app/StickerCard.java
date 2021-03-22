package edu.neu.madcourse.sticker_app;

public class StickerCard {

    private String image;
    private String sender;

    public StickerCard() {
    }

    public StickerCard(String image, String sender) {
        this.image = image;
        this.sender = sender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return this.image + " " + this.sender;
    }
}

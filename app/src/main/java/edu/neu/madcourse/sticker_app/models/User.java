package edu.neu.madcourse.sticker_app.models;

import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    public String username;
    public String clientToken;
    public Integer numStickersSent;
    public List<Map<String, String>> receivedHistory;

    public User() {

    }

    public User(String username, String clientToken, Integer numStickersSent, List<Map<String, String>> receivedHistory) {
        this.username = username;
        this.clientToken = clientToken;
        this.numStickersSent = numStickersSent;
        this.receivedHistory = receivedHistory;
    }

    public User(String username, String clientToken) {
        this.username = username;
        this.clientToken = clientToken;
        this.numStickersSent = 0;
        this.receivedHistory = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(username + "\n");
        sb.append(clientToken + "\n");
        sb.append(numStickersSent + "\n");
//        Log.v("User", history);
        if (receivedHistory != null) {
            for (Map<String, String> entry : receivedHistory) {
                for (Map.Entry<String,String> pair : entry.entrySet()) {
                    sb.append(pair.getKey() + " " + pair.getValue());
                }
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    // Typical user object could be:
    // - john
    //    - numStickersSent: 2
    //    - clientToken: asdjahlskjgdh
    //    - history:
    //          - {img: '.../img.png', sender: 'colin'}

}

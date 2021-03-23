package edu.neu.madcourse.sticker_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.sticker_app.models.User;

public class MessagingActivity extends AppCompatActivity {

    private final String SERVER_KEY = "key=AAAAYwQf6u0:APA91bFZ1hImC_ptJGPIScoJIUAsvUP2ORuOJkkFfCkWkWT3dGS6SnMtZ2u0U3hhpDkVwzOJLvdfXAH2KN6fqzSW4Zi4JgkK8n4wOIwMhpevlTCvVyGmzIxc0RRNab-89lK6m7MRbQgR";
    private static final String KEY_OF_INSTANCE = "KEY_OF_INSTANCE";
    private static final String NUMBER_OF_ITEMS = "NUMBER_OF_ITEMS";
    private static final String USERNAME_STRING = "USERNAME_STRING";
    String CLIENT_KEY = "";
    String TAG = "MessagingActivity";

    private DatabaseReference database;
    private TextView numStickersReceived;
    private List<StickerCard> stickers;
    private String username;
    private EditText usernameEditText;
    private Integer numStickersSent;

    private RecyclerView recyclerView;
    private RviewAdapter rviewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();

        setContentView(R.layout.activity_messaging);

        stickers = new ArrayList<>();

        init(savedInstanceState);

        numStickersReceived = findViewById(R.id.num_stickers_received);

        SharedPreferences userDetails = this.getSharedPreferences(getString(R.string.user_details), MODE_PRIVATE);
        username = userDetails.getString(getString(R.string.username), "");
        usernameEditText = findViewById(R.id.editTextTextPersonName);

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(MessagingActivity.this, token -> {
            this.CLIENT_KEY = token;
        });

        // Write a message to the database
        database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("users").child(username);

        // Set up list view for updates as you get them
        myRef.child("receivedHistory").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                // TODO: add notification
                GenericTypeIndicator<Map<String, String>> t = new GenericTypeIndicator<Map<String, String>>() {};
                Map<String, String> history = dataSnapshot.getValue(t);

                String img = history.get("img");
                String sender = history.get("sender");

                StickerCard content = new StickerCard(img, sender);

                MessagingActivity.this.displayNotification(content);
                MessagingActivity.this.addSticker(content);

                String message = String.format("Received %s from %s.", img, sender);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled:" + databaseError);
            }
        });

        // TODO: Not sure if all of this goes into onCreate or should be in a separate method

        // Sync with the database for all this user's data.
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.getValue(User.class);
                stickers.clear();

                if (user != null) {
                    user.username = username;
                    MessagingActivity.this.numStickersSent = user.numStickersSent;
                    numStickersReceived.setText(getString(R.string.num_stickers_received, user.numStickersSent.toString()));
                    if (user.receivedHistory != null) {

                        for (Map.Entry<String, Map<String, String>> entry : user.receivedHistory.entrySet()) {
                            Map<String, String> pair = entry.getValue();
                            MessagingActivity.this.addSticker(new StickerCard(pair.get("img"), pair.get("sender")));
                        }

                        rviewAdapter.notifyDataSetChanged();
                    }

                } else {
                    user = new User(username, CLIENT_KEY);
                    database.child("users").child(username).setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }


    private void addSticker(StickerCard sticker) {
        stickers.add(0, sticker);
        rviewAdapter.notifyItemInserted(0);
    }

    private void createRecyclerView() {
        layoutManager = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.sticker_list);
        recyclerView.setHasFixedSize(true);
        rviewAdapter = new RviewAdapter(stickers);
        recyclerView.setAdapter(rviewAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // use on new message receive to display a notification
    public void displayNotification(StickerCard content) {
        Intent intent = new Intent(this, MessagingActivity.class);
        PendingIntent newIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        String channelId = getString(R.string.channel_id);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Stick it")
                .setContentText("A new sticker received from " + content.getSender()).setSmallIcon(R.drawable.earth).setContentIntent(newIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags |= Notification.FLAG_AUTO_CANCEL ;
        notificationManager.notify(0, notification);
    }

    public void sendStickerToUser(View view) {
        // get username
        String clientUsername = usernameEditText.getText().toString();

        DatabaseReference clientRef = database.child("users").child(clientUsername);
        DatabaseReference userRef = database.child("users").child(username);

        userRef.child("numStickersSent").setValue(numStickersSent + 1);

        StickerCard sticker = new StickerCard(this.getEmojiByUnicode(0x1F975), username);

        DatabaseReference newHistoryElement = clientRef.child("receivedHistory").push();
        newHistoryElement.setValue(sticker);
    }

    private String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    // Send sticker notification to target device
    private void sendMessageToDevice(String targetToken) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "New sticker received!");
            jNotification.put("body", "Sticker");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");

            jPayload.put("to", targetToken);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jdata);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }


    // Handling Orientation Changes on Android
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        int size = stickers == null ? 0 : stickers.size();
        outState.putInt(NUMBER_OF_ITEMS, size);

        // Need to generate unique key for each item
        // This is only a possible way to do, please find your own way to generate the key
        for (int i = 0; i < size; i++) {
            // put image information into instance
            outState.putString(KEY_OF_INSTANCE + i + "0", stickers.get(i).getImage());
            // put sender information into instance
            outState.putString(KEY_OF_INSTANCE + i + "1", stickers.get(i).getSender());
        }
        outState.putString(USERNAME_STRING, username);
        super.onSaveInstanceState(outState);
    }

    private void init(Bundle savedInstanceState) {
        initialItemData(savedInstanceState);
        createRecyclerView();
    }

    private void initialItemData(Bundle savedInstanceState) {
        // Not the first time to open this Activity
        if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_OF_ITEMS)
                && savedInstanceState.containsKey(USERNAME_STRING)) {
            if (stickers == null || stickers.size() == 0) {

                int size = savedInstanceState.getInt(NUMBER_OF_ITEMS);

                // Retrieve keys we stored in the instance
                for (int i = 0; i < size; i++) {
                    String image = savedInstanceState.getString(KEY_OF_INSTANCE + i + "0");
                    String sender = savedInstanceState.getString(KEY_OF_INSTANCE + i + "1");

                    StickerCard itemCard = new StickerCard(image, sender);

                    stickers.add(itemCard);
                }
            }
            String savedUsername = savedInstanceState.getString(USERNAME_STRING);
            username = savedUsername;
            usernameEditText = findViewById(R.id.editTextTextPersonName);
            usernameEditText.setText(username);
        }
    }

}

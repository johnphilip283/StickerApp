package edu.neu.madcourse.sticker_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.sticker_app.models.User;

import static android.provider.Settings.System.getString;

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

    private RecyclerView recyclerView;
    private RviewAdapter rviewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();

        setContentView(R.layout.activity_messaging);

        stickers = new ArrayList<>();
        stickers.add(new StickerCard("hi", "hello"));
        init(savedInstanceState);

        numStickersReceived = findViewById(R.id.num_stickers_received);

        SharedPreferences userDetails = this.getSharedPreferences(getString(R.string.user_details), MODE_PRIVATE);
        username = userDetails.getString(getString(R.string.username), "");

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

//                List<Map<String, String>> history = dataSnapshot.getValue();
//                for (Map<String, String> entry : history) {
//                    for (Map.Entry<String,String> pair : entry.entrySet()) {
//                        pair.getKey(), pair.getValue()
//                    }
//                }
//                Log.e(TAG, "onChildAdded: dataSnapshot = " + dataSnapshot.getValue());
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

        // Read from the database by listening for a change to that item.
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    user.username = username;
                    numStickersReceived.setText(getString(R.string.num_stickers_received, user.numStickersSent.toString()));
                    if (user.receivedHistory != null) {
                        for (Map<String, String> sticker: user.receivedHistory) {
                            Log.v(TAG, sticker.get("img") + " " + sticker.get("sender"));
                            MessagingActivity.this.addSticker(new StickerCard(sticker.get("img"), sticker.get("sender")));
                        }
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

    // use on new message receive to send a notification
    public void sendNotification(View view, StickerCard content){
        Intent intent = new Intent(this, MessagingActivity.class);
        PendingIntent newIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        String channelId = getString(R.string.channel_id);
        Notification notification = new NotificationCompat.Builder(this,channelId)
                .setContentTitle("Stick it")
                .setContentText("A new sticker received from " + content.getSender()).setSmallIcon(R.drawable.earth).setContentIntent(newIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags |= Notification.FLAG_AUTO_CANCEL ;
        notificationManager.notify(0, notification);
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
            EditText usernameEditText = findViewById(R.id.editTextTextPersonName);
            usernameEditText.setText(username);
        }
        // The first time to open this Activity
        else {
            // empty list for now
        }
    }


}

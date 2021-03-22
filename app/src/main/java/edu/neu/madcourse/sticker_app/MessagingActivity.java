package edu.neu.madcourse.sticker_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.sticker_app.models.User;

public class MessagingActivity extends AppCompatActivity {

    private final String SERVER_KEY = "key=AAAAYwQf6u0:APA91bFZ1hImC_ptJGPIScoJIUAsvUP2ORuOJkkFfCkWkWT3dGS6SnMtZ2u0U3hhpDkVwzOJLvdfXAH2KN6fqzSW4Zi4JgkK8n4wOIwMhpevlTCvVyGmzIxc0RRNab-89lK6m7MRbQgR";
    String CLIENT_KEY = "";
    String TAG = "MessagingActivity";

    private DatabaseReference database;
    private TextView numStickersReceived;
    private List<StickerCard> stickers;

    private RecyclerView recyclerView;
    private RviewAdapter rviewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        stickers = new ArrayList<>();
        stickers.add(new StickerCard("hi", "hello"));
        createRecyclerView();

        numStickersReceived = findViewById(R.id.num_stickers_received);

        SharedPreferences userDetails = this.getSharedPreferences(getString(R.string.user_details), MODE_PRIVATE);
        String username = userDetails.getString(getString(R.string.username), "");

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




}

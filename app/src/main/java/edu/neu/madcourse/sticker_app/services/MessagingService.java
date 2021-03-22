package edu.neu.madcourse.sticker_app.services;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;

import edu.neu.madcourse.sticker_app.R;
import edu.neu.madcourse.sticker_app.models.User;

public class MessagingService extends FirebaseMessagingService {

    private DatabaseReference database;

    @Override
    public void onNewToken(String newToken) {
        super.onNewToken(newToken);

        // Update the user's associated client token when refreshed.
        SharedPreferences userDetails = this.getSharedPreferences(getString(R.string.user_details), MODE_PRIVATE);
        String username = userDetails.getString(getString(R.string.username), "");

        database = FirebaseDatabase.getInstance().getReference();

        // Since someone else could be reading someone else's client token at the same time it's
        // being updated, run this code in a transaction. Boilerplate code taken from example demo,
        // with obvious changes in functionality.
        database.child("users").child(username).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User u = mutableData.getValue(User.class);
                if (u == null) {
                    return Transaction.success(mutableData);
                }
                u.clientToken = newToken;

                mutableData.setValue(u);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d("MessagingService", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

}

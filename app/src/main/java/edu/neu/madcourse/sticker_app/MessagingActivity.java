package edu.neu.madcourse.sticker_app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.appcompat.app.AppCompatActivity;

public class MessagingActivity extends AppCompatActivity  {

    private final String SERVER_KEY = "key=AAAAYwQf6u0:APA91bFZ1hImC_ptJGPIScoJIUAsvUP2ORuOJkkFfCkWkWT3dGS6SnMtZ2u0U3hhpDkVwzOJLvdfXAH2KN6fqzSW4Zi4JgkK8n4wOIwMhpevlTCvVyGmzIxc0RRNab-89lK6m7MRbQgR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        Button logTokenButton = findViewById(R.id.button_log_token);

        logTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseMessaging.getInstance().getToken().addOnSuccessListener(MessagingActivity.this, new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e("Token", s);
                        Toast.makeText(MessagingActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}

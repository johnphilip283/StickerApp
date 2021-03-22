package edu.neu.madcourse.sticker_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.fragment.NavHostFragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String USERNAME_STRING = "USERNAME_STRING";
    private EditText usernameInput;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usernameInput = findViewById(R.id.username_input);
        init(savedInstanceState);
    }

    public void openMessageActivity(View view) {

        SharedPreferences sharedPref = this.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        username = usernameInput.getText().toString().trim();

        if (username.equals("")) {
            Toast.makeText(this, "Can't send stickers to friends without a username!", Toast.LENGTH_SHORT).show();
        } else {
            editor.putString(getString(R.string.username), username);
            editor.apply();
            startActivity(new Intent(MainActivity.this, MessagingActivity.class));
        }
    }

    private void init(Bundle savedInstanceState) {
        initialItemData(savedInstanceState);
    }

    // Handling Orientation Changes on Android
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(USERNAME_STRING, username);

        super.onSaveInstanceState(outState);
    }

    private void initialItemData(Bundle savedInstanceState) {
        // Not the first time to open this Activity
        if (savedInstanceState != null && savedInstanceState.containsKey(USERNAME_STRING)) {
            String savedUsername = savedInstanceState.getString(USERNAME_STRING);
            username = savedUsername;
            usernameInput.setText(username);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
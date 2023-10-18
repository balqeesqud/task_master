package activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.taskmaster.R;
import com.google.android.material.snackbar.Snackbar;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private static final String USERNAME_TAG = "username";

    TextView displayUsernameTextView;
    Button viewUsernameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Button saveBtn = findViewById(R.id.saveUserSettingsButton);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();
                EditText userNameEditText = (EditText) findViewById(R.id.UsernameEditText);
                String usernameString = userNameEditText.getText().toString();

                preferenceEditor.putString(USERNAME_TAG, usernameString);
                preferenceEditor.apply();
                Log.d("SettingsActivity", "Username to save: " + usernameString);
                Snackbar.make(findViewById(R.id.SettingsActivity), "Username saved", Snackbar.LENGTH_SHORT).show();
            }
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("SettingsActivity", "onResume called");

        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(this);
        String usernameString = sharedPreferences1.getString(SettingsActivity.USERNAME_TAG, "No username Found");
        Log.d("SettingsActivity", "Retrieved username from SharedPreferences: " + usernameString);

        TextView currentUsernameTextView = findViewById(R.id.CurrentUsernameTextView);
        currentUsernameTextView.setText(getString(R.string.current_username, usernameString));


    }
}
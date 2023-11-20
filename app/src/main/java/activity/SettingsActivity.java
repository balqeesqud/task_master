package activity;

import static com.example.taskmaster.MainActivity.TEAM_TAG;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmaster.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    public static final String TEAM_TAG = "team";
    SharedPreferences sharedPreferences;
    static final String USERNAME_TAG = "username";


    TextView displayUsernameTextView;
    Button saveBtn;
    EditText UsernameEditText;
    Spinner teamSpinner;

    List<String> teams; // List to hold team names

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        saveBtn = findViewById(R.id.saveUserSettingsButton);
        teamSpinner = findViewById(R.id.teamSpinner);

        // Initialize the teams list and add your teams
        teams = new ArrayList<>();
        teams.add("Amayreh");
        teams.add("Qudah");
        teams.add("Zarkani");

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teams);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        teamSpinner.setAdapter(adapter);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();
                EditText userNameEditText = findViewById(R.id.UsernameEditText);
                String usernameString = userNameEditText.getText().toString();
                String selectedTeam = teamSpinner.getSelectedItem().toString();

                preferenceEditor.putString(USERNAME_TAG, usernameString);
                preferenceEditor.putString(TEAM_TAG, selectedTeam);
                preferenceEditor.apply();

                Log.d("SettingsActivity", "Username to save: " + usernameString);
                Log.d("SettingsActivity", "Team to save: " + selectedTeam);

                Snackbar.make(findViewById(R.id.SettingsActivity), "Settings saved", Snackbar.LENGTH_SHORT).show();
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
        String usernameString = sharedPreferences.getString(SettingsActivity.USERNAME_TAG, "No username found");
        String teamString = sharedPreferences.getString(SettingsActivity.TEAM_TAG, "No team found");

        Log.d("SettingsActivity", "Retrieved username from SharedPreferences: " + usernameString);
        Log.d("SettingsActivity", "Retrieved team from SharedPreferences: " + teamString);

        TextView currentUsernameTextView = findViewById(R.id.CurrentUsernameTextView);
        currentUsernameTextView.setText(getString(R.string.current_username, usernameString));
        // Set the selected team in the Spinner
        setSpinnerToValue(teamSpinner, teamString);

        // Set the username in the EditText
        UsernameEditText = findViewById(R.id.UsernameEditText);
        UsernameEditText.setText(usernameString);
    }

    private void setSpinnerToValue(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
}

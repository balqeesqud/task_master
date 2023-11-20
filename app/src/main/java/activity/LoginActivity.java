package activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.core.Amplify;
import com.example.taskmaster.MainActivity;
import com.example.taskmaster.R;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent callingIntent = getIntent();
        String email = callingIntent.getStringExtra(VerifyAccountActivity.VERIFY_ACCOUNT_EMAIL_TAG);
        String username = callingIntent.getStringExtra(SignupActivity.SIGNUP_EMAIL_TAG);

        EditText usernameEditText = findViewById(R.id.usernameEditText);
        if (username != null) {
            usernameEditText.setText(username);
        } else if (email != null) {
            usernameEditText.setText(email);
        }

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            String enteredUsername = usernameEditText.getText().toString();
            String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();

            Amplify.Auth.signIn(
                    enteredUsername,
                    password,
                    success -> {
                        Log.i(TAG, "Login succeeded: " + success.toString());
                        Intent goToProductListIntent = new Intent(LoginActivity.this, MainActivity.class);
                        goToProductListIntent.putExtra(SettingsActivity.USERNAME_TAG, enteredUsername);
                        startActivity(goToProductListIntent);
                    },
                    failure -> {
                        Log.i(TAG, "Login failed: " + failure.toString());
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                        });
                    }
            );
        });

        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(v -> {
            Intent goToSignUpIntent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(goToSignUpIntent);
        });
    }
}
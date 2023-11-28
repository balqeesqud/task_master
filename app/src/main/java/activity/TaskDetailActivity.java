package activity;

import static com.example.taskmaster.MainActivity.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;
import com.bumptech.glide.Glide;

import com.amplifyframework.datastore.generated.model.Task;
import com.example.taskmaster.MainActivity;
import com.example.taskmaster.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TaskDetailActivity extends AppCompatActivity {
    private static final String USERNAME_TAG = "username";
    private Task task;
    public static final String TAG = "EditTaskActivity";
    private CompletableFuture<Task> taskCompletableFuture = null;
    private CompletableFuture<List<Team>> teamFuture = null;
    private Task taskToEdit = null;
    private EditText titleEditText;
    private EditText descriptionEditText;

    private Spinner taskStatusSpinner = null;

    private Spinner teamSpinner = null;

    ActivityResultLauncher<Intent> activityResultLauncher;
    private String s3ImageKey = "";
    private TextView locationTextView;
    static final int LOCATION_POLLING_INTERVAL = 5 * 1000;

    FusedLocationProviderClient locationProviderClient = null;

    Geocoder geocoder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail_page);
        locationTextView = findViewById(R.id.taskLocationTextView);

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(LOCATION_POLLING_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                try {
                    String address = geocoder.getFromLocation(
                                    locationResult.getLastLocation().getLatitude(),
                                    locationResult.getLastLocation().getLongitude(),
                                    1)
                            .get(0)
                            .getAddressLine(0);
                    runOnUiThread(() -> {
                        locationTextView.setText("Current Location: " + address);
                    });
                    Log.i(TAG, "Repeating current location is: " + address);
                } catch (IOException ioe) {
                    Log.e(TAG, "Could not get subscribed location: " + ioe.getMessage(), ioe);
                }
            }
        };



        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }


        });

        Button editTaskButton = findViewById(R.id.taskDetailsButton);


//        editTaskButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (task != null) {
//                    Intent editTaskPage = new Intent(TaskDetailActivity.this, EditTaskActivity.class);
//                    editTaskPage.putExtra(MainActivity.TASK_ID_TAG, task.getId());
//                    startActivity(editTaskPage);
//                } else {
//                    Log.e("TaskDetailActivity", "Task is null");
//                }
//            }
//        });
//        setUpEditableUIElement();

    }


    @Override
    protected void onResume() {
        super.onResume();

        TextView taskTitleTextView = findViewById(R.id.TaskTitleTextView);
        TextView taskStateTextView = findViewById(R.id.TaskStateTextView);
        TextView taskDescriptionTextView = findViewById(R.id.TaskDetailDescription);
        TextView TeamTextView = findViewById(R.id.TeamTextView);
        ImageView taskImageView = findViewById(R.id.taskDetailsImageView);


        String taskTitle = getIntent().getStringExtra("taskTitle");
        String taskState = getIntent().getStringExtra("taskState");
        String taskBody = getIntent().getStringExtra("Description");
        String teamTask = getIntent().getStringExtra("Team");
        String taskImage = getIntent().getStringExtra("taskImage");

        Log.d("TaskDetailActivity", "Received taskState: " + taskBody);

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(LOCATION_POLLING_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        locationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        locationProviderClient.flushLocations();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                try {
                    String address = geocoder.getFromLocation(
                                    locationResult.getLastLocation().getLatitude(),
                                    locationResult.getLastLocation().getLongitude(),
                                    1)
                            .get(0)
                            .getAddressLine(0);

                    runOnUiThread(() -> {
                        locationTextView.setText("Current Location: " + address);
                    });

                    Log.i(TAG, "Repeating current location is: " + address);
                } catch (IOException ioe) {
                    Log.e(TAG, "Could not get subscribed location: " + ioe.getMessage(), ioe);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());


        if (taskTitle != null) {
            taskTitleTextView.setText(taskTitle);
        }

        if (taskState != null) {
            taskStateTextView.setText("Task State: " + taskState);
        }

        if (taskBody != null) {
            taskDescriptionTextView.setText(taskBody);
        }
        if (teamTask != null) {
            TeamTextView.setText("Team: " + teamTask);

            if (taskImage != null) {
                Log.d("TaskDetailActivity", "Image URL: " + taskImage);
                String imagePath = "https://taskmaster1ac110bfc191422780d3c37527c67037202659-dev.s3.us-west-2.amazonaws.com/public/"+taskImage;
                Log.d("imagePath", "Image path: " + imagePath);
                Glide.with(this).load(imagePath).into(taskImageView);
            }

        }
    }
}

package activity;

import static android.app.ProgressDialog.show;
import static activity.EditTaskActivity.taskStatusFromString;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.amplifyframework.datastore.generated.model.TaskStateEnum;
import com.example.taskmaster.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;



public class AddTaskActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText descriptionEditText;
    private Spinner taskStatusSpinner = null;
    private Spinner teamSpinner= null;
    private ImageView taskImageView;
    private EditText taskTileEditText;
    private EditText taskDescriptionEditText;
    private Button SubmitAddTask;
    private TextView submittedLabel;
    //    private Button saveTask;
    private CompletableFuture<List<Team>> teamFuture = new CompletableFuture<>();
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private String s3ImageKey = "";

    private static final String TAG = "AddTaskActivity";
    static final int LOCATION_POLLING_INTERVAL = 5 * 1000;

    FusedLocationProviderClient locationProviderClient = null;

    Geocoder geocoder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        activityResultLauncher = getImagePickingActivityResultLauncher();

        teamFuture = new CompletableFuture<>();

        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        locationProviderClient.flushLocations();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationProviderClient.getLastLocation().addOnSuccessListener(location ->
        {
            if (location == null) {
                Log.e(TAG, "Location CallBack was null");
            }
            String currentLatitude = Double.toString(location.getLatitude());
            String currentLongitude = Double.toString(location.getLongitude());
            Log.i(TAG, "Our userLatitude: " + location.getLatitude());
            Log.i(TAG, "Our userLongitude: " + location.getLongitude());
        });

        locationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }

            @Override
            public boolean isCancellationRequested() {
                return false;
            }
        });

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
                    Log.i(TAG, "Repeating current location is: " + address);
                } catch (IOException ioe) {
                    Log.e(TAG, "Could not get subscribed location: " + ioe.getMessage(), ioe);
                }
            }
        };

        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });

        setUpSpinner();
        setUpSaveButton();
        setUpAddImageButton();
        setUpDeleteImageButton();
        updateImageButtons();
    }

    private void setUpSpinner() {
        teamSpinner = findViewById(R.id.addTaskTeamSpinner);
        Amplify.API.query(ModelQuery.list(Team.class),
                success -> {
                    Log.i(TAG, "Read Team successfully");
                    List<String> teamNames = new ArrayList<>();
                    List<Team> teams = new ArrayList<>();
                    for (Team team : success.getData()) {
                        teams.add(team);
                        teamNames.add(team.getName());
                    }
                    teamFuture.complete(teams);
                    runOnUiThread(() -> {
                        teamSpinner.setAdapter(new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                teamNames
                        ));
                    });
                },
                failure -> {
                    teamFuture.complete(null);
                    Log.i(TAG, "Did not read Teams successfully");
                }
        );
        taskStatusSpinner = findViewById(R.id.addTaskStateSpinner);
        taskStatusSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                TaskStateEnum.values()
        ));
    }

    private void setUpSaveButton() {
        Button saveButton = findViewById(R.id.saveTaskButton);
        saveButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            String taskTileEditText = ((EditText) findViewById(R.id.taskTileEditText)).getText().toString();
            String descriptionEditText = ((EditText) findViewById(R.id.taskDescriptionEditText)).getText().toString();


            List<Team> teams = null;
            String teamToSaveString = teamSpinner.getSelectedItem().toString();
            try {
                teams = teamFuture.get();
            } catch (InterruptedException ie) {
                Log.e(TAG, "InterruptedException while getting task");
                Thread.currentThread().interrupt();
            } catch (ExecutionException ee) {
                Log.e(TAG, "ExecutionException while getting task");
            }
            Team selctedTeam = teams.stream().filter(c -> c.getName().equals(teamToSaveString)).findAny().orElseThrow(RuntimeException::new);

            locationProviderClient.getLastLocation().addOnSuccessListener(location ->
                            {
                                if (location == null) {
                                    Log.e(TAG, "Location CallBack was null");
                                }
                                String currentLatitude = Double.toString(location.getLatitude());
                                String currentLongitude = Double.toString(location.getLongitude());
                                Log.i(TAG, "Our userLatitude: " + location.getLatitude());
                                Log.i(TAG, "Our userLongitude: " + location.getLongitude());
                                saveTask(taskTileEditText, descriptionEditText, currentLatitude, currentLongitude, selctedTeam, s3ImageKey);

                            }

                    )
                    .addOnCanceledListener(() ->
                    {
                        Log.e(TAG, "Location request was Canceled");
                    })
                    .addOnFailureListener(failure ->
                    {
                        Log.e(TAG, "Location request failed, Error was: " + failure.getMessage(), failure.getCause());
                    })
                    .addOnCompleteListener(complete ->
                    {
                        Log.e(TAG, "Location request Completed");
                    });
        });
    }

    private void saveTask(String title, String description, String latitude, String longitude, Team selectedTeam, String s3ImageKey) {
        Task taskToSave = Task.builder()
                .title(title)
                .body(description)
                .taskState(taskStatusFromString(taskStatusSpinner.getSelectedItem().toString()))
                .taskLatitude(latitude)
                .taskLongitude(longitude)
                .teamTask(selectedTeam)
                .taskImageS3Key(s3ImageKey)
                .build();

        Log.d(TAG, "Task to Save: " + taskToSave.toString());

        Amplify.API.mutate(ModelMutation.create(taskToSave),
                successResponse ->
                {
                    Log.i(TAG, "AddTaskActivity.onCreate(): edited a product successfully");
                    runOnUiThread(() -> {
                        Toast.makeText(AddTaskActivity.this, "Task is saved", Toast.LENGTH_SHORT).show();
                    });
                },
                failureResponse -> Log.i(TAG, "AddTaskActivity.onCreate(): failed with this response: " + failureResponse)
        );
//        Snackbar.make(findViewById(R.id.AddTaskActivity), "Task saved!", Snackbar.LENGTH_SHORT).show();
    }

    private void setUpAddImageButton() {
        Button addImageButton = findViewById(R.id.addTaskAddImageButton);
        addImageButton.setOnClickListener(b -> {
            launchImageSelectionIntent();
        });
    }

    private void launchImageSelectionIntent() {
        Intent imageFilePickingIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageFilePickingIntent.setType("*/*");
        imageFilePickingIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"});

        activityResultLauncher.launch(imageFilePickingIntent);
    }


    private ActivityResultLauncher<Intent> getImagePickingActivityResultLauncher() {
        // Part 2: Create an image picking activity result launcher
        ActivityResultLauncher<Intent> imagePickingActivityResultLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                Button addImageButton = findViewById(R.id.addTaskAddImageButton);
                                if (result.getResultCode() == Activity.RESULT_OK) {
                                    if (result.getData() != null) {
                                        Uri pickedImageFileUri = result.getData().getData();
                                        try {
                                            InputStream pickedImageInputStream = getContentResolver().openInputStream(pickedImageFileUri);
                                            String pickedImageFilename = getFileNameFromUri(pickedImageFileUri);
                                            Log.i(TAG, "Succeeded in getting input stream from file on phone! Filename is: " + pickedImageFilename);
                                            switchFromAddButtonToDeleteButton(addImageButton);
                                            uploadInputStreamToS3(pickedImageInputStream, pickedImageFilename, pickedImageFileUri);
                                        } catch (FileNotFoundException fnfe) {
                                            Log.e(TAG, "Could not get file from file picker! " + fnfe.getMessage(), fnfe);
                                        }
                                    }
                                } else {
                                    Log.e(TAG, "Activity result error in ActivityResultLauncher.onActivityResult");
                                }
                            }
                        }
                );

        return imagePickingActivityResultLauncher;
    }

    private void uploadInputStreamToS3(InputStream pickedImageInputStream, String pickedImageFilename, Uri pickedImageFileUri) {
        Amplify.Storage.uploadInputStream(
                pickedImageFilename,  // S3 key
                pickedImageInputStream,
                success ->
                {
                    Log.i(TAG, "Succeeded in getting file uploaded to S3! Key is: " + success.getKey());
                    s3ImageKey = success.getKey();
//                    saveButton(s3ImageKey);
                    updateImageButtons();
                    ImageView productImageView = findViewById(R.id.addTaskImageView);
                    InputStream pickedImageInputStreamCopy = null;

                    try {
                        pickedImageInputStreamCopy = getContentResolver().openInputStream(pickedImageFileUri);
                    } catch (FileNotFoundException fnfe) {
                        Log.e(TAG, "Could not get file stream from URI! " + fnfe.getMessage(), fnfe);
                    }
                    productImageView.setImageBitmap(BitmapFactory.decodeStream(pickedImageInputStreamCopy));

                },
                failure ->
                {
                    Log.e(TAG, "Failure in uploading file to S3 with filename: " + pickedImageFilename + " with error: " + failure.getMessage());
                }
        );
    }

    private void setUpDeleteImageButton() {
        Button deleteImageButton = findViewById(R.id.addTaskDeleteImageButton);
        deleteImageButton.setOnClickListener(v -> {
            Amplify.Storage.remove(
                    s3ImageKey,
                    success -> {
                        Log.i(TAG, "Succeeded in deleting file on S3! Key is: " + success.getKey());
                    },
                    failure -> {
                        Log.e(TAG, "Failure in deleting file on S3 with key: " + s3ImageKey + " with error: " + failure.getMessage());
                    }
            );
            ImageView taskImageView = findViewById(R.id.addTaskImageView);
            taskImageView.setImageResource(android.R.color.transparent);

            saveTask("", "", "", "", null, "");
            switchFromDeleteButtonToAddButton(deleteImageButton);

        });
    }


    private void updateImageButtons() {
        Button addImageButton = findViewById(R.id.addTaskAddImageButton);
        Button deleteImageButton = findViewById(R.id.addTaskDeleteImageButton);
        runOnUiThread(() -> {
            if (s3ImageKey.isEmpty()) {
                deleteImageButton.setVisibility(View.INVISIBLE);
                addImageButton.setVisibility(View.VISIBLE);
            } else {
                deleteImageButton.setVisibility(View.VISIBLE);
                addImageButton.setVisibility(View.INVISIBLE);
            }
        });
    }


    private void switchFromDeleteButtonToAddButton(Button deleteImageButton) {
        Button addImageButton = findViewById(R.id.addTaskAddImageButton);
        deleteImageButton.setVisibility(View.INVISIBLE);
        addImageButton.setVisibility(View.VISIBLE);
    }

    private void switchFromAddButtonToDeleteButton(Button addImageButton) {
        Button deleteImageButton = findViewById(R.id.addTaskDeleteImageButton);
        deleteImageButton.setVisibility(View.VISIBLE);
        addImageButton.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("Range")
    public String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent callingIntent = getIntent();

        // Task Title , and Description
        if (callingIntent != null) {
            if (callingIntent.getType() != null && callingIntent.getType().equals("text/plain")) {
                handleTextIntent(callingIntent);
            }
            // Task Image
            if (callingIntent.getType() != null && callingIntent.getType().startsWith("image")) {
                handleImageIntent(callingIntent);
            }
        }
    }

    private void handleTextIntent(Intent intent) {
        String callingText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (callingText != null) {
            String cleanedText = cleanText(callingText);
            ((EditText) findViewById(R.id.taskTileEditText)).setText(cleanedText);
            ((EditText) findViewById(R.id.taskDescriptionEditText)).setText(cleanedText);
        }
    }

    private void handleImageIntent(Intent intent) {
        Uri incomingImageFileUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (incomingImageFileUri != null) {
            try {
                InputStream incomingImageFileInputStream = getContentResolver().openInputStream(incomingImageFileUri);
                ImageView taskImageView = findViewById(R.id.addTaskImageView);
                Log.d(TAG, "Image URI: " + incomingImageFileUri);


                if (taskImageView != null) {
                    taskImageView.setImageBitmap(BitmapFactory.decodeStream(incomingImageFileInputStream));
                } else {
                    Log.e(TAG, "ImageView is null for some reason");
                }
            } catch (FileNotFoundException fnfe) {
                Log.e(TAG, "Could not get file stream from the URI " + fnfe.getMessage(), fnfe);
            }
        }
    }


    private String cleanText(String text) {
        text = text.replaceAll("\\b(?:https?|ftp):\\/\\/\\S+\\b", ""); // remove links
        text = text.replaceAll("\"", ""); // remove double quotation

        return text;
    }
}

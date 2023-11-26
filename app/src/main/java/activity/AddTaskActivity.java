package activity;

import static android.app.ProgressDialog.show;
import static activity.EditTaskActivity.taskStatusFromString;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
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
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStateEnum;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.MainActivity;
import com.example.taskmaster.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import enums.TaskState;

public class AddTaskActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText descriptionEditText;
    private Spinner taskStatusSpinner;
    private Spinner teamSpinner;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        teamFuture = new CompletableFuture<>();
        activityResultLauncher = getImagePickingActivityResultLauncher();

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener()

        {
            public void onClick (View view){
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
                TaskState.values()
        ));
    }

    private void setUpSaveButton() {
        Button saveButton = findViewById(R.id.saveTaskButton);
        saveButton.setOnClickListener(v ->
                saveButton(s3ImageKey));
    }

    private void saveButton(String s3ImageKey) {
//        Spinner taskStateSpinner = findViewById(R.id.addTaskStateSpinner);
//        taskStateSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TaskStateEnum.values()));
        taskTileEditText = findViewById(R.id.taskTileEditText);
        descriptionEditText = findViewById(R.id.taskDescriptionEditText);

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
        Team teamToSave = teams.stream().filter(c -> c.getName().equals(teamToSaveString)).findAny().orElseThrow(RuntimeException::new);
        Task taskToSave = Task.builder()
                .title(taskTileEditText.getText().toString())
                .body(descriptionEditText.getText().toString())
                .taskState(taskStatusFromString(taskStatusSpinner.getSelectedItem().toString()))
                .taskImageS3Key(s3ImageKey)
                .teamTask(teamToSave)
                .build();

        Log.d(TAG, "Task to Save: " + taskToSave.toString());

                Amplify.API.mutate(ModelMutation.create(taskToSave),
                        successResponse ->
                {
                    Log.i(TAG, "EditProductActivity.onCreate(): edited a product successfully");
                    runOnUiThread(() -> {
                        Toast.makeText(AddTaskActivity.this, "Task is saved", Toast.LENGTH_SHORT).show();
                    });
                },
                failureResponse -> Log.i(TAG, "EditProductActivity.onCreate(): failed with this response: " + failureResponse)  // failure callback
        );
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
            ImageView productImageView = findViewById(R.id.addTaskImageView);
            productImageView.setImageResource(android.R.color.transparent);

            saveButton("");
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

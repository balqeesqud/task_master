package activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.example.taskmaster.MainActivity;
import com.example.taskmaster.R;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.provider.OpenableColumns;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStateEnum;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EditTaskActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        activityResultLauncher = getImagePickingActivityResultLauncher();
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        taskCompletableFuture = new CompletableFuture<>();
        teamFuture = new CompletableFuture<>();

        setUpEditableUIElement();
        setUpSaveButton();
        setUpDeleteButton();
        setUpAddImageButton();
        setUpDeleteImageButton();
    }

    private void setUpEditableUIElement() {
        Intent callingIntent = getIntent();
        String taskId = null;

        if (callingIntent != null) {
            taskId = callingIntent.getStringExtra(MainActivity.TASK_ID_TAG);
        }

        String taskId2 = taskId;
        Amplify.API.query(
                ModelQuery.list(Task.class),
                success ->
                {
                    Log.i(TAG, "Read tasks Successfully");

                    for (Task databasetask : success.getData()) {
                        if (databasetask.getId().equals(taskId2)) {
                            taskCompletableFuture.complete(databasetask);
                        }
                    }

                    runOnUiThread(() ->
                    {
                    });
                },
                failure -> Log.i(TAG, "Couldn't read tasks successfully")
        );

        try {
            taskToEdit = taskCompletableFuture.get();
        } catch (InterruptedException ie) {
            Log.e(TAG, "InterruptedException while getting task");
            Thread.currentThread().interrupt();
        } catch (ExecutionException ee) {
            Log.e(TAG, "ExecutionException while getting task");
        }

        titleEditText = ((EditText) findViewById(R.id.editTaskTitleEditText));
        titleEditText.setText(taskToEdit.getTitle());
        descriptionEditText = ((EditText) findViewById(R.id.editTaskDescriptionEditText));
        descriptionEditText.setText(taskToEdit.getBody());
        s3ImageKey =taskToEdit.getTaskImageS3Key();
        if (s3ImageKey != null && !s3ImageKey.isEmpty()) {
            Amplify.Storage.downloadFile(
                    s3ImageKey,
                    new File(getApplication().getFilesDir(), s3ImageKey),
                    success -> {
                        ImageView productImageView = findViewById(R.id.editTaskImageImageView);
                        productImageView.setImageBitmap(BitmapFactory.decodeFile(success.getFile().getPath()));
                    },
                    failure -> {
                        Log.e(TAG, "Unable to get image from S3 for the product for S3 key: " + s3ImageKey + " for reason: " + failure.getMessage());
                    }
            );
        }
        setUpSpinners();
    }


    private void setUpSpinners() {
        teamSpinner = (Spinner) findViewById(R.id.editTaskTeamSpinner);

        Amplify.API.query(
                ModelQuery.list(Team.class),
                success ->
                {
                    Log.i(TAG, "Read tasks successfully!");
                    ArrayList<String> teamNames = new ArrayList<>();
                    ArrayList<Team> teams = new ArrayList<>();
                    for (Team team : success.getData()) {
                        teams.add(team);
                        teamNames.add(team.getName());
                    }
                    teamFuture.complete(teams);

                    runOnUiThread(() ->
                    {
                        teamSpinner.setAdapter(new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                teamNames));
                        teamSpinner.setSelection(getSpinnerIndex(teamSpinner, taskToEdit.getTeamTask().getName()));
                    });
                },
                failure -> {
                    teamFuture.complete(null);
                    Log.i(TAG, "Did not read tasks successfully!");
                }
        );

        taskStatusSpinner = (Spinner) findViewById(R.id.editTaskStatusSpinner);
        taskStatusSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                TaskStateEnum.values()));
        taskStatusSpinner.setSelection(getSpinnerIndex(taskStatusSpinner, taskToEdit.getTaskState().toString()));
    }

    private int getSpinnerIndex(Spinner spinner, String stringValueToCheck) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(stringValueToCheck)) {
                return i;
            }
        }

        return 0;
    }

    private void setUpAddImageButton() {
        Button addImageButton = (Button) findViewById(R.id.editTaskAddImageButton);
        addImageButton.setOnClickListener(b ->
        {
            launchImageSelectionIntent();
        });
    }


        private void setUpSaveButton() {
        Button saveButton = findViewById(R.id.editTaskAddImageButton);
        saveButton.setOnClickListener(v -> {
            saveTask(s3ImageKey);
        });
    }
    private void saveTask(String imageS3Key ) {
        Button saveButton = (Button) findViewById(R.id.editTaskSaveButton);
        saveButton.setOnClickListener(v ->
        {
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
                    .title(titleEditText.getText().toString())
                    .body(descriptionEditText.getText().toString())
                    .taskState((taskStatusFromString(taskStatusSpinner.getSelectedItem().toString()))) //////
//                    .teamTask(taskStatusFromString(taskStatusSpinner.getSelectedItem().toString()))
                    .teamTask(teamToSave)
                    .taskImageS3Key(imageS3Key)
                    .id(taskToEdit.getId())
                    .build();

            Amplify.API.mutate(
                    ModelMutation.update(taskToSave),
                    successResponse ->
                    {
                        Log.i(TAG, "EditTaskActivity.onCreate(): edited a task successfully");

//                        Snackbar.make(findViewById(R.id.editTaskActivity), "Task saved!", Snackbar.LENGTH_SHORT).show();
                    },
                    failureResponse -> Log.i(TAG, "EditTaskActivity.onCreate(): failed with this response: " + failureResponse)
            );
        });
    }

    private void updateTask() {
        List<Team> teams = null;
        String contactToSaveString = teamSpinner.getSelectedItem().toString();
        try {
            teams = teamFuture.get();
        } catch (InterruptedException ie) {
            Log.e(TAG, "InterruptedException while getting product");
            Thread.currentThread().interrupt();
        } catch (ExecutionException ee) {
            Log.e(TAG, "ExecutionException while getting product");
        }
        Team teamToSave = teams.stream().filter(c -> c.getName().equals(contactToSaveString)).findAny().orElseThrow(RuntimeException::new);
        Task taskToSave = Task.builder()
                .title(titleEditText.getText().toString())
                .body(descriptionEditText.getText().toString())
                .taskState((taskStatusFromString(taskStatusSpinner.getSelectedItem().toString()))) //////
                .teamTask(teamToSave)
                .id(taskToEdit.getId())
                .build();

        Amplify.API.mutate(
                ModelMutation.update(taskToSave),
                successResponse -> {
                    Log.i(TAG, "EditTaskActivity.onCreate(): edited a task successfully");
                    Snackbar.make(findViewById(R.id.editTaskActivity), "Task saved!", Snackbar.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditTaskActivity.this, MainActivity.class);
                    startActivity(intent);
                },
                failureResponse -> Log.i(TAG, "EditTaskActivity.onCreate(): failed with this response: " + failureResponse)
        );
    }

    public static TaskStateEnum taskStatusFromString(String inputProductCategoryText) {
        for (TaskStateEnum taskStatus : TaskStateEnum.values()) {
            if (taskStatus.toString().equals(inputProductCategoryText)) {
                return taskStatus;
            }
        }
        return null;
    }

    private void setUpDeleteButton() {
        Button deleteButton = (Button) findViewById(R.id.editTaskDeleteButton);
        deleteButton.setOnClickListener(v -> {
            Amplify.API.mutate(
                    ModelMutation.delete(taskToEdit),
                    successResponse ->
                    {
                        Log.i(TAG, "EditProductActivity.onCreate():  Task deleted successfully");
                        Intent goToMainActivity = new Intent(EditTaskActivity.this, MainActivity.class);
                        startActivity(goToMainActivity);
                    },
                    failureResponse -> Log.i(TAG, "EditTaskActivity.onCreate(): failed with this response: " + failureResponse)
            );
        });
    }

    private void launchImageSelectionIntent()
    {

        Intent imageFilePickingIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageFilePickingIntent.setType("*/*");
        imageFilePickingIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"});

        activityResultLauncher.launch(imageFilePickingIntent);

    }

    private ActivityResultLauncher<Intent> getImagePickingActivityResultLauncher()
    {
        // Part 2: Create an image picking activity result launcher
        ActivityResultLauncher<Intent> imagePickingActivityResultLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>()
                        {
                            @Override
                            public void onActivityResult(ActivityResult result)
                            {
                                Button addImageButton = findViewById(R.id.editTaskAddImageButton);
                                if (result.getResultCode() == Activity.RESULT_OK)
                                {
                                    if (result.getData() != null)
                                    {
                                        Uri pickedImageFileUri = result.getData().getData();
                                        try
                                        {
                                            InputStream pickedImageInputStream = getContentResolver().openInputStream(pickedImageFileUri);
                                            String pickedImageFilename = getFileNameFromUri(pickedImageFileUri);
                                            Log.i(TAG, "Succeeded in getting input stream from file on phone! Filename is: " + pickedImageFilename);
                                            // Part 3: Use our InputStream to upload file to S3
                                            switchFromAddButtonToDeleteButton(addImageButton);
                                            uploadInputStreamToS3(pickedImageInputStream, pickedImageFilename,pickedImageFileUri);

                                        } catch (FileNotFoundException fnfe)
                                        {
                                            Log.e(TAG, "Could not get file from file picker! " + fnfe.getMessage(), fnfe);
                                        }
                                    }
                                }
                                else
                                {
                                    Log.e(TAG, "Activity result error in ActivityResultLauncher.onActivityResult");
                                }
                            }
                        }
                );

        return imagePickingActivityResultLauncher;
    }

    private void switchFromDeleteButtonToAddButton(Button deleteImageButton) {
        Button addImageButton = findViewById(R.id.editTaskAddImageButton);
        deleteImageButton.setVisibility(View.INVISIBLE);
        addImageButton.setVisibility(View.VISIBLE);
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
    private void updateImageButtons() {
        Button addImageButton = findViewById(R.id.editTaskAddImageButton);
        Button deleteImageButton = findViewById(R.id.editTaskDeleteImageButton);
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
    private void switchFromAddButtonToDeleteButton(Button addImageButton) {
        Button deleteImageButton = findViewById(R.id.editTaskDeleteImageButton);
        deleteImageButton.setVisibility(View.VISIBLE);
        addImageButton.setVisibility(View.INVISIBLE);
    }

    private void uploadInputStreamToS3(InputStream pickedImageInputStream, String pickedImageFilename,Uri pickedImageFileUri)
    {
        Amplify.Storage.uploadInputStream(
                pickedImageFilename,  // S3 key
                pickedImageInputStream,
                success ->
                {
                    Log.i(TAG, "Succeeded in getting file uploaded to S3! Key is: " + success.getKey());
                    saveTask(success.getKey());
                    updateImageButtons();
                    ImageView productImageView = findViewById(R.id.editTaskImageImageView);
                    InputStream pickedImageInputStreamCopy = null;
                    try
                    {
                        pickedImageInputStreamCopy = getContentResolver().openInputStream(pickedImageFileUri);
                    }
                    catch (FileNotFoundException fnfe)
                    {
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

    private void setUpDeleteImageButton()
    {
        Button deleteImageButton = (Button)findViewById(R.id.editTaskDeleteImageButton);
        String s3ImageKey = this.s3ImageKey;
        deleteImageButton.setOnClickListener(v ->
        {
            Amplify.Storage.remove(
                    s3ImageKey,
                    success ->
                    {
                        Log.i(TAG, "Succeeded in deleting file on S3! Key is: " + success.getKey());

                    },
                    failure ->
                    {
                        Log.e(TAG, "Failure in deleting file on S3 with key: " + s3ImageKey + " with error: " + failure.getMessage());
                    }
            );
            ImageView productImageView = findViewById(R.id.editTaskImageImageView);
            productImageView.setImageResource(android.R.color.transparent);

            saveTask("");
            switchFromDeleteButtonToAddButton(deleteImageButton);
        });
    }



}
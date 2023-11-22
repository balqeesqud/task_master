package activity;

import static com.example.taskmaster.MainActivity.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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

import java.io.File;
import java.util.List;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail_page);

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
//    private void setUpEditableUIElement() {
//        Intent callingIntent = getIntent();
//        String taskId = null;
//
//        if (callingIntent != null) {
//            taskId = callingIntent.getStringExtra(MainActivity.TASK_ID_TAG);
//        }
//
//        String taskId2 = taskId;
//        Amplify.API.query(
//                ModelQuery.list(Task.class),
//                success ->
//                {
//                    Log.i(TAG, "Read tasks Successfully");
//
//                    for (Task databasetask : success.getData()) {
//                        if (databasetask.getId().equals(taskId2)) {
//                            taskCompletableFuture.complete(databasetask);
//                        }
//                    }
//
//                    runOnUiThread(() ->
//                    {
//                    });
//                },
//                failure -> Log.i(TAG, "Couldn't read tasks successfully")
//        );
//
//        try {
//            taskToEdit = taskCompletableFuture.get();
//        } catch (InterruptedException ie) {
//            Log.e(TAG, "InterruptedException while getting task");
//            Thread.currentThread().interrupt();
//        } catch (ExecutionException ee) {
//            Log.e(TAG, "ExecutionException while getting task");
//        }
//
//        titleEditText = ((EditText) findViewById(R.id.editTaskTitleEditText));
//        titleEditText.setText(taskToEdit.getTitle());
//        descriptionEditText = ((EditText) findViewById(R.id.editTaskDescriptionEditText));
//        descriptionEditText.setText(taskToEdit.getBody());
//        s3ImageKey =taskToEdit.getTaskImageS3Key();
//        if (s3ImageKey != null && !s3ImageKey.isEmpty()) {
//            Amplify.Storage.downloadFile(
//                    s3ImageKey,
//                    new File(getApplication().getFilesDir(), s3ImageKey),
//                    success -> {
//                        ImageView productImageView = findViewById(R.id.editTaskImageImageView);
//                        productImageView.setImageBitmap(BitmapFactory.decodeFile(success.getFile().getPath()));
//                    },
//                    failure -> {
//                        Log.e(TAG, "Unable to get image from S3 for the product for S3 key: " + s3ImageKey + " for reason: " + failure.getMessage());
//                    }
//            );
//        }
//    }

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
                String imagePath = "https://taskmaster1ac110bfc191422780d3c37527c67037202659-dev.s3.us-west-2.amazonaws.com/public/IMG-20231121-WA0042.jpeg";

                Glide.with(this).load(imagePath).into(taskImageView);
//                String testImageUrl = "data:image/jpeg;base64,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";
            }


        }
    }
}

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
                String imagePath = "https://taskmaster1ac110bfc191422780d3c37527c67037202659-dev.s3.us-west-2.amazonaws.com/public/"+taskImage;
                Log.d("imagePath", "Image path: " + imagePath);
                Glide.with(this).load(imagePath).into(taskImageView);
            }

        }
    }
}

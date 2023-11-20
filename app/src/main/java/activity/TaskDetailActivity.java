package activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amplifyframework.datastore.generated.model.Task;
import com.example.taskmaster.MainActivity;
import com.example.taskmaster.R;

public class TaskDetailActivity extends AppCompatActivity {
    private static final String USERNAME_TAG = "username";
    private Task task;

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView taskTitleTextView = findViewById(R.id.TaskTitleTextView);
        TextView taskStateTextView = findViewById(R.id.TaskStateTextView);
        TextView taskDescriptionTextView = findViewById(R.id.TaskDetailDescription);
        TextView TeamTextView= findViewById(R.id.TeamTextView);

        String taskTitle = getIntent().getStringExtra("taskTitle");
        String taskState = getIntent().getStringExtra("taskState");
        String taskBody = getIntent().getStringExtra("Description");
//        String teamTask= getIntent().getStringExtra("Team");

        Log.d("TaskDetailActivity", "Received taskState: " + taskBody);


        if (taskTitle != null) {
            taskTitleTextView.setText(taskTitle);
        }

        if (taskState != null) {
            taskStateTextView.setText("Task State: " + taskState);
        }

        if (taskBody != null) {
            taskDescriptionTextView.setText( taskBody);
        }
//        if (teamTask != null) {
//            TeamTextView.setText( taskBody);
//        }

    }


}

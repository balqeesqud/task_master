package activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.taskmaster.R;

public class TaskDetailActivity extends AppCompatActivity {
    private static final String USERNAME_TAG = "username";

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

    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView taskTitleTextView = findViewById(R.id.TaskTitleTextView);
        TextView taskStateTextView = findViewById(R.id.TaskStateTextView);
        TextView taskDescriptionTextView = findViewById(R.id.TaskDetailDescription);


        String taskTitle = getIntent().getStringExtra("taskTitle");
        String taskState = getIntent().getStringExtra("taskState");
        String taskBody = getIntent().getStringExtra("Description");

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

    }


}

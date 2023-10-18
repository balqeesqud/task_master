package activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
        String taskTitle = getIntent().getStringExtra("taskTitle");
        if (taskTitle != null) {
            taskTitleTextView.setText(taskTitle);
        }

        }


    }

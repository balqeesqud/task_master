package activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.example.taskmaster.R;
import java.util.Date;
import database.TaskMasterDatabase;
import enums.TaskState;
import model.Task;



public class AddTaskActivity extends AppCompatActivity {

    private EditText taskTileEditText;
    private EditText taskDescriptionEditText;
    private Button SubmitAddTask;
    private TextView submittedLabel;

    private Button saveTask;
    TaskMasterDatabase taskMasterDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        taskMasterDatabase = Room.databaseBuilder(
                        getApplicationContext(),
                        TaskMasterDatabase.class,
                        "task_master")
                .allowMainThreadQueries()
                .build();

        Spinner taskStateSpinner = findViewById(R.id.addTaskStateSpinner);
        taskStateSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TaskState.values()));


        taskTileEditText = findViewById(R.id.taskTileEditText);
        taskDescriptionEditText = findViewById(R.id.taskDescriptionEditText);
//        SubmitAddTask = findViewById(R.id.SubmitAddTask);
//        submittedLabel = findViewById(R.id.submittedLabel);
        saveTask = findViewById(R.id.saveTaskButton);


        saveTask.setOnClickListener(v -> {
            int x = 0;

            String selectedValue = taskStateSpinner.getSelectedItem().toString();

            Log.d("TaskState", "Selected Value: " + selectedValue);

            TaskState convertedState = TaskState.fromString(selectedValue);

            Log.d("TaskState", "Converted State: " + convertedState);


            Task newTask = new Task(
                    ((EditText) findViewById(R.id.taskTileEditText)).getText().toString(),
                    ((EditText) findViewById(R.id.taskDescriptionEditText)).getText().toString(),
                    new Date(),
                    TaskState.fromString(taskStateSpinner.getSelectedItem().toString())
            );

            taskMasterDatabase.taskDao().insertTask(newTask);

            Toast toast = Toast.makeText(getApplicationContext(), "Your Task was saved!", Toast.LENGTH_SHORT);
            toast.show();

            // to count tasks and display the number of it, we need to count it from the database
            int taskCount = taskMasterDatabase.taskDao().getTaskCount();
            TextView count = findViewById(R.id.counter);
            count.setText("Total Tasks: " + taskCount);



//            taskMasterDatabase.taskDao().insertTask(newTask);
//            Toast toast = Toast.makeText(getApplicationContext(), "Your Task was saved!", Toast.LENGTH_SHORT);
//            toast.show();
//            TextView count = findViewById(R.id.counter);
//            count.setText("Total Tasks:" + String.valueOf(x++));




        });


        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}

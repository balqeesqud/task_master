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

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStateEnum;
import com.example.taskmaster.R;

import java.util.Date;


public class AddTaskActivity extends AppCompatActivity {

    private EditText taskTileEditText;
    private EditText taskDescriptionEditText;
    private Button SubmitAddTask;
    private TextView submittedLabel;

    private Button saveTask;
    private int x = 0;

    public static final String TAG = "addTaskActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Spinner taskStateSpinner = findViewById(R.id.addTaskStateSpinner);
        taskStateSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TaskStateEnum.values()));


        taskTileEditText = findViewById(R.id.taskTileEditText);
        taskDescriptionEditText = findViewById(R.id.taskDescriptionEditText);
        saveTask = findViewById(R.id.saveTaskButton);


        saveTask.setOnClickListener(v -> {

            String title = taskTileEditText.getText().toString();
            String body = taskDescriptionEditText.getText().toString();
            String currentDateString = com.amazonaws.util.DateUtils.formatISO8601Date(new Date());
            TaskStateEnum selectedValue = (TaskStateEnum) taskStateSpinner.getSelectedItem();

            Task newTask = Task.builder()

                    .title(title)
                    .body(body)
                    .taskState(selectedValue)
                    .dateCreated(new Temporal.DateTime(new Date(), 0)).build();


            Amplify.API.mutate(
                    ModelMutation.create(newTask),
                    successResponse -> Log.i(TAG, "AddTaskActivity.onCreate(): add a Task Successfully"),
                    failureResponse -> Log.e(TAG, "AddTaskActivity.onCreate(): Failed to add a Task" + failureResponse)

            );

            Toast toast = Toast.makeText(getApplicationContext(), "Your Task was saved!", Toast.LENGTH_SHORT);
            toast.show();
            TextView count = findViewById(R.id.counter);
            count.setText("Total Tasks: " + ++x);


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

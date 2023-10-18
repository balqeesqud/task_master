package activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmaster.R;

public class AddTaskActivity extends AppCompatActivity {

    private EditText taskTileEditText;
    private EditText taskDescriptionEditText;
    private Button SubmitAddTask;
    private TextView submittedLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);


        taskTileEditText = findViewById(R.id.taskTileEditText);
        taskDescriptionEditText = findViewById(R.id.taskDescriptionEditText);
        SubmitAddTask = findViewById(R.id.SubmitAddTask);
        submittedLabel = findViewById(R.id.submittedLabel);


        SubmitAddTask.setOnClickListener(new View.OnClickListener() {
            int x = 0;

            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(), "Submitted!!", Toast.LENGTH_SHORT);
                toast.show();
                TextView count = findViewById(R.id.counter);
                count.setText("Total Tasks:" + String.valueOf(x++));
            }
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

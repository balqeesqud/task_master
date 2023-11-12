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
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStateEnum;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class AddTaskActivity extends AppCompatActivity {

    private EditText taskTileEditText;
    private EditText taskDescriptionEditText;
    private Button SubmitAddTask;
    private TextView submittedLabel;
    private Button saveTask;

    private Spinner teamSpinner = null;
    private Spinner taskStatusSpinner = null;

    CompletableFuture<List<Team>> teamFuture = new CompletableFuture<List<Team>>();
    private int x = 0;

    public static final String TAG = "addTaskActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        teamFuture = new CompletableFuture<>();
        setUpSpinner();
        setUpSaveButton();
    }

    private void setUpSpinner() {
        teamSpinner = findViewById(R.id.addTaskTeamSpinner);
        Amplify.API.query(ModelQuery.list(Team.class), success -> {
                    Log.i(TAG, "Read Team successfully");
                    List<String> teamNames = new ArrayList<>();
                    List<Team> teams = new ArrayList<>();
                    for (Team team : success.getData()) {
                        teams.add(team);
                        teamNames.add(team.getName());
                    }
                    teamFuture.complete(teams);
                    runOnUiThread(() ->
                    {
                        teamSpinner.setAdapter(new ArrayAdapter<>(
                                this,
                                (android.R.layout.simple_spinner_item),
                                teamNames
                        ));
                    });
                },
                failure -> {
                    teamFuture.complete(null);
                    Log.i(TAG, "Did not read Teams successfully");
                }
        );
    }

    private void setUpSaveButton(){
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
            String selectedTeamString = teamSpinner.getSelectedItem().toString();


            List<Team> teams=null;
            try {
                teams=teamFuture.get();
            }catch (InterruptedException ie){
                Log.e(TAG, " InterruptedException while getting teams");
            }catch (ExecutionException ee){
                Log.e(TAG," ExecutionException while getting teams");
            }

            Team selectedTeams = teams.stream().filter(c -> c.getName().equals(selectedTeamString)).findAny().orElseThrow(RuntimeException::new);

            Task newTask = Task.builder()

                    .title(title)
                    .body(body)
                    .taskState(selectedValue)
                    .teamTask(selectedTeams).build();
//                    .dateCreated(new Temporal.DateTime(new Date(), 0)).build();


            Amplify.API.mutate(
                    ModelMutation.create(newTask),
                    successResponse -> Log.i(TAG, "AddTaskActivity.onCreate(): added a Task successfully"),
                    failureResponse -> Log.e(TAG, "AddTaskActivity.onCreate(): failed with this response" + failureResponse)
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

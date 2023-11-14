package activity;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Spinner;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStateEnum;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

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

    private void setUpSaveButton() {
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
}
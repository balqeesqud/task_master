package com.example.taskmaster;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.auth.AuthUserAttribute;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import activity.AddTaskActivity;
import activity.AllTasksActivity;
import activity.LoginActivity;
import activity.SettingsActivity;
import activity.TaskDetailActivity;
import adapter.TasksListRecyclerViewAdapter;

import com.amplifyframework.datastore.generated.model.Team;

public class MainActivity extends AppCompatActivity {

    public static final String TASK_TITLE_TAG = "taskName";

    public static final String TASK_ID_TAG = "Task ID";
    private static final String USERNAME_TAG = "username";
    public static final String DATABASE_NAME = "task_master";
    private String username;
    private List<Task> tasks = new ArrayList<>();
    private TasksListRecyclerViewAdapter adapter;
    public static final String TAG = "addTaskActivity";
    private String selectedTeam;
    public static final String TEAM_TAG = "team";

    public static final String TASK_TITLE_EXTRA = "taskTitle";
    public static final String TASK_STATE_EXTRA = "taskState";
    public static final String DESCRIPTION_EXTRA = "Description";
    public static final String TEAM_EXTRA = "Team";
    public static final String TASK_ID_EXTRA = TASK_ID_TAG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();


        String emptyFilename = "emptyTestFileName";
        File emptyFile = new File(getApplicationContext().getFilesDir(), emptyFilename);

        try {
            BufferedWriter emptyFileBufferedWriter = new BufferedWriter(new FileWriter(emptyFile));

            emptyFileBufferedWriter.append("Balqees AlQudah\nHello Welcome ");
            emptyFileBufferedWriter.close();

        } catch (IOException ioe) {
            Log.i(TAG, "could not write locally with filename: " + emptyFilename);
        }

        String emptyFileS3Key = "TaskMasterTest.txt";
        Amplify.Storage.uploadFile(
                emptyFileS3Key,
                emptyFile,
                success ->
                {
                    Log.i(TAG, "S3 upload succeeded and the Key is: " + success.getKey());
                },
                failure ->
                {
                    Log.i(TAG, "S3 upload failed! " + failure.getMessage());
                }
        );

        setUpAddTaskButton();
        createTeams();
        queryTasks();
        setUpLoginAndLogoutButton();

    }


    @Override
    protected void onResume() {
        super.onResume();

        AuthUser authUser = Amplify.Auth.getCurrentUser();
        String username = "";
        if (authUser == null) {
            Button loginButton = findViewById(R.id.taskmasterLoginButton);
            loginButton.setVisibility(View.VISIBLE);
            Button logoutButton = findViewById(R.id.taskmasterLogoutButton);
            logoutButton.setVisibility(View.INVISIBLE);
        } else {
            username = authUser.getUsername();
            Log.i(TAG, "Username is: " + username);
            Button loginButton = findViewById(R.id.taskmasterLoginButton);
            loginButton.setVisibility(View.INVISIBLE);
            Button logoutButton = findViewById(R.id.taskmasterLogoutButton);
            logoutButton.setVisibility(View.VISIBLE);

            String finalUsername = username;
            Amplify.Auth.fetchUserAttributes(
                    success -> {
                        Log.i(TAG, "Fetch user attributes succeeded for username: " + finalUsername);
                        for (AuthUserAttribute userAttribute : success) {
                            if (userAttribute.getKey().getKeyString().equals("email")) {
                                String userEmail = userAttribute.getValue();
                                runOnUiThread(() -> {
                                    ((TextView) findViewById(R.id.usernameTextView)).setText(userEmail);
                                });
                            }
                        }
                    },
                    failure -> {
                        Log.i(TAG, "Fetch user attributes failed: " + failure.toString());
                    }
            );

            selectedTeam = "Amayreh";
        }

        // Query tasks after setting the selectedTeam
        queryTasks();
    }


//        if (username != null) {
//            TextView usernameTextView = findViewById(R.id.usernameTextView);
//            usernameTextView.setText(username + "'s tasks");
//        }


    private void createTeams() {
        // Check if teams already exist before creating them
        checkAndCreateTeam("Amayreh");
        checkAndCreateTeam("Qudah");
        checkAndCreateTeam("Zarkani");
    }

    private boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }


    private void checkAndCreateTeam(String teamName) {
        Amplify.API.query(
                ModelQuery.list(Team.class, Team.NAME.eq(teamName)),
                response -> {
                    List<Team> teams = (List<Team>) response.getData().getItems();
                    if (teams == null || teams.size() == 0) {
                        // Team doesn't exist, create it
                        Team team = Team.builder().name(teamName).build();
                        Amplify.API.mutate(
                                ModelMutation.create(team),
                                successResponse -> Log.i(TAG, "Team created successfully"),
                                failureResponse -> Log.e(TAG, "Team creation failed", failureResponse)
                        );
                    } else {
                        // Team already exists
                        Log.i(TAG, "Team already exists: " + teamName);
                    }
                },
                error -> Log.e(TAG, "Error checking team existence", error)
        );
    }


    private void queryTasks() {
        Amplify.API.query(
                ModelQuery.list(Task.class),
                success -> {
                    Log.i(TAG, "Read Task successfully");
                    tasks.clear();
                    for (Task databaseTask : success.getData()) {
                        // Check if the task belongs to the selected team
                        Team teamTask = databaseTask.getTeamTask();
                        if (teamTask != null && teamTask.getName().equals(selectedTeam)) {
                            tasks.add(databaseTask);
                        }
                    }
                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                    });
                },
                failure -> Log.i(TAG, "Couldn't read tasks from DynamoDB: " + failure.toString())
        );
    }

    private void setUpAddTaskButton() {
        Button addTaskBtn = findViewById(R.id.AddTaskButton);
        addTaskBtn.setOnClickListener(view -> {
            Intent addTaskForm = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(addTaskForm);
        });
    }

    private void init() {
        RecyclerView taskListRecyclerView = findViewById(R.id.TaskListRecyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        taskListRecyclerView.setLayoutManager(layoutManager);

        adapter = new TasksListRecyclerViewAdapter(tasks, MainActivity.this);
        taskListRecyclerView.setAdapter(adapter);

        Button allTaskBtn = (Button) findViewById(R.id.AllTaskButton);
        ImageButton settingsButton = findViewById(R.id.settingsImageButton);
        TextView usernameTextView = findViewById(R.id.usernameTextView);


        allTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent allTasksPage = new Intent(MainActivity.this, AllTasksActivity.class);
                startActivity(allTasksPage);
                Log.d("MainActivity", "All Tasks button clicked");
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsPage = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsPage);
            }
        });
    }

    private void setUpLoginAndLogoutButton() {
        Button loginButton = (Button) findViewById(R.id.taskmasterLoginButton);
        loginButton.setOnClickListener(v ->
        {
            Intent goToLogInIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(goToLogInIntent);
        });


        Button logoutButton = findViewById(R.id.taskmasterLogoutButton);
        logoutButton.setOnClickListener(v -> {
            Amplify.Auth.signOut(

                    () -> {
                        Log.i(TAG, "Logout succeeded");
                        runOnUiThread(() -> {
                            ((TextView) findViewById(R.id.usernameTextView)).setText("");
                        });
                        Intent goToLogInIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(goToLogInIntent);
                    },
                    error -> {
                        Log.i(TAG, "Logout failed");
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Log out failed", Toast.LENGTH_LONG).show();
                        });
                    }
            );
        });
    }


    private void openTaskDetail(String taskTitle, Task task) {
        Intent taskDetailIntent = new Intent(MainActivity.this, TaskDetailActivity.class);

        taskDetailIntent.putExtra(TASK_ID_EXTRA, task.getId());
        taskDetailIntent.putExtra("taskTitle", taskTitle);
        taskDetailIntent.putExtra("Description", task.getBody());
        taskDetailIntent.putExtra("taskState", task.getTaskState().toString());
        taskDetailIntent.putExtra("Team", task.getTeamTask().toString());

        startActivity(taskDetailIntent);
    }
}
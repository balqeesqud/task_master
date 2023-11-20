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
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;

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

    private static final String USERNAME_TAG = "username";
    public static final String DATABASE_NAME = "task_master";
    private String username;
    private List<Task> tasks = new ArrayList<>();
    private TasksListRecyclerViewAdapter adapter;
    public static final String TAG = "addTaskActivity";
    private String selectedTeam;
    public static final String TEAM_TAG = "team";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

//        Amplify.Auth.signUp("balqeesalqudah97@gmail.com",
//                "Balqees123",
//                AuthSignUpOptions.builder()
//                        .userAttribute(AuthUserAttributeKey.email(), "balqeesalqudah97@gmail.com")
//                        .userAttribute(AuthUserAttributeKey.nickname(), "Blq")
//                        .build(),
//                good ->
//                {
//                    Log.i(TAG, "Signup succeeded: "+ good.toString());
//                },
//                bad ->
//                {
//                    Log.i(TAG, "Signup failed with username: "+ "balqeesalqudah97@gmail.com"+ " with this message: "+ bad.toString());
//                });
//
//        Amplify.Auth.confirmSignUp("balqeesalqudah97@gmail.com",
//                "518354",
//                success ->
//                {
//                    Log.i(TAG,"verification succeeded: "+ success.toString());
//
//                },
//                failure ->
//                {
//                    Log.i(TAG,"verification failed: "+ failure.toString());
//                }
//        );

//          Amplify.Auth.signIn("balqeesalqudah97@gmail.com",
//                "Balqees123",
//                success ->
//                {
//                    Log.i(TAG, "Login succeeded: "+success.toString());
//                },
//                failure ->
//                {
//                    Log.i(TAG, "Login failed: "+failure.toString());
//                }
//        );

//        Amplify.Auth.signOut(
//                () ->
//                {
//                    Log.i(TAG,"Logout succeeded");
//                },
//                failure ->
//                {
//                    Log.i(TAG, "Logout failed");
//                }
//        );

        setUpAddTaskButton();
        createTeams();
        queryTasks();
        setUpLoginAndLogoutButton();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        username = sharedPreferences.getString(USERNAME_TAG, "");
//        selectedTeam = sharedPreferences.getString(SettingsActivity.TEAM_TAG, "");

        AuthUser authUser = Amplify.Auth.getCurrentUser();
        String username="";
        if (authUser == null){
            Button loginButton = (Button) findViewById(R.id.taskmasterLoginButton);
            loginButton.setVisibility(View.VISIBLE);
            Button logoutButton = (Button) findViewById(R.id.taskmasterLogoutButton);
            logoutButton.setVisibility(View.INVISIBLE);
        }else{
            username = authUser.getUsername();
            Log.i(TAG, "Username is: "+ username);
            Button loginButton = (Button) findViewById(R.id.taskmasterLoginButton);
            loginButton.setVisibility(View.INVISIBLE);
            Button logoutButton = (Button) findViewById(R.id.taskmasterLogoutButton);
            logoutButton.setVisibility(View.VISIBLE);

            String username2 = username;
            Amplify.Auth.fetchUserAttributes(
                    success ->
                    {
                        Log.i(TAG, "Fetch user attributes succeeded for username: "+username2);
                        for (AuthUserAttribute userAttribute: success){
                            if(userAttribute.getKey().getKeyString().equals("email")){
                                String userEmail = userAttribute.getValue();
                                runOnUiThread(() ->
                                {
                                    ((TextView)findViewById(R.id.usernameTextView)).setText(userEmail);
                                });
                            }
                        }
                    },
                    failure ->
                    {
                        Log.i(TAG, "Fetch user attributes failed: "+failure.toString());
                    }
            );
        }


//        if (username != null) {
//            TextView usernameTextView = findViewById(R.id.usernameTextView);
//            usernameTextView.setText(username + "'s tasks");
//        }

        queryTasks();
    }

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
                        Team teamTask = databaseTask.getTeamTask();
                        if (teamTask != null && teamTask.getName().equals(selectedTeam)) {
                            tasks.add(databaseTask);
                        }
                    }
                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                    });
                },
                failure -> Log.i(TAG, "Couldn't read tasks from DynamoDB ")
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

    private void setUpLoginAndLogoutButton(){
        Button loginButton = (Button) findViewById(R.id.taskmasterLoginButton);
        loginButton.setOnClickListener(v ->
        {
            Intent goToLogInIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(goToLogInIntent);
        });

        Button logoutButton = (Button) findViewById(R.id.taskmasterLogoutButton);
        logoutButton.setOnClickListener(v->
        {
            Amplify.Auth.signOut(
                    () ->
                    {
                        Log.i(TAG,"Logout succeeded");
                        runOnUiThread(() ->
                        {
                            ((TextView)findViewById(R.id.usernameTextView)).setText("");
                        });
                        Intent goToLogInIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(goToLogInIntent);
                    },
                    failure ->
                    {
                        Log.i(TAG, "Logout failed");
                        runOnUiThread(() ->
                        {
                            Toast.makeText(MainActivity.this, "Log out failed", Toast.LENGTH_LONG);
                        });
                    }
            );
        });
    }



    private void openTaskDetail(String taskTitle, Task task) {
        Intent taskDetailIntent = new Intent(MainActivity.this, TaskDetailActivity.class);
        taskDetailIntent.putExtra("taskTitle", taskTitle);
        taskDetailIntent.putExtra("Description", task.getBody());
        taskDetailIntent.putExtra("taskState", task.getTaskState().toString());
        taskDetailIntent.putExtra("TeamTasks", task.getTeamTask().toString());

        startActivity(taskDetailIntent);
    }
}

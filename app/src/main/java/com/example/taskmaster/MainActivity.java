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

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.amplifyframework.datastore.generated.model.TaskStateEnum;

import activity.AddTaskActivity;
import activity.AllTasksActivity;
import activity.SettingsActivity;
import activity.TaskDetailActivity;
import adapter.TasksListRecyclerViewAdapter;
import com.amplifyframework.datastore.generated.model.TaskStateEnum;


public class MainActivity extends AppCompatActivity {

    private static final String USERNAME_TAG = "username";
    public static final String DATABASE_NAME = "task_master";
    private String username;
    List<Task> tasks=null;
    public static final String TAG = "addTaskActivity";

    TasksListRecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tasks = new ArrayList<>();

        RecyclerView TaskListRecyclerView = findViewById(R.id.TaskListRecyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        TaskListRecyclerView.setLayoutManager(layoutManager);

        adapter = new TasksListRecyclerViewAdapter(tasks, MainActivity.this);

        TaskListRecyclerView.setAdapter(adapter);


        Amplify.API.query(
                ModelQuery.list(Task.class),
                success ->
                {
                    Log.i(TAG, "Read Task successfully");
                    tasks.clear();
                    for (Task databaseTask : success.getData()){
                        tasks.add(databaseTask);
                    }
                    //adapter.notifyDataSetChanged();
                    runOnUiThread(() ->{
                        adapter.notifyDataSetChanged();
                    });
                },
                failure -> Log.i(TAG, "Couldn't read tasks from DynamoDB ")
        );




        // TODO: We will convert it into GraphQL/DynamoDB
       //   tasks=taskMasterDatabase.taskDao().findAll();

        Button addTaskBtn = (Button) findViewById(R.id.AddTaskButton);
        Button allTaskBtn = (Button) findViewById(R.id.AllTaskButton);
        ImageButton settingsButton = findViewById(R.id.settingsImageButton);
        TextView usernameTextView = findViewById(R.id.usernameTextView);

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addTaskForm = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(addTaskForm);
            }
        });

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

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = sharedPreferences.getString(USERNAME_TAG, "");
        if (username != null) {
            TextView usernameTextView = findViewById(R.id.usernameTextView);
            usernameTextView.setText(username + "'s tasks");
        }

//        tasks.clear();
//        tasks.addAll(taskMasterDatabase.taskDao().findAll());
//        adapter.notifyDataSetChanged();
    }


    private void openTaskDetail(String taskTitle, Task task) {
        Intent taskDetailIntent = new Intent(MainActivity.this, TaskDetailActivity.class);
        taskDetailIntent.putExtra("taskTitle", taskTitle);
        taskDetailIntent.putExtra("Description", task.getBody());
        taskDetailIntent.putExtra("taskState", task.getTaskState().toString());
        startActivity(taskDetailIntent);
    }
}

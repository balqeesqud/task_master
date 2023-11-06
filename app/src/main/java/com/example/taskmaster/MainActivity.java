package com.example.taskmaster;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import activity.AddTaskActivity;
import activity.AllTasksActivity;
import activity.SettingsActivity;
import activity.TaskDetailActivity;
import adapter.TasksListRecyclerViewAdapter;
//import dao.TaskDao;
//import database.TaskMasterDatabase;
//import enums.TaskState;
import enums.TaskState;
import model.Task;

public class MainActivity extends AppCompatActivity {

    private static final String USERNAME_TAG = "username";
    public static final String DATABASE_NAME = "task_master";
    private String username;
    List <Task> tasks=null;

//    TaskMasterDatabase taskMasterDatabase;

    TasksListRecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tasks = new ArrayList<>();
        tasks.add(new Task("Cooking", "Buy Carrots", new Date(), TaskState.COMPLETE));


        // TODO: We will convert it into GraphQL/DynamoDB
       //   tasks=taskMasterDatabase.taskDao().findAll();

        Button addTaskBtn = (Button) findViewById(R.id.AddTaskButton);
        Button allTaskBtn = (Button) findViewById(R.id.AllTaskButton);
//        Button taskButton1 = findViewById(R.id.taskButton1);
//        Button taskButton2 = findViewById(R.id.taskButton2);
//        Button taskButton3 = findViewById(R.id.taskButton3);
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



        RecyclerView TaskListRecyclerView = findViewById(R.id.TaskListRecyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        TaskListRecyclerView.setLayoutManager(layoutManager);

        adapter = new TasksListRecyclerViewAdapter(tasks, MainActivity.this);

        TaskListRecyclerView.setAdapter(adapter);



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
        taskDetailIntent.putExtra("taskState", task.getState().toString());
        startActivity(taskDetailIntent);
    }
}

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

import java.util.ArrayList;
import java.util.List;

import activity.AddTaskActivity;
import activity.AllTasksActivity;
import activity.SettingsActivity;
import activity.TaskDetailActivity;
import adapter.TasksListRecyclerViewAdapter;
import enums.TaskState;
import model.Task;

public class MainActivity extends AppCompatActivity {

    private static final String USERNAME_TAG = "username";
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        // Setting a click listener for each task button
//        taskButton1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openTaskDetail(taskButton1.getText().toString());
//            }
//        });
//
//        taskButton2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openTaskDetail(taskButton2.getText().toString());
//            }
//        });
//
//        taskButton3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openTaskDetail(taskButton3.getText().toString());
//            }
//        });
//


        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsPage = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsPage);
            }
        });


        RecyclerView TaskListRecyclerView= findViewById(R.id.TaskListRecyclerView);
        List<Task> taskList = new ArrayList<>();


        taskList.add(new Task("Exercising", "Do Push Ups", TaskState.ASSIGNED));
        taskList.add(new Task("Cooking", "cook Kabseih and a salad",  TaskState.IN_PROGRESS ));
        taskList.add(new Task("Cleaning", "Clean Living Room", TaskState.COMPLETE ));
        taskList.add(new Task("Shopping", "Check Zara for a new Jeans", TaskState.NEW));
        taskList.add(new Task("See Friends", "Go out with friend on thursday Night",  TaskState.IN_PROGRESS ));
        taskList.add(new Task("Studying", "Prepare my Lessons", TaskState.COMPLETE ));



        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(this);
        TaskListRecyclerView.setLayoutManager(layoutManager);

        TasksListRecyclerViewAdapter adapter= new TasksListRecyclerViewAdapter(taskList, MainActivity.this);
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
    }


    private void openTaskDetail(String taskTitle, Task task) {
        Intent taskDetailIntent = new Intent(MainActivity.this, TaskDetailActivity.class);
        taskDetailIntent.putExtra("taskTitle", taskTitle);
        taskDetailIntent.putExtra("Description", task.getBody());
        taskDetailIntent.putExtra("taskState", task.getState().toString());
        startActivity(taskDetailIntent);
    }
}

package activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import androidx.room.Room;

import com.example.taskmaster.MainActivity;
import com.example.taskmaster.R;

import java.util.List;

import adapter.TasksListRecyclerViewAdapter;
//import database.TaskMasterDatabase;
import model.Task;

public class AllTasksActivity extends AppCompatActivity {

    private static final String USERNAME_TAG = "username";
    public static final String DATABASE_NAME = "task_master";
    private String username;

    List<Task> tasks=null;

//    TaskMasterDatabase taskMasterDatabase;

    TasksListRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);


       // TODO: We will convert it into GraphQL/DynamoDB
       //  tasks=taskMasterDatabase.taskDao().findAll();

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener()

        {
            public void onClick (View view){
                onBackPressed();
            }
        });

        RecyclerView TaskListRecyclerView = findViewById(R.id.TaskListRecyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        TaskListRecyclerView.setLayoutManager(layoutManager);

        adapter = new TasksListRecyclerViewAdapter(tasks, this);

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
        // TODO: We will convert it into GraphQL/DynamoDB
        //  tasks.addAll(taskMasterDatabase.taskDao().findAll());
//        adapter.notifyDataSetChanged();
    }

}

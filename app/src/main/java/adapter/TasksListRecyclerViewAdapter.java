package adapter;

import static com.example.taskmaster.MainActivity.TASK_ID_TAG;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.taskmaster.R;

import java.util.List;

import activity.EditTaskActivity;
import activity.TaskDetailActivity;


public class TasksListRecyclerViewAdapter extends RecyclerView.Adapter<TasksListRecyclerViewAdapter.TaskListViewHolder> {

    List<Task> TaskList;
    Context MainActivity;


    public TasksListRecyclerViewAdapter(List<Task> taskList, Context MainActivity) {
        this.TaskList = taskList;
        this.MainActivity = MainActivity;
    }


    @NonNull
    @Override
    public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View taskFragment= LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_task_list, parent, false);
        return new TaskListViewHolder(taskFragment);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListViewHolder holder, int position) {
        Task task = TaskList.get(position);
        TextView taskFragmentTextView = (TextView) holder.itemView.findViewById(R.id.taskFragmentTextView);
        String taskName = TaskList.get(position).getTitle();
        String taskState = TaskList.get(position).getTaskState().name();
        String taskBody = TaskList.get(position).getBody();
        String taskTeam= String.valueOf(TaskList.get(position).getTeamTask());///////////
        taskFragmentTextView.setText(taskName);


        View taskViewHolder = holder.itemView;
        taskViewHolder.setOnClickListener(view -> {
            Intent taskDetailIntent = new Intent(MainActivity, EditTaskActivity.class);
            taskDetailIntent.putExtra("taskTitle", taskName);
            taskDetailIntent.putExtra("taskState", taskState);
            taskDetailIntent.putExtra("Description", taskBody);
            taskDetailIntent.putExtra("Team", taskTeam);
            taskDetailIntent.putExtra(TASK_ID_TAG, task.getId());
            MainActivity.startActivity(taskDetailIntent);
        });
    }


    @Override
    public int getItemCount() {
        if (TaskList != null) {
            return TaskList.size();
        } else {
            return 0;
        }
            }

    public static class TaskListViewHolder extends RecyclerView.ViewHolder{
        public TaskListViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}

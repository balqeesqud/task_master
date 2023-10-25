package enums;

import androidx.annotation.NonNull;

public enum TaskState {
    NEW ("New"),
    ASSIGNED ("Assigned"),
    IN_PROGRESS ("In Progress"),
    COMPLETE ("Complete");

    private final String taskStateText;

    TaskState(String taskStateText) {
        this.taskStateText = taskStateText;
    }

    public String getTaskStateText() {
        return taskStateText;
    }

    public static TaskState fromString(String possibleTaskStateText){
        for(TaskState task: TaskState.values()){

            if ((task.taskStateText.equals(possibleTaskStateText))){
                return task;
            }
        }
        return null;
    }

    @NonNull
    @Override
    public String toString(){
        if(taskStateText == null){
            return "";
        }
        return taskStateText;
    }
}

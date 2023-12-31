package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;
import com.amplifyframework.core.model.temporal.Temporal;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the Task type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Tasks", authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
@Index(name = "byTeam", fields = {"teamId","title"})
public final class Task implements Model {
  public static final QueryField ID = field("Task", "id");
  public static final QueryField TITLE = field("Task", "title");
  public static final QueryField BODY = field("Task", "body");
  public static final QueryField TASK_STATE = field("Task", "TaskState");
  public static final QueryField TEAM_TASK = field("Task", "teamId");
  public static final QueryField TASK_LONGITUDE = field("Task", "TaskLongitude");
  public static final QueryField TASK_LATITUDE = field("Task", "TaskLatitude");
  public static final QueryField TASK_IMAGE_S3_KEY = field("Task", "taskImageS3Key");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String title;
  private final @ModelField(targetType="String", isRequired = true) String body;
  private final @ModelField(targetType="taskStateEnum", isRequired = true) TaskStateEnum TaskState;
  private final @ModelField(targetType="Team") @BelongsTo(targetName = "teamId",  type = Team.class) Team teamTask;
  private final @ModelField(targetType="String") String TaskLongitude;
  private final @ModelField(targetType="String") String TaskLatitude;
  private final @ModelField(targetType="String") String taskImageS3Key;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  /** @deprecated This API is internal to Amplify and should not be used. */
  @Deprecated
   public String resolveIdentifier() {
    return id;
  }
  
  public String getId() {
      return id;
  }
  
  public String getTitle() {
      return title;
  }
  
  public String getBody() {
      return body;
  }
  
  public TaskStateEnum getTaskState() {
      return TaskState;
  }
  
  public Team getTeamTask() {
      return teamTask;
  }
  
  public String getTaskLongitude() {
      return TaskLongitude;
  }
  
  public String getTaskLatitude() {
      return TaskLatitude;
  }
  
  public String getTaskImageS3Key() {
      return taskImageS3Key;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Task(String id, String title, String body, TaskStateEnum TaskState, Team teamTask, String TaskLongitude, String TaskLatitude, String taskImageS3Key) {
    this.id = id;
    this.title = title;
    this.body = body;
    this.TaskState = TaskState;
    this.teamTask = teamTask;
    this.TaskLongitude = TaskLongitude;
    this.TaskLatitude = TaskLatitude;
    this.taskImageS3Key = taskImageS3Key;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Task task = (Task) obj;
      return ObjectsCompat.equals(getId(), task.getId()) &&
              ObjectsCompat.equals(getTitle(), task.getTitle()) &&
              ObjectsCompat.equals(getBody(), task.getBody()) &&
              ObjectsCompat.equals(getTaskState(), task.getTaskState()) &&
              ObjectsCompat.equals(getTeamTask(), task.getTeamTask()) &&
              ObjectsCompat.equals(getTaskLongitude(), task.getTaskLongitude()) &&
              ObjectsCompat.equals(getTaskLatitude(), task.getTaskLatitude()) &&
              ObjectsCompat.equals(getTaskImageS3Key(), task.getTaskImageS3Key()) &&
              ObjectsCompat.equals(getCreatedAt(), task.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), task.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getTitle())
      .append(getBody())
      .append(getTaskState())
      .append(getTeamTask())
      .append(getTaskLongitude())
      .append(getTaskLatitude())
      .append(getTaskImageS3Key())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Task {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("title=" + String.valueOf(getTitle()) + ", ")
      .append("body=" + String.valueOf(getBody()) + ", ")
      .append("TaskState=" + String.valueOf(getTaskState()) + ", ")
      .append("teamTask=" + String.valueOf(getTeamTask()) + ", ")
      .append("TaskLongitude=" + String.valueOf(getTaskLongitude()) + ", ")
      .append("TaskLatitude=" + String.valueOf(getTaskLatitude()) + ", ")
      .append("taskImageS3Key=" + String.valueOf(getTaskImageS3Key()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static TitleStep builder() {
      return new Builder();
  }
  
  /**
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   */
  public static Task justId(String id) {
    return new Task(
      id,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      title,
      body,
      TaskState,
      teamTask,
      TaskLongitude,
      TaskLatitude,
      taskImageS3Key);
  }
  public interface TitleStep {
    BodyStep title(String title);
  }
  

  public interface BodyStep {
    TaskStateStep body(String body);
  }
  

  public interface TaskStateStep {
    BuildStep taskState(TaskStateEnum taskState);
  }
  

  public interface BuildStep {
    Task build();
    BuildStep id(String id);
    BuildStep teamTask(Team teamTask);
    BuildStep taskLongitude(String taskLongitude);
    BuildStep taskLatitude(String taskLatitude);
    BuildStep taskImageS3Key(String taskImageS3Key);
  }
  

  public static class Builder implements TitleStep, BodyStep, TaskStateStep, BuildStep {
    private String id;
    private String title;
    private String body;
    private TaskStateEnum TaskState;
    private Team teamTask;
    private String TaskLongitude;
    private String TaskLatitude;
    private String taskImageS3Key;
    public Builder() {
      
    }
    
    private Builder(String id, String title, String body, TaskStateEnum TaskState, Team teamTask, String TaskLongitude, String TaskLatitude, String taskImageS3Key) {
      this.id = id;
      this.title = title;
      this.body = body;
      this.TaskState = TaskState;
      this.teamTask = teamTask;
      this.TaskLongitude = TaskLongitude;
      this.TaskLatitude = TaskLatitude;
      this.taskImageS3Key = taskImageS3Key;
    }
    
    @Override
     public Task build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Task(
          id,
          title,
          body,
          TaskState,
          teamTask,
          TaskLongitude,
          TaskLatitude,
          taskImageS3Key);
    }
    
    @Override
     public BodyStep title(String title) {
        Objects.requireNonNull(title);
        this.title = title;
        return this;
    }
    
    @Override
     public TaskStateStep body(String body) {
        Objects.requireNonNull(body);
        this.body = body;
        return this;
    }
    
    @Override
     public BuildStep taskState(TaskStateEnum taskState) {
        Objects.requireNonNull(taskState);
        this.TaskState = taskState;
        return this;
    }
    
    @Override
     public BuildStep teamTask(Team teamTask) {
        this.teamTask = teamTask;
        return this;
    }
    
    @Override
     public BuildStep taskLongitude(String taskLongitude) {
        this.TaskLongitude = taskLongitude;
        return this;
    }
    
    @Override
     public BuildStep taskLatitude(String taskLatitude) {
        this.TaskLatitude = taskLatitude;
        return this;
    }
    
    @Override
     public BuildStep taskImageS3Key(String taskImageS3Key) {
        this.taskImageS3Key = taskImageS3Key;
        return this;
    }
    
    /**
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     */
    public BuildStep id(String id) {
        this.id = id;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String title, String body, TaskStateEnum taskState, Team teamTask, String taskLongitude, String taskLatitude, String taskImageS3Key) {
      super(id, title, body, TaskState, teamTask, TaskLongitude, TaskLatitude, taskImageS3Key);
      Objects.requireNonNull(title);
      Objects.requireNonNull(body);
      Objects.requireNonNull(TaskState);
    }
    
    @Override
     public CopyOfBuilder title(String title) {
      return (CopyOfBuilder) super.title(title);
    }
    
    @Override
     public CopyOfBuilder body(String body) {
      return (CopyOfBuilder) super.body(body);
    }
    
    @Override
     public CopyOfBuilder taskState(TaskStateEnum taskState) {
      return (CopyOfBuilder) super.taskState(taskState);
    }
    
    @Override
     public CopyOfBuilder teamTask(Team teamTask) {
      return (CopyOfBuilder) super.teamTask(teamTask);
    }
    
    @Override
     public CopyOfBuilder taskLongitude(String taskLongitude) {
      return (CopyOfBuilder) super.taskLongitude(taskLongitude);
    }
    
    @Override
     public CopyOfBuilder taskLatitude(String taskLatitude) {
      return (CopyOfBuilder) super.taskLatitude(taskLatitude);
    }
    
    @Override
     public CopyOfBuilder taskImageS3Key(String taskImageS3Key) {
      return (CopyOfBuilder) super.taskImageS3Key(taskImageS3Key);
    }
  }
  


  
}

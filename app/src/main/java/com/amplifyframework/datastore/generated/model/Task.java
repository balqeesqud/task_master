package com.amplifyframework.datastore.generated.model;
import static com.amplifyframework.core.model.query.predicate.QueryField.field;

import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelIdentifier;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;
import com.amplifyframework.core.model.temporal.Temporal;


/** This is an auto generated class representing the Task type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Tasks", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class Task implements Model {
  public static final QueryField ID = field("Task", "id");
  public static final QueryField TITLE = field("Task", "title");
  public static final QueryField BODY = field("Task", "body");
  public static final QueryField TASK_STATE = field("Task", "TaskState");
  public static final QueryField DATE_CREATED = field("Task", "dateCreated");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String title;
  private final @ModelField(targetType="String", isRequired = true) String body;
  private final @ModelField(targetType="taskStateEnum", isRequired = true) TaskStateEnum TaskState;
  private final @ModelField(targetType="AWSDateTime", isRequired = true) Temporal.DateTime dateCreated;
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
  
  public Temporal.DateTime getDateCreated() {
      return dateCreated;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Task(String id, String title, String body, TaskStateEnum TaskState, Temporal.DateTime dateCreated) {
    this.id = id;
    this.title = title;
    this.body = body;
    this.TaskState = TaskState;
    this.dateCreated = dateCreated;
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
              ObjectsCompat.equals(getDateCreated(), task.getDateCreated()) &&
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
      .append(getDateCreated())
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
      .append("dateCreated=" + String.valueOf(getDateCreated()) + ", ")
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
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      title,
      body,
      TaskState,
      dateCreated);
  }
  public interface TitleStep {
    BodyStep title(String title);
  }
  

  public interface BodyStep {
    TaskStateStep body(String body);
  }
  

  public interface TaskStateStep {
    DateCreatedStep taskState(TaskStateEnum taskState);
  }
  

  public interface DateCreatedStep {
    BuildStep dateCreated(Temporal.DateTime dateCreated);
  }
  

  public interface BuildStep {
    Task build();
    BuildStep id(String id);
  }
  

  public static class Builder implements TitleStep, BodyStep, TaskStateStep, DateCreatedStep, BuildStep {
    private String id;
    private String title;
    private String body;
    private TaskStateEnum TaskState;
    private Temporal.DateTime dateCreated;
    public Builder() {
      
    }
    
    private Builder(String id, String title, String body, TaskStateEnum TaskState, Temporal.DateTime dateCreated) {
      this.id = id;
      this.title = title;
      this.body = body;
      this.TaskState = TaskState;
      this.dateCreated = dateCreated;
    }
    
    @Override
     public Task build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Task(
          id,
          title,
          body,
          TaskState,
          dateCreated);
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
     public DateCreatedStep taskState(TaskStateEnum taskState) {
        Objects.requireNonNull(taskState);
        this.TaskState = taskState;
        return this;
    }
    
    @Override
     public BuildStep dateCreated(Temporal.DateTime dateCreated) {
        Objects.requireNonNull(dateCreated);
        this.dateCreated = dateCreated;
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
    private CopyOfBuilder(String id, String title, String body, TaskStateEnum taskState, Temporal.DateTime dateCreated) {
      super(id, title, body, TaskState, dateCreated);
      Objects.requireNonNull(title);
      Objects.requireNonNull(body);
      Objects.requireNonNull(TaskState);
      Objects.requireNonNull(dateCreated);
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
     public CopyOfBuilder dateCreated(Temporal.DateTime dateCreated) {
      return (CopyOfBuilder) super.dateCreated(dateCreated);
    }
  }
  

  public static class TaskIdentifier extends ModelIdentifier<Task> {
    private static final long serialVersionUID = 1L;
    public TaskIdentifier(String id) {
      super(id);
    }
  }
  
}

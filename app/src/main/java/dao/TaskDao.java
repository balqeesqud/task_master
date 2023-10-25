package dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import model.Task;

@Dao
public interface TaskDao {

    @Insert
    public void insertTask(Task task);

    @Query("select * from Task")
    public List<Task> findAll();


    @Query("select * from Task ORDER BY title" )
    public List<Task> findAllSortedByTitle();

    @Query("select * from Task where id=:id")
    Task findById(Long id);


    // To count the number of tasks
    @Query("SELECT COUNT(*) FROM Task")
    int getTaskCount();
}

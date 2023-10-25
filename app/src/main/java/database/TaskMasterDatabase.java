package database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import dao.TaskDao;
import model.Task;

@Database(entities = {Task.class}, version = 2)
@TypeConverters({TaskMasterDatabaseConverters.class})
public abstract class TaskMasterDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();
}

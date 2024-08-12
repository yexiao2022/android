package com.lh.painting.dao;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.lh.painting.entity.Favorite;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM collection")
    List<Favorite> getAllTasks();
    @Query("SELECT * FROM collection WHERE picture = :picture")
    Favorite getTaskByUrl(String picture);
    @Query("DELETE FROM collection WHERE picture = :picture")
    void deleteTaskByUrl(String picture);
    @Insert
    void insertTask(Favorite task);
    @Update
    void updateTask(Favorite task);
}

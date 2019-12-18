package com.wnp.passwdmanager.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PasswordDao {

    @Insert
    void insertAll(PasswordEntity... entities);

    @Insert
    void insert(PasswordEntity entity);

    @Query("SELECT * FROM passwordList")
    LiveData<List<PasswordEntity>> getAllEntities();

    @Update
    void update(PasswordEntity entity);

    @Delete
    void delete(PasswordEntity entity);

}

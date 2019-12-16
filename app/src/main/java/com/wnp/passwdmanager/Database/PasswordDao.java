package com.wnp.passwdmanager.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PasswordDao {

    @Insert
    void insertAll(PasswordEntity... entities);

    @Insert
    void insert(PasswordEntity entity);

    @Query("SELECT * FROM passwordList")
    List<PasswordEntity> getAllEntities();

    @Delete
    void delete(PasswordEntity entity);
}

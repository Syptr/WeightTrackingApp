package com.learn.weighttrackerapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users ORDER BY username")
    public List<User> getUsers();

    @Insert
    public void insertUser(User user);

    @Delete
    public void deleteUser(User user);
}

package com.learn.weighttrackerapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface WeightDao {

    @Query("SELECT * FROM weights WHERE username = :username ORDER BY date ASC")
    public List<Weight> getDailyWeightsOfUser(String username);

    @Query("SELECT * FROM weights WHERE username = :username AND date = :date LIMIT 1")
    public Weight getRecordWithDate(String username, Date date);

    @Query("UPDATE weights SET weight = :newWeight WHERE username = :username AND date = :date")
    void updateDailyWeight(String username, Date date, double newWeight);

    @Query("DELETE FROM weights WHERE username = :username AND  date = :date")
    void deleteUserWeight(String username, Date date);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public void insertWeight(Weight weight);

    @Update
    public void updateWeight(Weight weight);

    @Delete
    public void deleteWeight(Weight weight);
}
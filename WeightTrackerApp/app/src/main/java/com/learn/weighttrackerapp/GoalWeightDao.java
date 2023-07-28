package com.learn.weighttrackerapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GoalWeightDao {

    @Query("SELECT * FROM goalWeight WHERE username = :username ORDER BY id")
    public List<GoalWeight> getAllUserGoalWeights(String username);

    @Query("SELECT * FROM goalWeight WHERE username = :username")
    public GoalWeight getSingleGoalWeight(String username);

    @Query("UPDATE goalWeight SET goalWeight = :goalWeight WHERE username = :username")
    public void setGoalWeight(double goalWeight, String username);

    @Query("SELECT count(*) FROM goalWeight WHERE username = :username")
    public int countGoalEntries(String username);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public void insertGoalWeight(GoalWeight goalWeight);
}

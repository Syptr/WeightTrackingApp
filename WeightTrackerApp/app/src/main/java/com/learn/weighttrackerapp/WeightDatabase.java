package com.learn.weighttrackerapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {User.class, Weight.class, GoalWeight.class}, version = 1)
@TypeConverters({Converter.class})
public abstract class WeightDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "weightTracker.DB";

    private static WeightDatabase mWeightTrackerDatabase;

    // Singleton pattern
    public static WeightDatabase getInstance(Context context) {
        if (mWeightTrackerDatabase == null) {
            mWeightTrackerDatabase = Room.databaseBuilder(context, WeightDatabase.class,
                    DATABASE_NAME).allowMainThreadQueries().build();
        }
        return mWeightTrackerDatabase;
    }

    public abstract UserDao userDao();
    public abstract WeightDao weightDao();
    public abstract GoalWeightDao goalWeightDao();
}
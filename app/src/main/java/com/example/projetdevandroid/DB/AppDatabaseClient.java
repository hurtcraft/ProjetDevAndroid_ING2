package com.example.projetdevandroid.DB;

import android.content.Context;
import androidx.room.Room;

import com.example.projetdevandroid.DB.AppDatabase;

public class AppDatabaseClient {

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "people.db"
            ).allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
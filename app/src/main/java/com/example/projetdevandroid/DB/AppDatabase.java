package com.example.projetdevandroid.DB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.projetdevandroid.Dao.PersonDao;
import com.example.projetdevandroid.entity.Person;

@Database(entities = {Person.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PersonDao personDao();
}
package com.example.projetdevandroid.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.projetdevandroid.entity.Person;

import java.util.List;

@Dao
public interface PersonDao {

    @Insert
    void insert(Person person);

    @Query("SELECT * FROM people")
    List<Person> getAll();

    @Delete
    void delete(Person person);
}
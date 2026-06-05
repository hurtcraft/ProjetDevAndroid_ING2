package com.example.projetdevandroid.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.projetdevandroid.entity.PersonEntity;

import java.util.List;

@Dao
public interface PersonDao {

    @Insert
    void insert(PersonEntity person);

    @Delete
    void delete(PersonEntity person);

    @Query("SELECT * FROM people")
    List<PersonEntity> getAll();
}
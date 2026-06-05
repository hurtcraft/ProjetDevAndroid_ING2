package com.example.projetdevandroid.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "people")
public class Person {

        @PrimaryKey(autoGenerate = true)
        public int id;

        public String name;
        public int age;

        public Person(String name, int age) {
                this.name = name;
                this.age = age;
        }
}
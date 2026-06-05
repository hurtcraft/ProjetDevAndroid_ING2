package com.example.projetdevandroid;

import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.fragment.app.Fragment;


import com.example.projetdevandroid.DB.AppDatabase;
import com.example.projetdevandroid.DB.AppDatabaseClient;
import com.example.projetdevandroid.Dao.PersonDao;
import com.example.projetdevandroid.entity.PersonEntity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PeopleFragment extends Fragment {

    PersonDao dao;
    FirebaseFirestore db;

    List<PersonEntity> localList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_people, container, false);
        AppDatabase database = AppDatabaseClient.getInstance(getContext());
        dao = database.personDao();
        db = FirebaseFirestore.getInstance();

        Button btnAdd = view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> showAddDialog());

        loadData();
        return view;
    }


    private void showAddDialog() {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_person, null);

        EditText edtName = dialogView.findViewById(R.id.edtName);
        EditText edtAge = dialogView.findViewById(R.id.edtAge);

        new android.app.AlertDialog.Builder(getContext())
                .setTitle(R.string.add_person)
                .setView(dialogView)
                .setPositiveButton(R.string.add, (d, w) -> {

                    String name = edtName.getText().toString();
                    int age = Integer.parseInt(edtAge.getText().toString());

                    // 🟢 LOCAL
                    PersonEntity local = new PersonEntity(name, age);
                    dao.insert(local);

                    // ☁️ CLOUD
                    db.collection("people")
                            .add(new Person(name, age));

                    loadData();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }


    private void loadData() {

        db.collection("people")
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<Person> cloudList = new ArrayList<>();

                    for (DocumentSnapshot doc : snapshot) {
                        Person p = doc.toObject(Person.class);
                        cloudList.add(p);
                    }

                })
                .addOnFailureListener(e -> {

                    // fallback offline
                    localList = dao.getAll();

                    // update UI avec localList
                });
    }

    private void deletePerson(PersonEntity person) {

        dao.delete(person);

        db.collection("people")
                .whereEqualTo("name", person.name)
                .get()
                .addOnSuccessListener(q -> {
                    for (DocumentSnapshot d : q) {
                        d.getReference().delete();
                    }
                });

        loadData();
    }
}
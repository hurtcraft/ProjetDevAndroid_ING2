package com.example.projetdevandroid;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.fragment.app.Fragment;

import com.example.projetdevandroid.DB.AppDatabase;
import com.example.projetdevandroid.DB.AppDatabaseClient;
import com.example.projetdevandroid.Dao.PersonDao;
import com.example.projetdevandroid.PersonAdapter;
import com.example.projetdevandroid.entity.Person;

import java.util.ArrayList;
import java.util.List;

public class PeopleFragment extends Fragment {

    ListView listView;
    Button btnAdd;

    PersonAdapter adapter;
    List<Person> list = new ArrayList<>();

    AppDatabase db;
    PersonDao dao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_people, container, false);

        listView = view.findViewById(R.id.listView);
        btnAdd = view.findViewById(R.id.btnAdd);

        db = AppDatabaseClient.getInstance(getContext());
        dao = db.personDao();

        loadData();

        btnAdd.setOnClickListener(v -> showAddPersonDialog());

        return view;
    }

    private void loadData() {
        list = dao.getAll();

        if (adapter == null) {
            adapter = new PersonAdapter(getContext(), list, dao);
            listView.setAdapter(adapter);
        } else {
            adapter.update(list);
        }
    }


    private void showAddPersonDialog() {

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_person, null);

        EditText edtName = dialogView.findViewById(R.id.edtName);
        EditText edtAge = dialogView.findViewById(R.id.edtAge);

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.add_person)
                .setView(dialogView)
                .setPositiveButton(R.string.add, (dialog, which) -> {

                    String name = edtName.getText().toString();
                    String ageText = edtAge.getText().toString();

                    if (!name.isEmpty() && !ageText.isEmpty()) {

                        int age = Integer.parseInt(ageText);

                        dao.insert(new Person(name, age));
                        loadData();
                    }
                })
                .setNegativeButton("no", null)
                .show();
    }
}
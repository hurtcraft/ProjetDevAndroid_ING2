package com.example.projetdevandroid;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.projetdevandroid.entity.Person;

import java.util.ArrayList;
import java.util.List;

public class ExternalBDFragment extends Fragment {

    ListView listView;
    Button btnAdd;
    PersonAdapter adapter;
    List<Person> list = new ArrayList<>();
    SupabaseClient api = new SupabaseClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_people, container, false);
        listView = view.findViewById(R.id.listView);
        btnAdd = view.findViewById(R.id.btnAdd);
        loadData();
        btnAdd.setOnClickListener(v -> showAddPersonDialog());
        return view;
    }

    private void loadData() {
        new Thread(() -> {
            try {
                List<Person> result = api.getPeople();

                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {

                    list = result;
                    if (adapter == null) {
                        adapter = new PersonAdapter(getContext(), list, null);
                        listView.setAdapter(adapter);
                    } else {
                        adapter.update(list);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void showAddPersonDialog() {

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_person, null);

        EditText edtName = dialogView.findViewById(R.id.edtName);
        EditText edtAge = dialogView.findViewById(R.id.edtAge);
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.add_person)
                .setView(dialogView)
                .setPositiveButton(R.string.add, (dialog, which) -> {

                    String name = edtName.getText().toString().trim();
                    String ageText = edtAge.getText().toString().trim();

                    if (!name.isEmpty() && !ageText.isEmpty()) {

                        int age = Integer.parseInt(ageText);

                        new Thread(() -> {
                            try {
                                api.addPerson(new Person(name, age));

                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(this::loadData);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
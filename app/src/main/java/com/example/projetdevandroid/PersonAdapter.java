package com.example.projetdevandroid;

import android.content.Context;
import android.view.*;
import android.widget.*;

import com.example.projetdevandroid.Dao.PersonDao;
import com.example.projetdevandroid.entity.Person;

import java.util.List;

public class PersonAdapter extends BaseAdapter {

    private Context context;
    private List<Person> list;
    private PersonDao dao;

    public PersonAdapter(Context context, List<Person> list, PersonDao dao) {
        this.context = context;
        this.list = list;
        this.dao = dao;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return list.get(i).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.person_item, parent, false);
        }

        TextView txt = convertView.findViewById(R.id.txtPerson);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);

        Person p = list.get(position);
        txt.setText(p.name + " - " + p.age);

        btnDelete.setOnClickListener(v -> {
            dao.delete(p);
            list.remove(position);
            notifyDataSetChanged();
        });

        return convertView;
    }

    public void update(List<Person> newList) {
        list = newList;
        notifyDataSetChanged();
    }
}
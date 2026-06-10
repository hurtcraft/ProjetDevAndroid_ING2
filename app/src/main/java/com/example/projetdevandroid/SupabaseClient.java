package com.example.projetdevandroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.*;

import com.example.projetdevandroid.entity.Person;

public class SupabaseClient {

    private static final String SUPABASE_URL = "https://yacsfneirmcnzscnfzby.supabase.co";
    private static final String API_KEY = "sb_publishable_CzjDgm-1FH4sv1-tJqUbyA_XW-eSooU";
    private final OkHttpClient client = new OkHttpClient();


    public List<Person> getPeople() throws IOException, JSONException {
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/people?select=*")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful() || response.body() == null) {
                return new ArrayList<>();
            }

            String jsonData = response.body().string();
            JSONArray array = new JSONArray(jsonData);

            List<Person> list = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                Person p = new Person(
                        obj.getString("name"),
                        obj.getInt("age")
                );
                list.add(p);
            }
            return list;
        }
    }

    public void addPerson(Person person) throws IOException, JSONException {

        JSONObject json = new JSONObject();
        json.put("name", person.name);
        json.put("age", person.age);

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/people")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {

        }
    }
}
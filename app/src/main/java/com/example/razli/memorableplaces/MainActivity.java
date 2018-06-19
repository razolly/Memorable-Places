package com.example.razli.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;

    static ArrayList<String> listOfPlaces;
    static ArrayList<LatLng> listOfCoordinates;
    static ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.razli.memorableplaces", Context.MODE_PRIVATE);

        ArrayList<String> latitudes = new ArrayList<String>();
        ArrayList<String> longitudes = new ArrayList<String>();

        listOfPlaces = new ArrayList<String>();
//        listOfPlaces.add("Add a new location...");
        listOfCoordinates = new ArrayList<LatLng>();
//        listOfCoordinates.add(new LatLng(0,0));

        // Clear Arraylists before taking values from "database"
        listOfPlaces.clear();
        listOfCoordinates.clear();
        latitudes.clear();
        longitudes.clear();

        // GETS values from "database" ie. SharedPreferences
        try {
            listOfPlaces = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("listOfPlaces", ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("latitudes", ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longitudes", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Stores values from "database" into Arraylists
        if(listOfPlaces.size() > 0 && latitudes.size() > 0 && longitudes.size() > 0) {
            if(listOfPlaces.size() == latitudes.size() && listOfPlaces.size() == longitudes.size()) {
                for(int i = 0; i < listOfPlaces.size(); i++) {
                    listOfCoordinates.add(new LatLng(Double.parseDouble(latitudes.get(i)), Double.parseDouble(longitudes.get(i))));
                }
            }
        } else {
            listOfPlaces.add("Add a new location...");
            listOfCoordinates.add(new LatLng(0,0));
        }

        listView = findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listOfPlaces);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("placeIndex", position);
                startActivity(intent);
            }
        });
    }
}

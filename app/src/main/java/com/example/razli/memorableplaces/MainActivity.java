package com.example.razli.memorableplaces;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

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

        listView = findViewById(R.id.listView);

        listOfPlaces = new ArrayList<String>();
        listOfCoordinates = new ArrayList<LatLng>();

        listOfPlaces.add("Add a new location...");
        listOfCoordinates.add(new LatLng(0,0));

//        if(getIntent() != null) {
//            listOfPlaces.add(getIntent().getStringExtra("locationName"))
//        }

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

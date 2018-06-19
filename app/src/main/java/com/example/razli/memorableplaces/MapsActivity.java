package com.example.razli.memorableplaces;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;

    final int ZOOM_LEVEL = 13;
    private static final String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Pasir Ris, Singapore and move the camera
        LatLng currentLocation = new LatLng(1.3742686, 103.944687);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Home"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, ZOOM_LEVEL));

        // Listens for long clicks
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                // Get name of location using GeoCoder
                String locationName = getLocationName(latLng.latitude, latLng.longitude);

                // If location has no name, put time and date
                if(locationName.equals("")){
                    SimpleDateFormat date = new SimpleDateFormat("HH:mm yyyy-MM-dd");
                    locationName += date.format(new Date());
                }

                // Add Marker with name from GeoCoder
                mMap.addMarker(new MarkerOptions().position(latLng).title(locationName));
                Log.i(TAG, "onMapLongClick: Location saved: " + locationName);

                // Add to ArrayList in MainActivity and update ListView
                MainActivity.listOfPlaces.add(locationName);
                MainActivity.listOfCoordinates.add(latLng);
                MainActivity.arrayAdapter.notifyDataSetChanged();

                Toast.makeText(MapsActivity.this, "Location Saved!", Toast.LENGTH_SHORT).show();
            }
        });

        // Checks what location user pressed on
        // If 0 (ie. Add new place...), map zooms into last known location
        // If NOT 0, zooms into location of that place
        if(getIntent().getIntExtra("placeIndex", 0) == 0) {
            // Listens for change of location
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //mMap.clear();
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, ZOOM_LEVEL));
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            // Ask user for permission to access location AND GETS LAST KNOWN LOCATION
            if (Build.VERSION.SDK_INT < 23) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                } else {
                    // Gets last known location
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    mMap.clear();
                    LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                }
            }

        } else {
            LatLng latLngOfChosenPlace = MainActivity.listOfCoordinates.get(getIntent().getIntExtra("placeIndex", 0));

            mMap.addMarker(new MarkerOptions().position(latLngOfChosenPlace).title("Your Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOfChosenPlace, ZOOM_LEVEL));
        }
    }

    public String getLocationName(double latitude, double longitude) {

        String locationName = "";

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> locationDetails = geocoder.getFromLocation(latitude, longitude, 1);

            // Value of List<Address>
            // 06-19 05:35:28.934 4855-4855/com.example.razli.memorableplaces I/MapsActivity: getLocationName: [Address[addressLines=[0:"29A Tampines Ave 1, Singapore 529767"],
            // feature=29A,admin=null,sub-admin=null,locality=Singapore,thoroughfare=Tampines Avenue 1,
            // postalCode=529767,countryCode=SG,countryName=Singapore,hasLatitude=true,latitude=1.3452868999999998,hasLongitude=true,longitude=103.93167199999999,phone=null,url=null,extras=null]]

            locationName += locationDetails.get(0).getAddressLine(0);

            return locationName;

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Checks what user chose when asked if permission granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }
}

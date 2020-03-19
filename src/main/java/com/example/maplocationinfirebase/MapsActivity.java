package com.example.maplocationinfirebase;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.example.maplocationinfirebase.model.MyLocation;
import com.example.maplocationinfirebase.repo.FirebaseRepo;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationListener listener;  // listens for location updates
    LocationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        manager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE); // gets from device
        createListener();
        // handle permissions
        // check first, if we have already the permission
        handlePermissionUpdate();
    }

    private void handlePermissionUpdate() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1); //ask for p.
        }else {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener); // start listening to updates
        }
    }

    private void createListener() {
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //  Log.i("all", "new location " + location);
                // move (map) camera to this location
                addMarker(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            handlePermissionUpdate();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // wait to start Firebase listener, until we have a map!
        FirebaseRepo.setMapsActivity(this);  // this will start the FB listener
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.i("all", "long press " + latLng);
                // 1. call our FirebaseRepo class, and ask for add method
                FirebaseRepo.addMarker(latLng.latitude + "", latLng.longitude + "");
            }
        });

    }

    private void addMarker(double lat, double lon) {
        // Add a marker in Sydney and move the camera
        LatLng marker = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(marker).title("Marker"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
    }

    public void updateMarkers(){  // call this method from FirebaseRepo, when the listener gets data.
        mMap.clear();
        for(MyLocation location : FirebaseRepo.locations){
            addMarker(location.getLat(), location.getLon());
        }
    }
}
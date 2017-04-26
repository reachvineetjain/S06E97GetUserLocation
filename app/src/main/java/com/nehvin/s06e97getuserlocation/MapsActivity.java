package com.nehvin.s06e97getuserlocation;

import android.Manifest;
import android.content.Context;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locMgr;
    private LocationListener locListner;
    private Location loc;
    private double latitude;
    private double longitude;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1 && permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }

    private void startListening() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locListner);
        }
        Location lastUnkownLocation = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastUnkownLocation != null) {
            LatLng currentLocation = new LatLng(lastUnkownLocation.getLatitude(), lastUnkownLocation.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,5));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        loc = new Location(LocationManager.GPS_PROVIDER);
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

//        mMap.setMyLocationEnabled(true);
        locMgr = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locListner = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (location != null)
                {
                    Log.i("Location : ", location.toString());
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 5));

                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> address = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                    if(address != null && address.size()>0)
                    {
                        Log.i("Place",address.get(0).toString());
                        StringBuilder addressBuild = new StringBuilder();
                        addressBuild.append("Address Line 1: ").append(address.get(0).getAddressLine(0)).append("\n");
                        addressBuild.append("Address Line 2: ").append(address.get(0).getAddressLine(1)).append("\n");
                        addressBuild.append("Address Line 3: ").append(address.get(0).getAddressLine(2)).append("\n");
                        addressBuild.append("Address Line 4: ").append(address.get(0).getAddressLine(3)).append("\n");
                        Toast.makeText(MapsActivity.this, addressBuild.toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                }
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

        if(Build.VERSION.SDK_INT < 23)
        {
            locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locListner);
        }
        else
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else
            {
                locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locListner);
                Location lastUnkownLocation = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastUnkownLocation != null) {
                    LatLng currentLocation = new LatLng(lastUnkownLocation.getLatitude(), lastUnkownLocation.getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,5));
                }
            }
        }
    }
}
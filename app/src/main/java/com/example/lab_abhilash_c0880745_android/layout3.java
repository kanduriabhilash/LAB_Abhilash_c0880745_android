package com.example.lab_abhilash_c0880745_android;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;


import java.io.IOException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class AddPlaceActivity extends FragmentActivity implements OnMapReadyCallback {

    AddPlaceActivityBinding binding = null;

    private GoogleMap mMap;

    private static final int REQUEST_CODE = 1;
    private Marker homeMarker;
    private Marker destMarker;

    Polyline line;
    Polygon shape;

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(binding == null){
            binding = AddPlaceActivityBinding.inflate(getLayoutInflater());
        }

        setContentView(binding.root());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setHomeMarker(location);
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

        if (!hasLocationPermission())
            requestLocationPermission();
        else
            startUpdateLocation();

        // apply long press gesture
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                try {
                    setMarker(latLng);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            private void setMarker(LatLng latLng) throws IOException {
                String address = getAddress(latLng);
                MarkerOptions options = new MarkerOptions().position(latLng)
                        .title(address);

                mMap.addMarker(options);
            }

        });



        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddPlaceActivity.this);
                builder.setTitle("Do you want to add this to Favourite place list ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    Double markerLong = marker.getPosition().longitude;
                    Double markerLat= marker.getPosition().latitude;
                    try {
                        favPlaceData(markerLat, markerLong, FavPlaceViewModel);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Toast.makeText(AddPlaceActivity.this,  " added ", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                });
                builder.setNegativeButton("no", (dialog, which) -> {

                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

    }


    private void setHomeMarker(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userLocation)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("Your Location");
        homeMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);

        /*Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        setHomeMarker(lastKnownLocation);*/
    }


    public void favPlaceData(Double latitude, Double longitude, Object FavPlaceViewModel) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String name = addresses.get(0).getLocality();
        Date currentTime = Calendar.getInstance().getTime();

        if(name.equals("")){
            name = String.valueOf(currentTime);
        }

        String postal_code = addresses.get(0).getPostalCode();
        String country = addresses.get(0).getCountryName();
        // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        FavPlaces favPlace = new FavPlaces(name, postal_code, country);
        FavPlaceViewModel.wait(favPlace);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
        }
    }

    public String getAddress(LatLng latLng) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        return address;
    }


    private static class AddPlaceActivityBinding {
    }

    private class FavPlaces {
    }
}



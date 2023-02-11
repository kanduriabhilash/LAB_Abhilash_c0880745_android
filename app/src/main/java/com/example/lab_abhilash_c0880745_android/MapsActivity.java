package com.example.lab_abhilash_c0880745_android;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.lab_abhilash_c0880745_android.databinding.ActivityMapsBinding;
import com.google.android.material.snackbar.Snackbar;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Add a marker in Sydney and move the camera
       /* LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        LatLng mylocation=new LatLng(43.77274419346524, -79.33350264142315);
        mMap.addMarker(new MarkerOptions().position(mylocation).title("my location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));

        drawCircle(new LatLng(43.77274419346524, -79.33350264142315));

    }

    private void drawCircle(LatLng latLng)
    {
        CircleOptions myoptions=new CircleOptions();
        myoptions.center(latLng);
        myoptions.radius(150);
        myoptions.strokeColor(Color.BLUE);
        myoptions.strokeWidth(3);
        mMap.addCircle(myoptions);

    }

    private void updateUI() {
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Employees Menu Selected", Toast.LENGTH_SHORT).show();
    }

    private void deletePlace(int position) {
        Myplace favPlace = listofPlaces.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Are you sure?");
        RecyclerView.Adapter<RecyclerView.ViewHolder> recyclerViewAdapter = null;
        builder.setPositiveButton("Yes", (dialog, which) -> {
            deletedPlace = favPlace;
            FavPlaceViewModel.deleteList(position);
            recyclerViewAdapter.notifyItemRemoved(position);
            Snackbar.make(recyclerView, deletedPlace.getName() + " is deleted!", Snackbar.LENGTH_LONG)
                    .setAction("Undo", v -> FavPlaceViewModel.setList(deletedPlace)).show();
            Toast.makeText(MapsActivity.this, deletedPlace.getName() + " deleted", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (dialog, which) -> recyclerViewAdapter.notifyItemChanged(position));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private static class FavPlaceViewModel {
        public static void deleteList(int position) {

        }
    }
}


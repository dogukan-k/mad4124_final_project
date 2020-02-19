package com.dku.dogukankarayilanoglu_mad4124_fp.Controller;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.dku.dogukankarayilanoglu_mad4124_fp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowOnMapsActivityController extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    String incomingLat;
    String incomingLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_on_maps_controller);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        incomingLat = getIntent().getExtras().getString("lat");
        incomingLng = getIntent().getExtras().getString("lng");
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


        LatLng location = new LatLng(Double.parseDouble(incomingLat),Double.parseDouble(incomingLng));
        mMap.addMarker(new MarkerOptions().position(location).title("Note Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.setMinZoomPreference(15);
    }
}

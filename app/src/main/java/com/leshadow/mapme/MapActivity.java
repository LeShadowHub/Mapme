package com.leshadow.mapme;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Random;

/**
 * The MapActivity allows user to view the location of an image once they
 * have selected from Photos or Gallery in StoreImageActivity
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Random rand = new Random();
    LatLng pos;
    ArrayList<LatLng> locs = new ArrayList<LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // For single photo selection
        //Bundle bundle = getIntent().getParcelableExtra("bundle");
        //pos = bundle.getParcelable("pos");

        locs = getIntent().getParcelableArrayListExtra("allPos");
    }

    /**
    * This is where we can add markers or lines, add listeners or move the camera. In this case,
    * we
    * just add a marker near Africa.
     */

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
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // For multiple markers
        for(int i = 0; i < locs.size(); i++){
            mMap.addMarker(new MarkerOptions().position(locs.get(i)).icon(BitmapDescriptorFactory.defaultMarker(rand.nextInt(359)+1)));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(locs.get(locs.size()-1)));
    }
}

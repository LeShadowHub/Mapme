package com.leshadow.mapme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ImageInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    CardModel card;
    private GoogleMap mMap;
    LatLng pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_info);
        getSupportActionBar().hide();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_frag);
        mapFragment.getMapAsync(this);

        card = (CardModel)getIntent().getSerializableExtra("CardObj");
        ImageView cover_image = (ImageView)findViewById(R.id.cover_image);
        TextView image_title = (TextView)findViewById(R.id.image_title);
        TextView image_details = (TextView)findViewById(R.id.image_details);

        pos = new LatLng(card.getLat(), card.getLon());

        Glide.with(this).load(card.getImage()).into(cover_image);
        image_title.setText(card.getTitle());
        image_details.setText(card.getDesc());
    }

    @Override
    public void onMapReady(GoogleMap map){
        mMap = map;

        if(card.getLat() == 0 && card.getLon() == 0){
            Toast.makeText(ImageInfoActivity.this, "Unable to locate image location", Toast.LENGTH_LONG).show();
        } else{
            mMap.addMarker(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.defaultMarker()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        }

    }
}

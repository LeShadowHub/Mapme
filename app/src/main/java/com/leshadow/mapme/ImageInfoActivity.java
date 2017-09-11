package com.leshadow.mapme;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.ion.Ion;

import java.util.NoSuchElementException;

public class ImageInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    CardModel card;
    private GoogleMap mMap;
    LatLng pos;
    boolean isExpanded = true;
    Point size = new Point();
    int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_info);
        //getSupportActionBar().hide();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_frag);
        mapFragment.getMapAsync(this);

        card = (CardModel)getIntent().getSerializableExtra("CardObj");
        final ImageView cover_image = (ImageView)findViewById(R.id.cover_image);
        final TextView image_title = (TextView)findViewById(R.id.image_title);
        final TextView image_details = (TextView)findViewById(R.id.image_details);
        final Button btnExpand = (Button)findViewById(R.id.btnExpand);
        final Button btnCollapse = (Button)findViewById(R.id.btnCollapse);
        final CardView cardView = (CardView)findViewById(R.id.card_view);
        final View viewExpand = findViewById(R.id.viewExpand);
        final View viewCollapse = findViewById(R.id.viewCollapse);

        viewExpand.bringToFront();
        viewCollapse.bringToFront();

        pos = new LatLng(card.getLat(), card.getLon());

        Glide.with(this)
                .load(card.getImage())
                //.centerCrop()
                .into(cover_image);

        image_title.setText(card.getTitle());
        image_details.setText(card.getDesc());

        //Getting height of the screen
        Display display = getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(size);
            height = size.y;
            height = height/2;
        //} else{
            height = display.getHeight();
        }


        btnCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isExpanded){
                    RelativeLayout mapLayout = (RelativeLayout)findViewById(R.id.mapLayout);
                    cardView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up));

                    //mapLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_in));
                    //Expand map
                    //RelativeLayout.LayoutParams fullMapParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    //mapLayout.setLayoutParams(fullMapParams);
                    isExpanded = false;
                }
            }
        });

        btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isExpanded){
                    RelativeLayout mapLayout = (RelativeLayout)findViewById(R.id.mapLayout);
                    cardView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down));

                    //mapLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_out));
                    //RelativeLayout.LayoutParams defaultMapParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    //defaultMapParams.addRule(RelativeLayout.BELOW, R.id.card_view);
                    //mapLayout.setLayoutParams(defaultMapParams);
                    isExpanded = true;
                }
            }
        });
    }

    private void scaleImage(ImageView view) throws NoSuchElementException {
        // Get bitmap from the the ImageView.
        Bitmap bitmap = null;

        try {
            Drawable drawing = view.getDrawable();
            bitmap = ((BitmapDrawable) drawing).getBitmap();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("No drawable on given view");
        } catch (ClassCastException e) {
            // Check bitmap is Ion drawable
            bitmap = Ion.with(view).getBitmap();
        }

        // Get current dimensions AND the desired bounding box
        int width = 0;

        try {
            width = bitmap.getWidth();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Can't find bitmap on given view/drawable");
        }

        int height = bitmap.getHeight();
        int bounding = dpToPx(250);
        Log.i("Test", "original width = " + Integer.toString(width));
        Log.i("Test", "original height = " + Integer.toString(height));
        Log.i("Test", "bounding = " + Integer.toString(bounding));

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
        Log.i("Test", "xScale = " + Float.toString(xScale));
        Log.i("Test", "yScale = " + Float.toString(yScale));
        Log.i("Test", "scale = " + Float.toString(scale));

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        Log.i("Test", "scaled width = " + Integer.toString(width));
        Log.i("Test", "scaled height = " + Integer.toString(height));

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);

        Log.i("Test", "done");
    }

    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

    @Override
    public void onMapReady(GoogleMap map){
        mMap = map;

        //mMap.setPadding(0,height,0,0);
        //RelativeLayout mapLayout = (RelativeLayout)findViewById(R.id.mapLayout);
        Log.d("HEIGHT", Integer.toString(height));

        if(card.getLat() == 0 && card.getLon() == 0){
            Toast.makeText(ImageInfoActivity.this, "Unable to locate image location", Toast.LENGTH_LONG).show();
        } else{
            mMap.addMarker(new MarkerOptions().position(pos));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));

            //mMap.animateCamera(CameraUpdateFactory.newLatLng(targetPosition), 1000, null);
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            /*CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(pos)
                    .zoom(15)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/

            //mMap.animateCamera(CameraUpdateFactory.newLatLng(pos), 1000, null);
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 1000, null);
        }
    }
}

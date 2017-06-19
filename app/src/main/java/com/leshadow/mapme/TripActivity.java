package com.leshadow.mapme;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class TripActivity extends AppCompatActivity {

    ArrayList<Bitmap> images = new ArrayList<Bitmap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        images = getIntent().getParcelableArrayListExtra("Images");
        addImagesToScrollView();
    }

    public void addImagesToScrollView(){
        LinearLayout imageGallery = (LinearLayout) findViewById(R.id.ImageGallery);
        for(Bitmap image : images){
            imageGallery.addView(getImageView(image));
        }
    }

    public View getImageView(Bitmap image){
        ImageView imageView = new ImageView((getApplicationContext()));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(1,0,1,10);
        imageView.setLayoutParams(lp);
        imageView.setImageBitmap(image);
        return imageView;
    }



}

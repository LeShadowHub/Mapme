package com.leshadow.mapme;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.solver.SolverVariable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * The TripActivity is testing custom implementation of image gallery
 * Not used in MapMe
 */
public class TripActivity extends AppCompatActivity {

    public static final String IMAGES_FILE = "SavedImages";
    ArrayList<Uri> images = new ArrayList<Uri>();
    String[] imageUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        ListView imageGallery = (ListView) findViewById(R.id.ImageGallery);

        // Retrieve previously selected images
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriDeserializer())
                .create();
        SharedPreferences prefs = getSharedPreferences(IMAGES_FILE, Context.MODE_PRIVATE);
        String jsonText = prefs.getString(IMAGES_FILE, null);
        images = gson.fromJson(jsonText, new TypeToken<ArrayList<Uri>>(){}.getType());


        try{
            Log.d("Saved Image Uri form", images.toString());
            imageUrls = new String[images.size()];

            //Log.d("Check", images.toString());
            for(int i = 0; i < images.size(); i++){
                imageUrls[i] = images.get(i).toString();
            }
            Log.d("Saved Image String form", imageUrls.toString());

            imageGallery.setAdapter(new ImageListAdapter(TripActivity.this, imageUrls));
        } catch(Exception e){
            e.printStackTrace();
        }

        try{
            images = getIntent().getParcelableArrayListExtra("Images");
            Log.d("Original Uri", images.toString());
            imageUrls = new String[images.size()];

            for(int i = 0; i < images.size(); i++){
                imageUrls[i] = images.get(i).toString();
            }
            Log.d("Original String form", imageUrls.toString());

            imageGallery.setAdapter(new ImageListAdapter(TripActivity.this, imageUrls));
        } catch(Exception e){
            e.printStackTrace();
        }

        //images = getIntent().getParcelableArrayListExtra("Images");
        //addImagesToScrollView();
    }

    protected void onStop(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriSerializer())
                .create();
        String jsonText = gson.toJson(images);
        SharedPreferences prefs = getSharedPreferences(IMAGES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(IMAGES_FILE, jsonText);
        editor.apply();
        super.onStop();
    }

    public class UriSerializer implements JsonSerializer<Uri> {
        public JsonElement serialize(Uri src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    public class UriDeserializer implements JsonDeserializer<Uri>{
        @Override
        public Uri deserialize(final JsonElement src, final Type srcType,
                               final JsonDeserializationContext context) throws JsonParseException{
            return Uri.parse(src.toString());
        }
    }


    public class ImageListAdapter extends ArrayAdapter{
        private Context context;
        private LayoutInflater inflater;

        private String[] imageUrls;

        public ImageListAdapter(Context context, String[] imageUrls){
            super(context, R.layout.listview_item, imageUrls);
            this.context = context;
            this.imageUrls = imageUrls;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(null == convertView){
                convertView = inflater.inflate(R.layout.listview_item, parent, false);
            }
            Picasso
                    .with(context)
                    .load(imageUrls[position])
                    .fit()
                    .into((ImageView) convertView);

            return convertView;
        }
    }


    public void addImagesToScrollView(){
        ListView imageGallery = (ListView) findViewById(R.id.ImageGallery);
        for(int i = 0; i < images.size(); i++){
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), images.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageGallery.addView(getImageView(bitmap));
        }
    }

    public View getImageView(Bitmap image){
        ImageView imageView = new ImageView((getApplicationContext()));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(1, 0, 1, 10);
        imageView.setLayoutParams(lp);
        imageView.setImageBitmap(image);
        return imageView;
    }
}
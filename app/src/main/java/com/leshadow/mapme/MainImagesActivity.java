package com.leshadow.mapme;

import android.content.ClipData;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * The MainImagesActivity is used for testing image EXIF extraction and file paths of internal storage
 * Not used in MapMe
 */
public class MainImagesActivity extends AppCompatActivity {

    public static final int IMAGE_GALLERY_REQUEST = 20;
    private ImageView imgPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_images);
    }

    /**
     * This method is invoked when the user click upload button
     * @param v
     */
    public void onAddClicked(View v) {
        // Invoke the image gallery using implicit intent
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        // Where to find the data
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        // Get a URI representation
        Uri data = Uri.parse(pictureDirectoryPath);

        // Set the data and type. Get all image types
        photoPickerIntent.setDataAndType(data, "image/*");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
            // Invoke this activity and get something back from it
            startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Photo"), IMAGE_GALLERY_REQUEST);
        } else{
            startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);
        }
    }

    public void onSaveClicked(View v){
        Intent intent = new Intent(MainImagesActivity.this, TripActivity.class);
        startActivity(intent);
    }

    /**
     * This method gets the actual file path for image
     * @param contentUri
     * @return
     */
    private String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
            cursor = loader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally{
            if(cursor != null){
                cursor.close();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ArrayList<String> paths = new ArrayList<String>();
        ArrayList<LatLng> locs = new ArrayList<LatLng>();
        ArrayList<Uri> imgUri = new ArrayList<Uri>();
        InputStream inputStream = null;
        float[] latLong = new float[2];

        if(resultCode == RESULT_OK){
            // Everything processed successfully at this point
            if(requestCode == IMAGE_GALLERY_REQUEST){
                // Hearing back from the image gallery at this point

                if(data != null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        ClipData clipData = data.getClipData();
                        if(clipData != null){
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                ClipData.Item item = clipData.getItemAt(i);
                                Uri uri = item.getUri();
                                imgUri.add(uri);

                                /*try {
                                    inputStream = getContentResolver().openInputStream(uri);
                                    android.support.media.ExifInterface exifInterface = new android.support.media.ExifInterface(inputStream);
                                    if(exifInterface.getLatLong(latLong)){
                                        LatLng pos = new LatLng(latLong[0], latLong[1]);
                                        locs.add(pos);
                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }*/
                            }

                            //Intent tripIntent = new Intent(MainImagesActivity.this, TripActivity.class);
                            //tripIntent.putParcelableArrayListExtra("Images", imgUri);
                            //startActivity(tripIntent);

                        } else {
                            Uri imageUri = data.getData();
                            try {
                                inputStream = getContentResolver().openInputStream(imageUri);

                                // working with Exif
                                //String imagePath = getRealPathFromURI(imageUri);
                                android.support.media.ExifInterface exifInterface = new android.support.media.ExifInterface(inputStream);
                                //String lat = exifInterface.getAttribute(android.support.media.ExifInterface.TAG_GPS_LATITUDE);
                                //String lon = exifInterface.getAttribute(android.support.media.ExifInterface.TAG_GPS_LONGITUDE);

                                //EditText latView = (EditText) findViewById(R.id.latNum);
                                //EditText longView = (EditText) findViewById(R.id.longNum);
                                //latView.setText(lat);
                                //longView.setText(lon);

                                if (exifInterface.getLatLong(latLong)) {
                                    //latView.setText(Float.toString(latLong[0]));
                                    //longView.setText(Float.toString(latLong[1]));

                                    LatLng pos = new LatLng(latLong[0], latLong[1]);
                                    locs.add(pos);
                                    Intent posIntent = new Intent(MainImagesActivity.this, MapActivity.class);
                                    posIntent.putParcelableArrayListExtra("allPos", locs);
                                    startActivity(posIntent);
                                }

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                // Show a message to the user indicating the image is unavailable
                                Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                                // Handle any errors
                            } finally {
                                if (inputStream != null) {
                                    try {
                                        inputStream.close();
                                    } catch (IOException ignored) {
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


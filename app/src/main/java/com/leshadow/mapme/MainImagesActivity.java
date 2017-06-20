package com.leshadow.mapme;

import android.content.ClipData;
import android.content.Context;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainImagesActivity extends AppCompatActivity {

    public static final int IMAGE_GALLERY_REQUEST = 20;
    private ImageView pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_images);
        pic = (ImageView) findViewById(R.id.pic);
    }
    /**
     * This method is invoked when the user click upload button
     * @param v
     */
    public void onAddClicked(View v) {
        // Invoke the image gallery using implicit intent
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        // where do we want to find the data
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        // get a URI representation
        Uri data = Uri.parse(pictureDirectoryPath);

        // set the data and type. Get all image types
        photoPickerIntent.setDataAndType(data, "image/*");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
            // we will invoke this activity and get something back from it
            startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Photo"), IMAGE_GALLERY_REQUEST);
        } else{
            startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);
        }
    }

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
        String fileName = "myImage";
        InputStream inputStream = null;
        FileOutputStream fo = null;
        float[] latLong = new float[2];

        if(resultCode == RESULT_OK){
            // if we are here, everything processed successfully.
            if(requestCode == IMAGE_GALLERY_REQUEST){
                // if we are here, we are hearing back from the image gallery

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
                                    //android.support.media.ExifInterface exifInterface = new android.support.media.ExifInterface(inputStream);
                                    //if(exifInterface.getLatLong(latLong)){
                                    //    LatLng pos = new LatLng(latLong[0], latLong[1]);
                                    //    locs.add(pos);
                                    //}

                                    // Generate image bitmaps and put into arraylist
                                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                    image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                                    fo = openFileOutput(fileName, Context.MODE_PRIVATE);
                                    fo.write(bytes.toByteArray());

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    if (inputStream != null) {
                                        try {
                                            inputStream.close();
                                        } catch (IOException ignored) {
                                        }
                                    }
                                    if(fo != null){
                                        try {
                                            fo.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }*/
                            }

                            //Testing single image
                            /*Bitmap bitmap = null;
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri.get(0));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            pic.setImageBitmap(bitmap);*/
                            //pic.setImageBitmap(images.get(0));

                            //Intent posIntent = new Intent(MainImagesActivity.this, MapActivity.class);
                            //posIntent.putParcelableArrayListExtra("allPos", locs);
                            //startActivity(posIntent);


                            Intent tripIntent = new Intent(MainImagesActivity.this, TripActivity.class);
                            tripIntent.putParcelableArrayListExtra("Images", imgUri);
                            startActivity(tripIntent);

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
                                    //startActivity(posIntent);
                                }

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                // show a message to the user indicating the image is unavailable
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


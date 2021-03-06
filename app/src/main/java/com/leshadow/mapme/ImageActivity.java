package com.leshadow.mapme;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * The ImageActivity allows user to select an image and extracts the EXIF data from the image
 */
public class ImageActivity extends AppCompatActivity {

    public static final int IMAGE_GALLERY_REQUEST = 20;
    private ImageView imgPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        // Get a reference to the image view that holds the image that the user will see
        imgPicture = (ImageView) findViewById(R.id.imgPicture);


    }

    /**
     * This method is invoked when the user click upload button
     * @param v
     */
    public void onUploadClicked(View v) {
        // Invoke the image gallery using implicit intent
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        // Where to find the data
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        // Get a URI representation
        Uri data = Uri.parse(pictureDirectoryPath);

        // Set the data and type. Get all image types
        photoPickerIntent.setDataAndType(data, "image/*");

        // Invoke this activity and get something back from it
        startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);

    }

    /**
     * This method retrieves the file path of an image
     * @param contentUri
     * @return
     */
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            // Everything processed successfully.
            if(requestCode == IMAGE_GALLERY_REQUEST){
                // Hearing back from the image gallery

                // The address of the image on the SD Card
                Uri imageUri = data.getData();

                // Declare a stream to read the image data from the SD Card
                InputStream inputStream = null;

                // Getting an input stream, based on the URI of the image
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);

                    // Get a bitmap from the stream
                    //Bitmap image = BitmapFactory.decodeStream(inputStream);

                    // Show the image to the user
                    //imgPicture.setImageBitmap(image);

                    // Working with Exif
                    //String imagePath = getRealPathFromURI(imageUri);
                    ExifInterface exifInterface = new ExifInterface(inputStream);
                    //String lat = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                    //String lon = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);

                    float[] latLong = new float[2];

                    EditText latView = (EditText) findViewById(R.id.latNum);
                    EditText longView = (EditText) findViewById(R.id.longNum);
                    //latView.setText(lat);
                    //longView.setText(lon);


                    if(exifInterface.getLatLong(latLong)){
                        latView.setText(Float.toString(latLong[0]));
                        longView.setText(Float.toString(latLong[1]));

                        // Bundle latlon and send to mapActivity
                        LatLng pos = new LatLng(latLong[0], latLong[1]);
                        Bundle args = new Bundle();
                        args.putParcelable("pos", pos);
                        Intent posIntent = new Intent(ImageActivity.this, MapActivity.class);
                        posIntent.putExtra("bundle", args);
                        startActivity(posIntent);

                    } else{
                        latView.setText("2");
                        longView.setText("2");
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // Show a message to the user indicating the image is unavailable
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    // Handle any errors
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException ignored) {}
                    }
                }
            }
        }
    }
}

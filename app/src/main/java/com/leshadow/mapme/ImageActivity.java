package com.leshadow.mapme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImageActivity extends AppCompatActivity {

    public static final int IMAGE_GALLERY_REQUEST = 20;
    private ImageView imgPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        // gt a reference to the image view that holds the image that the user will see
        imgPicture = (ImageView) findViewById(R.id.imgPicture);


    }

    /**
     * This method is invoked when the user click upload button
     * @param v
     */
    public void onUploadClicked(View v) {
        // Invoke the image gallery using implicit intent
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        // where do we want to find the data
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        // get a URI representation
        Uri data = Uri.parse(pictureDirectoryPath);

        // set the data and type. Get all image types
        photoPickerIntent.setDataAndType(data, "image/*");

        // we will invoke this activity and get something back from it
        startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            // if we are here, everything processed successfully.
            if(requestCode == IMAGE_GALLERY_REQUEST){
                // if we are here, we are hearing back from the image gallery

                // the address of the image on the SD Card
                Uri imageUri = data.getData();

                // declare a stream to read the image data from the SD Card
                InputStream inputStream;

                // we are getting an input stream, based on the URI of the image
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);

                    // get a bitmap from the stream
                    Bitmap image = BitmapFactory.decodeStream(inputStream);

                    // show the image to the user
                    imgPicture.setImageBitmap(image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // show a message to the user indicating the image is unavailable
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}

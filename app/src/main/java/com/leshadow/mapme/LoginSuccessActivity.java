package com.leshadow.mapme;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;

public class LoginSuccessActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 100;
    ImageView dpImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);

        //To hide AppBar
        ActionBar ab = getSupportActionBar();
        ab.hide();

        TextView txtname = (TextView) findViewById(R.id.txt_success_name);
        TextView txtemail = (TextView) findViewById(R.id.txt_success_email);
        Button btnlogout = (Button) findViewById(R.id.btn_logout);
        dpImage = (ImageView) findViewById(R.id.imgclick);

        Intent intent = getIntent();

        String loginName = intent.getStringExtra("fullname");
        String loginEmail = intent.getStringExtra("email");
        txtname.setText("Welcome, " +loginName);
        txtemail.setText(loginEmail);
        
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(LoginSuccessActivity.this);
                builder.setTitle("Info");
                builder.setMessage("Do you want to logout ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(LoginSuccessActivity.this, LoginAccountActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //Section for Changing Display Image when Clicked
        dpImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });
    }

    //Method call when user picks an image from ImagePicker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent){
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode){
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    Bitmap yourSelectedImage = null;
                    try{
                        yourSelectedImage = decodeUri(selectedImage);
                    } catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                    dpImage.setImageBitmap(yourSelectedImage);
                }
        }
    }

    //Method for decoding image for Out of Memory Exception
    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException{
        //Decode Image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        //new size we want to scale to
        final int REQUIRED_SIZE = 140;

        //Find the correct scale value, should be power of 2
        int width_tmp = o.outWidth, height_temp = o.outHeight;
        int scale = 1;
        while(true){
            if(width_tmp / 2 < REQUIRED_SIZE || height_temp / 2 < REQUIRED_SIZE){
                break;
            }
            width_tmp /= 2;
            height_temp /= 2;
            scale *= 2;
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
    }
}

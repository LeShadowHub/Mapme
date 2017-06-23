package com.leshadow.mapme;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LoginSuccessActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 100;
    ImageView dpImage;
    int id;

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
        Button btndelete = (Button) findViewById(R.id.btn_delete);
        dpImage = (ImageView) findViewById(R.id.imgclick);

        Intent intent = getIntent();

        String loginName = intent.getStringExtra("fullname");
        String loginEmail = intent.getStringExtra("email");
        id = intent.getIntExtra("id", 0);
        txtname.setText("Welcome, " + loginName);
        txtemail.setText(loginEmail);

        //Retrieve Profile picture if exist
        String imagePath = getImagePath();
        if(imagePath != null && imagePath.length() != 0){
            Bitmap savedImage = BitmapFactory.decodeFile(imagePath);
            dpImage.setImageBitmap(savedImage);
        }

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(LoginSuccessActivity.this);
                builder.setTitle("Log Out");
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

        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProfile();
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
                    updateProfileImage(yourSelectedImage);
                    dpImage.setImageBitmap(yourSelectedImage);
                }
        }
    }

    //Save image to file in internal storage and store filepath in database
    public void updateProfileImage(Bitmap image){
        SQLiteOpenHelper dbhelper = new SQLiteDBHelper(this);
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        File internalStorage = getApplicationContext().getDir("ProfilePictures", Context.MODE_PRIVATE);
        File imageFilePath = new File(internalStorage, id + ".jpeg");
        String imagePath = imageFilePath.toString();

        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(imageFilePath);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

        } catch (Exception e) {
            Log.i("DATABASE", "Problem updating picture", e);
            imagePath = "";
        }

        ContentValues values = new ContentValues();
        values.put(SQLiteDBHelper.COLUMN_IMAGE, imagePath);

        //Which row to update
        String selection = SQLiteDBHelper.COLUMN_ID + " =?";
        String[] selectionArgs = {String.valueOf(id)};
        db.update(SQLiteDBHelper.TABLE_NAME, values, selection, selectionArgs);

        Toast.makeText(LoginSuccessActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();
    }

    //Retrieve file path from database
    private String getImagePath(){
        SQLiteOpenHelper openHelper = new SQLiteDBHelper(this);
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.query(SQLiteDBHelper.TABLE_NAME,
                null,
                SQLiteDBHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);

        cursor.moveToNext();
        String imagePath = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.COLUMN_IMAGE));
        cursor.close();
        return imagePath;
    }

    public void deleteProfile(){
        String imagePath = getImagePath();
        boolean deleteProfile = false;
        if(imagePath != null && imagePath.length() != 0){
            File imageFilePath = new File(imagePath);
            deleteProfile = imageFilePath.delete();
        }

        //Remove image file path from database
        SQLiteOpenHelper openHelper = new SQLiteDBHelper(this);
        SQLiteDatabase db = openHelper.getWritableDatabase();

        String selection = SQLiteDBHelper.COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(id)};
        db.delete(SQLiteDBHelper.TABLE_NAME, selection, selectionArgs);

        if(deleteProfile){
            Toast.makeText(LoginSuccessActivity.this, "Profile Deleted Successfully", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(LoginSuccessActivity.this, LoginAccountActivity.class);
        startActivity(intent);
    }

    //Method for decoding image for Out of Memory Exception
    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException{
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 140;

        // Find the correct scale value. It should be the power of 2
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
    }
}

package com.leshadow.mapme;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class StoreImageActivity extends AppCompatActivity {
    Button chooseImg, uploadImg, mapImg, viewImg;
    ImageView imgView, imgView2;
    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;
    int num = 1;
    float[] latLong = new float[2];
    float lat = 0;
    float lon = 0;
    ArrayList<LatLng> locs = new ArrayList<LatLng>();
    ProgressDialog pd;
    String username;

    //creating reference to firebase storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
    //StorageReference storageRef = storage.getReferenceFromUrl("gs://projectweb-92615.appspot.com"); //change the url according to your firebase app

    //creating reference to firebase database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_image);
        chooseImg = (Button)findViewById(R.id.chooseImg);
        uploadImg = (Button)findViewById(R.id.uploadImg);
        imgView = (ImageView)findViewById(R.id.imgView);
        mapImg = (Button)findViewById(R.id.mapImg);
        viewImg = (Button)findViewById(R.id.viewImg);

        username = getIntent().getStringExtra("username");
        storageRef = storage.getReference(username + "/Trip1");
        myRef = database.getReference(username + "/Trip1");


        pd = new ProgressDialog(this);
        pd.setTitle("Uploading");

        //Track image number
        /*imageNum.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Integer.class) != null){
                    num = dataSnapshot.getValue(Integer.class);
                    Log.w("NUMBER", dataSnapshot.getValue(Integer.class).toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR", databaseError.toException());
            }
        });*/

        /*myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue(String.class) != null){
                    Log.d("RETURNED", dataSnapshot.getValue(String.class));

                    //Create tempRef and use Glide to download and display image
                    StorageReference httpRef = storage.getReferenceFromUrl(dataSnapshot.getValue(String.class));
                    Glide.with(StoreImageActivity.this)
                            .using(new FirebaseImageLoader())
                            .load(httpRef)
                            .into(imgView2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR", databaseError.toException());
            }
        });*/

        viewImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoreImageActivity.this, UserViewActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        mapImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locs.size() > 0){
                    Intent posIntent = new Intent(StoreImageActivity.this, MapActivity.class);
                    posIntent.putParcelableArrayListExtra("allPos", locs);
                    startActivity(posIntent);
                } else{
                    Toast.makeText(StoreImageActivity.this, "No Latitude and Longitude Found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filePath != null) {

                    pd.show();

                    StorageReference childRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(filePath));

                    //uploading the image
                    UploadTask uploadTask = childRef.putFile(filePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Toast.makeText(StoreImageActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            Log.d("URI", taskSnapshot.getDownloadUrl().toString());

                            CardModel card = new CardModel(taskSnapshot.getDownloadUrl().toString());
                            String uploadId = myRef.push().getKey();
                            card.setKey(uploadId);
                            card.setUsername(username);
                            try {
                                card.setLat(lat);
                                card.setLon(lon);
                            } catch(Exception e){
                                Log.e("ERROR", "No lat or long found");
                                e.printStackTrace();
                            }
                            myRef.child(uploadId).setValue(card);
                            num++;

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(StoreImageActivity.this, "Upload Failed -> " + e, Toast.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            pd.setMessage("Uploaded " + ((int)progress) + "%...");
                        }
                    });

                    /*childRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.e("uri: ", uri.toString());
                            myRef.setValue(uri.toString());
                        }
                    });*/
                }
                else {
                    Toast.makeText(StoreImageActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            //Getting lat and long
            try {
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                ExifInterface exifInterface = new ExifInterface(inputStream);
                if(exifInterface.getLatLong(latLong)){
                    lat = latLong[0];
                    lon = latLong[1];
                }

                if(lat == 0 && lon == 0){
                    Toast.makeText(StoreImageActivity.this, "No Latitude and Longitude Found", Toast.LENGTH_LONG).show();
                } else{
                    LatLng pos = new LatLng(lat, lon);
                    locs.add(pos);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                imgView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}

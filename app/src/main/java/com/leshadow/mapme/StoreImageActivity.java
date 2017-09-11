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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
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
    String myUsername;
    String username;
    String trip;

    boolean firstImage = false;

    //creating reference to firebase storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
    //StorageReference storageRef = storage.getReferenceFromUrl("gs://projectweb-92615.appspot.com"); //change the url according to your firebase app

    //creating reference to firebase database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    DatabaseReference allRef;

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
        myUsername = getIntent().getStringExtra("myUsername");
        trip = getIntent().getStringExtra("trip");

        storageRef = storage.getReference(myUsername + "/" + trip);
        myRef = database.getReference(myUsername + "/" + trip);
        allRef = database.getReference(myUsername + "/AllTrips");

        pd = new ProgressDialog(this);
        pd.setTitle("Uploading");

        //Check for First Image of each trip to upload to AllTrips
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("NUMBER OF IMAGES", Integer.toString((int)dataSnapshot.getChildrenCount()));
                if(dataSnapshot.getChildrenCount() == 0){
                    firstImage = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR", databaseError.toException());
            }
        });


        viewImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoreImageActivity.this, UserViewActivity.class);
                intent.putExtra("myUsername", myUsername);
                intent.putExtra("username", myUsername);
                intent.putExtra("trip", trip);
                startActivity(intent);
                finish();
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

                    //Compress and upload the image
                    Bitmap bmp = null;
                    try {
                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = childRef.putBytes(data);

                    //UploadTask uploadTask = childRef.putFile(filePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Toast.makeText(StoreImageActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            Log.d("URI", taskSnapshot.getDownloadUrl().toString());

                            CardModel card = new CardModel(taskSnapshot.getDownloadUrl().toString());
                            String uploadId = myRef.push().getKey();
                            card.setKey(uploadId);
                            card.setTrip(trip);
                            card.setUsername(myUsername);
                            try {
                                card.setLat(lat);
                                card.setLon(lon);
                            } catch(Exception e){
                                Log.e("ERROR", "No lat or long found");
                                e.printStackTrace();
                            }
                            myRef.child(uploadId).setValue(card);

                            //Place first card into list of main trip view
                            if(firstImage){
                                uploadId = allRef.push().getKey();
                                card.setKey(uploadId);
                                card.setTitle(trip);
                                allRef.child(uploadId).setValue(card);
                                firstImage = false;
                            }

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

    //Getting type of image(.jpg, .png, etc)
    public String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}

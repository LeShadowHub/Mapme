package com.leshadow.mapme;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class InputInfoActivity extends AppCompatActivity {

    //creating reference to firebase storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    //creating reference to firebase database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Kevin/Trip1");
    DatabaseReference imageNum = database.getReference("Kevin/Trip1/image number");
    public String title;
    public String desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_info);

        final EditText etTitle = (EditText)findViewById(R.id.etTitle);
        final EditText etDesc = (EditText)findViewById(R.id.etDesc);
        final Button btnSave = (Button)findViewById(R.id.btnSaveInfo);

        etDesc.setMaxLines(5);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etTitle.getText().toString().trim().length() == 0 || etDesc.getText().toString().trim().length() == 0) {
                    Toast.makeText(InputInfoActivity.this, "Please Fill Out All Fields", Toast.LENGTH_SHORT).show();
                } else{
                    title = etTitle.getText().toString();
                    desc = etDesc.getText().toString();
                }

            }
        });



    }
}

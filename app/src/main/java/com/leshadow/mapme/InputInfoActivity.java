package com.leshadow.mapme;

import android.content.Intent;
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

import java.util.HashMap;
import java.util.Map;

public class InputInfoActivity extends AppCompatActivity {

    //creating reference to firebase storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    //creating reference to firebase database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Kevin/Trip1");

    CardModel card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_info);

        final EditText etTitle = (EditText)findViewById(R.id.etTitle);
        final EditText etDesc = (EditText)findViewById(R.id.etDesc);
        final Button btnSave = (Button)findViewById(R.id.btnSaveInfo);

        etDesc.setMaxLines(5);

        card = (CardModel)getIntent().getSerializableExtra("CardObj");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etTitle.getText().toString().trim().length() == 0 || etDesc.getText().toString().trim().length() == 0) {
                    Toast.makeText(InputInfoActivity.this, "Please Fill Out All Fields", Toast.LENGTH_SHORT).show();
                } else{
                    card.setTitle(etTitle.getText().toString());
                    card.setDesc(etDesc.getText().toString());

                    Map<String, Object> taskMap = new HashMap<String, Object>();
                    //taskMap.put("title", etTitle.getText().toString());
                    //taskMap.put("desc", etDesc.getText().toString());
                    myRef.child(card.getKey()).setValue(card);

                    Intent intent = new Intent(InputInfoActivity.this, UserViewActivity.class);
                    startActivity(intent);
                }

            }
        });



    }
}

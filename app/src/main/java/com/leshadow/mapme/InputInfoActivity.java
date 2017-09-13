package com.leshadow.mapme;

import android.content.Intent;
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

/**
 * The InputInfoActivity allows user to add title and description to individual images
 */
public class InputInfoActivity extends AppCompatActivity {

    // Creating reference to firebase storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    // Creating reference to firebase database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

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
        myRef = database.getReference(card.getUsername() + "/" + card.getTrip());

        // Saves the Title and Description of an image
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etTitle.getText().toString().trim().length() == 0 || etDesc.getText().toString().trim().length() == 0) {
                    Toast.makeText(InputInfoActivity.this, "Please Fill Out All Fields", Toast.LENGTH_SHORT).show();
                } else{
                    card.setTitle(etTitle.getText().toString());
                    card.setDesc(etDesc.getText().toString());

                    myRef.child(card.getKey()).setValue(card);

                    Intent intent = new Intent(InputInfoActivity.this, UserViewActivity.class);
                    intent.putExtra("username", card.getUsername());
                    intent.putExtra("myUsername", card.getUsername());
                    intent.putExtra("trip", card.getTrip());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}

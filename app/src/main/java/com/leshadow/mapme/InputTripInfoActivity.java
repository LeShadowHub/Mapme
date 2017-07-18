package com.leshadow.mapme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class InputTripInfoActivity extends AppCompatActivity {

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_trip_info);
        //getSupportActionBar().setTitle("New Trip");

        final EditText etTripTitle = (EditText)findViewById(R.id.etTripTitle);
        final Button btnSaveTrip = (Button)findViewById(R.id.btnSaveTrip);

        username = getIntent().getStringExtra("username");

        btnSaveTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etTripTitle.getText().toString().trim().length() == 0) {
                    Toast.makeText(InputTripInfoActivity.this, "Please enter a title", Toast.LENGTH_SHORT).show();

                } else{
                    Intent intent = new Intent(InputTripInfoActivity.this, UserViewActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("trip", etTripTitle.getText().toString().trim());
                    startActivity(intent);
                }

            }
        });
    }
}

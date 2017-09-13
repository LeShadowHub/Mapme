package com.leshadow.mapme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * The LocalServerUserAreaActivity takes user to user area once login is successful
 * from LocalServerLoginActivity
 * Not used in MapMe
 */
public class LocalServerUserAreaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_server_user_area);
        Button btnLogout = (Button) findViewById(R.id.bLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocalServerUserAreaActivity.this, LocalServerLoginActivity.class));
            }
        });
    }
}

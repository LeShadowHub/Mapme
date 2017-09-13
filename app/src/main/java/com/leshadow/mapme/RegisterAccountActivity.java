package com.leshadow.mapme;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ContentFrameLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * The RegisterAccountActivity registers new user using SQLite Database
 * Not used in MapMe
 */
public class RegisterAccountActivity extends AppCompatActivity {

    SQLiteOpenHelper openHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        ActionBar ab = getSupportActionBar();
        ab.hide();

        openHelper = new SQLiteDBHelper(this);

        // Referencing EditText Widgets and Button placed inside xml
        final EditText txtfullname = (EditText) findViewById(R.id.txtname_reg);
        final EditText txtemail = (EditText) findViewById(R.id.txtemail_reg);
        final EditText txtpass = (EditText) findViewById(R.id.txtpass_reg);
        final EditText txtmobile = (EditText) findViewById(R.id.txtmobile_reg);
        Button btnreg = (Button) findViewById(R.id.btn_reg);

        // Register Button Click Event
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = openHelper.getWritableDatabase();

                String fullname = txtfullname.getText().toString();
                String email = txtemail.getText().toString();
                String pass = txtpass.getText().toString();
                String mobile = txtmobile.getText().toString();

                // Calling InsertData Method
                insertData(fullname, email, pass, mobile);

                // Alert dialong fter clicking the Register Account
                final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterAccountActivity.this);
                builder.setTitle("Information");
                builder.setMessage("Your Account is Successfullly Created.");
                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    // Inserting Data into database (Like INSERT INTO QUERY)
    public void insertData(String fullName, String email, String password, String mobile){
        ContentValues values = new ContentValues();
        values.put(SQLiteDBHelper.COLUMN_FULLNAME, fullName);
        values.put(SQLiteDBHelper.COLUMN_EMAIL, email);
        values.put(SQLiteDBHelper.COLUMN_PASSWORD, password);
        values.put(SQLiteDBHelper.COLUMNG_MOBILE, mobile);
        long id = db.insert(SQLiteDBHelper.TABLE_NAME, null, values);
    }
}

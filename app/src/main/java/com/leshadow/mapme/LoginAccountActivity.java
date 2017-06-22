package com.leshadow.mapme;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginAccountActivity extends AppCompatActivity {

    SQLiteOpenHelper dbhelper;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_account);

        //Hide AppBar for fullScreen
        ActionBar ab = getSupportActionBar();
        ab.hide();

        //Referencing UserEmail, Password EditText and TextView for Signup Now
        final EditText txtemail = (EditText) findViewById(R.id.txtemail);
        final EditText txtpass = (EditText) findViewById(R.id.txtpass);
        Button btnlogin = (Button) findViewById(R.id.btnsignin);
        TextView btnreg = (TextView) findViewById(R.id.btnreg);

        //Opening SQLite Pipeline
        dbhelper = new SQLiteDBHelper(this);
        db = dbhelper.getReadableDatabase();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = txtemail.getText().toString();
                String pass = txtpass.getText().toString();

                cursor = db.rawQuery("SELECT *FROM " + SQLiteDBHelper.TABLE_NAME + " WHERE " + SQLiteDBHelper.COLUMN_EMAIL + "=? AND " + SQLiteDBHelper.COLUMN_PASSWORD + "=?", new String[]{email, pass});
                if(cursor != null){
                    if(cursor.getCount() > 0){
                        cursor.moveToFirst();
                        //Retrieving User Fullname and Email after successfull login and passing to LoginSuccessActivity
                        String _fname = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.COLUMN_FULLNAME));
                        String _email = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.COLUMN_EMAIL));
                        Toast.makeText(LoginAccountActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginAccountActivity.this, LoginSuccessActivity.class);
                        intent.putExtra("fullname", _fname);
                        intent.putExtra("email", _email);
                        startActivity(intent);

                        //Removing activity from the stack for preventing back button press
                        finish();
                    } else{
                        //Alert Dialog box for alerting user about wrong credentials
                        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginAccountActivity.this);
                        builder.setTitle("Alert");
                        builder.setMessage("Username or Passord is wrong.");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }
        });

        //Intent for Opening RegisterAccountActivity
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginAccountActivity.this, RegisterAccountActivity.class);
                startActivity(intent);
            }
        });
    }
}

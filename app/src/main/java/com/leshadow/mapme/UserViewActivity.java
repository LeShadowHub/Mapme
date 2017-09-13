package com.leshadow.mapme;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
* The UserViewActivity displays all the images with a "Trip" submitted by individual User
* Utilizes Custom Adapter to display images
*/
public class UserViewActivity extends AppCompatActivity {

    // Recyclerview object
    private RecyclerView recyclerView;

    // Adapter object
    private RecyclerView.Adapter adapter;

    // Database reference
    private DatabaseReference mDatabase;
    private ProgressDialog pd;
    private List<CardModel> cards;
    private Toolbar toolbar;

    String username;
    String myUsername;
    String trip;
    Boolean publicView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pd = new ProgressDialog(this);
        cards = new ArrayList<>();

        username = getIntent().getStringExtra("username");
        myUsername = getIntent().getStringExtra("myUsername");
        trip = getIntent().getStringExtra("trip");

        getSupportActionBar().setTitle(trip);
        if(!username.equals(myUsername)){
            publicView =  true;
            invalidateOptionsMenu();
        }

        // Displaying progress dialog while fetching images
        pd.setMessage("Please wait...");
        pd.show();
        mDatabase = FirebaseDatabase.getInstance().getReference(username + "/" + trip);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pd.dismiss();

                // Iterating through all values in database
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    CardModel card = postSnapshot.getValue(CardModel.class);
                    cards.add(card);
                }

                // Creating adapter
                adapter = new MyAdapter(getApplicationContext(), cards, myUsername);

                // Adding adapter to recyclerView
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pd.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        if(publicView){
            menu.findItem(R.id.action_add).setVisible(false);
            menu.findItem(R.id.action_edit).setVisible(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(!username.equals(myUsername)){
            Intent intent = new Intent(UserViewActivity.this, PublicViewActivity.class);
            intent.putExtra("myUsername", myUsername);
            startActivity(intent);
            finish();
        } else{
            Intent intent = new Intent(UserViewActivity.this, UserMainViewActivity.class);
            intent.putExtra("myUsername", myUsername);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int res_id = item.getItemId();
        switch (res_id){
            case R.id.action_add:
                Intent intent = new Intent(UserViewActivity.this, StoreImageActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("myUsername", myUsername);
                intent.putExtra("trip", trip);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_edit:
                return true;
            case R.id.action_search:
                return true;
            case R.id.action_contact_us:
                return true;
            case R.id.action_logout:
                final AlertDialog.Builder builder = new AlertDialog.Builder(UserViewActivity.this);
                builder.setTitle("Log Out");
                builder.setMessage("Do you want to logout ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(UserViewActivity.this, ServerLoginActivity.class);
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
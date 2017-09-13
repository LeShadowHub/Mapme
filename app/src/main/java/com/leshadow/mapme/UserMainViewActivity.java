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
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

/**
 * The UserMainViewActivity displays all the "Trips" submitted by individual User
 * Utilizes Custom Adapter to display images
 */
public class UserMainViewActivity extends AppCompatActivity {

    // Recyclerview object
    private RecyclerView recyclerViewMain;

    // Adapter object
    private RecyclerView.Adapter adapter;

    // Database reference
    private DatabaseReference mDatabase;
    private ProgressDialog pd;
    private List<CardModel> cards;
    private Toolbar toolbar;

    String myUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_view);

        myUsername = getIntent().getStringExtra("myUsername");

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(myUsername + "'s Journey");

        recyclerViewMain = (RecyclerView) findViewById(R.id.recyclerViewMain);
        recyclerViewMain.setHasFixedSize(true);
        recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));

        pd = new ProgressDialog(this);
        cards = new ArrayList<>();

        // Displaying progress dialog while fetching images
        pd.setMessage("Please wait...");
        pd.show();
        mDatabase = FirebaseDatabase.getInstance().getReference(myUsername + "/AllTrips");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pd.dismiss();

                // Iterating through all values in database
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    CardModel card = postSnapshot.getValue(CardModel.class);
                    cards.add(card);
                }

                // Creating adapter
                adapter = new MyMainAdapter(getApplicationContext(), cards, myUsername);

                // Adding adapter to recyclerView
                recyclerViewMain.setAdapter(adapter);
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

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserMainViewActivity.this, PublicViewActivity.class);
        intent.putExtra("myUsername", myUsername);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int res_id = item.getItemId();
        switch (res_id){
            case R.id.action_add:
                Intent intent = new Intent(UserMainViewActivity.this, InputTripInfoActivity.class);
                intent.putExtra("myUsername", myUsername);
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
                final AlertDialog.Builder builder = new AlertDialog.Builder(UserMainViewActivity.this);
                builder.setTitle("Log Out");
                builder.setMessage("Do you want to logout ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(UserMainViewActivity.this, ServerLoginActivity.class);
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

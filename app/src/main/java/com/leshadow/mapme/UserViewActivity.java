package com.leshadow.mapme;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class UserViewActivity extends AppCompatActivity {

    //recyclerview object
    private RecyclerView recyclerView;

    //adapter object
    private RecyclerView.Adapter adapter;

    //database reference
    private DatabaseReference mDatabase;

    //progress dialog
    private ProgressDialog pd;

    //list to hold all the uploaded cards;
    private List<CardModel> cards;

    //toolbar
    private Toolbar toolbar;

    String username;
    String trip;

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
        trip = getIntent().getStringExtra("trip");

        getSupportActionBar().setTitle(trip);

        //displaying progress dialog while fetching images
        pd.setMessage("Please wait...");
        pd.show();
        mDatabase = FirebaseDatabase.getInstance().getReference(username + "/" + trip);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pd.dismiss();

                //iterating through all values in database
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    CardModel card = postSnapshot.getValue(CardModel.class);
                    cards.add(card);
                }

                //creating adapter
                adapter = new MyAdapter(getApplicationContext(), cards);

                //adding adapter to recyclerView
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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int res_id = item.getItemId();
        switch (res_id){
            case R.id.action_add:
                Intent intent = new Intent(UserViewActivity.this, StoreImageActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("trip", trip);
                startActivity(intent);
                return true;
            case R.id.action_edit:
                return true;
            case R.id.action_search:
                return true;
            case R.id.action_contact_us:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
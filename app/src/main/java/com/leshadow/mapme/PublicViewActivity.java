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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PublicViewActivity extends AppCompatActivity {

    //recyclerview object
    private RecyclerView recyclerViewMain;

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

    String myUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_view);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Front Page");

        recyclerViewMain = (RecyclerView) findViewById(R.id.recyclerViewPublic);
        recyclerViewMain.setHasFixedSize(true);
        recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));

        pd = new ProgressDialog(this);
        cards = new ArrayList<>();

        myUsername = getIntent().getStringExtra("myUsername");

        //displaying progress dialog while fetching images
        pd.setMessage("Please wait...");
        pd.show();
        mDatabase = FirebaseDatabase.getInstance().getReference("PublicWall");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pd.dismiss();

                //iterating through all values in database
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    CardModel card = postSnapshot.getValue(CardModel.class);
                    cards.add(card);
                }

                //creating adapter
                adapter = new PublicAdapter(getApplicationContext(), cards, myUsername);

                //adding adapter to recyclerView
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
        menuInflater.inflate(R.menu.menu_public, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int res_id = item.getItemId();
        switch (res_id){
            case R.id.action_edit:
                Intent intent = new Intent(PublicViewActivity.this, UserMainViewActivity.class);
                intent.putExtra("myUsername", myUsername);
                startActivity(intent);
                return true;
            case R.id.action_search:
                return true;
            case R.id.action_contact_us:
                return true;
            case R.id.action_logout:
                final AlertDialog.Builder builder = new AlertDialog.Builder(PublicViewActivity.this);
                builder.setTitle("Log Out");
                builder.setMessage("Do you want to logout ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(PublicViewActivity.this, ServerLoginActivity.class);
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
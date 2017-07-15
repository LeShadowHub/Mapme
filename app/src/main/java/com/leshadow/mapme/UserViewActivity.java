package com.leshadow.mapme;

import android.app.ProgressDialog;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pd = new ProgressDialog(this);
        cards = new ArrayList<>();

        //displaying progress dialog while fetching images
        pd.setMessage("Please wait...");
        pd.show();
        mDatabase = FirebaseDatabase.getInstance().getReference("Kevin/Trip1");

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
}
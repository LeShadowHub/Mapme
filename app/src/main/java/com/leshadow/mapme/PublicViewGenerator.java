package com.leshadow.mapme;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by OEM on 7/21/2017.
 */

public class PublicViewGenerator {
    //creating reference to firebase storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;

    //creating reference to firebase database
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("AllUsers");
    private DatabaseReference publicWall = database.getReference("PublicWall");
    private DatabaseReference mDatabase;

    List<String> users = new ArrayList<String>();
    List<CardModel> allCards = new ArrayList<CardModel>();

    public void queryPublicView(){

    }

    public void generateUsers(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                users = dataSnapshot.getValue(t);
                Log.d("CHECKING FIRST", users.toString());
                generateWall(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });
    }

    public void generateWall(List<String> users){

        Log.d("CHECKING", "GENERATEWALL REACHED");
        Log.d("CHECKING SECOND", users.toString());

        final int[] remaining = {users.size()};

        for(final int[] i = {0}; i[0] < users.size(); i[0]++){
            final String user = users.get(i[0]);
            mDatabase = FirebaseDatabase.getInstance().getReference(user + "/AllTrips");

            //Query itemsQuery = mDatabase.orderByKey();
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){
                        //iterating through all values in database
                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            CardModel card = postSnapshot.getValue(CardModel.class);
                            allCards.add(card);
                            //Log.d("CHECKING THIRD", allCards.toString());
                        }
                    }
                    if(--remaining[0] == 0){
                        sortCards(allCards);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    databaseError.toException().printStackTrace();
                }
            });
        }

    }

    public void sortCards(List<CardModel> allCards){
        Collections.sort(allCards, new Comparator<CardModel>() {
            @Override
            public int compare(CardModel o1, CardModel o2) {
                return o2.getIsLiked()-o1.getIsLiked();
            }
        });
        //Log.d("CHECKING FOURTH", allCards.toString());
        for(int i = 0; i < allCards.size(); i++){
            String uploadId = publicWall.push().getKey();
            publicWall.child(uploadId).setValue(allCards.get(i));
        }
    }

    public void generateUsers(final String username){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};

                if(dataSnapshot.getValue(t) != null){
                    users = dataSnapshot.getValue(t);
                    users.add(username);
                } else{
                    users.add(username);
                }
                myRef.setValue(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });
    }
}

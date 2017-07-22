package com.leshadow.mapme;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
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


    /*public void generateWall(){
        //users = generateView();

        Log.d("CHECKING", "RETURN TO GENERATEWALL");
        if(users.size() > 0){
            for(int i = 0; i < users.size(); i++){
                final String user = users.get(i);
                mDatabase = FirebaseDatabase.getInstance().getReference(user + "/AllTrips");

                Log.d("CHECKING", "GENERATING ALL CARDS");

                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //iterating through all values in database
                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            CardModel card = postSnapshot.getValue(CardModel.class);
                            allCards.add(card);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });

            }

            Collections.sort(allCards, new Comparator<CardModel>() {
                @Override
                public int compare(CardModel o1, CardModel o2) {
                    return o1.getIsLiked()-o2.getIsLiked();
                }
            });

            for(int i = 0; i < allCards.size(); i++){
                String uploadId = publicWall.push().getKey();
                publicWall.child(uploadId).setValue(allCards.get(i));
            }
        }
    }*/

    public List<String> generateView(final String username){
        List<String> temp;
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
        Log.d("LIST OF USERS SECOND", users.toString());
        return users;
    }
}

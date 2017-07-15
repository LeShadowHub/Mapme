package com.leshadow.mapme;

/**
 * Created by OEM on 7/14/2017.
 */
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;


public class CardFragment extends Fragment{
    //creating reference to firebase storage
    /*FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    //creating reference to firebase database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Kevin/Trip1");
    DatabaseReference imageNum = database.getReference("Kevin/Trip1/image number");

    ArrayList<CardModel> listitems = new ArrayList<>();
    RecyclerView MyRecyclerView;

    ArrayList<String> imagePaths = new ArrayList<>();
    int num;
    MyAdapter myAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle("My Adventure");


        //Track image number
        imageNum.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Integer.class) != null){
                    num = dataSnapshot.getValue(Integer.class);
                    //Log.w("NUMBER", dataSnapshot.getValue(Integer.class).toString());

                    for(int i = 1; i < num; i++){
                        imagePaths.add(getImageURI(i));
                    }


                    //Log.d("LIST", Integer.toString(listitems.size()));
                    //Log.d("CHECK", "Finished with OnCreate");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR", databaseError.toException());
            }
        });


        Log.w("NUMBER", Integer.toString(num));
        initializeList(num);
        myAdapter = new MyAdapter(listitems);
        //getImageURI(1);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_card, container, false);

        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        Log.d("LISTSIZE", "Reached onCreateView");
        MyRecyclerView = (RecyclerView) view.findViewById(R.id.cardView);
        //MyRecyclerView.setHasFixedSize(true);
        MyRecyclerView.setLayoutManager(MyLayoutManager);
        MyRecyclerView.setAdapter(myAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<CardModel> list;

        public MyAdapter(ArrayList<CardModel> Data) {
            list = Data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycle_items, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            holder.titleTextView.setText(list.get(position).getCardTitle());
            holder.coverImageView.setImageResource(list.get(position).getImagePath());
            holder.coverImageView.setTag(list.get(position).getImagePath());
            holder.likeImageView.setTag(R.drawable.ic_like);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public ImageView coverImageView;
        public ImageView likeImageView;
        public ImageView shareImageView;

        public MyViewHolder(View v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.titleTextView);
            coverImageView = (ImageView) v.findViewById(R.id.coverImageView);
            likeImageView = (ImageView) v.findViewById(R.id.likeImageView);
            shareImageView = (ImageView) v.findViewById(R.id.shareImageView);
            likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int id = (int)likeImageView.getTag();
                    if( id == R.drawable.ic_like){

                        likeImageView.setTag(R.drawable.ic_liked);
                        likeImageView.setImageResource(R.drawable.ic_liked);

                        Toast.makeText(getActivity(),titleTextView.getText()+" added to favourites",Toast.LENGTH_SHORT).show();
                    }else{
                        likeImageView.setTag(R.drawable.ic_like);
                        likeImageView.setImageResource(R.drawable.ic_like);
                        Toast.makeText(getActivity(),titleTextView.getText()+" removed from favourites",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            shareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                            "://" + getResources().getResourcePackageName(coverImageView.getId())
                            + '/' + "drawable" + '/' + getResources().getResourceEntryName((int)coverImageView.getTag()));

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM,imageUri);
                    shareIntent.setType("image/jpeg");
                    startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
                }
            });
        }
    }


    public void initializeList(int num) {
        listitems.clear();

        //Log.d("NUMINLIST", Integer.toString(num));
        for(int i = 0; i < num; i++){
            CardModel item = new CardModel();
            //item.setCardTitle(Wonders[i]);
            //item.setImagePath(Images[i]);
            item.setIsfav(0);
            item.setIsturned(0);
            listitems.add(item);
        }
    }

    public String getImageURI(int num){
        final String[] path = {""};
        myRef.child("card" + num + "/image").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("IMAGEPATH", dataSnapshot.getValue(String.class));
                path[0] = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ERROR", "Cannot acquire image path");
                databaseError.toException().printStackTrace();

            }
        });
        return path[0];
    }*/
}

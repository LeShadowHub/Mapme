package com.leshadow.mapme;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Adapter used in UserViewActivity to display all images in a CardView
 * Created by OEM on 7/14/2017.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    private Context context;
    private List<CardModel> cards;
    private String myUsername;

    // Creating reference to firebase database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    public MyAdapter(Context context, List<CardModel> cards, String myUsername){
        this.cards = cards;
        this.context = context;
        this.myUsername = myUsername;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_items, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position){
        final CardModel card = cards.get(position);
        holder.titleTextView.setText(card.getTitle());

        // Disallow edits if not user
        if(!card.getUsername().equals(myUsername)){
            holder.settingView.setVisibility(View.GONE);
        }

        // Set Like if user liked the photo before
        if(card.getLikes() != null) {
            if (card.getLikes().contains(myUsername)) {
                holder.likeImageView.setImageResource(R.drawable.ic_liked);
            }
        }

        Glide.with(context)
                .load(card.getImage())
                //.thumbnail(0.5f)
                .into(holder.coverImageView);

        // Handle image click
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "You Clicked the Card", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(v.getContext(), ImageInfoActivity.class);
                intent.putExtra("CardObj", card);
                v.getContext().startActivity(intent);

            }
        });

        // Change Title and Desc of image
        holder.settingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), InputInfoActivity.class);
                intent.putExtra("CardObj", card);
                v.getContext().startActivity(intent);
            }
        });

        // Like Button Click
        holder.likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int liked = 0;
                String title;
                myRef = database.getReference(card.getUsername() + "/" + card.getTrip());
                holder.likeImageView.setImageResource(R.drawable.ic_liked);

                if(card.getTitle() != null){
                    title = card.getTitle();
                } else{
                    title = "this trip";
                }

                List<String> likes = new ArrayList<String>();
                if(card.getLikes() != null){
                    likes = card.getLikes();
                    if(likes.contains(myUsername)){
                        Toast.makeText(v.getContext(), "You have already liked " + title, Toast.LENGTH_SHORT).show();

                    } else{
                        Toast.makeText(v.getContext(), "You liked " + title, Toast.LENGTH_SHORT).show();
                        liked = card.getIsLiked();
                        liked++;
                        card.setIsLiked(liked);
                        likes.add(myUsername);
                        card.setLikes(likes);
                        myRef.child(card.getKey()).setValue(card);
                    }
                } else{
                    liked++;
                    Toast.makeText(v.getContext(), "You liked " + title, Toast.LENGTH_SHORT).show();
                    card.setIsLiked(liked);
                    //can't add to a null object
                    likes.add(myUsername);
                    card.setLikes(likes);
                    myRef.child(card.getKey()).setValue(card);
                }
            }
        });


    }

    @Override
    public int getItemCount(){
        return cards.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTextView;
        public ImageView coverImageView;
        public ImageView settingView;
        public LinearLayout linearLayout;
        public ImageView likeImageView;

        public MyViewHolder(View itemView){
            super(itemView);

            titleTextView = (TextView)itemView.findViewById(R.id.titleTextView);
            coverImageView = (ImageView)itemView.findViewById(R.id.coverImageView);
            settingView = (ImageView)itemView.findViewById(R.id.settingView);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.linearLayout);
            likeImageView = (ImageView)itemView.findViewById(R.id.likeImageView);
        }
    }
}

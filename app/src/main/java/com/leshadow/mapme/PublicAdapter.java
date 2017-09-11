package com.leshadow.mapme;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.nearby.connection.Payload;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by OEM on 7/22/2017.
 */

public class PublicAdapter extends RecyclerView.Adapter<PublicAdapter.PublicViewHolder>{
    private Context context;
    private List<CardModel> cards;
    private String myUsername;

    //creating reference to firebase database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    public PublicAdapter(Context context, List<CardModel> cards, String myUsername){
        this.cards = cards;
        this.context = context;
        this.myUsername = myUsername;
    }

    @Override
    public PublicAdapter.PublicViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_items, parent, false);
        PublicAdapter.PublicViewHolder viewHolder = new PublicAdapter.PublicViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PublicAdapter.PublicViewHolder holder, final int position){
        final CardModel card = cards.get(position);
        holder.titleTextView.setText(card.getTitle());
        holder.settingView.setVisibility(View.GONE);

        //Set Like if user liked the photo before
        if(card.getLikes() != null) {
            if (card.getLikes().contains(myUsername)) {
                holder.likeImageView.setImageResource(R.drawable.ic_liked);
            }
        }

        Glide.with(context)
                .load(card.getImage())
                //.thumbnail(0.5f)
                .into(holder.coverImageView);

        //Handle image click
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "You Clicked the Card", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(v.getContext(), UserViewActivity.class);
                intent.putExtra("username", cards.get(position).getUsername());
                intent.putExtra("myUsername", myUsername);
                intent.putExtra("trip", cards.get(position).getTrip());
                v.getContext().startActivity(intent);

            }
        });

        //Like Button Click
        holder.likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int liked = 0;
                String title;
                myRef = database.getReference(card.getUsername() + "/AllTrips");
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

        //Share button click
        /*holder.shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(card.getImage());
                Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                        "://" + card.getImage());

                //Uri imageUri = Uri.parse(card.getImage());
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.setType("image/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                v.getContext().startActivity(Intent.createChooser(shareIntent, v.getContext().getResources().getText(R.string.send_to)));

            }
        });*/
    }

    @Override
    public int getItemCount(){
        return cards.size();
    }

    class PublicViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTextView;
        public ImageView coverImageView;
        public ImageView settingView;
        public LinearLayout linearLayout;
        public ImageView likeImageView;
        //public ImageView shareImageView;

        public PublicViewHolder(View itemView){
            super(itemView);

            titleTextView = (TextView)itemView.findViewById(R.id.titleTextView);
            coverImageView = (ImageView)itemView.findViewById(R.id.coverImageView);
            settingView = (ImageView)itemView.findViewById(R.id.settingView);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.linearLayout);
            likeImageView = (ImageView)itemView.findViewById(R.id.likeImageView);
            //shareImageView = (ImageView)itemView.findViewById(R.id.shareImageView);

        }
    }
}

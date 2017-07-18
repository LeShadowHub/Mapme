package com.leshadow.mapme;

/**
 * Created by OEM on 7/17/2017.
 */
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
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
import java.util.List;

public class MyMainAdapter extends RecyclerView.Adapter<MyMainAdapter.MyMainViewHolder>{
    private Context context;
    private List<CardModel> cards;

    public MyMainAdapter(Context context, List<CardModel> cards){
        this.cards = cards;
        this.context = context;
    }

    @Override
    public MyMainAdapter.MyMainViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_items, parent, false);
        MyMainAdapter.MyMainViewHolder viewHolder = new MyMainAdapter.MyMainViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyMainAdapter.MyMainViewHolder holder, final int position){
        final CardModel card = cards.get(position);
        holder.titleTextView.setText(card.getTitle());
        Glide.with(context)
                .load(card.getImage())
                //.thumbnail(0.5f)
                .into(holder.coverImageView);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "You Clicked the Card", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(v.getContext(), UserViewActivity.class);
                intent.putExtra("username", cards.get(position).getUsername());
                intent.putExtra("trip", cards.get(position).getTrip());
                v.getContext().startActivity(intent);

            }
        });

        holder.settingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), InputInfoActivity.class);
                intent.putExtra("CardObj", cards.get(position));
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount(){
        return cards.size();
    }

    class MyMainViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTextView;
        public ImageView coverImageView;
        public ImageView settingView;
        public LinearLayout linearLayout;

        public MyMainViewHolder(View itemView){
            super(itemView);

            titleTextView = (TextView)itemView.findViewById(R.id.titleTextView);
            coverImageView = (ImageView)itemView.findViewById(R.id.coverImageView);
            settingView = (ImageView)itemView.findViewById(R.id.settingView);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.linearLayout);
        }
    }
}


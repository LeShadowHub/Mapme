package com.leshadow.mapme;

/**
 * Created by OEM on 7/14/2017.
 */
import android.content.Context;
import android.content.Intent;
import android.renderscript.ScriptGroup;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    private Context context;
    private List<CardModel> cards;

    public MyAdapter(Context context, List<CardModel> cards){
        this.cards = cards;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_items, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position){
        CardModel card = cards.get(position);
        holder.titleTextView.setText(card.getTitle());
        Glide.with(context).load(card.getImage()).into(holder.coverImageView);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You Clicked the Card", Toast.LENGTH_LONG).show();
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

    class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTextView;
        public ImageView coverImageView;
        public ImageView settingView;
        public LinearLayout linearLayout;

        public MyViewHolder(View itemView){
            super(itemView);

            titleTextView = (TextView)itemView.findViewById(R.id.titleTextView);
            coverImageView = (ImageView)itemView.findViewById(R.id.coverImageView);
            settingView = (ImageView)itemView.findViewById(R.id.settingView);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.linearLayout);
        }
    }
}

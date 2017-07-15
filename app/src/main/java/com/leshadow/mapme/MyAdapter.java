package com.leshadow.mapme;

/**
 * Created by OEM on 7/14/2017.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
    public void onBindViewHolder(MyViewHolder holder, int position){
        CardModel card = cards.get(position);
        //holder.titleTextView.setText(card.getCardTitle());
        Glide.with(context).load(card.getImage()).into(holder.coverImageView);
    }

    @Override
    public int getItemCount(){
        return cards.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        //public TextView titleTextView;
        public ImageView coverImageView;

        public MyViewHolder(View itemView){
            super(itemView);

            //titleTextView = (TextView)itemView.findViewById(R.id.titleTextView);
            coverImageView = (ImageView)itemView.findViewById(R.id.coverImageView);
        }
    }
}

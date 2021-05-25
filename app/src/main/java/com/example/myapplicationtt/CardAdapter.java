package com.example.myapplicationtt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private List<String> cards;
    private LayoutInflater cardIn;

    public CardAdapter(Context context, List<String> cards) {
        this.cards = cards;
        this.cardIn = LayoutInflater.from(context);;
    }


    @NonNull
    @Override
    public CardAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = cardIn.inflate(R.layout. card_container,
                parent, false);
        return new CardViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.CardViewHolder holder, int position) {
        String mCurrent = cards.get(position);
        holder.word.setText(mCurrent);

    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
    class CardViewHolder extends RecyclerView.ViewHolder{
        public TextView word;
        final CardAdapter cardAdapter;

        public CardViewHolder(@NonNull View itemView, CardAdapter cardAdapter) {
            super(itemView);
            word = itemView.findViewById(R.id.word);
            this.cardAdapter = cardAdapter;
        }
    }
}

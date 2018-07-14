package com.stage2.move.pop.popmovstage2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;
import com.stage2.move.pop.popmovstage2.data.MovieData;
import com.stage2.move.pop.popmovstage2.utilities.NetworkUtils;

import java.net.URL;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private List<MovieData> movieData;
    private LayoutInflater layoutInflater;
    private ItemClickListener itemClickListener;
    private Context context;

    public RecyclerViewAdapter(Context context, List<MovieData> data) {
        this.layoutInflater = LayoutInflater.from(context);
        this.movieData = data;
        context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        URL posterUrl = NetworkUtils.buildPosterUrlString( movieData.get(position).getPoster_path());
        Picasso.get()
                .load(posterUrl.toString())
                .placeholder(R.mipmap.poster)
                .error(R.mipmap.error)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if(movieData == null){
            return 0;
        }else {
            return movieData.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.imageView_item_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) itemClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public MovieData getItem(int id) {
        return movieData.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

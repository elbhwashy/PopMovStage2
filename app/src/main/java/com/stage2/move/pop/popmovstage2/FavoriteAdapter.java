package com.stage2.move.pop.popmovstage2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.stage2.move.pop.popmovstage2.database.FavoriteEntry;
import com.stage2.move.pop.popmovstage2.utilities.NetworkUtils;

import java.net.URL;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<FavoriteEntry> favoriteEntries;
    private Context context;
    private LayoutInflater layoutInflater;
    private FavoriteAdapter.ItemClickListener itemClickListener;

    public FavoriteAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;

    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.favorite_item, parent, false);

        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        FavoriteEntry taskEntry = favoriteEntries.get(position);
        String title = taskEntry.getTitle();
        String poster_path = taskEntry.getPoster_path();
        holder.textViewTitle.setText(title);
        URL posterUrl = NetworkUtils.buildPosterUrlString(poster_path);
        Picasso.get()
                .load(posterUrl.toString())
                .placeholder(R.mipmap.poster)
                .into(holder.imageViewImage);
    }

    @Override
    public int getItemCount() {
        if (favoriteEntries == null) {
            return 0;
        }
        return favoriteEntries.size();
    }

    public void setFavorites(List<FavoriteEntry> favoriteEntries) {
        this.favoriteEntries = favoriteEntries;
        notifyDataSetChanged();
    }

    public List<FavoriteEntry> getFavorites() {
        return this.favoriteEntries;
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textViewTitle;
        ImageView imageViewImage;

        FavoriteViewHolder(View itemView) {
            super(itemView);

            textViewTitle = (TextView)itemView.findViewById(R.id.textView_favorite_title);
            imageViewImage = (ImageView) itemView.findViewById(R.id.imageView_favorite_poster);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) itemClickListener.onItemClickListener(view, getAdapterPosition());
        }
    }

    public FavoriteEntry getFavoriteItem(int id) {
        return favoriteEntries.get(id);
    }
    public void setClickListener(FavoriteAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public interface ItemClickListener {
        void onItemClickListener(View view, int position);
    }
}

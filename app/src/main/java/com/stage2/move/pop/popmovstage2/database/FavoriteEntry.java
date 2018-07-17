package com.stage2.move.pop.popmovstage2.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.stage2.move.pop.popmovstage2.data.MovieData;

@Entity(tableName = "favorites")
public class FavoriteEntry implements Parcelable {
    public static final String EXTRA_NAME_MOVIEDATA = "favorite_data";
    @PrimaryKey(autoGenerate = false)
    private int id;
    private String title;
    private String poster_path;
    private String releaseDate;
    private double voteAverage;
    private String overview;
//    @Ignore
//    public FavoriteEntry(int id, String title, String poster_path){
//        this.id = id;
//        this.title = title;
//        this.poster_path = poster_path;
//    }

    public FavoriteEntry(int id, String title, String poster_path,  double voteAverage,String releaseDate) {
        this.id = id;
        this.title = title;
        this.poster_path = poster_path;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public double getVoteAverage() {
        return voteAverage;
    }


    public String getOverview() {
        return overview;
    }

    public int getId() {
        return id;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeDouble(voteAverage);
        dest.writeString(overview);
        dest.writeString(poster_path);
        dest.writeString(releaseDate);
    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        title = in.readString();
        voteAverage = in.readDouble();
        overview = in.readString();
        poster_path = in.readString();
        releaseDate = in.readString();
    }

    private FavoriteEntry(Parcel in){
        readFromParcel(in);
    }

    public static final Creator<FavoriteEntry> CREATOR = new Creator<FavoriteEntry>() {
        @Override
        public FavoriteEntry createFromParcel(Parcel source) {
            return new FavoriteEntry(source);
        }

        @Override
        public FavoriteEntry[] newArray(int size) {
            return new FavoriteEntry[size];
        }
    };
}

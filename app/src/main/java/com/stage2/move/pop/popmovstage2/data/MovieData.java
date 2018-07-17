package com.stage2.move.pop.popmovstage2.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieData implements Parcelable  {

    public static final String EXTRA_NAME_MOVIEDATA = "movie_data";

    private int id;
    private String title;
    private String releaseDate;
    private String posterPath;
    private double voteAverage;
    private String overview;

    public MovieData(int id, String title, String releaseDate, String posterPath, double voteAverage, String overview){
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.overview = overview;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getVote_average() {
        return voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public String getPoster_path() {
        return posterPath;
    }

    public String getRelease_date() { return releaseDate; }

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
        dest.writeString(posterPath);
        dest.writeString(releaseDate);
    }

      private void readFromParcel(Parcel in) {
        id = in.readInt();
        title = in.readString();
        voteAverage = in.readDouble();
        overview = in.readString();
        posterPath = in.readString();
        releaseDate = in.readString();
    }

    private MovieData(Parcel in){
        readFromParcel(in);
    }

    public static final Creator<MovieData> CREATOR = new Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel source) {
            return new MovieData(source);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };
}

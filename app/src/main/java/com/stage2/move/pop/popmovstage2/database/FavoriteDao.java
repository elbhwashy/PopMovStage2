package com.stage2.move.pop.popmovstage2.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM favorites")
    LiveData<List<FavoriteEntry>> loadAllFavorites();

    @Query("SELECT * FROM favorites where id = :movieId")
    FavoriteEntry getFavoriteByMovieId(int movieId);

    @Insert
    void insertFavorite(FavoriteEntry favoriteEntry);

    @Delete
    void deleteFavorite(FavoriteEntry favoriteEntry);
}

package com.stage2.move.pop.popmovstage2;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.stage2.move.pop.popmovstage2.database.FavoriteDatabase;
import com.stage2.move.pop.popmovstage2.database.FavoriteEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String LOG_TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<FavoriteEntry>> favoriteEntries;

    public MainViewModel(@NonNull Application application) {
        super(application);

        Log.d(LOG_TAG, "Call loadAllFavorites");
        FavoriteDatabase database = FavoriteDatabase.getInstance(this.getApplication());
        favoriteEntries = database.favoriteDao().loadAllFavorites();
    }

    public LiveData<List<FavoriteEntry>> getFavorites(){
        return favoriteEntries;
    }
}

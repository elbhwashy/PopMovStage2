package com.stage2.move.pop.popmovstage2.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.stage2.move.pop.popmovstage2.AppExecutors;
import com.stage2.move.pop.popmovstage2.FavoriteAdapter;
import com.stage2.move.pop.popmovstage2.MainViewModel;
import com.stage2.move.pop.popmovstage2.R;
import com.stage2.move.pop.popmovstage2.RecyclerViewAdapter;
import com.stage2.move.pop.popmovstage2.data.MovieData;
import com.stage2.move.pop.popmovstage2.database.FavoriteDatabase;
import com.stage2.move.pop.popmovstage2.database.FavoriteEntry;
import com.stage2.move.pop.popmovstage2.utilities.JSONUtils;
import com.stage2.move.pop.popmovstage2.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener,LoaderManager.LoaderCallbacks<String>{

    private RecyclerView movieRecyclerView;
    private RecyclerViewAdapter movieJSonDataAdapter;
    private ProgressBar movieLoadingIndicator;
    private TextView movieErrorMessage;
    private TextView movieNoFavorites;

    private FavoriteDatabase movieFavoriteDatabase;
    private FavoriteAdapter movieFavoriteDatabaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieLoadingIndicator = (ProgressBar)findViewById(R.id.progressbar_loading_indicator);
        movieRecyclerView = (RecyclerView)findViewById(R.id.Recyclerview_posters);
        movieErrorMessage = (TextView)findViewById(R.id.textview_error_message_display);
        movieNoFavorites = (TextView)findViewById(R.id.textview_no_favorites);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        String api_key = sharedPrefs.getString(
                getString(R.string.settings_api_key_key),
                "");

        String selectedMode  = sharedPrefs.getString(
                getString(R.string.settings_search_key),
                getString(R.string.settings_search_most_popular_value)
        );

        String title;
        if(selectedMode.equals(getString(R.string.settings_search_most_popular_value))){
            title = getString(R.string.app_title_popular);
        } else if (selectedMode.equals(getString(R.string.settings_search_highest_rated_value))){
            title = getString(R.string.app_title_highest);
        } else {
            title = getString(R.string.app_title_favorites);
        }

        setTitle(title);

        if(selectedMode.equals(getString(R.string.settings_search_favorites_value))) {

            movieFavoriteDatabase = FavoriteDatabase.getInstance(getApplicationContext());

            movieRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

            movieFavoriteDatabaseAdapter = new FavoriteAdapter(this);
            movieRecyclerView.setAdapter(movieFavoriteDatabaseAdapter);

            movieLoadingIndicator.setVisibility(View.VISIBLE);
            movieRecyclerView.setVisibility(View.INVISIBLE);

            setupViewModel();

            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }
                @Override
                public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {

                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    movieLoadingIndicator.setVisibility(View.VISIBLE);
                                    movieRecyclerView.setVisibility(View.INVISIBLE);
                                }
                            });

                            int pos = viewHolder.getAdapterPosition();
                            FavoriteEntry favoriteEntry = movieFavoriteDatabaseAdapter.getFavorites().get(pos);

                            movieFavoriteDatabase.favoriteDao().deleteFavorite(favoriteEntry);
                        }
                    });
                }
            }).attachToRecyclerView(movieRecyclerView);
            
        } else {
            movieRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.BUNDLE_API_KEY), api_key);
            bundle.putString(getString(R.string.BUNDLE_ORDER_BY), selectedMode);

            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<Object> dataLoader = loaderManager.getLoader(1);

            if (dataLoader == null) {
                loaderManager.initLoader(1, bundle, MainActivity.this);
            } else {
                loaderManager.restartLoader(1, bundle, MainActivity.this);
            }
        }
    }

    private void setupViewModel(){

        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getFavorites().observe(this, new Observer<List<FavoriteEntry>>() {
            @Override
            public void onChanged(@Nullable List<FavoriteEntry> favoriteEntries) {
                movieFavoriteDatabaseAdapter.setFavorites(favoriteEntries);
                final int favoriteCount = favoriteEntries.size();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        movieLoadingIndicator.setVisibility(View.INVISIBLE);

                        movieRecyclerView.setVisibility(favoriteCount > 0 ? View.VISIBLE : View.INVISIBLE);
                        movieNoFavorites.setVisibility(favoriteCount > 0 ? View.INVISIBLE : View.VISIBLE);
                    }
                });
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        MovieData clickedMovie = movieJSonDataAdapter.getItem(position);

        launchDetailActivity(clickedMovie);
    }

    private void launchDetailActivity(MovieData clickedMovie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(MovieData.EXTRA_NAME_MOVIEDATA, clickedMovie);
        startActivity(intent);
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(args == null){
                    return;
                }

                movieLoadingIndicator.setVisibility(View.VISIBLE);
                movieRecyclerView.setVisibility(View.INVISIBLE);

                forceLoad();
            }

            @Override
            public String loadInBackground() {

                String api_key = args.getString(getString(R.string.BUNDLE_API_KEY));
                String orderBy  = args.getString(getString(R.string.BUNDLE_ORDER_BY));

                URL url = NetworkUtils.buildUrl(api_key, orderBy);
                String jsonString = "";
                try {
                    jsonString = NetworkUtils.getResponseFromHttpUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

                return jsonString;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        movieLoadingIndicator.setVisibility(View.INVISIBLE);

        if(!TextUtils.isEmpty(data)){
            movieRecyclerView.setVisibility(View.VISIBLE);
            List<MovieData> movieArray = JSONUtils.ParseOverview(data);
            movieJSonDataAdapter = new RecyclerViewAdapter(MainActivity.this, movieArray);
            movieJSonDataAdapter.setClickListener(MainActivity.this);
            movieRecyclerView.setAdapter(movieJSonDataAdapter);
        } else {
            movieErrorMessage.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

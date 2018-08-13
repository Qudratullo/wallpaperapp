package com.example.wallpaperappdemo.Activities;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.wallpaperappdemo.Adapters.GalleryAdapter;
import com.example.wallpaperappdemo.Api;
import com.example.wallpaperappdemo.Constants;
import com.example.wallpaperappdemo.Classes.Image;
import com.example.wallpaperappdemo.R;
import com.example.wallpaperappdemo.Classes.Response;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ArrayList<Image> images;
    RecyclerView recyclerView;
    GalleryAdapter adapter;
    int currentPage = 0;
    final int COUNT_IMAGES_PER_PAGE = 20;
    MaterialSearchView searchView;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    boolean loading = false;
    RecyclerView.LayoutManager mLayoutManager;
    String queryString = "best";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.SET_WALLPAPER
                },
                1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*Настройка RecyclerView*/
        recyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        uploadImages();

        /*Загрузка следующую страницу*/
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    firstVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    if (!loading && totalItemCount <= visibleItemCount + firstVisibleItem + 2) {
                        loading = true;
                        uploadData(++currentPage);
                    }
                }
            }
        });

        /*Поиск обои по запросу*/
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryString = query;
                uploadImages();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    /*начальная загрузка обои*/
    private void uploadImages() {
        images = new ArrayList<>();
        currentPage = 1;
        uploadData(currentPage);
    }

    void uploadData(final int pageNumber) {
        /*Для отображать JSON файлы в лог*/
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        /*Запрос к серверу*/
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).client(httpClient.build()).addConverterFactory(GsonConverterFactory.create()).build();
        Api api = retrofit.create(Api.class);
        api.getImages(Constants.API_KEY, "photo", COUNT_IMAGES_PER_PAGE, pageNumber, queryString).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.code() == 200) {
                    images.addAll(response.body().getHits());
                    if (pageNumber == 1) {
                        recyclerView.setLayoutManager(mLayoutManager);
                        adapter = new GalleryAdapter(MainActivity.this, images);
                        recyclerView.setAdapter(adapter);
                    }
                    else {
                        adapter.notifyItemInserted(images.size());
                    }

                    if(images.size() == 0) {
                        Toast.makeText(MainActivity.this, "Images not found for this query.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    makeToast("No more images.");
                }
                loading = false;
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                makeToast("Could not connect to server");
            }
        });
    }

    void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }
}

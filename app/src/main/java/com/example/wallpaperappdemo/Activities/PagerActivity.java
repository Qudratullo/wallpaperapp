package com.example.wallpaperappdemo.Activities;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.example.wallpaperappdemo.Classes.Image;
import com.example.wallpaperappdemo.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PagerActivity extends AppCompatActivity {

    ViewPager viewPager;
    ArrayList<Image> images;
    int position;
    Button btnDownload, btnSetWallpaper;
    ImageView imageView;
    private LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        layoutInflater = (LayoutInflater) PagerActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        viewPager = findViewById(R.id.viewPager);

        images = (ArrayList<Image>)getIntent().getSerializableExtra("images");
        position = getIntent().getIntExtra("position", 0);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(position);
        btnDownload = findViewById(R.id.btnDownload);
        btnSetWallpaper = findViewById(R.id.btnSetWallpaper);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView img = viewPager.findViewWithTag(viewPager.getCurrentItem());
                img.buildDrawingCache();
                Bitmap bitmap = img.getDrawingCache();
                saveImage(bitmap, viewPager.getCurrentItem());
            }
        });

        btnSetWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView img = viewPager.findViewWithTag(viewPager.getCurrentItem());
                img.buildDrawingCache();
                Bitmap bitmap = img.getDrawingCache();
                try {
                    setAsWallpaper(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setAsWallpaper(Bitmap bitmapImage) throws IOException {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        wallpaperManager.setBitmap(bitmapImage);
        Toast.makeText(this, "Wallpaper has been changed successfully.", Toast.LENGTH_SHORT).show();
    }

    void saveImage(Bitmap bitmapImage, int imageIndex) {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/WallpaperDemo");
        String imageName = getName(imageIndex);
        File newImage = new File(storageDir, imageName);
        boolean success = true;
        System.out.println(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/WallpaperDemo");
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            FileOutputStream outputStream;
            try {
                outputStream = new FileOutputStream(newImage);
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                success = false;
                e.printStackTrace();
            } catch (IOException e) {
                success = false;
                e.printStackTrace();
            }
        }

        if (success) {
            Toast.makeText(this, "Image saved.", Toast.LENGTH_SHORT).show();
        }

        else {
            Toast.makeText(this, "Error on loading image.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getName(int imageIndex) {
        Image image = images.get(imageIndex);
        int indexStartName = image.getLargeImageURL().lastIndexOf("/")+1;
        return "Wallpaper_" + image.getLargeImageURL().substring(indexStartName) + ".png";
    }

    public class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return object == view;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            View view = layoutInflater.inflate(R.layout.full_screen_image, null);
            imageView = view.findViewById(R.id.imageView);
            final ProgressBar progressBar = view.findViewById(R.id.progressBar);

            progressBar.setVisibility(View.VISIBLE);
            final Image image = images.get(position);
            Glide.with(PagerActivity.this).load(image.getWebformatURL())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
            ViewPager viewPager = (ViewPager) container;
            imageView.setTag(position);
            viewPager.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            ViewPager viewPager = (ViewPager) container;
            View view = (View) object;
            viewPager.removeView(view);
        }
    }
}

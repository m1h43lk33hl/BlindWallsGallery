 package com.example.anotherwall;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {

    private ImageView mImageViewFull;
    private String[] imageURLArray;
    private int imageURLArrayCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        // Get the wallItem back
        Intent i = getIntent();
        final WallItem wallItem = (WallItem) i.getParcelableExtra("WallItem");

        //Set attributes
        mImageViewFull = (ImageView) findViewById(R.id.iv_image_view_full);
        imageURLArray = wallItem.getImgURLArray();

        // Load Initial picture with Picasso
        Picasso.with(ImageViewActivity.this).load(imageURLArray[imageURLArrayCounter])
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(mImageViewFull);

        // Increment so for the counter to stay in place
        imageURLArrayCounter++;

        //Show toast message
        Toast.makeText(ImageViewActivity.this, getString(R.string.image_toast_amount)+this.imageURLArray.length, Toast.LENGTH_SHORT).show();

        // OnclickListener for item
        mImageViewFull.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // Reset counter if it reaches limit, otherwise count up
                if(imageURLArrayCounter == imageURLArray.length)
                    imageURLArrayCounter = 0;

                // Load image with Picasso
                Picasso.with(ImageViewActivity.this).load(imageURLArray[imageURLArrayCounter])
                        .error(R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder)
                        .into(mImageViewFull);

                imageURLArrayCounter++;
            }
        });

    }



}

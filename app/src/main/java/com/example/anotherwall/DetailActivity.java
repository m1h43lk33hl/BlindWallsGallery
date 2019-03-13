package com.example.anotherwall;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private ImageView mImageViewThumbnail;
    private TextView mTextViewAuthor;
    private TextView mTextViewMaterial;
    private TextView mTextViewAddress;
    private TextView mTextViewPhotographer;
    private TextView mTextViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Initialise components
        mImageViewThumbnail = findViewById(R.id.iv_detail_thumbnail);
        mTextViewAuthor = findViewById(R.id.tv_detail_author);
        mTextViewMaterial = findViewById(R.id.tv_detail_material_var);
        mTextViewAddress = findViewById(R.id.tv_detail_address_var);
        mTextViewPhotographer = findViewById(R.id.tv_detail_photographer_var);
        mTextViewDescription = findViewById(R.id.tv_detail_description);

        // Get the wallItem back
        Intent i = getIntent();
        WallItem wallItem = (WallItem) i.getParcelableExtra("detailWallItem");

        // Fill textView components with wallItem values
        this.setComponents(wallItem);
    }

    private void setComponents(final WallItem wallItem)
    {
        final String[] imgURLArray = wallItem.getImgURLArray();

        Log.d("HEREO", "999"+wallItem.getDescription());


        // Set textViews with wallItem object fields
        this.mTextViewAuthor.setText(wallItem.getTitle());
        this.mTextViewMaterial.setText(wallItem.getMaterial());
        mTextViewAddress.setText(wallItem.getAddress());
        mTextViewPhotographer.setText(wallItem.getPhotographer());
        mTextViewDescription.setText(wallItem.getDescription());

        // Load image with Picasso
        Picasso.with(DetailActivity.this).load(wallItem.getThumbnail())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(mImageViewThumbnail);

        mImageViewThumbnail.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                // Start new activity
                Class imageViewActivity = ImageViewActivity.class;

                // Create intent
                Intent startImageViewActivityIntent = new Intent(DetailActivity.this, imageViewActivity);

                // Add array to intent
                startImageViewActivityIntent.putExtra("WallItem", (Parcelable)wallItem);

                // Start activity
                startActivity(startImageViewActivityIntent);
            }
        });
    }
}

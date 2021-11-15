package com.example.image_chan;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.example.image_chan.core.DatabaseHelper;

public class ImageViewActivity extends AppCompatActivity {
    private boolean hidden = true;
    private Main main;

    private DatabaseHelper databaseHelper;
    private ImageMatrixTouchHandler zoomHandler;

    private void update(Button addFav, String url) {
        boolean Exists = databaseHelper.ifExists(url);

        addFav.setText(Exists ? R.string.remove : R.string.add);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_content);

        databaseHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        Bundle data = intent.getExtras();

        String tag = (String) data.get("tag");
        String ratingLong = (String) data.get("ratingLong");
        String ratingShort = (String) data.get("ratingShort");
        String directory = (String) data.get("directory");
        String url = (String) data.get("url");

        ImageView image = findViewById(R.id.image_viewer);
        TextView tags = findViewById(R.id.tags);
        RelativeLayout actions = findViewById(R.id.text_actions);

        RequestOptions requestOptions = new RequestOptions()
                .error(R.drawable.error)
                .placeholder(R.drawable.loader);

        Glide.with(this)
                .load(url)
                .apply(requestOptions)
                .into(image);

        tags.setText("RATING: " + ratingLong.toUpperCase() + "\n\nTAGS:\n\n" + tag.substring(1, tag.length() - 1));

        zoomHandler = new ImageMatrixTouchHandler(this);
        image.setOnTouchListener(zoomHandler);

        Button viewOnBrowser = findViewById(R.id.download);
        Button addFav = findViewById(R.id.favorite_action);
        ImageButton contents = findViewById(R.id.contents);

        update(addFav, url);

        String urlBrowser = url;
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            urlBrowser = "http://" + url;

        String finalUrlBrowser = urlBrowser;
        viewOnBrowser.setOnClickListener(view -> {
            Intent toOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrlBrowser));
            startActivity(toOpen);
        });

        boolean exists = databaseHelper.ifExists(url);
        if (exists)
            Toast.makeText(this, "This image is in your favorites!", Toast.LENGTH_SHORT).show();

        addFav.setOnClickListener(view -> {
            if (!exists) {
                String[] entry = {
                        url,
                        tag.substring(1, tag.length() - 1),
                        ratingLong,
                        ratingShort,
                        null,
                        directory,
                        null,
                };

                boolean ifSuccess = databaseHelper.addFavorite(entry);

                if (ifSuccess) {
                    System.out.println("Successfully added entry with ID: " + url);
                    Toast.makeText(this, "Image successfully added to favorites!", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(this, "There was an error trying to add image to favorites.", Toast.LENGTH_LONG).show();
            } else {
                boolean ifSuccess = databaseHelper.removeFavorite(url);

                if (ifSuccess) {
                    System.out.println("Successfully removed entry with ID: " + url);
                    Toast.makeText(this, "Image successfully removed from favorites!", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(this, "There was an error trying to remove image from favorites.", Toast.LENGTH_LONG).show();

                Intent mIntent = new Intent(this, Main.class);
                mIntent.putExtra("updateFavorites", true);
                startActivity(mIntent);
            };

            finish();
            update(addFav, url);
        });

        contents.setOnClickListener((View.OnClickListener) view -> {
            hidden = !hidden;

            if (hidden) {
                tags.setVisibility(View.GONE);
                actions.setVisibility(View.GONE);

                contents.setImageResource(R.drawable.contents);
            } else {
                tags.setVisibility(View.VISIBLE);
                actions.setVisibility(View.VISIBLE);

                try {
                    zoomHandler.animateZoomOutToFit(250);
                } catch (Exception ignored) {};

                contents.setImageResource(R.drawable.hied);
            }
        });
    }
}

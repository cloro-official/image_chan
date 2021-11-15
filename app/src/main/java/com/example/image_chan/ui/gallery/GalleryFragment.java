package com.example.image_chan.ui.gallery;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.image_chan.R;
import com.example.image_chan.core.StaggeredAdapter;

import net.kodehawa.lib.imageboards.entities.impl.SafebooruImage;

import java.io.Serializable;
import java.util.ArrayList;

public class GalleryFragment extends Fragment {
    private TextView text_state;
    private TextView querying;

    private static final int NUM_COLUMNS  = 2;

    private ArrayList<String> urls = new ArrayList<String>();
    private ArrayList<String> names = new ArrayList<String>();

    private GalleryViewModel galleryViewModel;
    private GalleryListener listener;

    public interface GalleryListener {
        void onInputSent(int ID);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        Bundle b1 = getArguments();

        Button favorites = root.findViewById(R.id.favorites);
        Button clear = root.findViewById(R.id.clear);

        try {
            String state = b1.getString("state");

            text_state = root.findViewById(R.id.text_state);
            querying = root.findViewById(R.id.loading);

            querying.setVisibility(View.GONE);
            switch (state) {
                case "querying":
                    text_state.setVisibility(View.VISIBLE);
                    text_state.setText(R.string.query);
                    break;
                case "none":
                    text_state.setVisibility(View.VISIBLE);
                    text_state.setText(R.string.none);
                    break;
                case "issearch":
                    text_state.setVisibility(View.GONE);
                    querying.setVisibility(View.VISIBLE);
                    break;
            }

            try {
                ArrayList<String> urls = b1.getStringArrayList("urls");

                ArrayList<String> ratingLong = b1.getStringArrayList("ratingLong");
                ArrayList<String> ratingShort = b1.getStringArrayList("ratingShort");
                ArrayList<String> directory = b1.getStringArrayList("directory");
                ArrayList<String> tags = b1.getStringArrayList("tags");

                boolean[] existing = b1.getBooleanArray("exists");

                querying.setVisibility(View.GONE);
                if (urls != null && urls.size() > 0)
                    initList(root, urls, ratingLong, ratingShort, directory, tags, existing);
                else {
                    text_state.setVisibility(View.VISIBLE);
                    text_state.setText(R.string.nofav);
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return root;
    }

    private void initList(View view, ArrayList<String> urls, ArrayList<String> ratingLong, ArrayList<String> ratingShort, ArrayList<String> directory, ArrayList<String> tags, boolean[] existing) {
        RecyclerView recyclerView = view.findViewById(R.id.imageview);
        recyclerView.removeAllViewsInLayout();
        recyclerView.setVisibility(View.GONE);

        StaggeredAdapter staggeredAdapter =
                new StaggeredAdapter(urls, ratingLong, ratingShort, directory, tags, existing, this);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(staggeredAdapter);

        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof GalleryListener)
            listener = (GalleryListener) context;
    }

    public void updateText(int ID) {
        text_state.setText(ID);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
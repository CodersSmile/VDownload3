package com.videosdownide.loadbase.vdownload.wallpaperFragment;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.videosdownide.loadbase.vdownload.R;

import java.util.ArrayList;
import java.util.List;

public class AllPhotosFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Image>> {

    private ProgressBar mProgressBar;


//    private static final String REQUEST_URL = "https://api.unsplash.com/photos/?client_id=abb421feccb501c53657b2f464cb49103ff0111dfed7854413e0f4863c673992&per_page=100";

    private static final String REQUEST_URL ="https://pixabay.com/api/?key=18343821-ea5a45b632d168d33955de45d&image_type=photo&per_page=200";

    private ImageAdapter mImageAdapter;
    private List<Image> images = new ArrayList<>();
    private static final int LOADER_ID = 1;

    public AllPhotosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_photos, container, false);

        TextView errorMessage = view.findViewById(R.id.error_msg);

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else
            connected = false;

        if (!connected) {
            errorMessage.setVisibility(View.VISIBLE);
        } else {
            errorMessage.setVisibility(View.GONE);
        }


        mProgressBar = view.findViewById(R.id.loading_indicator);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        // set a StaggeredGridLayoutManager with 3 number of columns and vertical orientation
      // StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);


        RecyclerView mRecyclerview = view.findViewById(R.id.recyclerView_image);
        mRecyclerview.setLayoutManager(layoutManager);
      //  mRecyclerview.setLayoutManager(staggeredGridLayoutManager); // set LayoutManager to RecyclerView
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setItemAnimator(new DefaultItemAnimator());

        mImageAdapter = new ImageAdapter(getActivity(), images);
        mRecyclerview.setAdapter(mImageAdapter);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        return view;

    }


    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<List<Image>> onCreateLoader(int id, Bundle args) {

        return new ImageLoader(getContext(), REQUEST_URL);
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<List<Image>> loader, List<Image> data) {

        mProgressBar.setVisibility(View.GONE);
      mImageAdapter.setImageData(data);

    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<List<Image>> loader) {
        mImageAdapter.setImageData(null);
    }

}
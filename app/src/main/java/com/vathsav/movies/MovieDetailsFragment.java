package com.vathsav.movies;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vathsav.movies.support.FavoritesDatabase;
import com.vathsav.movies.support.GridItem;
import com.vathsav.movies.support.TrailerItem;
import com.vathsav.movies.support.TrailerListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MovieDetailsFragment extends Fragment {

    String jsonResponse_videos, jsonResponse_reviews;
    String movieID;
    String shareLink;
    ArrayList<TrailerItem> trailerItemArrayList;
    View rootView;
    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        Picasso.with(getActivity())
                .load("http://image.tmdb.org/t/p/w500/" + getActivity().getIntent().getStringExtra("Backdrop Path"))
                .into((ImageView) rootView.findViewById(R.id.details_image_view));
        ((TextView) rootView.findViewById(R.id.details_release_date)).setText(getActivity().getIntent().getStringExtra("Release Date"));
        ((TextView) rootView.findViewById(R.id.details_synopsis)).setText(getActivity().getIntent().getStringExtra("Synopsis"));
        ((TextView) rootView.findViewById(R.id.details_title)).setText(getActivity().getIntent().getStringExtra("Title"));
        ((TextView) rootView.findViewById(R.id.details_rating)).setText(getActivity().getIntent().getStringExtra("Rating"));
        movieID = getActivity().getIntent().getStringExtra("Movie ID");

        if (savedInstanceState == null || !savedInstanceState.containsKey("JSON Response")) {
            trailerItemArrayList = new ArrayList<>();
            if (isNetworkAvailable()) {
                new getTrailers().execute(movieID);
            } else {
                Toast.makeText(getActivity(), "Unable to access the internet. Please check your connection.", Toast.LENGTH_SHORT).show();
            }
        } else {
            trailerItemArrayList = savedInstanceState.getParcelableArrayList("JSON Response");
            ListView listView = (ListView) rootView.findViewById(R.id.details_list_view);
            TrailerListAdapter trailerListAdapter = new TrailerListAdapter(getActivity(), trailerItemArrayList);
            trailerListAdapter.notifyDataSetChanged();
            listView.setAdapter(trailerListAdapter);
            setListViewHeightBasedOnChildren(listView);
        }

        rootView.findViewById(R.id.details_favorite_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoritesDatabase fd = new FavoritesDatabase(getActivity());
                GridItem gridItem = new GridItem(getActivity().getIntent().getStringExtra("Movie ID"), getActivity().getIntent().getStringExtra("Title"), getActivity().getIntent().getStringExtra("Release Date")
                        , getActivity().getIntent().getStringExtra("Rating"), getActivity().getIntent().getStringExtra("Poster Path"), getActivity().getIntent().getStringExtra("Synopsis"), getActivity().getIntent().getStringExtra("Backdrop Path"));
                fd.addItem(gridItem);
                fd.close();
            }
        });

        if (isNetworkAvailable())
            new getTrailers().execute(movieID);
        return rootView;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ActionBar.LayoutParams(desiredWidth, ActionBar.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public void parseJSONVideos(String jsonResponse) {
        try {
            String movie_results = "results";
            String video_key = "key";
            String video_name = "name";
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray jsonArray = jsonObject.getJSONArray(movie_results);

            int numberOfResults;
            if (jsonArray != null)
                numberOfResults = jsonArray.length();
            else
                numberOfResults = 0;

            String names[] = new String[numberOfResults];
            String keys[] = new String[numberOfResults];

            trailerItemArrayList = new ArrayList<>();

            for (int i = 0; i < numberOfResults; i++) {
                JSONObject trailer = jsonArray.getJSONObject(i);
                names[i] = String.valueOf(trailer.getString(video_name));
                keys[i] = String.valueOf(trailer.getString(video_key));
                Log.v("Trailer", keys[i] + " " + names[i]);
                trailerItemArrayList.add(new TrailerItem(names[i], keys[i]));
            }

            final String[] key = keys;

            for (int i = 0; i < names.length; i++) {
                Log.v("Trailers: ", names[i]);
            }

            shareLink = "http://www.youtube.com/watch?v=" + key[0];

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ListView listView = (ListView) getActivity().findViewById(R.id.details_list_view);
                    TrailerListAdapter trailerListAdapter = new TrailerListAdapter(getActivity(), trailerItemArrayList);
                    trailerListAdapter.notifyDataSetChanged();
                    listView.setAdapter(trailerListAdapter);
                    setListViewHeightBasedOnChildren(listView);
                }
            });
        } catch (JSONException ex) {
            Log.e("JSON Error", String.valueOf(ex));
        }
    }

    public void parseJSONReviews(String jsonResponse) {
        try {
            String movie_results = "results";
            JSONObject jsonObject = new JSONObject(jsonResponse);
            final JSONArray jsonArray = jsonObject.getJSONArray(movie_results);

            int numberOfResults;
            if (jsonArray != null)
                numberOfResults = jsonArray.length();
            else
                numberOfResults = 0;

            String review_author = "author";
            String review_content = "content";

            for (int i = 0; i < numberOfResults; i++) {
                JSONObject review = jsonArray.getJSONObject(i);
                final String author = String.valueOf(review.getString(review_author));
                final String content = String.valueOf(review.getString(review_content));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textViewReviews = (TextView) getActivity().findViewById(R.id.details_text_reviews);
                        textViewReviews.append(author + ":\n" + content + "\n\n");
                    }
                });

                Log.v("Review", author + " " + content);
            }

        } catch (JSONException ex) {
            Log.e("JSON Error", String.valueOf(ex));
        }
    }

    public class getTrailers extends AsyncTask<String, Void, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
//            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection httpURLConnectionVideos = null;
            BufferedReader bufferedReader = null;

            // Query vars
            String apiKey = "f1cd8e955ce536c89bfa7aa2726cbc55";
            try {
                // Query parameters
                final String url_videos = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos?";

                final String api = "api_key";

                Uri uri_videos;
                uri_videos = Uri.parse(url_videos).buildUpon()
                        .appendQueryParameter(api, apiKey)
                        .build();
                URL finalURL_videos = new URL(uri_videos.toString());

                // For Videos
                httpURLConnectionVideos = (HttpURLConnection) finalURL_videos.openConnection();
                httpURLConnectionVideos.setRequestMethod("GET");

                httpURLConnectionVideos.connect();
                InputStream inputStream = httpURLConnectionVideos.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) return null;
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String jsonText;
                while ((jsonText = bufferedReader.readLine()) != null) {
                    buffer.append(jsonText);
                }
                if (buffer.length() == 0) return null;
                jsonResponse_videos = buffer.toString();

            } catch (IOException ex) {
                Log.e("Main Activity", "Error ", ex);
            } finally {
                if (httpURLConnectionVideos != null) {
                    httpURLConnectionVideos.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException ex) {
                        Log.e("Main Activity", "Error ", ex);
                    }
                }
            }

            parseJSONVideos(jsonResponse_videos);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isNetworkAvailable())
                new getReviews().execute(movieID);
//            progressDialog.dismiss();
        }
    }

    public class getReviews extends AsyncTask<String, Void, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
//            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection httpURLConnectionReviews = null;
            BufferedReader bufferedReader = null;

            // Query vars
            String apiKey = "f1cd8e955ce536c89bfa7aa2726cbc55";
            try {
                // Query parameters
                final String url_reviews = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews?";

                final String api = "api_key";

                Uri uri_reviews;

                uri_reviews = Uri.parse(url_reviews).buildUpon()
                        .appendQueryParameter(api, apiKey)
                        .build();

                URL finalURL_reviews = new URL(uri_reviews.toString());

                // For Reviews
                httpURLConnectionReviews = (HttpURLConnection) finalURL_reviews.openConnection();
                httpURLConnectionReviews.setRequestMethod("GET");

                httpURLConnectionReviews.connect();

                String jsonText;
                InputStream inputStream = httpURLConnectionReviews.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) return null;
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                while ((jsonText = bufferedReader.readLine()) != null) {
                    buffer.append(jsonText);
                }
                if (buffer.length() == 0) return null;
                jsonResponse_reviews = buffer.toString();
            } catch (IOException ex) {
                Log.e("Main Activity", "Error ", ex);
            } finally {
                if (httpURLConnectionReviews != null) {
                    httpURLConnectionReviews.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException ex) {
                        Log.e("Main Activity", "Error ", ex);
                    }
                }
            }
            parseJSONReviews(jsonResponse_reviews);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            progressDialog.dismiss();
        }
    }
}

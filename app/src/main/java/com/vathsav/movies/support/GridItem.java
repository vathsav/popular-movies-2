package com.vathsav.movies.support;

import android.os.Parcel;
import android.os.Parcelable;

public class GridItem implements Parcelable {
    private String movie_id;
    private String movie_release_date;
    private String movie_synopsis;
    private String movie_user_rating;
    private String movie_title;
    private String movie_image;
    private String movie_backdrop;

    public GridItem(){}

    public GridItem(String id, String title, String release_date, String user_rating, String image, String synopsis, String backdrop) {
        this.movie_id = id;
        this.movie_title = title;
        this.movie_release_date = release_date;
        this.movie_user_rating = user_rating;
        this.movie_image = image;
        this.movie_synopsis = synopsis;
        this.movie_backdrop = backdrop;
    }

    private GridItem(Parcel parcel) {
        movie_id = parcel.readString();
        movie_release_date = parcel.readString();
        movie_synopsis = parcel.readString();
        movie_user_rating = parcel.readString();
        movie_title = parcel.readString();
        movie_image = parcel.readString();
        movie_backdrop = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movie_id);
        dest.writeString(movie_release_date);
        dest.writeString(movie_synopsis);
        dest.writeString(movie_user_rating);
        dest.writeString(movie_title);
        dest.writeString(movie_image);
        dest.writeString(movie_backdrop);
    }

    public final Parcelable.Creator<GridItem> CREATOR = new Parcelable.Creator<GridItem>() {
        @Override
        public GridItem[] newArray(int size) {
            return new GridItem[size];
        }

        @Override
        public GridItem createFromParcel(Parcel source) {
            return new GridItem(source);
        }
    };

    public String getMovieID() {
        return this.movie_id;
    }

    public String getTitle() {
        return this.movie_title;
    }

    public String getReleaseDate() {
        return this.movie_release_date;
    }

    public String getUserRating() {
        return this.movie_user_rating;
    }

    public String getImage() {
        return this.movie_image;
    }

    public String getBackdrop() {
        return this.movie_backdrop;
    }

    public String getSynopsis() {
        return this.movie_synopsis;
    }

    public void setMovieID(String id){
        this.movie_id = id;
    }

    public void setMovieTitle(String title){
        this.movie_title = title;
    }

    public void setReleaseDate(String releaseDate){
        this.movie_release_date = releaseDate;
    }

    public void setRating(String rating) {
        this.movie_user_rating = rating;
    }

    public void setImage(String image) {
        this.movie_image = image;
    }

    public void setBackdrop(String backdrop) {
        this.movie_backdrop = backdrop;
    }

    public void setSynopsis(String synopsis) {
        this.movie_synopsis = synopsis;
    }
}

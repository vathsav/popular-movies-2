package com.vathsav.movies.support;

import android.os.Parcel;
import android.os.Parcelable;

public class TrailerItem implements Parcelable {

        private String trailer_name;
        private String trailer_key;

        public TrailerItem(){}

        public TrailerItem(String name, String key) {
            this.trailer_name = name;
            this.trailer_key = key;
        }

        private TrailerItem(Parcel parcel) {
            trailer_name = parcel.readString();
            trailer_key = parcel.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(trailer_name);
            dest.writeString(trailer_key);
        }

        public final Parcelable.Creator<TrailerItem> CREATOR = new Parcelable.Creator<TrailerItem>() {
            @Override
            public TrailerItem[] newArray(int size) {
                return new TrailerItem[size];
            }

            @Override
            public TrailerItem createFromParcel(Parcel source) {
                return new TrailerItem(source);
            }
        };

        public String getTrailerName() {
            return this.trailer_name;
        }

        public String getTrailerKey() {
            return this.trailer_key;
        }

        public void setTrailerName(String name) {
            this.trailer_name = name;
        }
        public void setTrailerKey(String key){
            this.trailer_key = key;
        }
    }

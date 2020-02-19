package com.dku.dogukankarayilanoglu_mad4124_fp.Modal;

import androidx.annotation.NonNull;

public class Item {

    private int id;
    private String latitude;
    private String longitude;
    private String title;
    private String content;
    private String dateAdded;

    public Item(){

    }

    public Item(int id, String latitude, String longitude, String title, String content, String dateAdded) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.content = content;
        this.dateAdded = dateAdded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    @NonNull
    @Override
    public String toString() {
        return getTitle();
    }
}

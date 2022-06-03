package com.example.anomalbrowser.tabLogic;

public class Tab {
    public String name;
    public String URL;
    public String url_image;


    public Tab(String title, String url, String url_image) {
        name = title;
        URL = url;
        this.url_image = url_image;
    }
}

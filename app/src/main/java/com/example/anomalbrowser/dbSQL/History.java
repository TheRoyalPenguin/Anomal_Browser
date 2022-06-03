package com.example.anomalbrowser.dbSQL;

public class History {
    int id;
    private String name;
    private String URL;
    private String data;
    private String time;

    public History(int id, String name, String URL, String data, String time) {
        this.id = id;
        this.name = name;
        this.URL = URL;
        this.data = data;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}

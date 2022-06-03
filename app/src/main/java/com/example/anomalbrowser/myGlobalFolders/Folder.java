package com.example.anomalbrowser.myGlobalFolders;

import com.example.anomalbrowser.mediaLogic.Media;

import java.util.ArrayList;

public class Folder {
    private String name;
    private String pass;
    private String id;

    public Folder(String name, String pass, String id) {
        this.name = name;
        this.pass = pass;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}

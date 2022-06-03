package com.example.anomalbrowser.usersLogic;

public class User {
    private String name, email, urlLogo;

    public User(String name, String email, String urlLogo)
    {
        this.name = name;
        this.email = email;
        this.urlLogo = urlLogo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrlLogo() {
        return urlLogo;
    }

    public void setUrlLogo(String urlLogo) {
        this.urlLogo = urlLogo;
    }
}

package com.example.anomalbrowser.chatLogic;

public class Mess {
public String text;
public String sender;
    public Mess(String text, String sender)
    {
        this.text = text;
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

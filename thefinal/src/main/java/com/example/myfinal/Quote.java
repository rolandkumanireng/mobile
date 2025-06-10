package com.example.myfinal;

import com.google.gson.annotations.SerializedName;

public class Quote {
    @SerializedName("_id")
    private String id;
    @SerializedName("content")
    private String content;
    @SerializedName("author")
    private String author;

    // Constructor
    public Quote(String id, String content, String author) {
        this.id = id;
        this.content = content;
        this.author = author;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    // Setters (opsional, tergantung kebutuhan)
    public void setId(String id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
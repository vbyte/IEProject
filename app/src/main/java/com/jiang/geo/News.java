package com.jiang.geo;

public class News {
    private String description;
    private String image;
    private String title;
    private String url;


    public News(String description, String image, String title, String url) {
        this.description = description;
        this.image = image;
        this.title = title;
        this.url = url;
    }

    public News() {

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
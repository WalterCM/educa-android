package com.instituto.educa;

public class Card {
    private String imgURL;
    private String title;
    private Runnable onClick;

    public Card(String imgURL, String title, Runnable onClick) {
        this.imgURL = imgURL;
        this.title = title;
        this.onClick = onClick;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Runnable getOnClick() {
        return onClick;
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }
}

package com.instituto.educa;

public class Card {
    private String imgURL;
    private String title;
    private Runnable onClick;
    //private String newActivityName;

    public Card(String imgURL, String title, Runnable onClick /*String newActivityName*/) {
        this.imgURL = imgURL;
        this.title = title;
        this.onClick = onClick;
        //this.newActivityName = newActivityName;
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

    /*public String getNewActivityName() {
        return newActivityName;
    }

    public void setNewActivityName(String newActivityName) {
        this.newActivityName = newActivityName;
    }*/
}

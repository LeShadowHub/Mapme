package com.leshadow.mapme;

/**
 * Created by OEM on 7/12/2017.
 */

public class CardModel {
    String cardTitle;
    String imagePath;
    String desc;


    public CardModel(String cardTitle, String imagePath, String desc){
        this.cardTitle = cardTitle;
        this.imagePath = imagePath;
        this.desc = desc;
    }

    public String getcardTitle() {
        return cardTitle;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
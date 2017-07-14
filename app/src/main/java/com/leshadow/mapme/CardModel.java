package com.leshadow.mapme;

/**
 * Created by OEM on 7/12/2017.
 */

public class CardModel {
    String cardTitle;
    int imagePath;
    String desc;
    int isfav;
    int isturned;

    public CardModel(){
        cardTitle = "";
        imagePath = 0;
        desc = "";
    }

    public CardModel(String cardTitle, int imagePath, String desc){
        this.cardTitle = cardTitle;
        this.imagePath = imagePath;
        this.desc = desc;
    }

    public String getCardTitle() {
        return cardTitle;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
    }

    public int getImagePath() {
        return imagePath;
    }

    public void setImagePath(int imagePath) {
        this.imagePath = imagePath;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getIsturned() {
        return isturned;
    }

    public void setIsturned(int isturned) {
        this.isturned = isturned;
    }

    public int getIsfav() {
        return isfav;
    }

    public void setIsfav(int isfav) {
        this.isfav = isfav;
    }
}
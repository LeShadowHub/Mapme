package com.leshadow.mapme;

/**
 * Created by OEM on 7/12/2017.
 */

public class CardModel {
    //String cardTitle;
    String image;
    //String desc;
    //int isFav;
    //int isShared;

    public CardModel(){

    }

    public CardModel(String image){
        //this.cardTitle = cardTitle;
        this.image = image;
    }

    /*public String getCardTitle() {
        return cardTitle;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
    }*/

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    /*public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getIsShared() {
        return isShared;
    }

    public void setIsShared(int isShared) {
        this.isShared = isShared;
    }

    public int getIsfav() {
        return isFav;
    }

    public void setIsfav(int isFav) {
        this.isFav = isFav;
    }*/
}
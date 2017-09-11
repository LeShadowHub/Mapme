package com.leshadow.mapme;

import java.io.Serializable;
import java.util.List;

/**
 * Created by OEM on 7/12/2017.
 */

@SuppressWarnings("serial")
public class CardModel implements Serializable{
    String title;
    String image;
    String desc;
    String key;
    String username;
    String trip;
    float lat;
    float lon;
    int isLiked;
    List<String> likes;
    //int isShared;

    public CardModel(){

    }

    public CardModel(String image){
        //this.cardTitle = cardTitle;
        this.image = image;
    }

    public String getTrip(){
        return trip;
    }

    public void setTrip(String trip){
        this.trip = trip;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getKey(){
        return key;
    }

    public void setKey(String key){
        this.key = key;
    }

    public float getLat(){
        return lat;
    }

    public void setLat(float lat){
        this.lat = lat;
    }

    public float getLon(){
        return lon;
    }

    public void setLon(float lon){
        this.lon = lon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List getLikes(){
        return likes;
    }

    public void setLikes(List<String> likes){
        this.likes = likes;
    }

    /*public int getIsShared() {
        return isShared;
    }

    public void setIsShared(int isShared) {
        this.isShared = isShared;
    }*/

    public int getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(int isLiked) {
        this.isLiked = isLiked;
    }
}
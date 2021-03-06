package com.liuliugeek.sanc.news.Model;

import java.io.Serializable;

/**
 * Created by 73732 on 2016/8/19.
 */
public class Data implements Serializable{
    private String newTitle;
    private String newContent;
    private String newUrl;
    private int newTypeid;
    private int newArcid;
    private String date;
    private int isFavorite;

    public Data(String newTitle, String newContent){
        this.newContent = newContent;
        this.newTitle = newTitle;
    }
    public Data(){}
    public String getNewTitle() {
        return newTitle;
    }

    public String getNewContent() {
        return newContent;
    }

    public String getNewUrl(){
        return newUrl;
    }

    public int getNewTypeid(){
        return newTypeid;
    }

    public int getNewArcid(){
        return newArcid;
    }

    public String getNewDate(){
        return this.date;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setNewTitle(String newTitle) {
        this.newTitle = newTitle;
    }

    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }

    public void setNewUrl(String newUrl){
        this.newUrl = newUrl;
    }

    public void setNewTypeid(int typeid){
        this.newTypeid = typeid;
    }

    public void setNewArcid(int arcid){
        this.newArcid = arcid;
    }

    public void setNewDate(String date){
        this.date = date;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }
}

package com.betterclever.smartlocator.Utils;

import java.util.ArrayList;

/**
 * Created by better_clever on 11/9/16.
 */
public class Item {
    private String name;
    private String location;
    private String latx;
    private String laty;
    private ArrayList<String> tags;
    private String imgPath;

    public Item(){

    }

    public String getItemName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getLatx() {
        return latx;
    }

    public String getLaty() {
        return laty;
    }

    public String getImgPath() {
        return imgPath;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public void setLaty(String laty) {
        this.laty = laty;
    }

    public void setLatx(String latx) {
        this.latx = latx;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }
}

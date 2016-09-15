package com.liuliugeek.sanc.news;

/**
 * Created by 73732 on 2016/9/15.
 */
public class DrawerListData {
    private String itemName;
    private int itemImage;
    private int typeId;

    public DrawerListData(String itemName, int itemImage, int typeId){
        this.itemImage = itemImage;
        this.itemName = itemName;
        this.typeId = typeId;
    }

    public int getItemImage() {
        return itemImage;
    }

    public String getItemName() {
        return itemName;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setItemImage(int itemImage) {
        this.itemImage = itemImage;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
}

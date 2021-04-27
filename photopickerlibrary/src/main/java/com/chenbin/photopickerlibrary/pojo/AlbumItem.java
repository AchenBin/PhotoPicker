package com.chenbin.photopickerlibrary.pojo;

public class AlbumItem {
    private String name;
    private String firstImgPath;
    private int size;

    public AlbumItem(String name, String firstImgPath, int size) {
        this.name = name;
        this.firstImgPath = firstImgPath;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstImgPath() {
        return firstImgPath;
    }

    public void setFirstImgPath(String firstImgPath) {
        this.firstImgPath = firstImgPath;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}

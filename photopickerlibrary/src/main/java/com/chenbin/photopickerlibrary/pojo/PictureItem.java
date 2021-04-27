package com.chenbin.photopickerlibrary.pojo;


import android.graphics.Rect;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * 图片类
 *  收集每张图片信息
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PictureItem implements Serializable {
    private String path;
    private String type;
    private String name;
    private float size;
    private String width;
    private String height;
    private long Date;
    private int orientation;


    private boolean isLoading = false;  //是否处于加载中
    private boolean isChecked = false;  //是否被选中

    private Rect rect;  //记录位置大小

    public PictureItem(String path, String name){
        this.path = path;
        this.name = name;
    }

    public PictureItem(String path, String type, String name, float size, String width, String length) {
        this.path = path;
        this.type = type;
        this.name = name;
        this.size = size;
        this.width = width;
        this.height = length;
    }
    public PictureItem(String path, String type, String name, float size, String width, String length, int orientation) {
        this.path = path;
        this.type = type;
        this.name = name;
        this.size = size;
        this.width = width;
        this.height = length;
        this.orientation = orientation;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }


    public long getDate() {
        return Date;
    }

    public void setDate(long date) {
        Date = date;
    }



    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
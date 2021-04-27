package com.chenbin.photopickerlibrary.Utils;


import com.chenbin.photopickerlibrary.pojo.PictureItem;

import java.util.Comparator;

/**
 * 用于将照片排序，最新的排在最前
 */
public class DateComparator implements Comparator<PictureItem> {

    @Override
    public int compare(PictureItem pictureItem, PictureItem t1) {
        if(pictureItem.getDate() < t1.getDate()){
            return 1;
        }else if(pictureItem.getDate() == t1.getDate()){
            return 0;
        }else
            return -1;
    }
}
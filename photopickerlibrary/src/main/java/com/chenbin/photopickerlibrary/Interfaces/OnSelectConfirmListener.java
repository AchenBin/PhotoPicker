package com.chenbin.photopickerlibrary.Interfaces;

import com.chenbin.photopickerlibrary.pojo.PictureItem;

import java.util.List;

public interface OnSelectConfirmListener {
    void onSelected(List<PictureItem> pictureItemList);
}

package com.chenbin.photopickerlibrary.Builders;

import android.content.Context;
import android.widget.Toast;

import com.chenbin.photopickerlibrary.Activities.PictureSelectActivity;
import com.chenbin.photopickerlibrary.Adapters.PictureSelectAdapter;
import com.chenbin.photopickerlibrary.Interfaces.OnSelectConfirmListener;
import com.chenbin.photopickerlibrary.pojo.PictureItem;

import java.util.ArrayList;
import java.util.List;


public class PhotoPickerBuilder {
    private Context context;
    private int maxNum = 9;
    private OnSelectConfirmListener onSelectConfirmListener = new OnSelectConfirmListener() {
        @Override
        public void onSelected(List<PictureItem> pictureItemList) {
            List<String> pathList = new ArrayList<>();
            for (PictureItem pictureItem : pictureItemList) {
                pathList.add(pictureItem.getPath());
            }
            Toast.makeText(context, pathList.toString(), Toast.LENGTH_SHORT).show();
        }
    };
    //设置context
    public PhotoPickerBuilder with(Context context){
        this.context = context;
        return this;
    }
    //设置最大选择数量
    public PhotoPickerBuilder setMaxSelectNum(int maxNum){
        this.maxNum = maxNum;
        return this;
    }
    //设置选择回调监听
    public PhotoPickerBuilder setOnSelectConfirmListener(OnSelectConfirmListener onSelectConfirmListener){
        this.onSelectConfirmListener = onSelectConfirmListener;
        return this;
    }
    //启动选择页面
    public void start(){
        if(this.context == null)
            throw new IllegalStateException("context不能为空");
        PictureSelectActivity.actionStart(context,maxNum,onSelectConfirmListener);
    }
}

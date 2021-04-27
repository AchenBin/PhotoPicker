package com.chenbin.photopickerlibrary.Builders;

import android.content.Context;
import android.widget.Toast;

import com.chenbin.photopickerlibrary.Activities.PictureSelectActivity;
import com.chenbin.photopickerlibrary.Adapters.PictureSelectAdapter;
import com.chenbin.photopickerlibrary.Interfaces.OnSelectConfirmListener;
import com.chenbin.photopickerlibrary.R;
import com.chenbin.photopickerlibrary.pojo.PictureItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class PhotoPickerBuilder {
    private Context context;
    private int maxNum;             //最大选择数量
    private float titleBar_height;  //标题栏高度
    private int titleBar_color;         //标题栏背景颜色
    private int titleBar_text_color;         //标题栏字体颜色
    private int columnNum;  //图片显示列数
    private OnSelectConfirmListener onSelectConfirmListener;    //选择回调监听


    public PhotoPickerBuilder(Context context){
        this.context = context;
        this.maxNum = 9;
        this.columnNum = 4;
        this.titleBar_height = context.getResources().getDimension(R.dimen.actionbar_height);
        this.titleBar_color = context.getResources().getColor(R.color.colorPrimaryDark,null);
        this.titleBar_text_color = context.getResources().getColor(R.color.white,null);
        this.onSelectConfirmListener = new OnSelectConfirmListener() {
            @Override
            public void onSelected(List<PictureItem> pictureItemList) {
                List<String> pathList = new ArrayList<>();
                for (PictureItem pictureItem : pictureItemList) {
                    pathList.add(pictureItem.getPath());
                }
                Toast.makeText(context, pathList.toString(), Toast.LENGTH_SHORT).show();
            }
        };
    }


    //设置最大选择数量
    public PhotoPickerBuilder setMaxSelectNum(int maxNum){
        this.maxNum = maxNum;
        return this;
    }

    //设置标题栏高度
    public PhotoPickerBuilder setTitleBarHeight(float titleBar_height){
        this.titleBar_height = titleBar_height;
        return this;
    }

    //设置标题栏背景颜色
    public PhotoPickerBuilder setTitleBarColor(int colorId){
        this.titleBar_color = context.getResources().getColor(colorId,null);
        return this;
    }
    //设置标题栏字体颜色
    public PhotoPickerBuilder setTitleBarTextColor(int colorId){
        this.titleBar_text_color = context.getResources().getColor(colorId,null);
        return this;
    }
    //设置图片列数
    public PhotoPickerBuilder setColumnNum(int columnNum){
        this.columnNum = columnNum;
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
        PhotoPickerConfig config = new PhotoPickerConfig(maxNum,titleBar_height,titleBar_color,titleBar_text_color,columnNum);
        PictureSelectActivity.actionStart(context,config,onSelectConfirmListener);
    }
}

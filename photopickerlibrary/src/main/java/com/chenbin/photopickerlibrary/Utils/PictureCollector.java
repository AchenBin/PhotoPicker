package com.chenbin.photopickerlibrary.Utils;

import android.content.Context;

import com.chenbin.photopickerlibrary.R;
import com.chenbin.photopickerlibrary.pojo.PictureItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PictureCollector {
    // <父路径，图片信息列表>
    private Map<String, List<PictureItem>> pictureMap;

    public PictureCollector(){
        this.pictureMap = new HashMap<>();
    }
    public PictureCollector(HashMap pictureMap){
        this.pictureMap = pictureMap;
    }


    /**
     * 从其他Collector中添加相册以及图片信息
     * @param otherCollector
     */
    public void addFromOther(PictureCollector otherCollector){
        for(Map.Entry<String, List<PictureItem>> entry : otherCollector.getPictureMap().entrySet()){
            pictureMap.put(entry.getKey(),entry.getValue());
        }
    }


    /**
     * 添加新的父目录及其子图片信息列表
     * @param parentPath       父目录名
     * @param childInfoList     子信息列表
     */
    public void putParentAndList(String parentPath, List<PictureItem> childInfoList){
        pictureMap.put(parentPath,childInfoList);
    }


    /**
     * 获取指定目录下的“图片信息”列表
     * @param parentPathName    父目录名，即pictureMap中的键
     * @return      “图片信息”列表
     */
    public List<PictureItem> getChildPictureList(String parentPathName){
        if(pictureMap.containsKey(parentPathName)){
            return pictureMap.get(parentPathName);
        }
        else return new ArrayList<>();
    }





    /**
     * 判断该照片父路径是否已存在Map键中
     * @param parentPath    父路径
     * @return     键中是/否存在
     */
    public boolean hasParentPathKey(String parentPath){
        if(pictureMap.containsKey(parentPath))
            return true;
        else return false;
    }


    /**
     * 获取所有的父路径(所有图片最前)
     * @return  父目录的绝对路径（用于从map中获取图片信息列表）
     */
    public List<String> getAllParentPath(Context context){
        List<String> parentPathList = new ArrayList();

        for(HashMap.Entry<String, List<PictureItem>> entry : pictureMap.entrySet()) {
            System.out.println(entry.getKey());
            if(entry.getKey() != null){
                if(entry.getKey().equals(context.getResources().getString(R.string.all_picture))){
                    parentPathList.add(0,entry.getKey());
                }else parentPathList.add(entry.getKey());
            }
        }
        return parentPathList;
    }

    /**
     * 获取所有的父路径(最近编辑最前)
     * @return  父目录的绝对路径（用于从map中获取图片信息列表）
     */
    public List<String> getAllParentPathWithEdit(Context context){
        List<String> parentPathList = new ArrayList();

        for(HashMap.Entry<String, List<PictureItem>> entry : pictureMap.entrySet()) {
//            System.out.println(entry.getKey());
            if(entry.getKey().equals(context.getResources().getString(R.string.recently_edit))){
                parentPathList.add(0,entry.getKey());
            }else parentPathList.add(entry.getKey());
        }
        return parentPathList;
    }


    public Map<String, List<PictureItem>> getPictureMap() {
        return pictureMap;
    }

    public void setPictureMap(Map<String, List<PictureItem>> pictureMap) {
        this.pictureMap = pictureMap;
    }
}

package com.chenbin.photopickerlibrary.Builders;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PhotoPickerConfig implements Serializable {
    private int maxNum;     //最大选择数量
    private float titleBar_height;  //标题栏高度
    private int titleBar_color;         //标题栏背景颜色
    private int titleBar_text_color;         //标题栏字体颜色
    private int columnNum;  //图片显示列数
}

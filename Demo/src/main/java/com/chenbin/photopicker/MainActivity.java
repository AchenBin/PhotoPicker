package com.chenbin.photopicker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chenbin.photopicker.Utils.PermissionUtils;
import com.chenbin.photopickerlibrary.Builders.PhotoPickerBuilder;

import com.chenbin.photopicker.R;
import com.chenbin.photopickerlibrary.Interfaces.OnSelectConfirmListener;
import com.chenbin.photopickerlibrary.pojo.PictureItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        PermissionUtils.getPermission(this);    //获取权限
        img = findViewById(R.id.img_test);
    }

    /**
     * 启动选择界面
     * @param view
     */
    public void clickToStart(View view) {
        new PhotoPickerBuilder()
                .with(this)
                .setMaxSelectNum(1)
                .setOnSelectConfirmListener(new OnSelectConfirmListener() {
                    @Override
                    public void onSelected(List<PictureItem> pictureItemList) {
                        Glide.with(MainActivity.this)
                                .load(pictureItemList.get(0).getPath())
                                .into(img);
                    }
                })
                .start();
        //测试
    }
}

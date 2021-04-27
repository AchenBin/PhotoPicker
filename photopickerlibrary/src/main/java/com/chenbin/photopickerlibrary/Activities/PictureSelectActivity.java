package com.chenbin.photopickerlibrary.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.chenbin.photopickerlibrary.Adapters.PctSelectedAdapter;
import com.chenbin.photopickerlibrary.Adapters.PictureSelectAdapter;
import com.chenbin.photopickerlibrary.Adapters.SpinnerAdapter;
import com.chenbin.photopickerlibrary.Builders.PhotoPickerBuilder;
import com.chenbin.photopickerlibrary.Builders.PhotoPickerConfig;
import com.chenbin.photopickerlibrary.Interfaces.OnSelectConfirmListener;
import com.chenbin.photopickerlibrary.R;
import com.chenbin.photopickerlibrary.Utils.DateComparator;
import com.chenbin.photopickerlibrary.Utils.PictureCollector;
import com.chenbin.photopickerlibrary.Utils.PictureUtils;
import com.chenbin.photopickerlibrary.Views.TitleBar;
import com.chenbin.photopickerlibrary.pojo.AlbumItem;
import com.chenbin.photopickerlibrary.pojo.PictureItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import butterknife.ButterKnife;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


import static com.chenbin.photopickerlibrary.Utils.PermissionUtils.getPermission;
import static com.chenbin.photopickerlibrary.Utils.PictureUtils.Tag;

public class PictureSelectActivity extends BaseActivity {

    private static final String TAG = "PictureSelectActivity";

    private int maxNum = 9;
    private int columnNum = 4;  //图片显示列数
    private static OnSelectConfirmListener onSelectConfirmListener;

    private List<PictureItem> pictureInfoList;
    private List<AlbumItem> albumInfoList = new ArrayList<>();

    private RecyclerView recyclerImgSelect;
    private RecyclerView recycler_is_select;

    private  List<PictureItem> isSelectedList = new ArrayList<>(); //static后返回可以保留列表！！！！！
    public PictureSelectAdapter adapter;

    public static TitleBar titleBar;
    public static TextView is_select_num; //底部显示已选择


    private AppCompatSpinner spinner_album;

//    public static void actionStart(Context context, int maxNum1, OnSelectConfirmListener onSelectConfirmListener1) {
//        maxNum = maxNum1;
//        onSelectConfirmListener = onSelectConfirmListener1;
//        Intent intent = new Intent(context,PictureSelectActivity.class);
//        context.startActivity(intent);
//    }

    public static void actionStart(Context context, PhotoPickerConfig config, OnSelectConfirmListener onSelectConfirmListener1) {
        onSelectConfirmListener = onSelectConfirmListener1;
        Intent intent = new Intent(context,PictureSelectActivity.class);
        intent.putExtra("config",config);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picutre_select);
        if(isSelectedList == null) isSelectedList = new ArrayList<>();
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();   //隐藏自带标题栏
        }
        Log.e(TAG,isSelectedList.toString());
        initView();
        initConfig();
        getPermission(this);
    }

    /**
     * 初始化配置
     */
    PhotoPickerConfig config;
    private void initConfig() {
        Intent intent = getIntent();
        config = (PhotoPickerConfig)intent.getSerializableExtra("config");
        //最大选择数量
        maxNum = config.getMaxNum();
        //标题栏
        titleBar.setHeight((int) config.getTitleBar_height());
        titleBar.setColor(config.getTitleBar_color());
        titleBar.setTitleBarTextColor(config.getTitleBar_text_color());
        //图片显示列数
        columnNum = config.getColumnNum();
    }


    public void initView() {
        recyclerImgSelect = findViewById(R.id.recycler_img_select);
        recycler_is_select = findViewById(R.id.recycler_is_select);
        is_select_num = findViewById(R.id.is_select_num);
        spinner_album = findViewById(R.id.spinner_album);

        titleBar = findViewById(R.id.title_pct_select);
        titleBar.getTv_btn_title_bar().setOnClickListener(getOnclickListener());
        titleBar.setNextEnable(false);  //一开始禁止确认选择
//        titleBar.getTv_title_bar().setHeight((int) config.getTitleBar_height());




        initSelectedView(); //添加已选列表视图
        initAllPictureView();///添加图片列表视图
    }

    //选择完成回调
    private View.OnClickListener getOnclickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (isSelectedList.size() > 0) {
                    List<PictureItem> tempList = isSelectedList;
                    if(onSelectConfirmListener != null){
                        onSelectConfirmListener.onSelected(tempList);
                    }
//                    isSelectedList = new ArrayList<>();
//                Toast.makeText(PictureSelectActivity.this, "已选择："+tempList.toString(), Toast.LENGTH_SHORT).show();
                    finish();
//                }
            }
        };
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initView();
        initSelectedView();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        isSelectedList.clear();
    }

    /**
     * 点击图片后加入已选队列
     */
    public void initSelectedView() {
        final PctSelectedAdapter selectedAdapter = new PctSelectedAdapter(isSelectedList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recycler_is_select.setLayoutManager(linearLayoutManager);
        recycler_is_select.setAdapter(selectedAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(recycler_is_select, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);

        //选择图片添加到已选列表
        PictureSelectAdapter.setOnPictureSelectListener(pictureItem -> {
            Log.i(TAG, "选择图片" + pictureItem.getName());
//                Toast.makeText(PictureSelectActivity.this, "选择图片", Toast.LENGTH_SHORT).show();

            //达到选择上限
            if (isSelectedList.size() == maxNum) {
                    Toast.makeText(PictureSelectActivity.this, getString(R.string.most_select) + " " + isSelectedList.size() + " " + getString(R.string.item_photos), Toast.LENGTH_SHORT).show();
                return;
            }
            isSelectedList.add(pictureItem);
            int position = isSelectedList.lastIndexOf(pictureItem);
            selectedAdapter.notifyItemInserted(position);
            selectedAdapter.notifyItemRangeChanged(position, isSelectedList.size() - position);    //重新绑定数据，不然只会改变界面，但数据不会重新绑定
            recycler_is_select.scrollToPosition(isSelectedList.size() - 1);   //滑动到底部

            if (isSelectedList.size() > 0) {
                titleBar.setNextEnable(true);
            } else {
                titleBar.setNextEnable(false);
            }
            //显示已选数量
            is_select_num.setText(
                    getString(R.string.has_selected)
                            + " " +
                            isSelectedList.size()
                            +" "+
                            getString(R.string.photo));
        });
    }

    /**
     * 一进来填充所有图片，以及填充相册信息列表
     */
    PictureCollector mpictureCollector;

    public void initAllPictureView() {
        albumInfoList.clear();
        PictureUtils.setOnGetPictureListener(new PictureUtils.OnGetPictureListener() {
            @Override
            public void OnGetPictureOK(final PictureCollector pictureCollector) {
                mpictureCollector = pictureCollector;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pictureInfoList = new ArrayList<>();
                        List<String> albumTitleList = pictureCollector.getAllParentPath(getApplicationContext());

                        for (String albumName : albumTitleList) {
                            Log.i(Tag,albumName);
                            List<PictureItem> imgList =  pictureCollector.getChildPictureList(albumName);
                            String firstImgPath;
                            if(imgList.size()>0) {
                                Collections.sort(imgList, new DateComparator()); //这个方法以后List里的数据是已经排好序的
                                firstImgPath = imgList.get(0).getPath();
                            }else{
                                firstImgPath = "";
                            }
                            //将相册信息存储
                            albumInfoList.add(new AlbumItem(albumName,firstImgPath,imgList.size()));
                        }
                        //设置下拉选择相册适配器，以及监听
                        spinner_album.setAdapter(new SpinnerAdapter(albumInfoList));
                        spinner_album.setOnItemSelectedListener(getAlbumSelectListener());
                        //瀑布布局，列数，方向
                        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(columnNum, StaggeredGridLayoutManager.VERTICAL);
                        recyclerImgSelect.setLayoutManager(layoutManager);
//                        if(pictureInfoList!=null && pictureInfoList.size()>0){
                        adapter = new PictureSelectAdapter(pictureInfoList, maxNum);
                        recyclerImgSelect.setAdapter(adapter);
                        recyclerImgSelect.setHasFixedSize(true);
//                        }
                        //回弹效果
                        OverScrollDecoratorHelper.setUpOverScroll(recyclerImgSelect, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
                    }
                });
            }
        });
        ////获取相册图片
        PictureUtils.getAllPicture(this);
    }


    /**
     *相册选择
     * @return
     */
    public AdapterView.OnItemSelectedListener getAlbumSelectListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                Toast.makeText(PictureSelectActivity.this, "已选择相册："+albumInfoList.get(position).getName(), Toast.LENGTH_SHORT).show();
                pictureInfoList =  mpictureCollector.getChildPictureList(albumInfoList.get(position).getName());
                if(pictureInfoList!=null && pictureInfoList.size()>0) {
                    adapter.setInfoList(pictureInfoList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //通过requestCode来识别是否同一个请求
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户同意，执行操作
                initView();
            } else {
                //用户不同意，向用户展示该权限作用
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.request_permission_again)
                            .setPositiveButton(R.string.confirm, (dialog1, which) ->
                                    getPermission(this)
                            )
                            .setNegativeButton(R.string.cancel, null)
                            .create()
                            .show();
                }
            }
        }
    }

}
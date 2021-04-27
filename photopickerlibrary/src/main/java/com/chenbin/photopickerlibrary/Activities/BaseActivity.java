package com.chenbin.photopickerlibrary.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import com.chenbin.photopickerlibrary.R;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    //获取TAG的activity名称
    protected final String TAG = this.getClass().getSimpleName();
    //封装Toast对象
    private static Toast toast;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);     //状态栏深色字体
//        getWindow().setBackgroundDrawableResource(R.mipmap.bg_qdy);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //不会旋转
//        getSupportActionBar().hide();
//        settingActionBar();
    }



//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch(item.getItemId()){
//            case android.R.id.home: //标题栏回退按钮
//                onBackPressed();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }


    /**
     * 显示提示  toast
     *
     * @param msg 提示信息
     */
    @SuppressLint("ShowToast")
    public void showToast(String msg) {
        try {
            if (null == toast) {
                toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
            } else {
                toast.setText(msg);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toast.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            //解决在子线程中调用Toast的异常情况处理
            Looper.prepare();
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }

}
package com.chenbin.photopickerlibrary.Views;

import android.app.Instrumentation;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.chenbin.photopickerlibrary.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class TitleBar extends ConstraintLayout {

    private ImageView tv_back;
    private TextView tv_title_bar;
    private TextView tv_btn_title_bar;

    private int enableColor;    //下一步按钮可操作时的颜色

    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.bar_title,this);
        initView(context,attrs);
    }

    public void initView( Context context, AttributeSet attrs){
        tv_back = findViewById(R.id.tv_back);
        tv_title_bar = findViewById(R.id.tv_title_bar);
        tv_btn_title_bar = findViewById(R.id.tv_btn_title_bar);

        setBackOnclick(tv_back);

        //xml视图属性绑定
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.TitleBar);
        setTitle(typedArray.getString(R.styleable.TitleBar_title));
        String btnText = typedArray.getString(R.styleable.TitleBar_btn_text);
        if(btnText!=null && !btnText.equals("")){
            setNextBtnText(btnText);
        }
        int id = typedArray.getResourceId(R.styleable.TitleBar_btn_src,0);
        if(id != 0){
            setNextBtnSrc(context.getDrawable(id));
        }

        setTitleBarTextColor(getResources().getColor(R.color.white,null));
    }


    /**
     * 默认返回
     * @param view
     */
    public void setBackOnclick(View view) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread () {
                    public void run () {
                        try {
                            Instrumentation inst= new Instrumentation();
                            inst.sendKeyDownUpSync(KeyEvent. KEYCODE_BACK);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }
        });
    }



    public void setOKBtn(){
//        getTv_title_bar().setText(R.string.adjust);
        getTv_btn_title_bar().setText(R.string.complete);
        getTv_btn_title_bar().setTextColor(enableColor);
    }

    public void setNextEnable(boolean isEnable){
        if(isEnable){
            getTv_btn_title_bar().setTextColor(enableColor);
            getTv_btn_title_bar().setEnabled(true);
        }else{
            getTv_btn_title_bar().setTextColor(getResources().getColor(R.color.white_translucence,null));
            getTv_btn_title_bar().setEnabled(false);
        }
    }



    public ImageView getTv_back() {
        return tv_back;
    }

    public TextView getTv_title_bar() {
        return tv_title_bar;
    }

    public TextView getTv_btn_title_bar() {
        return tv_btn_title_bar;
    }

    public void setTitle(String title){
        tv_title_bar.setText(title);
    }

    public void setNextBtnText(String text){
        tv_btn_title_bar.setVisibility(VISIBLE);
        tv_btn_title_bar.setText(text);
    }

    public void setNextBtnSrc(Drawable drawable){
        tv_btn_title_bar.setVisibility(VISIBLE);
        tv_btn_title_bar.setText("");
        tv_btn_title_bar.setHeight(20);  //没用但是可以让背景变成正方形，图片不会变形
        tv_btn_title_bar.setWidth(20);
        ViewGroup.LayoutParams layoutParams = tv_btn_title_bar.getLayoutParams();
        layoutParams.width = 90;
        layoutParams.height = 90;
        tv_btn_title_bar.setLayoutParams(layoutParams);
        tv_btn_title_bar.setBackground(drawable);
    }
    //设置标题栏高度
    public void setHeight(int height){
        TextView title_textView = getTv_title_bar();
        ViewGroup.LayoutParams layoutParams = title_textView.getLayoutParams();
        layoutParams.height = height;
        title_textView.setLayoutParams(layoutParams);
    }
    //设置标题栏颜色
    public void setColor(int color){
        getTv_title_bar().setBackgroundColor(color);
    }
    //设置下一步按钮可操作时的颜色
    public void setTitleBarTextColor(int color){
        enableColor = color;
        tv_title_bar.setTextColor(color);
        tv_back.setColorFilter(color);
    }
}

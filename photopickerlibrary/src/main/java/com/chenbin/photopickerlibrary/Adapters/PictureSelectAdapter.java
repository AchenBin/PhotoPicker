package com.chenbin.photopickerlibrary.Adapters;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chenbin.photopickerlibrary.R;
import com.chenbin.photopickerlibrary.pojo.PictureItem;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PictureSelectAdapter extends RecyclerView.Adapter<PictureSelectAdapter.ViewHolder>{
    private static final String Tag = "PictureSelectAdapter";

    protected ViewHolder viewHolder;
    Context context;
    List<PictureItem> infoList;

    Animation animation;
    public List<PictureItem> checkedList;
    int canCheckNum;   //可勾选数量          //要可以多选相册，请设为static,并在此初始化
    private List<Integer> whichCheckedList; //记录哪个项已选

    private static OnPictureSelectListener onPictureSelectListener;

    public static void setOnPictureSelectListener(OnPictureSelectListener onPictureSelectListener1){
        onPictureSelectListener = onPictureSelectListener1;
    }



    public interface OnPictureSelectListener{
        void onSelected(PictureItem pictureItem);
    }

    public PictureSelectAdapter(List<PictureItem> infoList, int canCheckNum){
        this.infoList = infoList;
        this.canCheckNum = canCheckNum;
        checkedList = new ArrayList<>();
        whichCheckedList = new ArrayList<>();
    }

    public PictureSelectAdapter(List<PictureItem> infoList){
        this.infoList = infoList;
        whichCheckedList = new ArrayList<>();
    }

    public void setInfoList(List<PictureItem> infoList) {
        this.infoList = infoList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private View itemView;      //整个组件
        private CheckBox radio_img_select;
        private ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = itemView.findViewById(R.id.imageView);
            radio_img_select = itemView.findViewById(R.id.radio_img_select);

            //
            radio_img_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        if(!whichCheckedList.contains(compoundButton.getTag())){
                            whichCheckedList.add(new Integer((Integer) compoundButton.getTag()));
                            notifyItemChanged((Integer) compoundButton.getTag());
                        }
                    }else{
                        if(whichCheckedList.contains((Integer) compoundButton.getTag())){
                            whichCheckedList.remove(new Integer((Integer) compoundButton.getTag()));
                            notifyItemChanged((Integer) compoundButton.getTag());
                        }
                    }
                }
            });
            //
            //点击项
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PictureItem pictureItem = infoList.get((Integer) view.getTag());
                    radio_img_select.setChecked(!radio_img_select.isChecked());
                    itemView.startAnimation(animation);//開始动画
                    if(onPictureSelectListener != null){

                        onPictureSelectListener.onSelected(pictureItem);
                    }
                }
            });


        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.anim_click);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_img_select,parent,false);
        viewHolder = new ViewHolder(view);
        viewHolder.radio_img_select.setVisibility(View.INVISIBLE);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final PictureItem pictureItem = infoList.get(position);

        Glide.with(holder.itemView)
                .asBitmap()         //作为bitmap对待，不加载gif
                .load(pictureItem.getPath())
                .skipMemoryCache(false)
                .placeholder(R.color.gray)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .dontAnimate()
                .into(holder.imageView);

        //选择改变
        holder.radio_img_select.setTag(position);

        ViewTreeObserver observer = holder.imageView.getViewTreeObserver();
        observer.addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                //记录位置大小
                int[] location = new  int[2] ;
//                holder.imageView.getLocationInWindow(location); //获取在当前窗口内的绝对坐标，含toolBar
                holder.imageView.getLocationOnScreen(location); //获取在整个屏幕内的绝对坐标，含statusBar
//                Log.i("asasas","x="+location[0]+",y="+location[1]);
                pictureItem.setRect(
                        new Rect(0+(int)location[0],
                                0+(int)location[1],
                                (int)location[0]+holder.imageView.getWidth(),
                                (int)location[1]+holder.imageView.getHeight())
                );
            }
        });
        holder.itemView.setTag(position);
//        //点击项
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                holder.radio_img_select.setChecked(!holder.radio_img_select.isChecked());
//                holder.itemView.startAnimation(animation);//開始动画
//                if(onPictureSelectListener != null){
//
//                     onPictureSelectListener.onSelected(pictureItem);
//                }
//            }
//        });

        //根据记录还原选择
        if(whichCheckedList.contains(holder.getLayoutPosition())){
            holder.radio_img_select.setChecked(true);
        }else holder.radio_img_select.setChecked(false);


    }





    public List<PictureItem> getCheckedList() {
        return checkedList;
    }

    @Override
    public int getItemCount() {
        if(infoList != null)
            return infoList.size();
        else return 0;
    }

}

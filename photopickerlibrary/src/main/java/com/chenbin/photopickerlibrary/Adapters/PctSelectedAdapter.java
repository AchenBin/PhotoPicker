package com.chenbin.photopickerlibrary.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chenbin.photopickerlibrary.Activities.PictureSelectActivity;
import com.chenbin.photopickerlibrary.R;
import com.chenbin.photopickerlibrary.pojo.PictureItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PctSelectedAdapter extends RecyclerView.Adapter<PctSelectedAdapter.ViewHolder> {
    private static final String TAG = "PctSelectedAdapter";
    Animation animation;
    List<PictureItem> pictureItemList;
    private ViewHolder viewHolder;


    public PctSelectedAdapter(List<PictureItem> pictureItemList){
        this.pictureItemList = pictureItemList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_is_selected,parent,false);
        animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.anim_click);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PictureItem pictureItem = pictureItemList.get(position);
        Glide.with(holder.itemView.getContext())
                .asBitmap()         //作为bitmap对待，不加载gif
                .load(pictureItem.getPath())
                .placeholder(holder.img_is_select.getDrawable())
                .into(holder.img_is_select);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                holder.itemView.startAnimation(animation);//開始动画
                pictureItemList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, pictureItemList.size() - position);    //重新绑定数据
                if(pictureItemList.size()>0){
                    PictureSelectActivity.is_select_num.setText(
                            holder.itemView.getContext().getString(R.string.has_selected)
                                    +" "+
                                    pictureItemList.size()
                                    +" "+
                                    holder.itemView.getContext().getString(R.string.photo));

                    PictureSelectActivity.titleBar.setNextEnable(true);
                }else{
                    PictureSelectActivity.titleBar.setNextEnable(false);
                    PictureSelectActivity.is_select_num.setText( holder.itemView.getContext().getString(R.string.please_select_photo));
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        if(pictureItemList != null){
            return pictureItemList.size();
        }else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img_is_select;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_is_select = itemView.findViewById(R.id.img_is_select);

        }
    }
}

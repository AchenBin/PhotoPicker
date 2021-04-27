package com.chenbin.photopickerlibrary.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chenbin.photopickerlibrary.R;
import com.chenbin.photopickerlibrary.pojo.AlbumItem;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {

    private List<AlbumItem> albumItemList;

    public SpinnerAdapter(List<AlbumItem> albumItemList){
        this.albumItemList = albumItemList;

    }

    @Override
    public int getCount() {
        return albumItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return albumItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_select,viewGroup,false);
        ImageView imgView_album = itemView.findViewById(R.id.imgView_album);
        TextView textView_albumName = itemView.findViewById(R.id.textView_albumName);
        TextView textView_album_num = itemView.findViewById(R.id.textView_album_num);

        Glide.with(itemView)
                .asBitmap()         //作为bitmap对待，不加载gif
                .load(albumItemList.get(i).getFirstImgPath())
                .into(imgView_album);
        textView_albumName.setText(albumItemList.get(i).getName());
        textView_album_num.setText(albumItemList.get(i).getSize()+"");


        return itemView;
    }
}

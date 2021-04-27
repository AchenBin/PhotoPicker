package com.chenbin.photopickerlibrary.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.chenbin.photopickerlibrary.R;
import com.chenbin.photopickerlibrary.pojo.PictureItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.FileProvider;

public class PictureUtils {
    public static final String Tag = "PictureUtils";
    private static PictureCollector pictureCollector;
    private static Bitmap bitmapAf; //用于在activity中传递图片

    public interface OnGetPictureListener{
        void OnGetPictureOK(PictureCollector pictureCollector);
    }
    public static OnGetPictureListener onGetPictureListener;
    public static void setOnGetPictureListener(OnGetPictureListener onGetPictureListener1){
        onGetPictureListener = onGetPictureListener1;
    }

    public static void getAllPicture(final Context mContext)    {
        new Thread(){
            @Override
            public void run() {
//                super.run();
                pictureCollector = new PictureCollector();
                //存所有图片
                List<PictureItem> allPictureList = new ArrayList<>();
                pictureCollector.putParentAndList(mContext.getResources().getString(R.string.all_picture),allPictureList);

                Cursor photoCursor = mContext.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null, null, null, null);
                //////获取索引，避免在循环中重复获取
                int dirIndex = photoCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME); //相册名
                int pathIndex = photoCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int dateIndex = photoCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
                int nameIndex = photoCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);

                int heightIndex = photoCursor.getColumnIndex(MediaStore.Images.Media.HEIGHT);
                int widthIndex = photoCursor.getColumnIndex(MediaStore.Images.Media.WIDTH);
                int typeIndex = photoCursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE);
                int sizeIndex = photoCursor.getColumnIndex(MediaStore.Images.Media.SIZE);
                int orientationIndex = photoCursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION);
                /////
                try {
                    while (photoCursor.moveToNext()) {
                        //照片路径
                        String photoPath = photoCursor.getString(pathIndex);
                        //照片日期
                        long photoDate = photoCursor.getLong(dateIndex);
                        //照片标题
                        String photoTitle = photoCursor.getString(nameIndex);
                        //类型
                        String photoType = photoCursor.getString(typeIndex);

                        //相册名
//                        String parentDir = getFileName(new File(photoPath).getParentFile().getAbsolutePath());
                        String parentDir = photoCursor.getString(dirIndex);

                        //宽高
                        String photoLength = photoCursor.getString(heightIndex);
                        String  photoWidth = photoCursor.getString(widthIndex);
                        //转向的调换方向
                        int orientation = photoCursor.getInt(orientationIndex);

                        //照片大小
                        float size = photoCursor.getFloat(sizeIndex);

                        PictureItem pictureItem = new PictureItem(photoPath,photoType,photoTitle,
                                size,photoWidth,photoLength,orientation);
                        pictureItem.setDate(photoDate);
                        //已存在父目录就添加，不存在就创建再添加
                        if(pictureCollector.hasParentPathKey(parentDir)){
                            List<PictureItem> pictureItems = pictureCollector.getChildPictureList(parentDir);
                            pictureItems.add(pictureItem);
                        }else{
                            List<PictureItem> pictureItems = new ArrayList<>();
                            pictureItems.add(pictureItem);
                            pictureCollector.putParentAndList(parentDir,pictureItems);
                        }
                        //添加到所有图片列表中
                        allPictureList.add(pictureItem);
                    }
                }catch (Exception e) {
                    Log.e(Tag,e+"");
                    e.printStackTrace();
                } finally {
                    if(onGetPictureListener != null)
                        onGetPictureListener.OnGetPictureOK(pictureCollector);   //回调
                    if (photoCursor!= null) photoCursor.close();
                }

            }
        }.start();

    }



    /**
     * 获取创作的图片文件
     * @param context
     */
    public static void getCollectionPicture(Context context){
        pictureCollector = new PictureCollector();
        //存最近编辑的所有图片
        List<PictureItem> allPictureList = new ArrayList<>();

        String dirPath = context.getExternalFilesDir(null).getAbsolutePath()+File.separator+"PIS";
        File dir = new File(dirPath);
        //由于默认放在pis目录下，其他目录也在pis下，所以要先遍历一次，不然默认的图不会添加到“最近编辑”
        if(dir.length()>0){
            for (File file : dir.listFiles()) {
                findPicture(allPictureList,file);
            }
        }

        pictureCollector.putParentAndList(context.getResources().getString(R.string.recently_edit),allPictureList);//添加最近编辑目录
        if(onGetPictureListener != null)
            onGetPictureListener.OnGetPictureOK(pictureCollector);   //回调
    }


    private static void findPicture(List<PictureItem> pictureItemList,File dir){
        if(dir == null || pictureItemList==null) return;
        if(!dir.isDirectory()){
            PictureItem pictureItem = new PictureItem(dir.getAbsolutePath(),dir.getName());
            pictureItemList.add(pictureItem);
        }else {
            List<PictureItem> list = new ArrayList<>();
            pictureCollector.putParentAndList(PictureUtils.getFileName(dir.getAbsolutePath()),list);
            for (File file : dir.listFiles()) {
                findPicture(list,file);
            }
        }
    }

    /**
     *去除绝对路径中多余的信息，只保留最后一部分目录名
     * @param filePath
     * @return  父目录名
     */
    public static String getFileName(String filePath){
        int start = filePath.lastIndexOf("/");
        if(start != -1)
            return filePath.substring(start+1);
        else return filePath;
    }

    /**
     * 获取绝对路径，去除具体文件名
     * @param filePath
     * @return
     */
    public static String getFilePath(String filePath){
        if(filePath != null){
            int end = filePath.lastIndexOf("/");
            return filePath.substring(0,end);
        }
        else return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 图片大小单位转换
     * @param num
     * @return
     */
    public static String UnitTransform(double num){
        String result;
        String[] unit = {"B","KB","MB","GB","TB","PB","EB"};
        int i = 0;
        boolean isPositive = true;  //是否为正
        if(num < 0){
            num = -num;
            isPositive = false;
        }
        while(num > 1000 && i<unit.length){
            num = num/1000;
            i++;
        }
        BigDecimal b  =   new  BigDecimal(num);
        float floatValue = b.setScale(2,  BigDecimal.ROUND_HALF_UP).floatValue();
        result = floatValue+unit[i];
        if(isPositive)
            return result;
        else return "-"+result;
    }

    /**
     * 去除文件后缀
     * @param fileName
     * @return
     */
    public static String deleteHouZhui(String fileName){
        if(fileName != null){
            int end = fileName.lastIndexOf(".");
            return fileName.substring(0,end);
        }
        else return "";
    }

    /**
     * 获取文件后缀
     * @param fileName
     * @return
     */
    public static String getHouZhui(String fileName){
        return fileName.substring(fileName.lastIndexOf("."));
    }


    /**
     * 获取图片方向
     * @param imgpath
     * @return
     */
    public static int getDigress(String imgpath){
        int digree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imgpath);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        if (exif != null) {
            // 读取图片中相机方向信息
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            // 计算旋转角度
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    digree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    digree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    digree = 270;
                    break;
                default:
                    digree = 0;
                    break;
            }
        }
        if (digree != 0) {
            // 旋转图片
            return digree;
        }else return 0;
    }


    /**
     * 旋转图片
     * @param bitmap
     * @param degrees
     */
    public static Bitmap rotateImg(Bitmap bitmap,int degrees){
        Matrix matrix = new Matrix();
        if(degrees != 0){
            matrix.setRotate(degrees);
            bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        }
        return bitmap;
    }

    /**
     * 图片比例压缩
     * @param percent   宽高压到的百分比
     * @return
     */
    public static Bitmap resolutionCompress(Bitmap beforeBitmap,int percent,int digress){
        if(digress!=0){
            Matrix matrix = new Matrix();
            matrix.setRotate(digress);
            beforeBitmap = Bitmap.createBitmap(beforeBitmap,0,0,beforeBitmap.getWidth(),beforeBitmap.getHeight(),matrix,true);
        }
        float rate = (float)percent/100;
        int newWidth = (int)(beforeBitmap.getWidth()*rate);
        int newHeight = (int)(beforeBitmap.getHeight()*rate);

        try{
            beforeBitmap = Bitmap.createScaledBitmap(beforeBitmap,newWidth,newHeight,true);
            Log.i(Tag,beforeBitmap.getWidth()+"x"+beforeBitmap.getHeight());
            Log.i(Tag,"尺寸压缩："+beforeBitmap.getWidth()+"X"+beforeBitmap.getHeight());
        }catch (OutOfMemoryError e){
            Log.e(Tag,"尺寸压缩出错："+e);
        }
        return beforeBitmap;
    }
    /**
     * 图片指定宽高压缩
     * @return
     */
    public static Bitmap appointResolutionCompress(Bitmap beforeBitmap,int maxLength,int digress){
        int newWidth = beforeBitmap.getWidth();
        int newHeight = beforeBitmap.getHeight();

        //重设最大宽高
        if(beforeBitmap.getWidth()>beforeBitmap.getHeight()){
            newHeight = (int) (maxLength*newHeight/(float)newWidth);
            newWidth = maxLength;
        }else{
            newWidth = (int) (maxLength*newWidth/(float)newHeight);
            newHeight = maxLength;
        }

        if(digress!=0){
            Matrix matrix = new Matrix();
            matrix.setRotate(digress);
            beforeBitmap = Bitmap.createBitmap(beforeBitmap,0,0,beforeBitmap.getWidth(),beforeBitmap.getHeight(),matrix,true);
        }
        try{
            beforeBitmap = Bitmap.createScaledBitmap(beforeBitmap,newWidth,newHeight,true);
            Log.i(Tag,beforeBitmap.getWidth()+"x"+beforeBitmap.getHeight());
            Log.i(Tag,"尺寸压缩："+beforeBitmap.getWidth()+"X"+beforeBitmap.getHeight());
        }catch (OutOfMemoryError e){
            Log.e(Tag,"尺寸压缩出错："+e);
        }
        return beforeBitmap;
    }

    /**
     * 根据图片目录获取bitmap
     * @param filePath
     * @return  视大小设置压缩参数后才读取的图片
     */
    public static Bitmap getBitmapFromFilePath(String filePath){
        int max = 2000;
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(filePath,options);
        options.inJustDecodeBounds = false;
        int degrees = PictureUtils.getDigress(filePath);
        int width = options.outWidth;
        int height = options.outHeight;
        int ratio = 1;
        if( width> max && width > height){
            ratio = width/max;
        }else if(height> max && height > width){
            ratio = height/max;
        }
        if(ratio <= 0) ratio = 1;
        options.inSampleSize = ratio;
        bitmap = BitmapFactory.decodeFile(filePath,options);
        Log.i(Tag,"读取的图片：x="+bitmap.getWidth()+",y="+bitmap.getHeight());
        if(degrees != 0){
            bitmap = PictureUtils.rotateImg(bitmap,degrees);
        }
        return bitmap;
    }


    /**
     * 布局生成图片
     * @param v
     * @return
     */
    public static Bitmap getViewBitmap(View v) {
        v.clearFocus();
        clearFocusEvery(v);
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }

    private static void clearFocusEvery(View view){
        view.clearFocus();
        if(view instanceof ViewGroup && ((ViewGroup) view).getChildCount()>0){
            for(int i = 0;i<((ViewGroup) view).getChildCount();i++){
                View child = ((ViewGroup) view).getChildAt(i);
                clearFocusEvery(child);
            }
        }
    }



    /**
     * 保存到私有目录
     * @param context
     * @param bitmap
     * @return
     */
    public static String save(Context context,Bitmap bitmap,String name){
        String path = context.getExternalFilesDir(null).getAbsolutePath()+File.separator+context.getResources().getString(R.string.app_name);

        bitmap = PictureUtils.appointResolutionCompress(bitmap,1600,0); //分辨率压缩

        File dir = new File(path);
//        File file = new File(context.getResources().getString(R.string.app_name)
//                +System.nanoTime()+".jpg");
        int quality = 90;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while (quality>40){
            bos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG,quality,bos);
            quality -= 5;
            if(bitmap.getByteCount()<500*1000) break;//大于500k就继续压缩
        }
        File image;
        if(name != null){
            image = new File(dir,name);
        }else{
            image = new File(dir,context.getResources().getString(R.string.app_name)+System.nanoTime());
        }
        try {
            if(!dir.exists()){
                dir.mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(image);

            fileOutputStream.write(bos.toByteArray());
            fileOutputStream.flush();
            fileOutputStream.close();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Tag,"保存错误："+e);
        }
        return image.getAbsolutePath();
    }

    /**
     * 保存小图
     * @param context
     * @param bitmap
     * @param name
     * @return
     */
    public static String saveThumbnail(Context context,Bitmap bitmap,String name){
        bitmap = PictureUtils.appointResolutionCompress(bitmap,800,0); //分辨率压缩

        int quality = 90;
        String path = context.getExternalFilesDir(null).getAbsolutePath()+File.separator+context.getResources().getString(R.string.app_name);
        File dir = new File(path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while (quality>40){
            bos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG,quality,bos);
            quality -= 5;
            if(bitmap.getByteCount()<300*1000) break;//大于500k就继续压缩
        }
        File image;
        if(name != null){
            image = new File(dir,name+".png");
        }else{
            image = new File(dir,context.getResources().getString(R.string.app_name)+System.nanoTime()+".png");
        }
        try {
            if(!dir.exists()){
                dir.mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(image);

            fileOutputStream.write(bos.toByteArray());
            fileOutputStream.flush();
            fileOutputStream.close();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Tag,"保存错误："+e);
        }
        return image.getAbsolutePath();
    }

    /**
     * 保存文件
     */
    public static String savePicture(Context context, ByteArrayOutputStream outputStream, String path){
        FileOutputStream fileOutputStream = null;
        File file = null;
        try {
            //在原路径，以当前纳秒级时间戳和原始图片类型生成文件名
            file = new File(path, context.getResources().getString(R.string.app_name)
                    +System.nanoTime()+".jpg");

            if(!file.exists()){
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(outputStream.toByteArray());
            fileOutputStream.flush();
            fileOutputStream.close();

//            Toast.makeText(context, "已保存至："+file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            Log.i(Tag,"压缩后大小："+UnitTransform(outputStream.size()));


            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Tag,"存储文件出错！"+e);
        }finally {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            context.sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦
        }
        return file.getAbsolutePath();
    }


    /**
     *  分享保存后的图片
     * @param context
     * @param filePath  图片地址
     */
    public static void sharePicture(Context context, String filePath){
        Uri uri = null;
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int targetSDK = applicationInfo.targetSdkVersion;
        if (targetSDK >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri =  FileProvider.getUriForFile(context, context.getApplicationInfo().packageName, new File(filePath));
        } else {
            uri = Uri.fromFile(new File(filePath));
        }
        Intent imageIntent = new Intent(Intent.ACTION_SEND);
        imageIntent.setType("image/*");
        imageIntent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(imageIntent,""));
    }

    /**
     * 分享视图上的图片
     * @param context
     * @param imageView
     */
    public static void shareByImageView(Context context, ImageView imageView){
        String path = context.getExternalCacheDir().getAbsolutePath();
        File bitmapFile = new File(path,"share_temp.jpg");
        Bitmap bitmap = null;
        try {
            FileOutputStream fos = new FileOutputStream(bitmapFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            if(imageView.getDrawable() == null) return;
            bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            if(bitmap != null){
                Bitmap newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
                Canvas canvas = new Canvas(newBitmap);
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(bitmap,0,0,null); //为了背景是白色，自己画图
                newBitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
                fos.write(bos.toByteArray());
                fos.flush();

                PictureUtils.sharePicture(context,bitmapFile.getAbsolutePath());
                bos.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
//                bitmapFile.deleteOnExit();//不知道是否已分享，有点风险
        }
    }



    /**
     * 把两个位图覆盖合成为一个位图，以底层位图的长宽为基准
     * @param backBitmap 在底部的位图
     * @param frontBitmap 盖在上面的位图
     * @return
     */
    public static Bitmap mergeBitmap(Bitmap backBitmap, Bitmap frontBitmap,int alpha) {

        if (backBitmap == null || backBitmap.isRecycled()
                || frontBitmap == null || frontBitmap.isRecycled()) {
            Log.e(Tag, "backBitmap=" + backBitmap + ";frontBitmap=" + frontBitmap);
            return null;
        }
        Bitmap bitmap = backBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        Bitmap bitmap = Bitmap.createBitmap(backBitmap.getWidth(),backBitmap.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Rect baseRect  = new Rect(0, 0, backBitmap.getWidth(), backBitmap.getHeight());
        Rect frontRect = new Rect(0, 0, frontBitmap.getWidth(), frontBitmap.getHeight());

        Paint paint = new Paint();
        paint.setAlpha(alpha);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));  //只绘制重合部分
//        canvas.drawBitmap(backBitmap,0,0,null   );
        canvas.save();
        //重复平铺前景
        int myWidth;
        int myHeight;
        for(myWidth = 0;myWidth<backBitmap.getWidth();myWidth+=frontRect.right){
            for(myHeight = 0;myHeight<backBitmap.getHeight();myHeight+=frontRect.bottom){
                canvas.drawBitmap(frontBitmap, myWidth, myHeight, paint);
            }
        }
        canvas.restore();
        return bitmap;
    }

    /**
     * 把两个位图覆盖合成为一个位图，以底层位图的长宽为基准
     * @param backBitmap 在底部的位图
     * @param frontBitmap 盖在上面的位图
     * @return
     */
    public static Bitmap mergeBitmap(Bitmap backBitmap, Bitmap frontBitmap) {
        if (backBitmap == null || backBitmap.isRecycled()
                || frontBitmap == null || frontBitmap.isRecycled()) {
            Log.e(Tag, "backBitmap=" + backBitmap + ";frontBitmap=" + frontBitmap);
            return null;
        }
        Bitmap bitmap = backBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Rect baseRect  = new Rect(0, 0, backBitmap.getWidth(), backBitmap.getHeight());
        Rect frontRect = new Rect(0, 0, frontBitmap.getWidth(), frontBitmap.getHeight());

        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));  //只绘制重合部分
        canvas.drawBitmap(frontBitmap,frontRect,baseRect, paint);
        return bitmap;
    }

    public static Bitmap getBitmapAf() {
        return bitmapAf;
    }

    public static void setBitmapAf(Bitmap bitmapAf) {
        PictureUtils.bitmapAf = bitmapAf;
    }
}

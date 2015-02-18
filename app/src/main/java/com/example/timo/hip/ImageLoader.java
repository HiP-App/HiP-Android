package com.example.timo.hip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
 
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.widget.ImageView;
 
public class ImageLoader {
 
    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService; 
 
    public ImageLoader(Context context){
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
    }
 
    int stub_id = R.drawable.ic_launcher;
    public void DisplayImageThumbnail(String url, int loader, ImageView imageView)
    {
    	stub_id = loader;
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null)
            renderThumbnail(bitmap, imageView);
            //imageView.setImageBitmap(bitmap);
        else
        {
            queuePhoto(url, imageView, true);
            imageView.setImageResource(loader);
        }
    }

    public void DisplayImage(String url, ImageView imageView)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null)
            renderImage(bitmap, imageView);
            //imageView.setImageBitmap(bitmap);
        else
        {
            queuePhoto(url, imageView, false);
            //imageView.setImageResource(loader);
        }
    }

    public Bitmap quickLoad(String url) {
        return this.getBitmap(url);
    }
 
    private void queuePhoto(String url, ImageView imageView, boolean thumbnail)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView, thumbnail);
        executorService.submit(new PhotosLoader(p));
    }
 
    private Bitmap getBitmap(String url)
    {
        File f=fileCache.getFile(url);
 
        //from SD cache
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;
 
        //from web
        try {
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Exception ex){
           ex.printStackTrace();
           return null;
        }
    }
 
    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
 
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=500;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
 
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

    private void renderThumbnail(Bitmap bmp, ImageView imageView) {
        if(bmp != null) {
            int radius = bmp.getWidth();
            Bitmap sbmp;
            if (bmp.getWidth() != radius || bmp.getHeight() != radius)
                sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
            else
                sbmp = bmp;
            Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xffa19774;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f, sbmp.getWidth() / 2 + 0.1f, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(sbmp, rect, rect, paint);

            imageView.setImageBitmap(output);
        }
    }

    private void renderImage(Bitmap bmp, ImageView imageView) {
        if(bmp != null) {
            int radius = bmp.getWidth();
            Bitmap sbmp;
//            if (bmp.getWidth() != radius || bmp.getHeight() != radius)
//                sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
//            else
                sbmp = bmp;
            Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xffa19774;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRect(0,0, imageView.getWidth(), imageView.getHeight(), paint);
            //canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f, sbmp.getWidth() / 2 + 0.1f, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(sbmp, rect, rect, paint);

            imageView.setImageBitmap(bmp);
        }
    }
 
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public boolean thumbnail;
        public PhotoToLoad(String u, ImageView i, boolean t){
            url=u;
            imageView=i;
            thumbnail = t;
        }
    }
 
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
 
        @Override
        public void run() {
            if(imageViewReused(photoToLoad))
                return;
            Bitmap bmp=getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if(imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
            Activity a=(Activity)photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }
 
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
 
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null)
                if(photoToLoad.thumbnail) renderThumbnail(bitmap, photoToLoad.imageView);
                else renderImage(bitmap, photoToLoad.imageView);
            else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }
 
    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }
 
}

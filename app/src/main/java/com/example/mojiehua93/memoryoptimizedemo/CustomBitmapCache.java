package com.example.mojiehua93.memoryoptimizedemo;

import android.graphics.Bitmap;
import android.util.ArrayMap;
import android.util.Log;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 * Created by MOJIEHUA93 on 2017/11/11.
 */

public class CustomBitmapCache {

    public static final String TAG = "CustomBitmapCache";

    static private CustomBitmapCache bitmapCache;
    private ArrayMap<String, MySoftReference> hashReference;
    private ReferenceQueue<Bitmap> queue;

    private class MySoftReference extends SoftReference<Bitmap>{
        private String _key = "";
        public MySoftReference(Bitmap bitmap, ReferenceQueue<Bitmap> queue, String _key){
            super(bitmap,queue);
            this._key = _key;
        }
    }

    private CustomBitmapCache (){
        hashReference = new ArrayMap<String, MySoftReference>();
        queue = new ReferenceQueue<Bitmap>();
    }
    public CustomBitmapCache getInstance(){
        if (bitmapCache == null){
            bitmapCache = new CustomBitmapCache();
        }
        return bitmapCache;
    }
    public void addCacheBitmap(Bitmap bitmap, String key){
        cleanCache();
        MySoftReference reference = new MySoftReference(bitmap, queue, key);
        hashReference.put(key, reference);
    }

    private void cleanCache() {
        MySoftReference reference = null;
        while ((reference = (MySoftReference) queue.poll()) != null){
            hashReference.remove(reference._key);
        }
    }

    public Bitmap getBitmap(String resId){
        Bitmap bitmap = null;
        try{
            if (hashReference.containsKey(resId)){
                MySoftReference reference = hashReference.get(resId);
                bitmap = reference.get();
            }
        }catch (Exception e){
            Log.d(TAG, "getBitmap: ", e);
        }
        return null;
    }

    public void clearCache(){
        cleanCache();
        hashReference.clear();
        System.gc();
        System.runFinalization();
    }
}

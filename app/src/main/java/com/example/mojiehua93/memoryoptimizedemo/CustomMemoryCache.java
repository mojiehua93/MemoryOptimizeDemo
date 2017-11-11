package com.example.mojiehua93.memoryoptimizedemo;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by MOJIEHUA93 on 2017/11/11.
 */

public class CustomMemoryCache {
    public static final String TAG = "CustomMemoryCache";

    private Map<String, Bitmap> cacheMap = Collections.synchronizedMap(
            new LinkedHashMap<String, Bitmap>(8, 0.75F, true));
    private long currentSize = 0;
    private long limitSize = 1000000L;

    public CustomMemoryCache(){
        setLimit(Runtime.getRuntime().maxMemory() / 4);
    }

    private void setLimit(long limit) {
        limitSize = limit;
    }

    public Bitmap getBitmap(String id){
        try {
            if (!cacheMap.containsKey(id)){
                return null;
            }
            return cacheMap.get(id);
        } catch (Exception e){
            Log.d(TAG, "getBitmap: ", e);
            return null;
        }
    }

    public void getBitmap(Bitmap bitmap, String id){
        try {
            if (cacheMap.containsKey(id)){
                currentSize -= getSizeInBytes(bitmap);
            }
            cacheMap.put(id, bitmap);
            currentSize += getSizeInBytes(bitmap);
            checkSize();
        }catch (Exception e){
            Log.d(TAG, "getBitmap: ", e);
        }
    }

    private void checkSize() {
        Log.d(TAG, "checkSize: currenSize = " + currentSize + " length = "
        + cacheMap.size());
        if (currentSize > limitSize){
            Iterator<Map.Entry<String, Bitmap>> iterator = cacheMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, Bitmap> entry = iterator.next();
                currentSize -= getSizeInBytes(entry.getValue());
                iterator.remove();
                if (currentSize < limitSize){
                    break;
                }
            }
            Log.d(TAG, "checkSize: new currenSize = " + currentSize);
        }
    }

    private long getSizeInBytes(Bitmap bitmap) {
        if (bitmap == null){
            return 0;
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}

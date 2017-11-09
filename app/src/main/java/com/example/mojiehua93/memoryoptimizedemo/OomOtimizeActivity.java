package com.example.mojiehua93.memoryoptimizedemo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.util.Random;

/**
 * Created by MOJIEHUA93 on 2017/11/5.
 */

public class OomOtimizeActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "OomOtimizeActivity";
    public static final String DCIM_DIRECTORY = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM).getAbsolutePath();

    private String mPhotoDirectory = DCIM_DIRECTORY + "/Camera/";
    private ImageView mImageView;
    private Bitmap mBitmap;
    private File mPhotoFile;
    private Random mRandom;
    private int mScreenWidth;
    private int mScreenHeigth;
    private int mOffset;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oom_optimize);
        mRandom = new Random();
        bindViews();

        DisplayMetrics metrics = new DisplayMetrics();
        metrics = getResources().getDisplayMetrics();
        mScreenWidth = metrics.widthPixels;
        mScreenHeigth = metrics.heightPixels;
    }

    private void bindViews() {
        mImageView = findViewById(R.id.imageview);
        findViewById(R.id.choose_picture).setOnClickListener(this);
        findViewById(R.id.button_scale).setOnClickListener(this);
        findViewById(R.id.change_rgb).setOnClickListener(this);
        findViewById(R.id.part_load).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.choose_picture:
                getPicture();
                break;

            case R.id.button_scale:
                pictureScaleOptimize();
                break;

            case R.id.change_rgb:
                changPixelRgb();
                break;

            case R.id.part_load:
                partLoad();
                break;
            default:break;
        }
    }

    private void partLoad() {
        Log.d(TAG, "partLoad: ");
        if (mPhotoFile == null){
            return;
        }

        try {
            FileInputStream inputStream = new FileInputStream(mPhotoFile);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            int pictureWidth = options.outWidth;
            int pictureHeight = options.outHeight;

            inputStream = new FileInputStream(mPhotoFile);
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(inputStream, false);
            mBitmap = decoder.decodeRegion(new Rect(
                    (pictureWidth - mScreenWidth) / 2 + mOffset,
                    (pictureHeight - mScreenHeigth) / 2,
                    (pictureWidth + mScreenWidth) / 2 + mOffset,
                    (pictureHeight + mScreenHeigth) / 2), options);
            mImageView.setImageBitmap(mBitmap);
            mOffset += mScreenWidth / 4;
            if (mOffset > pictureWidth / 2){
                mOffset = - pictureWidth / 2;
            }
            inputStream.close();
        }catch (Exception e){
            Log.d(TAG, "partLoad: ", e);
        }
    }

    private void pictureScaleOptimize() {
        Log.d(TAG, "pictureScaleOptimize: entry ==>");
        if (mPhotoFile == null){
            return;
        }
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(mPhotoFile),null, options);
            Log.d(TAG, "pictureScaleOptimize: options = " + options.toString());
            int tempWidth = options.outWidth;
            int tempHeight = options.outHeight;
            int scale = 2;
            while (true){
                if (tempWidth / scale < mScreenWidth){
                    break;
                }
                Log.d(TAG, "pictureScaleOptimize: tempWidth = " + tempWidth);
                scale *= 2;
            }
            scale /= 2;
            BitmapFactory.Options options1 = new BitmapFactory.Options();
            options1.inSampleSize = scale;
            Log.d(TAG, "pictureScaleOptimize: scale = " + scale);
            FileInputStream inputStream = new FileInputStream(mPhotoFile);
            mBitmap = BitmapFactory.decodeStream(inputStream, null, options1);
            Log.d(TAG, "pictureScaleOptimize: bitmap length = " + mBitmap
                    .getByteCount());
            mImageView.setImageBitmap(mBitmap);
            inputStream.close();
        }catch (Exception e){
            Log.d(TAG, "pictureScaleOptimize: ", e);
        }
    }

    private void changPixelRgb() {
        Log.d(TAG, "changPixelRgb: entry ==>");
        if (mPhotoFile == null){
            return;
        }
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            FileInputStream inputStream = new FileInputStream(mPhotoFile);
            Log.d(TAG, "changPixelRgb: inputStream");
            mBitmap = BitmapFactory.decodeStream(inputStream, null, options);
            Log.d(TAG, "changPixelRgb: bitmap length = " + mBitmap.getByteCount());
            mImageView.setImageBitmap(mBitmap);
            inputStream.close();
        }catch (Exception e){
            Log.d(TAG, "changPixelRgb: ", e);
        }
    }

    private void getPicture() {
        Intent intentPick = new Intent(Intent.ACTION_PICK, null);
        intentPick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(intentPick,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            String path = getFilePathFromUri(data.getData());
            mPhotoFile = new File(path);
            if (mPhotoFile == null){
                return;
            }
            if (mPhotoFile.length() == 0){
                mPhotoFile.delete();
                return;
            }
            Log.d(TAG, "onActivityResult: file: " + mPhotoFile.getName() + " length = "
            + mPhotoFile.length());
            FileInputStream inputStream = new FileInputStream(mPhotoFile);
            mBitmap = BitmapFactory.decodeStream(inputStream);
            Log.d(TAG, "onActivityResult: bitmap length = " + mBitmap.getByteCount());
            mImageView.setImageBitmap(mBitmap);
            inputStream.close();
//            GlideApp.with(this)
//                    .asBitmap()
//                    .load(mBitmap)
//                    .placeholder(R.mipmap.ic_launcher)
//                    .error(R.mipmap.ic_launcher_round)
//                    .override(1080, 1920)
//                    .thumbnail(0.2F)
//                    .into(mImageView);
        } catch (Exception e){
            Log.d(TAG, "onActivityResult: ", e);
        }
    }

    private String getFilePathFromUri(Uri data) {
        String path = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(data, proj, null, null,
                    null);
            if (cursor.moveToFirst()){
                int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                path = cursor.getString(index);
            }
        } catch (Exception e){
            Log.d(TAG, "getFilePathFromUri: ", e);
        }finally {
            cursor.close();
        }
        return path;
    }
}

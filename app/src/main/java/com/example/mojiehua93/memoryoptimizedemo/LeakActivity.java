package com.example.mojiehua93.memoryoptimizedemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.mojiehua93.memoryoptimizedemo.R;

/**
 * Created by MOJIEHUA93 on 2017/11/2.
 */

public class LeakActivity extends AppCompatActivity {
    private Thread mLeakThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mLeakThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try{
                        Thread.sleep(1000 * 60 * 5);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        mLeakThread.start();
        Log.d("jiehua", "<SecondActiv ity.onCreate>;");
    }
}

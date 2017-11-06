package com.example.mojiehua93.memoryoptimizedemo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int ROW_LENGTH = 20;
    public static final int COLUMN_LENGTH = 300;

    private int[][] intMatrix = new int[ROW_LENGTH][COLUMN_LENGTH];
    private Random random = new Random();

    private TextView mTextView;
    private Button mStringButton;
    private Button mStringBuilderButton;
    private Button mLeakButton;
    private Button mOomOtimizeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();
        bindListenner();
        calculate();
        initMatrix();
    }

    private void initMatrix() {
        for (int i=0; i < ROW_LENGTH; i++){
            for (int j=0; j < COLUMN_LENGTH; j++){
                intMatrix[i][j] = random.nextInt();
            }
        }
    }

    private void bindView() {
        mTextView = (TextView) findViewById(R.id.text_view);
        mStringButton = (Button) findViewById(R.id.button_string);
        mStringBuilderButton = (Button) findViewById(R.id.button_stringbuilder);
        mLeakButton = (Button) findViewById(R.id.button_leak);
        mOomOtimizeButton = (Button) findViewById(R.id.button_oom);
    }

    private void bindListenner() {
        mStringButton.setOnClickListener(this);
        mStringBuilderButton.setOnClickListener(this);
        mLeakButton.setOnClickListener(this);
        mOomOtimizeButton.setOnClickListener(this);
    }

    private void calculate() {

        StringBuilder stringBuilder = new StringBuilder();

        ActivityManager activityManager = (ActivityManager) getSystemService(
                Context.ACTIVITY_SERVICE);
        int memoryClass = activityManager.getMemoryClass();
        int largeMemoryClass = activityManager.getLargeMemoryClass();
        stringBuilder.append("memory: ")
                .append(memoryClass)
                .append("\n")
                .append("largeMemory: ")
                .append(largeMemoryClass)
                .append("\n");

        float totalMemroy = Runtime.getRuntime().totalMemory() * 1.0F / (1024 * 1024);
        float freeMemroy = Runtime.getRuntime().freeMemory() * 1.0F / (1024 * 1024);
        float maxMemroy = Runtime.getRuntime().maxMemory() * 1.0F / (1024 * 1024);

        stringBuilder.append("totalMemory: " + totalMemroy + "\n");
        stringBuilder.append("freeMemory: " + freeMemroy + "\n");
        stringBuilder.append("maxMemory: " + maxMemroy + "\n");

        mTextView.setText(stringBuilder);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_string:
                doJoin();
                break;

            case R.id.button_stringbuilder:
                doAppend();
                break;

            case R.id.button_leak:
                Intent intent = new Intent(this, LeakActivity.class);
                startActivity(intent);
                break;

            case R.id.button_oom:
                Intent intentOom = new Intent(this, OomOtimizeActivity.class);
                startActivity(intentOom);
            default:break;
        }
    }

    private void doAppend() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i < ROW_LENGTH; i++){
            for (int j=0; j < COLUMN_LENGTH; j++){
                stringBuilder.append(intMatrix[i][j]).append(",");
            }
            Log.d("jiehua", "<doAppend> row = " + i);
        }
        Log.d("jiehua", "<doAppend> builderlength = " + stringBuilder.length());
    }

    private void doJoin() {
        String string = null;
        for (int i=0; i < ROW_LENGTH; i++){
            for (int j=0; j < COLUMN_LENGTH; j++){
                string += intMatrix[i][j];
                string += ",";
            }
            Log.d("jiehua", "<doJoin> row = " + i);
        }
        Log.d("jiehua", "<doJoin> stringlength = " + string.length());
    }
}

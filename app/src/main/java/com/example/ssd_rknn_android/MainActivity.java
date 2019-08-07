package com.example.ssd_rknn_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.libssddetect.JavaBridgeSSD;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JavaBridgeSSD javaBridgeSSD = new JavaBridgeSSD(this,R.raw.ssd_mobilenet_v1_hand_tensorflow);
        javaBridgeSSD.loadOpencv();
        try{
            javaBridgeSSD.jTools.copyAssertFile(this,"videos","video_2019-07-05_15-09-37.avi");
        }catch (IOException e){

        }

    }
}

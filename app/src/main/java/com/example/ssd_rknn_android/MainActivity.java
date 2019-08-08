package com.example.ssd_rknn_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.jade.JadeLog;
import com.example.libssddetect.JavaBridgeSSD;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JavaBridgeSSD javaBridgeSSD = new JavaBridgeSSD(this,R.raw.ssd_mobilenet_v1_hand_tensorflow,R.raw.video);
        javaBridgeSSD.loadOpencv();
//        try{
//            javaBridgeSSD.jTools.copyAssertFile(this,"videos","video.avi");
//        }catch (IOException e){
//
//        }
//
        VideoCapture videoCapture = new VideoCapture(javaBridgeSSD.fileDirPath+"/"+"video.avi");
        JadeLog.e(this,""+videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT));

        for (int i=0;i<videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT);i++){
            Mat img = new Mat();
            videoCapture.read(img);
            Mat img_cvt = new Mat();
            Imgproc.cvtColor(img,img_cvt,Imgproc.COLOR_RGB2BGR);
            byte[] data = new byte[img_cvt.channels()*img_cvt.cols()*img_cvt.rows()];
            img.get(0,0,data);
            javaBridgeSSD.Inference(data);
            JadeLog.e(this,"正在读取"+i+"帧");
        }
    }

}

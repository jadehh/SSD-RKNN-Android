package com.rockchip.ssd_rknn_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.rockchip.gpadc.ssddemo.InferenceResult;
import com.rockchip.jade.JadeLog;
import com.rockchip.gpadc.ssddemo.JavaBridgeSSD;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    reflashUI();
            }
        }
    };
    private static ImageView imageView;
    private static VideoCapture videoCapture;
    private static JavaBridgeSSD javaBridgeSSD;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        showView();


    }

    public void showView() {

        javaBridgeSSD = new JavaBridgeSSD(this, R.raw.ssd_mobilenet_v1_hand_tensorflow, R.raw.video);
        javaBridgeSSD.loadOpencv();
        videoCapture =   new VideoCapture(javaBridgeSSD.fileDirPath + "/" + "video.avi");
        JadeLog.e(this, "" + videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT));
        new Thread(){
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT); i++) {
                    Mat img = new Mat();
                    videoCapture.read(img);
                    ArrayList<InferenceResult.Recognition> recognitions = javaBridgeSSD.Inference(img);
                    if (recognitions.size() > 0) {
                        JadeLog.e(this, "id = " + recognitions.get(0).getId() + "title = " + recognitions.get(0).getTitle() + "confidence = " + recognitions.get(0).getConfidence());
                    } else {
                        JadeLog.e(this, "没有检测到目标");
                    }
                    Mat drawImg = javaBridgeSSD.DrawBboxesText(img,recognitions);
                    bitmap = Bitmap.createBitmap(drawImg.width(), drawImg.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(drawImg, bitmap);
                    //处理完成后给handler发送消息  
                    Message msg = new Message();
                    msg.what = 0;
                    handler.sendMessage(msg);
                    JadeLog.e(this, "正在读取" + i + "帧");

                }
            }
        }.start();

    }
    public void  reflashUI(){
        imageView.setImageBitmap(bitmap);
    }
}

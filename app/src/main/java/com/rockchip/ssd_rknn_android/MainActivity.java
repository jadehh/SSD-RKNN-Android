package com.rockchip.ssd_rknn_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
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
    private static ImageView faceImageView;
    private static ImageView smallImageView;
    private static VideoCapture videoCapture;
    private static JavaBridgeSSD javaBridgeSSD;
    private Bitmap bitmap;
    private Bitmap faceBitmap;
    private Bitmap smallBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        faceImageView = findViewById(R.id.faceImageView);
        smallImageView = findViewById(R.id.samllerImageView);
        setSystemUIVisible(false);
        showView();


    }

    public void showView() {

        javaBridgeSSD = new JavaBridgeSSD(this);
        videoCapture =   new VideoCapture(javaBridgeSSD.fileDirPath + "/" + "gesture_face.avi");
        JadeLog.e(this, "" + videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT));
        new Thread(){
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT); i++) {
                    Mat img = new Mat();
                    videoCapture.read(img);
                    Boolean isSuccess = javaBridgeSSD.Predict(img,0.9,javaBridgeSSD.fileDirPath+javaBridgeSSD.jTools.getTime()+".jpg");
                    if (isSuccess){
                        JadeLog.e(this,"保存图片成功");
                    }else{
                        JadeLog.e(this,"保存图片失败");
                    }
                    ArrayList<InferenceResult.Recognition> recognitions = javaBridgeSSD.Inference(img);
                    Mat drawImg = javaBridgeSSD.DrawBboxesText(img,recognitions,0.9);
                    Mat faceImg = javaBridgeSSD.PredictFaceGesture(img,recognitions,0.9);
                    bitmap = Bitmap.createBitmap(drawImg.width(), drawImg.height(), Bitmap.Config.ARGB_8888);
                    if (faceImg.size().width > 0 && faceImg.size().height > 0){
                        faceBitmap = Bitmap.createBitmap(faceImg.width(), faceImg.height(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(faceImg,faceBitmap);

                        smallBitmap = Bitmap.createBitmap(img.width(), img.height(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(drawImg,smallBitmap);
                    }
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
        if (faceBitmap != null){
            faceImageView.setImageBitmap(faceBitmap);
            smallImageView.setImageBitmap(smallBitmap);
        }

    }


    private void setSystemUIVisible(boolean show) {
        if (show) {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            uiFlags |= 0x00001000;
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        } else {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            uiFlags |= 0x00001000;
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }
}

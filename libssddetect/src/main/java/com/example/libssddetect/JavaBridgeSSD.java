package com.example.libssddetect;
import android.app.Activity;
import android.util.Log;

import com.example.jade.JadeLog;
import com.example.jade.jade_tools;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import static com.example.libssddetect.PostProcess.*;
public class JavaBridgeSSD {
    public static final String TAG = "ssd";
    private String mModelName = "ssd.rknn";
    public jade_tools jTools;

    private String fileDirPath;     // file dir to store model cache
    private InferenceWrapper mInferenceWrapper;
    private Activity mActivity;

    public JavaBridgeSSD(Activity activity,int id){
        fileDirPath = activity.getCacheDir().getAbsolutePath();
        jTools = new jade_tools();
        jTools.copyRawFile(activity,fileDirPath,mModelName,id);
        mActivity = activity;

    }

    public void Inference(byte[] data){
        int count = 0;
        long oldTime = System.currentTimeMillis();
        long currentTime;
        String paramPath = fileDirPath + "/" + mModelName;
        mInferenceWrapper = new InferenceWrapper(INPUT_SIZE,INPUT_CHANNEL,NUM_RESULTS,NUM_CLASSES,paramPath);
        mInferenceWrapper.run(data);
    }


    //Load opencv的回调函数
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(mActivity) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    /**
     * 加载openc库
     */
    public void loadOpencv() {
        if (!OpenCVLoader.initDebug()) {
            JadeLog.e("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, mActivity, mLoaderCallback);
        } else {
            JadeLog.e("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}

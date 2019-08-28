package com.rockchip.gpadc.ssddemo;
import android.app.Activity;
import android.graphics.RectF;
import android.util.Log;

import com.rockchip.gpadc.R;
import com.rockchip.jade.JadeLog;
import com.rockchip.jade.jade_tools;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static com.rockchip.gpadc.ssddemo.PostProcess.*;
import com.rockchip.gpadc.ssddemo.InferenceResult.OutputBuffer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

public class JavaBridgeSSD {
    public static final String TAG = "ssd";
    public jade_tools jTools;
    private String mVideoName = "gesture_face.avi";
    private String mModelName = "ssd_mobilenet_v1_gesture_face_do_quantization_tensorflow.rknn" ;
    public String fileDirPath;     // file dir to store model cache
    private InferenceWrapper mInferenceWrapper;
    private Activity mActivity;
    private InferenceResult mInferenceResult = new InferenceResult();  // detection result

    public JavaBridgeSSD(Activity activity){
        loadOpencv();
        try {
            mInferenceResult.init(activity.getAssets());
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileDirPath = activity.getCacheDir().getAbsolutePath();
        jTools = new jade_tools();
        jTools.copyRawFile(activity,fileDirPath,mModelName, R.raw.ssd_mobilenet_v1_gesture_face_do_quantization_tensorflow);
        jTools.copyRawFile(activity,fileDirPath,mVideoName,R.raw.gesture_face);
        mActivity = activity;
        String paramPath = fileDirPath + "/" + mModelName;
        mInferenceWrapper = new InferenceWrapper(INPUT_SIZE,INPUT_CHANNEL,NUM_RESULTS,NUM_CLASSES,paramPath);
    }
    public Boolean Predict(Mat img,double threshold,String savePath){
        mInferenceResult.reset();
        long oldTime = System.currentTimeMillis();
        long currentTime;
        Mat img_cvt = new Mat();
        Imgproc.cvtColor(img, img_cvt, Imgproc.COLOR_RGB2BGR);
        Mat img_resize = new Mat();
        Size size = new  Size(300,300);
        Imgproc.resize(img_cvt,img_resize,size);
        byte[] data = new byte[img_resize.channels() * img_resize.cols() * img_resize.rows()];
        img_resize.get(0, 0, data);
        OutputBuffer bufferResult = mInferenceWrapper.run(data);
        mInferenceResult.setResult(bufferResult);
        ArrayList<InferenceResult.Recognition> recognitions = mInferenceResult.getResult();
        currentTime = System.currentTimeMillis();
        JadeLog.e(this,"预测时间 "+(currentTime-oldTime)+" ms");
        Boolean success = GetFaceImgPath(img,recognitions,threshold,savePath);
        return success;
    }
    public ArrayList<InferenceResult.Recognition> Inference(Mat img){
        mInferenceResult.reset();
        long oldTime = System.currentTimeMillis();
        long currentTime;
        Mat img_cvt = new Mat();
        Imgproc.cvtColor(img, img_cvt, Imgproc.COLOR_RGB2BGR);
        Mat img_resize = new Mat();
        Size size = new  Size(300,300);
        Imgproc.resize(img_cvt,img_resize,size);
        byte[] data = new byte[img_resize.channels() * img_resize.cols() * img_resize.rows()];
        img_resize.get(0, 0, data);
        OutputBuffer bufferResult = mInferenceWrapper.run(data);
        mInferenceResult.setResult(bufferResult);
        ArrayList<InferenceResult.Recognition> recognitions = mInferenceResult.getResult();
        currentTime = System.currentTimeMillis();
        JadeLog.e(this,"预测时间 "+(currentTime-oldTime)+" ms");
        return recognitions;
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
    public Mat PredictFaceGesture(Mat img,ArrayList<InferenceResult.Recognition> recognitions,double threshold){
        Mat cvtImg = new Mat();
        Imgproc.cvtColor(img,cvtImg,Imgproc.COLOR_RGB2BGR);
        Size size = img.size();
        Double width = size.width;
        Double height = size.height;
        double max_face_area = 0;
        int face_id = -1;
        Mat face_img = new Mat();
        boolean isThumb_up = false;
        for (int i = 0;i<recognitions.size();i++) {
            float score = recognitions.get(i).getConfidence();
            if (score > threshold ) {
                String title = recognitions.get(i).getTitle();
                RectF rectF = recognitions.get(i).getLocation();
                if (title.equals("face")){
                    double face_height = rectF.height()*height;
                    double face_width = rectF.width()*width;
                    double face_area = face_height * face_width;
                    if (face_area > max_face_area){
                        max_face_area = face_area;
                        face_id = i;
                    }
                }
                if (title.equals("thumb_up")){
                    isThumb_up = true;
                }
            }
        }
        if (face_id >= 0 && isThumb_up){
            JadeLog.e(this,"有人做了点赞的手势");
            RectF face_rectF = recognitions.get(face_id).getLocation();
            Size face_size = new Size(face_rectF.width()*width,face_rectF.height()*height);
            Point centerPoint = new Point(face_rectF.centerX()*width,face_rectF.centerY()*height);
            Imgproc.getRectSubPix(cvtImg, face_size, centerPoint, face_img, -1);
            Mat face_resize = new Mat();
            Size re_size = new Size(100,100);
            Imgproc.resize(face_img,face_resize,re_size);
            face_img = face_resize;
        }
        return face_img;
    }
    public Mat DrawBboxesText(Mat img,ArrayList<InferenceResult.Recognition> recognitions,double threshold){
        Mat cvtImg = new Mat();
        Imgproc.cvtColor(img,cvtImg,Imgproc.COLOR_RGB2BGR);
        Size size = img.size();
        Double width = size.width;
        Double height = size.height;
        for (int i = 0;i<recognitions.size();i++){
            float score = recognitions.get(i).getConfidence();
            if (score > threshold){
                JadeLog.e(this,   ""+"title = " + recognitions.get(i).getTitle() + ", confidence = " + recognitions.get(i).getConfidence());
                String title = recognitions.get(i).getTitle();
                String confidence =String.format("%.2f", recognitions.get(i).getConfidence());
                RectF rectF = recognitions.get(i).getLocation();
                Double xmin = rectF.left * width;
                Double xmax = rectF.right * width;
                Double ymin = rectF.top * height;
                Double ymax = rectF.bottom * height;
                Point point1 = new Point(xmin,ymin);
                Point point2 = new Point(xmax,ymax);
                Scalar scalar1 = new Scalar(255,0,255);
                Imgproc.rectangle(cvtImg,point1,point2,scalar1,4,4);
                String text = title + " confidence: "+confidence;
                Scalar scalar2 = new Scalar(0,255,255);
                Point point3 = new Point(xmin,ymin+20);
                Imgproc.putText(cvtImg,text,point3,Imgproc.FONT_HERSHEY_COMPLEX,0.8,scalar2);
            }
        }
        return cvtImg;
    }

    public Boolean GetFaceImgPath(Mat img,ArrayList<InferenceResult.Recognition> recognitions,double threshold,String save_img_path){
        Size size = img.size();
        Double width = size.width;
        Double height = size.height;
        double max_face_area = 0;
        int face_id = -1;
        Mat face_img = new Mat();
        boolean isThumb_up = false;
        for (int i = 0;i<recognitions.size();i++) {
            float score = recognitions.get(i).getConfidence();
            if (score > threshold ) {
                String title = recognitions.get(i).getTitle();
                RectF rectF = recognitions.get(i).getLocation();
                if (title.equals("face")){
                    double face_height = rectF.height()*height;
                    double face_width = rectF.width()*width;
                    double face_area = face_height * face_width;
                    if (face_area > max_face_area){
                        max_face_area = face_area;
                        face_id = i;
                    }
                }
                if (title.equals("thumb_up")){
                    isThumb_up = true;
                }
            }
        }
        if (face_id >= 0 && isThumb_up){
            JadeLog.e(this,"有人做了点赞的手势");
            RectF face_rectF = recognitions.get(face_id).getLocation();
            Size face_size = new Size(face_rectF.width()*width,face_rectF.height()*height);
            Point centerPoint = new Point(face_rectF.centerX()*width,face_rectF.centerY()*height);
            Imgproc.getRectSubPix(img, face_size, centerPoint, face_img, -1);
            JadeLog.e(this,save_img_path);
            Imgcodecs.imwrite(save_img_path ,face_img);
            return  true;
        }else{
            return false;
        }

    }
}

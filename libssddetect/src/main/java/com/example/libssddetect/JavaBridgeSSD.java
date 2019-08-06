package com.example.libssddetect;
import static com.example.libssddetect.PostProcess.*;
public class JavaBridgeSSD {
    public static final String TAG = "ssd";
    private String mModelName = "ssd.rknn";


    private String fileDirPath;     // file dir to store model cache
    private InferenceWrapper mInferenceWrapper;


    public void Inference(){
        int count = 0;
        long oldTime = System.currentTimeMillis();
        long currentTime;
        String paramPath = fileDirPath + "/" + mModelName;
        mInferenceWrapper = new InferenceWrapper(INPUT_SIZE,INPUT_CHANNEL,NUM_RESULTS,NUM_CLASSES,paramPath);
        mInferenceWrapper.run()

    }
}

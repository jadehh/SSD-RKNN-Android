package com.example.jade;

import android.util.Log;

/**
 * 作者：Create on 2019/7/4 14:42  by  jadehh
 * 邮箱：
 * 描述：TODO
 * 最近修改：2019/7/4 14:42 modify by jadehh
 */
public class JadeLog {
    private static boolean DEBUG = true;

    public static void e(Object object,String message){
        if (DEBUG){
            if (object instanceof String){
                Log.e(object.toString(),"----------------->" + message+"<-----------------");
            }
            else{
                String []A = object.getClass().getName().split("\\.");
                String TAG = A[A.length-1];
                Log.e(TAG, "----------------->" + message+"<-----------------");
            }
        }
    }
    public static void v(Object object,String message){
        if (DEBUG){
            if (object instanceof String){
                Log.v(object.toString(), "----------------->" + message+"<-----------------");
            }
            else{
                String []A = object.getClass().getName().split("\\.");
                String TAG = A[A.length-1];
                Log.v(TAG, "----------------->" + message+"<-----------------");
            }
        }
    }

    public static void i(Object object,String message){
        if (DEBUG){
            if (object instanceof String){
                Log.i(object.toString(), "----------------->" + message+"<-----------------");
            }else{
                String []A = object.getClass().getName().split("\\.");
                String TAG = A[A.length-1];
                Log.i(TAG, "----------------->" + message+"<-----------------");
            }

        }
    }

    public static void w(Object object,Exception message){
        if (DEBUG){
            if (object instanceof String){
                Log.w(object.toString(), "----------------->" + message+"<-----------------");
            }else{
                String []A = object.getClass().getName().split("\\.");
                String TAG = A[A.length-1];
                Log.w(TAG, "----------------->" + message+"<-----------------");
            }

        }
    }
    public static void printArray(double[] arr) {
        String message = "";
        for (int i = 0; i < arr.length; i++) {
            message = message + arr[i] + ",";
        }
        if (DEBUG) {
            System.out.println(message);
        }
    }
}

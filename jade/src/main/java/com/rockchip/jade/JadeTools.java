package com.rockchip.jade;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class JadeTools {
    private String TAG = "JadeTools";
    public  String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator ;
    //获取当前时间戳
    public long getTimeStamp(){
        return System.currentTimeMillis();
    }

    //获取当前时间
    public  String getTime(){
        System.currentTimeMillis();
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date curDate =  new Date(System.currentTimeMillis());
        String str   =   formatter.format(curDate);
        return str;
    }

    /**
     * Copy assert file.//拷贝Assert文件
     *
     * @param c        the Activity
     * @param pathName the path name
     * @param Name     the name
     * @throws IOException the io exception
     */
    public void copyAssertFile(Activity c,String pathName, String Name) throws IOException{
        String NEW_ROOT_PATH = ROOT_PATH + pathName + "/";
        try {
            createDir(NEW_ROOT_PATH);
        }catch (IOException e){
            System.out.println(e);
        }
        File outfile = null;
        outfile = new File(  NEW_ROOT_PATH+Name);
        if (!outfile.exists()) {
            outfile.createNewFile();
            FileOutputStream out = new FileOutputStream(outfile);
            byte[] buffer = new byte[1024];
            InputStream in;
            int readLen = 0;
            in = c.getAssets().open(Name);
            while((readLen = in.read(buffer)) != -1){
                out.write(buffer, 0, readLen);
            }
            out.flush();
            in.close();
            out.close();
        }
    }

    /**
     * Copy file.//拷贝文件
     *
     * @param oldPath the old path
     * @param newPath the new path
     */
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                Log.e(TAG,"复制文件成功");
            }
        }
        catch (Exception e) {
            Log.e(TAG,"复制单个文件操作出错");
            e.printStackTrace();
        }

    }


    /**
     * Create dir. 新建文件夹
     *
     * @param ROOT_PATH the root path
     * @throws IOException the io exception
     */
    public void createDir(String ROOT_PATH) throws IOException {
        File saveDir = new File(ROOT_PATH.substring(0,ROOT_PATH.length()-1));
        if (!saveDir.exists()){
            saveDir.mkdirs();
        }
    }

    /**
     * Write txt to file. 写入内容到txt文件
     *
     * @param strcontent the strcontent
     * @param fileName   the file name
     */
    public void writeTxtToFile(String strcontent, String fileName) {
        String NEW_ROOT_PATH = ROOT_PATH + "txt" + "/";
        try {
            createDir(NEW_ROOT_PATH);
        }catch (IOException e){
            System.out.println(e);
        }
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(NEW_ROOT_PATH, fileName);
        String strFilePath = NEW_ROOT_PATH + fileName ;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d(TAG, "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    /**
     * Make file path file.//生成文件
     *
     * @param filePath the file path
     * @param fileName the file name
     * @return the file
     */
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }


    /**
     * Make root directory.生成文件夹
     *
     * @param filePath the file path
     */
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    //删除文件夹
    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    public String listToString( ArrayList<double []> weights_list){
        String weights_string = "[";
        for (int i = 0; i < weights_list.size(); i++) {
            String weights_list_string = "[";
            double[] weights = weights_list.get(i);
            for (int j = 0;j<weights.length;j++){
                String ping;
                if (j==weights.length-1){ ping = "]";
                }else{ ping = ","; }
                weights_list_string = weights_list_string + String.valueOf(weights[j])+ping;
            }
            String ping;
            if (i==weights_list.size()-1){ ping = "]";
            }else{ ping = ","; }
            weights_string = weights_string+weights_list_string+ping;
        }
        return weights_string;
    }

    /**
     * Array to string string.
     *
     * @param arr the arr
     * @return the string
     */
    public static String arrayToString(double[] arr){
        String message = "";
        for (int i = 0; i < arr.length; i++) {
            message = message + arr[i] + ",";
        }
        return message;
    }
}

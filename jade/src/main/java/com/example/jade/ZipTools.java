package com.example.jade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipTools {


    public static void zip(String srcFileString, String zipPath) throws Exception {
        //创建ZIP
        File zipDir = new File(srcFileString);
        String zipFileName = zipPath+zipDir.getName() + ".zip" ;//压缩后生成的zip文件名
        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileName));
        //创建文件
        File file = new File(srcFileString);
        //压缩
        ZipFiles(file.getParent()+ File.separator, file.getName(), outZip);
        //完成和关闭
        outZip.finish();
        outZip.close();
    }

    /**
     * 压缩文件
     *
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    private static void ZipFiles(String folderString, String fileString, ZipOutputStream zipOutputSteam) throws Exception {
        if (zipOutputSteam == null)
            return;
        File file = new File(folderString + fileString);
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(fileString);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
            zipOutputSteam.closeEntry();
        } else {
            //文件夹
            String fileList[] = file.list();
            //没有子文件和压缩
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }
            //子文件和递归
            for (int i = 0; i < fileList.length; i++) {
                ZipFiles(folderString+fileString+"/",  fileList[i], zipOutputSteam);
            }
        }
    }

      public static void unzip(String path, String target) throws IOException {
        File targetfolder = new File(target);
        ZipInputStream zi = new ZipInputStream(new FileInputStream(path));
        ZipEntry ze = null;
        FileOutputStream fo = null;
        byte[] buff = new byte[1024];
        int len;
        while((ze =  zi.getNextEntry())!=null)
        {
            File _file = new File(targetfolder,ze.getName());
            if(!_file.getParentFile().exists()) _file.getParentFile().mkdirs();
            if(ze.isDirectory())
            {
                _file.mkdir();
            }
            else //file
            {
                fo = new FileOutputStream(_file);
                while((len=zi.read(buff))>0) fo.write(buff, 0, len);
                fo.close();
            }
            zi.closeEntry();
        }
        zi.close();
    }

}

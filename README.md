# SSD-RKNN-Android
ssd mobilenet v1 人脸手势检测

### 模型路径地址在
```
libssddetect/src/main/res/raw
```
### 类别文件txt路径在
```
libssddetect/src/main/assets/hand.txt
```

### 更换模型需要修改的地方
```
新增 libssddetect/src/main/assets/ 目录下的类别文件
修改 libssddetect/src/main/java/ssddemo/PostProcess 文件
NUM_CLASSES = 4;    //输出分类
```

libssddetect java 的库名称必须为com.rockchip.gpadc.ssddemo

因为jni函数名称Java_com_rockchip_gpadc_ssddemo，需要做到对应

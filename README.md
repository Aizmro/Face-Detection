# Face-Detection
Java基于开源模型实现人脸识别

模型来源：https://github.com/biubug6/Pytorch_Retinaface

# 如何使用
Linux/Macos
```java
./gradlew bootRun
```
Windows
```java
./gradlew.bat bootRun
```

# 请求示例
```shell
curl --location --request POST 'http://localhost:8181/resource/process' --form 'image=@"img/largest_selfie.jpg"' --form 'roi="0.0 0.0,1.0 0.0,1.0 1.0,0.0 1.0"'
```

# 响应

```json
{
    "is_alert": true,
    "buffer": "/9j/4AAQSkZJRgABAQAAAQABAAD/...", // 图片Base64
    "data": [
        {
            "x": 0.xxx,
            "y": 0.xxx,
            "width": 0.xxx,
            "height": 0.xxx,
            "className": "Face",
            "probability": 0.xxx
        }
     ]
}
```

![Image text](https://github.com/Pecokie/Face-Detection/blob/main/img/result.png)

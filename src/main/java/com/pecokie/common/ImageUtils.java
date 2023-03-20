package com.pecokie.common;

import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.Rectangle;
import ai.djl.util.RandomUtils;
import com.pecokie.domain.ElectricFence;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageUtils {

    public static Map<String, Object> convertImageROI(int width, int height, Mat image, ElectricFence electricFence) {
        // 定位 ROI 中心坐标点
        int x = (int) (electricFence.maxX() - electricFence.minX()) / 2;
        int centerX = (int) (electricFence.minX() + x);

        int y = (int) (electricFence.maxY() - electricFence.minY()) / 2;
        int centerY = (int) (electricFence.minY() + y);

        // 判断X轴坐标点是否会超出图像
        int startX = centerX - (width / 2);
        int endX = centerX + (width / 2);
        if (startX < 0) {
            // X轴坐标点在图像前
            endX += Math.abs(startX);
            startX = 0;
        } else if (endX > image.width()) {
            // X轴坐标点在图像后
            startX += image.width() - endX;
            endX = width;
        }

        // 判断Y轴坐标点是否会超出图像
        int startY = centerY - (height / 2);
        int endY = centerY + (height / 2);
        if (startY < 0) {
            // Y轴坐标点在图像前
            endY += Math.abs(startY);
            startY = 0;
        } else if (endY > image.height()) {
            // Y轴坐标点在图像后
            startY += image.height() - endY;
            endY = image.height();
        }

        Mat mat = new Mat(image, new Rect(new Point(startX, startY), new Point(endX, endY)));

        Map<String, Object> map = new HashMap<>();
        map.put("startX", startX);
        map.put("startY", startY);
        map.put("mat", mat);

        return map;
    }

    public static byte[] drawExport(Mat mat, DetectedObjects detectedObjects, List<MatOfPoint> matOfPoints) {
        // 绘制 ROI 边框
        Imgproc.polylines(mat, matOfPoints, true, new Scalar(0, 0, 255), 5);

        int imageWidth = mat.width();
        int imageHeight = mat.height();

        List<DetectedObjects.DetectedObject> list = detectedObjects.items();
        for (DetectedObjects.DetectedObject result : list) {
            String className = result.getClassName();
            double probability = result.getProbability();
            BoundingBox box = result.getBoundingBox();

            Rectangle rectangle = box.getBounds();
            int x = (int) (rectangle.getX() * imageWidth);
            int y = (int) (rectangle.getY() * imageHeight);

            Rect rect = new Rect(x, y, (int) (rectangle.getWidth() * imageWidth), (int) (rectangle.getHeight() * imageHeight));
            Scalar color = new Scalar(RandomUtils.nextInt(178), RandomUtils.nextInt(178), RandomUtils.nextInt(178));

            Imgproc.rectangle(mat, rect.tl(), rect.br(), color, 2);

            Size size = Imgproc.getTextSize(className + " " + String.format("%.2f", probability), Imgproc.FONT_HERSHEY_PLAIN, 1.3, 1, null);
            Point br = new Point(x + size.width + 4, y + size.height + 4);
            Imgproc.rectangle(mat, rect.tl(), br, color, -1);

            Point point = new Point(x, y + size.height + 2);
            color = new Scalar(255, 255, 255);
            Imgproc.putText(mat, className + " " + String.format("%.2f", probability), point, Imgproc.FONT_HERSHEY_PLAIN, 1.3, color, 1);
        }

        MatOfByte ofByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, ofByte);
        return ofByte.toArray();
    }

}

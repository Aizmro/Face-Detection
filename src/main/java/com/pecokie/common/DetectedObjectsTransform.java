package com.pecokie.common;

import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.Rectangle;
import com.pecokie.domain.ElectricFence;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class DetectedObjectsTransform {

    public static DetectedObjects transform(DetectedObjects detections, Mat src, Mat dist, ElectricFence resolve) {

        int width = src.width();
        int height = src.height();
        int distWidth = dist.width();
        int distHeight = dist.height();

        List<DetectedObjects.DetectedObject> items = detections.items();

        List<String> classNames = new ArrayList<>();
        List<Double> probabilities = new ArrayList<>();
        List<BoundingBox> boundingBoxes = new ArrayList<>();

        DetectedObjects detectedObjects = new DetectedObjects(classNames, probabilities, boundingBoxes);

        items.forEach(detectedObject -> {
            Rectangle bounds = detectedObject.getBoundingBox().getBounds();
            double x = bounds.getX() * width;
            double y = bounds.getY() * height;
            double wh = bounds.getWidth() * width;
            double ht = bounds.getHeight() * height;

            double centerX = x + (wh / 2);
            double centerY = y + (ht / 2);

            // 判断中心点是否在 ROI 内
            MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
            matOfPoint2f.fromArray(resolve.matOfPoints().toArray());
            double v = Imgproc.pointPolygonTest(matOfPoint2f, new Point(centerX, centerY), true);
            if (v >= 0) {
                classNames.add(detectedObject.getClassName());
                probabilities.add(detectedObject.getProbability());
                boundingBoxes.add(new Rectangle(x / distWidth, y / distHeight, wh / distWidth, ht / distHeight));
            }
        });
        return detectedObjects;
    }

}

package com.pecokie.common;

import com.pecokie.domain.ElectricFence;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ElectricFenceUtils {

    public static ElectricFence resolve(int width, int height, String roi) {
        String[] split = roi.split(",");
        List<Point> points = new ArrayList<>();
        List<Double> xList = new ArrayList<>();
        List<Double> yList = new ArrayList<>();

        Arrays.stream(split).map(s -> s.split(" ")).forEach(coordinate -> {
            double x = Double.parseDouble(coordinate[0]) * width;
            double y = Double.parseDouble(coordinate[1]) * height;
            xList.add(x);
            yList.add(y);
            points.add(new Point(x, y));
        });
        MatOfPoint matOfPoint = new MatOfPoint();
        matOfPoint.fromList(points);

        Double minX = Collections.min(xList);
        Double maxX = Collections.max(xList);
        Double minY = Collections.min(yList);
        Double maxY = Collections.max(yList);

        return new ElectricFence.Builder().minX(minX).maxX(maxX).minY(minY).maxY(maxY).matOfPoints(matOfPoint).build();
    }

}

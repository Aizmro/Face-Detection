package com.pecokie.application;

import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.Rectangle;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import com.pecokie.common.DetectedObjectsTransform;
import com.pecokie.common.ElectricFenceUtils;
import com.pecokie.common.ImageUtils;
import com.pecokie.domain.ElectricFence;
import com.pecokie.param.AlgorithmData;
import com.pecokie.param.AlgorithmView;
import com.pecokie.properties.AlgorithmProperties;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Collections;
import java.util.List;


@Service
public class AlgorithmAppService {
    private final static Logger LOGGER = LoggerFactory.getLogger(AlgorithmAppService.class);

    static {
        OpenCV.loadLocally();
    }

    private final AlgorithmProperties algorithmProperties;
    private final ZooModel<Image, DetectedObjects> model;

    public AlgorithmAppService(AlgorithmProperties algorithmProperties, ZooModel<Image, DetectedObjects> model) {
        this.algorithmProperties = algorithmProperties;
        this.model = model;
    }


    public AlgorithmView process(byte[] bytes, String roi) {
        Mat imdecode = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_UNCHANGED);

        ElectricFence resolve = ElectricFenceUtils.resolve(imdecode.width(), imdecode.height(), roi);

        Image img = ImageFactory.getInstance().fromImage(imdecode);
        long predictStartTime = System.currentTimeMillis();

        DetectedObjects detections;
        try (Predictor<Image, DetectedObjects> predictor = model.newPredictor()) {
            detections = predictor.predict(img);
        } catch (TranslateException e) {
            throw new RuntimeException(e);
        }

        // 过滤 Face
        List<DetectedObjects.DetectedObject> detectedObjectList = detections.items();
        List<DetectedObjects.DetectedObject> objectList = detectedObjectList.stream().filter(classification -> classification.getClassName().equals("Face")).toList();

        // 重新打包
        DetectedObjects detectedObjects = new DetectedObjects(
                objectList.stream().map(Classifications.Classification::getClassName).toList(),
                objectList.stream().map(Classifications.Classification::getProbability).toList(),
                objectList.stream().map(DetectedObjects.DetectedObject::getBoundingBox).toList()
        );

        DetectedObjects transform = DetectedObjectsTransform.transform(detectedObjects, imdecode, imdecode, resolve);

        LOGGER.info("detect cost {}ms | results: {}", System.currentTimeMillis() - predictStartTime, transform);

        List<DetectedObjects.DetectedObject> items = transform.items();

        List<AlgorithmData> collect = items.stream().map(detectedObject -> {
            Rectangle bounds = detectedObject.getBoundingBox().getBounds();
            return new AlgorithmData.Builder()
                    .x(bounds.getX()).y(bounds.getY())
                    .width(bounds.getWidth()).height(bounds.getHeight())
                    .className(detectedObject.getClassName()).probability(detectedObject.getProbability())
                    .build();
        }).toList();

        byte[] image = ImageUtils.drawExport(imdecode, transform, Collections.singletonList(resolve.matOfPoints()));
        return new AlgorithmView.Builder().is_alert(!collect.isEmpty()).buffer(Base64.getEncoder().encodeToString(image)).data(collect).build();
    }

}

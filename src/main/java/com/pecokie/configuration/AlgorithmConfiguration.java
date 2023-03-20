package com.pecokie.configuration;

import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import com.pecokie.properties.AlgorithmProperties;
import com.pecokie.translator.FaceDetectionTranslator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
public class AlgorithmConfiguration {

    private final AlgorithmProperties algorithmProperties;

    public AlgorithmConfiguration(AlgorithmProperties algorithmProperties) {
        this.algorithmProperties = algorithmProperties;
    }


    @Bean
    public Criteria<Image, DetectedObjects> criteria() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:" + algorithmProperties.getModelPath());

        Device device = Device.Type.CPU.equalsIgnoreCase(algorithmProperties.getType()) ? Device.cpu() : Device.gpu();

        double confThresh = 0.85f;
        double nmsThresh = 0.45f;
        double[] variance = {0.1f, 0.2f};
        int topK = 5000;
        int[][] scales = {{16, 32}, {64, 128}, {256, 512}};
        int[] steps = {8, 16, 32};
        FaceDetectionTranslator translator =
                new FaceDetectionTranslator(confThresh, nmsThresh, variance, topK, scales, steps);

        return Criteria.builder()
                .setTypes(Image.class, DetectedObjects.class)
                .optModelPath(file.toPath())
                // Load model from local file, e.g:
                .optModelName("retinaface") // specify model file prefix
                .optTranslator(translator)
                .optProgress(new ProgressBar())
                .optEngine("PyTorch") // Use PyTorch engine
                .optDevice(device)
                .build();
    }



    @Bean
    public ZooModel<Image, DetectedObjects> model() throws ModelNotFoundException, MalformedModelException, IOException {
        return ModelZoo.loadModel(criteria());
    }
}

package com.pecokie.common;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.Rectangle;
import ai.djl.ndarray.NDList;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import com.pecokie.properties.AlgorithmProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 相对位置的Translator
 */
public class RelativeTranslator implements Translator<Image, DetectedObjects> {
    @Autowired
    AlgorithmProperties algorithmProperties;
    private final Translator<Image, DetectedObjects> delegated;

    public RelativeTranslator(Translator<Image, DetectedObjects> translator) {
        this.delegated = translator;
    }

    @Override
    public DetectedObjects processOutput(TranslatorContext ctx, NDList list) throws Exception {
        DetectedObjects output = delegated.processOutput(ctx, list);
        List<String> classList = new ArrayList<>();
        List<Double> probList = new ArrayList<>();
        List<BoundingBox> rectList = new ArrayList<>();

        final Integer width = algorithmProperties.getWidth();
        final Integer height = algorithmProperties.getHeight();

        final List<DetectedObjects.DetectedObject> items = output.items();
        items.forEach(item -> {
            classList.add(item.getClassName());
            probList.add(item.getProbability());

            Rectangle b = item.getBoundingBox().getBounds();
            Rectangle newBox = new Rectangle(b.getX() / width, b.getY() / height, b.getWidth() / width, b.getHeight() / height);

            rectList.add(newBox);
        });
        return new DetectedObjects(classList, probList, rectList);
    }

    @Override
    public NDList processInput(TranslatorContext ctx, Image input) throws Exception {
        return delegated.processInput(ctx, input);
    }

    @Override
    public void prepare(TranslatorContext ctx) throws Exception {
        delegated.prepare(ctx);
    }

    @Override
    public Batchifier getBatchifier() {
        return delegated.getBatchifier();
    }
}

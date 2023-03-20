package com.pecokie.domain;

import org.opencv.core.MatOfPoint;

public record ElectricFence(Double minX, Double maxX, Double minY, Double maxY, MatOfPoint matOfPoints) {

    public static class Builder {
        Double minX;
        Double maxX;
        Double minY;
        Double maxY;
        MatOfPoint matOfPoints;

        public Builder minX(Double minX) {
            this.minX = minX;
            return this;
        }

        public Builder maxX(Double maxX) {
            this.maxX = maxX;
            return this;
        }

        public Builder minY(Double minY) {
            this.minY = minY;
            return this;
        }

        public Builder maxY(Double maxY) {
            this.maxY = maxY;
            return this;
        }

        public Builder matOfPoints(MatOfPoint matOfPoints) {
            this.matOfPoints = matOfPoints;
            return this;
        }

        public ElectricFence build() {
            return new ElectricFence(minX, maxX, minY, maxY, matOfPoints);
        }
    }

}

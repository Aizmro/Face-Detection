package com.pecokie.param;

public record AlgorithmData(double x, double y, double width, double height, String className, double probability) {
    public static class Builder {
        double x;
        double y;
        double width;
        double height;
        String className;
        double probability;

        public Builder x(double x) {
            this.x = x;
            return this;
        }

        public Builder y(double y) {
            this.y = y;
            return this;
        }

        public Builder width(double width) {
            this.width = width;
            return this;
        }

        public Builder height(double height) {
            this.height = height;
            return this;
        }

        public Builder className(String className) {
            this.className = className;
            return this;
        }

        public Builder probability(double probability) {
            this.probability = probability;
            return this;
        }

        public AlgorithmData build() {
            return new AlgorithmData(x, y, width, height, className, probability);
        }
    }
}
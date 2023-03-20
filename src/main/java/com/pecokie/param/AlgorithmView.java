package com.pecokie.param;

import java.util.List;

public record AlgorithmView(Boolean is_alert, String buffer, List<AlgorithmData> data) {
    public static class Builder {
        private Boolean is_alert;
        private String buffer;
        private List<AlgorithmData> data;

        public Builder is_alert(Boolean is_alert) {
            this.is_alert = is_alert;
            return this;
        }

        public Builder buffer(String buffer) {
            this.buffer = buffer;
            return this;
        }

        public Builder data(List<AlgorithmData> data) {
            this.data = data;
            return this;
        }

        public AlgorithmView build() {
            return new AlgorithmView(is_alert, buffer, data);
        }
    }
}
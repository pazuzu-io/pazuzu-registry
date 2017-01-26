package org.zalando.pazuzu.feature;

import java.util.Objects;

public enum FeatureStatus {
    APPROVED("approved"),
    DECLINED("declined"),
    PENDING("pending");

    private final String jsonValue;

    FeatureStatus(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    public String jsonValue() {
        return jsonValue;
    }

    static public FeatureStatus fromJsonValue(String jsonValue) {
        for (FeatureStatus fs : values()) {
            if (Objects.equals(fs.jsonValue, jsonValue)) {
                return fs;
            }
        }
        return null;
    }
}
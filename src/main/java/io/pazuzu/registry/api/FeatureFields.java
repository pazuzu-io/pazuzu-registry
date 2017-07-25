package io.pazuzu.registry.api;

import java.util.Locale;

/**
 * The possible fields that can be returned by the features api call.
 * Currently we have only 2 case, every fields and meta only.
 */
public enum FeatureFields {
    ALL,
    META;

    static public FeatureFields getFields(String fields) {
        if (fields == null) {
            // by default only meta.
            return META;
        }

        if (fields.toLowerCase(Locale.ENGLISH).contains("snippet")) {
            return ALL;
        } else {
            return META;
        }

    }
}

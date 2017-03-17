package org.zalando.pazuzu.feature;

import com.google.common.base.MoreObjects;
import org.zalando.pazuzu.exception.FeatureNameEmptyException;

import java.util.Optional;

public final class NameVersion {

    private final String name;
    private final String version;

    private NameVersion(String name) {
        this(name, null);
    }

    private NameVersion(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String name() {
        return this.name;
    }

    public Optional<String> version() {
        return Optional.ofNullable(this.version);
    }

    public static NameVersion parse(String s) {
        if (s == null || s.isEmpty()) {
            throw new FeatureNameEmptyException();
        }

        final int indexOfColon = s.indexOf(":");
        if (indexOfColon == 0) {
            throw new FeatureNameEmptyException();
        } else if (indexOfColon > 0) {
            return new NameVersion(s.substring(0, indexOfColon), s.substring(indexOfColon + 1));
        } else {
            // There is no version specified.
            return new NameVersion(s);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", this.name)
                .add("version", this.version)
                .omitNullValues()
                .toString();
    }
}

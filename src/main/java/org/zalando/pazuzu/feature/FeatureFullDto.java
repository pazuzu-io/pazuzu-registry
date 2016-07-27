package org.zalando.pazuzu.feature;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import org.zalando.pazuzu.feature.file.FileDto;
import org.zalando.pazuzu.feature.tag.TagDto;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class FeatureFullDto extends FeatureDto {

    @JsonProperty("dependencies")
    private Set<FeatureDto> dependencies = new HashSet<>();

    @JsonProperty("files")
    private Set<FileDto> files = new HashSet<>();

    public static FeatureFullDto makeFull(Feature feature) {
        Preconditions.checkNotNull(feature);

        final FeatureFullDto result = new FeatureFullDto();
        fillShort(feature, result);

        if (feature.getDependencies() != null) {
            result.dependencies = feature.getDependencies().stream().map(FeatureDto::ofShort).collect(toSet());
        }

        if (feature.getTags() != null && !feature.getTags().isEmpty()) {
            result.setTags(feature.getTags().stream().map(TagDto::ofShort).collect(Collectors.toList()));
        }

        if (feature.getFiles() != null) {
            result.files = feature.getFiles().stream().map(FileDto::fromFile).collect(toSet());
        }
        return result;
    }

    public Set<FeatureDto> getDependencies() {
        return dependencies;
    }

    public Set<FileDto> getFiles() {
        return files;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        FeatureFullDto that = (FeatureFullDto) o;
        return Objects.equals(dependencies, that.dependencies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dependencies);
    }

    @Override
    public String toString() {
        return "FeatureFullDto{" +
                "dependencies=" + dependencies +
                ", files=" + files +
                "} " + super.toString();
    }
}

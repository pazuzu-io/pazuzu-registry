package org.zalando.pazuzu.infrastructure.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "feature_name", nullable = false, length = 256)
    private String name;
    @Column(name = "docker_data", nullable = false, length = 4096)
    private String dockerData;
    @Column(name = "test_instruction", nullable = true, length = 4096)
    private String testInstruction;
    @Column(name = "description", nullable = true, length = 4096)
    private String description;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "feature_dependency",
            joinColumns = @JoinColumn(name = "feature_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "dependency_feature_id", nullable = false))
    public Set<Feature> dependencies;
    @ManyToMany(fetch = FetchType.LAZY)
    public List<Tag> tags;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDockerData() {
        return dockerData;
    }

    public void setDockerData(String dockerData) {
        this.dockerData = dockerData;
    }

    public Set<Feature> getDependencies() {
        if (null == dependencies) {
            dependencies = new HashSet<>();
        }
        return dependencies;
    }

    public void setDependencies(Set<Feature> dependencies) {
        this.dependencies = dependencies;
    }

    public boolean containsDependencyRecursively(Feature f) {
        if (this == f) {
            return true;
        }
        return getDependencies().stream().filter(item -> item.containsDependencyRecursively(f)).findAny().isPresent();
    }

    public String getTestInstruction() {
        return testInstruction;
    }

    public void setTestInstruction(String testInstruction) {
        this.testInstruction = testInstruction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Feature)) {
            return false;
        }
        Feature other = (Feature) obj;
        return Objects.equals(getId(), other.getId());
    }
}

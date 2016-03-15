package pazuzu.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity(name = "Feature")
@Table(name = "feature")
public class Feature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feature_id", nullable = false, updatable = false)
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "docker_data", nullable = false, length = 4096)
    private String dockerData;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "feature_dependency",
            joinColumns = @JoinColumn(name = "feature_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "dependency_feature_id", nullable = false))
    public Set<Feature> dependencies;

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
}

package org.zalando.pazuzu.registry;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * Entity representing the registry.
 * Until the api allow update, this is a read only entity.
 */
@Entity
public class Registry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "owner", length = 256, nullable = false)
    private String owner;
    @Column(name = "version", length = 64, nullable = false)
    private String version;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public Integer getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getVersion() {
        return version;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Registry registry = (Registry) o;
        return Objects.equals(id, registry.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Registry{" +
                "id=" + id +
                ", owner='" + owner + '\'' +
                ", version='" + version + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

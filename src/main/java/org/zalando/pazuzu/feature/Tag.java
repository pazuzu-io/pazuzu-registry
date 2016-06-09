package org.zalando.pazuzu.feature;

import javax.persistence.*;

/**
 * Created by vpavlyshyn on 09/06/16.
 */
@Entity
public class Tag {
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "tag_name", nullable = false, length = 256)
    private String name;
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
        if (!(obj instanceof Tag)) {
            return false;
        }

        Tag other = (Tag)obj;
        return this.getId() == other.getId();
    }
}

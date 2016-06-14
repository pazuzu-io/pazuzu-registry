package org.zalando.pazuzu.feature.tag;

import javax.persistence.*;

/**
 * Created by vpavlyshyn on 09/06/16.
 */
@Entity
public class Tag {
    public Tag() {
        this.name = "";
    }

    public Tag(String name) {
        this.name = name;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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

    @Column(name = "tag_name", nullable = false, length = 256, unique = true)

    private String name;

    @Override
    public int hashCode() {
        return this.getName().hashCode();
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

        Tag other = (Tag) obj;
        return this.getName() == other.getName();
    }
}

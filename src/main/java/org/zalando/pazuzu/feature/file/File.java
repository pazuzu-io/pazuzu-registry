package org.zalando.pazuzu.feature.file;

import javax.persistence.*;
import java.nio.file.Path;

@Entity
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 256, unique = true)
    private String name;

    @Column(name = "content_path", nullable = false, length = 512)
    private String contentPath;

    public File() {
    }

    public File(String name, Path contentPath) {
        setName(name);
        setContentPath(contentPath.toString());
    }

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        File file = (File) o;

        return id != null ? id.equals(file.id) : file.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
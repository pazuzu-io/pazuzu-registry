package pazuzu.model;

import java.util.ArrayList;

public class Feature {

    public final String name;
    public final String description;
    public final String dockerfile_snippet;
    public final String test_command;
    public final ArrayList<Feature> dependencies;


    public Feature(String name, String description,
                   String dockerfile_snippet,
                   String test_command,
                   ArrayList<Feature> dependencies) {
        this.name = name;
        this.description = description;
        this.dockerfile_snippet = dockerfile_snippet;
        this.test_command = test_command;
        this.dependencies = dependencies;
    }

    @Override
    public String toString() {
        return "DAL.Feature{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dockerfile_snippet='" + dockerfile_snippet + '\'' +
                ", test_command='" + test_command + '\'' +
                ", dependencies=" + dependencies +
                '}';
    }

}

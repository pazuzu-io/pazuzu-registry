package pazuzu.model;

import java.util.List;

public class Feature {

    public String name;
    public String description;
    public String dockerfile_snippet;
    public String test_command;
    public List<Feature> dependencies;

    public Feature() {
	}

	public Feature(String name, String description,
                   String dockerfile_snippet,
                   String test_command,
                   List<Feature> dependencies) {
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


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDockerfile_snippet() {
        return dockerfile_snippet;
    }

    public String getTest_command() {
        return test_command;
    }

    public List<Feature> getDependencies() {
        return dependencies;
    }

}

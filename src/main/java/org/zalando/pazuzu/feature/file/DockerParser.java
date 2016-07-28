package org.zalando.pazuzu.feature.file;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Is used to find source file references for ADD and COPY commands
 */
public class DockerParser {
    // TODO Handle quoted names (probably just regexp is insufficient then).
    // TODO Recognize "file://" scheme for COPY command.
    private static final Pattern SOURCEFILE_REGEX = Pattern.compile("^\\s*(ADD|COPY)\\s+(?<src>[^\\s]+).+", Pattern.MULTILINE);

    public static Set<String> getCopyFiles(String source) {
        final Set<String> result = new HashSet<>();
        final Matcher matcher = SOURCEFILE_REGEX.matcher(source);
        while (matcher.find()) {
            result.add(matcher.group("src"));
        }
        return result;
    }
}

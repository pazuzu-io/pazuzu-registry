package org.zalando.pazuzu.feature;


import org.zalando.pazuzu.exception.Error;

public class FeatureErrors {
    public static final Error FEATURE_DUPLICATE = new Error("feature_duplicate", "Feature with this name already exists");
    public static final Error FEATURE_NAME_EMPTY = new Error("feature_name_empty", "Feature name is empty");
    public static final Error FEATURE_NOT_FOUND = new Error("feature_not_found", "Feature was not found");
    public static final Error FEATURE_NOT_DELETABLE_DUE_TO_REFERENCES = new Error("feature_not_deletable_due_to_references", "Can't delete feature because it still has references");
    public static final Error FEATURE_HAS_RECURSIVE_DEPENDENCY = new Error("feature_has_recursive_dependency", "Recursive dependencies found");
}

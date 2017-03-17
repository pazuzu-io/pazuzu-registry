package org.zalando.pazuzu.feature;

import java.util.Comparator;

public final class FeatureComparator implements Comparator<Feature> {

    @Override
    public int compare(Feature f1, Feature f2) {
        String[] versionParts1 = f1.getVersion().split("\\.");
        String[] versionParts2 = f2.getVersion().split("\\.");

        int versionLength1 = versionParts1.length;
        int versionLength2 = versionParts2.length;

        int length = Math.min(versionLength1, versionLength2);

        for (int i = 0; i < length; ++i) {
            int versionPart1 = Integer.parseInt(versionParts1[i]);
            int versionPart2 = Integer.parseInt(versionParts2[i]);

            if (versionPart1 < versionPart2) {
                return -1;
            } else if (versionPart1 > versionPart2) {
                return 1;
            }
        }

        if (versionLength1 == versionLength2) {
            return 0;
        } else if (versionLength1 > versionLength2) {
            return 1;
        } else {
            return -1;
        }
    }

}

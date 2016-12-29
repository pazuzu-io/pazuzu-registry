package org.zalando.pazuzu.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.zalando.pazuzu.exception.ServiceException;
import org.zalando.pazuzu.security.Roles;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/feature-metas")
public class FeaturesMetaResource {

    private static final String X_TOTAL_COUNT = "X-Total-Count";
    private final FeatureService featureService;

    @Autowired
    public FeaturesMetaResource(FeatureService featureService) {
        this.featureService = featureService;
    }

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FeatureMetaDto> listFeaturesMeta(
            @RequestParam(required = false, name = "name") String[] featureNames,
            @RequestParam(required = false, name = "offset") Integer offset,
            @RequestParam(required = false, name = "limit") Integer limit,
            HttpServletResponse response)
            throws ServiceException {
        if (featureNames == null) {
            if (offset != null && limit != null) {
                FeaturesWithTotalCount<FeatureMetaDto> featuresTotalCount =
                        featureService.getFeaturesWithTotalCount(offset, limit, FeatureMetaDto::of);
                response.setHeader(X_TOTAL_COUNT, Long.toString(featuresTotalCount.getTotalCount()));
                response.setHeader("Access-Control-Expose-Headers", X_TOTAL_COUNT);
                return featuresTotalCount.getFeatures();
            } else {
                return featureService.listFeatures("", FeatureMetaDto::of);
            }
        }
        Set<Feature> featureSet = featureService.loadFeatures(Arrays.stream(featureNames).collect(Collectors.toList()));
        return featureSet.stream().map(FeatureMetaDto::of).collect(Collectors.toList());
    }

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    @RequestMapping(value = "/{featureName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public FeatureMetaDto getFeature(@PathVariable String featureName) throws ServiceException {
        return featureService.getFeature(featureName, FeatureMetaDto::of);
    }
}

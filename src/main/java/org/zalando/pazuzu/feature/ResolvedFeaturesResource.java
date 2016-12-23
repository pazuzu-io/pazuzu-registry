package org.zalando.pazuzu.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.zalando.pazuzu.exception.ServiceException;
import org.zalando.pazuzu.security.Roles;

import javax.annotation.security.RolesAllowed;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/resolved-features")
public class ResolvedFeaturesResource {
    private final FeatureService featureService;

    @Autowired
    public ResolvedFeaturesResource(FeatureService featureService) {
        this.featureService = featureService;
    }

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FeatureDto> listResolvedFeatures(@RequestParam(name = "name") String[] featureNames) throws ServiceException {

        return featureService
                .resolveFeatures(Arrays.stream(featureNames).collect(Collectors.toList()))
                .stream().map(FeatureDto::of).collect(Collectors.toList());
    }
}

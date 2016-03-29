package org.zalando.pazuzu.docker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.pazuzu.exception.ServiceException;
import org.zalando.pazuzu.feature.FeatureService;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/dockerfile")
public class DockerfileResource {

    private final FeatureService featureService;

    @Autowired
    public DockerfileResource(FeatureService featureService) {
        this.featureService = featureService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String generateDockerfile(@RequestParam(required = false) List<String> features) throws ServiceException {
        return featureService.generateDockerfile(features);
    }
}

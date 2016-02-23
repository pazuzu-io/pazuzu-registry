package pazuzu.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pazuzu.service.FeatureService;
import pazuzu.model.Feature;
import pazuzu.web.InvalidFeatureException;
import pazuzu.web.to.ErrorTO;
import pazuzu.web.to.FeatureTO;
import pazuzu.web.to.FeatureRefTO;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FeatureRefsController {

    @Autowired
    public FeatureService featureService;

    public ErrorTO handleFeatureExceptions(InvalidFeatureException e){
        return new ErrorTO(ErrorTO.INVALID_FEATURE_REQUESTED, e.getMessage());
    }

    /**
     * Return a List of all Features
     * @return
     */
    @RequestMapping(value = "/featurerefs", method = RequestMethod.GET)
    List<FeatureRefTO> getFeatureRefs(){

        return featureService.getAllFeatures().stream()
                .map(FeatureRefTO.byFeature)
                .collect(Collectors.toList());
    }


    /**
     * Return a List of Features
     * @param features
     * @return
     */
    @RequestMapping(value = "/featurama", method = RequestMethod.GET)
    List<FeatureTO> getFeatures(List<String> features) throws InvalidFeatureException{

        List<String> validFeatureNames = featureService.validateFeatureNames(features);
        if(validFeatureNames.size() != features.size()){
            throw new InvalidFeatureException("You requested a invalid feature.");
        }

        try {
            return featureService.createSortedFeaturelistWithDependencies(validFeatureNames).stream()
                    .map(FeatureTO.byFeature)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            // Feature was removed between check an actual List creation. Try again
            throw new InvalidFeatureException("The requested Feature is not available");
        }
    }
}

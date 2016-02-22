package pazuzu.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pazuzu.service.FeatureService;
import pazuzu.model.Feature;
import pazuzu.web.to.FeatureTO;
import pazuzu.web.to.FeatureRefTO;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FeatureRefsController {

    @Autowired
    public FeatureService featureService;

    @RequestMapping(value = "/featurerefs", method = RequestMethod.GET)
    FeatureRefTO getFeatureRefs(){

        return FeatureRefTO.byFeature(new Feature("Java", "super duper awesome java stuff"));
    }


    @RequestMapping(value = "/featurama", method = RequestMethod.GET)
    List<FeatureTO> getFeatures(){
        ArrayList features = new ArrayList<FeatureTO>();
        features.add(new FeatureTO("asf", "asdfasdfa"));
        features.add(new FeatureTO("ewrgwer", "4t34t234t"));
        features.add(new FeatureTO("nfghng", "67j6767"));
        features.add(new FeatureTO("rtertyer", "wergwerg"));

        return features;
    }
}

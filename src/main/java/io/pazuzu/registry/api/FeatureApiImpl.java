package io.pazuzu.registry.api;

import io.pazuzu.registry.model.Feature;
import io.pazuzu.registry.model.FeatureList;
import io.pazuzu.registry.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
public final class FeatureApiImpl implements FeatureApi {
    private FeatureApiService featureApiService;

    @Autowired
    public FeatureApiImpl(FeatureApiService featureApiService) {
        this.featureApiService = featureApiService;
    }

    @Override
    @RequestMapping(value = "/features", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<FeatureList> listFeatures(@RequestParam(value = "q", required = false)
                                                            String q,
                                                    @RequestParam(value = "author", required = false)
                                                            String author,
                                                    @RequestParam(value = "fields", required = false)
                                                            String fields,
                                                    @RequestParam(value = "status", required = false)
                                                            String status,
                                                    @RequestParam(value = "offset", required = false)
                                                            Integer offset,
                                                    @RequestParam(value = "limit", required = false)
                                                            Integer limit) {
        return this.featureApiService.listFeatures(q,
                author,
                fields,
                status,
                offset,
                limit);
    }

    @Override
    @RequestMapping(value = "/features/{name}", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<Feature> getFeature(@PathVariable("name")
                                                      String name) {
        return this.featureApiService.getFeature(name);
    }

    @Override
    @RequestMapping(value = "/features/{name}/reviews", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Review> reviewFeature(@PathVariable("name")
                                                        String name,
                                                @RequestBody
                                                        Review review) {
        return this.featureApiService.reviewFeature(name,
                review);
    }

    @Override
    @RequestMapping(value = "/features", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Feature> createFeature(@RequestBody
                                                         Feature feature) {
        return this.featureApiService.createFeature(feature);
    }

}

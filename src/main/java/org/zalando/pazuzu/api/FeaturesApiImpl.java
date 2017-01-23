package org.zalando.pazuzu.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zalando.pazuzu.model.Feature;
import org.zalando.pazuzu.model.FeatureList;

//
// GENERATED CLASS, DO NOT EDIT
//
// RestController implementation. No need to modify or extend. Just use it.
//

@RestController
@RequestMapping("api")
public final class FeaturesApiImpl implements FeaturesApi {
  private FeatureServiceImpl impl;

  @Autowired
  public FeaturesApiImpl(FeatureServiceImpl impl) {
    this.impl = impl;
  }

  @Override
  @RequestMapping(value="/features",method=RequestMethod.GET , produces={ "application/json" })
  public ResponseEntity<FeatureList> featuresGet( @RequestParam(value="q", required=false)
    String q,
     @RequestParam(value="author", required=false)
    String author,
     @RequestParam(value="fields", required=false)
    String fields,
     @RequestParam(value="status", required=false)
    String status,
     @RequestParam(value="offset", required=false)
    Integer offset,
     @RequestParam(value="limit", required=false)
    Integer limit) {
    return this.impl.featuresGet(    q,
      author,
      fields,
      status,
      offset,
      limit);
  }

  @Override
  @RequestMapping(value="/features/{name}",method=RequestMethod.DELETE )
  public ResponseEntity<Void> featuresNameDelete( @PathVariable("name")
    String name) {
    return this.impl.featuresNameDelete(    name);
  }

  @Override
  @RequestMapping(value="/features/{name}",method=RequestMethod.GET , produces={ "application/json" })
  public ResponseEntity<Feature> featuresNameGet( @PathVariable("name")
    String name) {
    return this.impl.featuresNameGet(    name);
  }

  @Override
  @RequestMapping(value="/features/{name}",method=RequestMethod.PUT,consumes={ "application/json" } , produces={ "application/json" })
  public ResponseEntity<Feature> featuresNamePut( @PathVariable("name")
    String name,
     @RequestBody
    Feature feature) {
    return this.impl.featuresNamePut(    name,
      feature);
  }

  @Override
  @RequestMapping(value="/features",method=RequestMethod.POST,consumes={ "application/json" } , produces={ "application/json" })
  public ResponseEntity<Feature> featuresPost( @RequestBody
    Feature feature) {
    return this.impl.featuresPost(    feature);
  }

}

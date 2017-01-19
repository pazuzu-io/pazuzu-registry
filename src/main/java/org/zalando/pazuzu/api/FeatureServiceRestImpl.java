package org.zalando.pazuzu.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zalando.pazuzu.model.Feature;
import org.zalando.pazuzu.model.FeatureList;

import java.util.List;

//
// GENERATED CLASS, DO NOT EDIT
//
// RestController implementation. No need to modify or extend. Just use it.
//

@RestController
public final class FeatureServiceRestImpl implements ApiApi {
  private ApiApi impl;

  @Autowired
  public FeatureServiceRestImpl(ApiApi impl) {
    this.impl = impl;
  }

  @Override
  @RequestMapping(value="/api/features",method=RequestMethod.GET , produces={ "application/json" })
  public ResponseEntity<FeatureList> apiFeaturesGet( @RequestParam(value="names", required=false)
    List<String> names,
     @RequestParam(value="resolve", required=false)
    Boolean resolve,
     @RequestParam(value="q", required=false)
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
    return this.impl.apiFeaturesGet(    names,
      resolve,
      q,
      author,
      fields,
      status,
      offset,
      limit);
  }

  @Override
  @RequestMapping(value="/api/features/{name}",method=RequestMethod.DELETE )
  public ResponseEntity<Void> apiFeaturesNameDelete( @PathVariable("name")
    String name) {
    return this.impl.apiFeaturesNameDelete(    name);
  }

  @Override
  @RequestMapping(value="/api/features/{name}",method=RequestMethod.GET , produces={ "application/json" })
  public ResponseEntity<Feature> apiFeaturesNameGet( @PathVariable("name")
    String name) {
    return this.impl.apiFeaturesNameGet(    name);
  }

  @Override
  @RequestMapping(value="/api/features/{name}",method=RequestMethod.PUT,consumes={ "application/json" } , produces={ "application/json" })
  public ResponseEntity<Feature> apiFeaturesNamePut( @PathVariable("name")
    String name,
     @RequestBody
    Feature feature) {
    return this.impl.apiFeaturesNamePut(    name,
      feature);
  }

  @Override
  @RequestMapping(value="/api/features",method=RequestMethod.POST,consumes={ "application/json" } , produces={ "application/json" })
  public ResponseEntity<Feature> apiFeaturesPost( @RequestBody
    Feature feature) {
    return this.impl.apiFeaturesPost(    feature);
  }

}

package org.zalando.pazuzu.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zalando.pazuzu.model.Feature;
import org.zalando.pazuzu.model.FeatureMeta;

import java.util.List;

@RestController
public final class FeatureServiceRestImpl implements ApiApi {
  private FeatureServiceImpl impl;

  @Autowired
  public FeatureServiceRestImpl(FeatureServiceImpl impl) {
    this.impl = impl;
  }

  @Override
  @RequestMapping(value="/api/feature-metas",method=RequestMethod.GET , produces={ "application/json" })
  public ResponseEntity<List<FeatureMeta>> apiFeatureMetasGet( @RequestParam(value="name", required=false)
    List<String> name,
     @RequestParam(value="offset", required=false)
    Integer offset,
     @RequestParam(value="limit", required=false)
    Integer limit) {
    return this.impl.apiFeatureMetasGet(    name,
      offset,
      limit);
  }

  @Override
  @RequestMapping(value="/api/feature-metas/{name}",method=RequestMethod.GET , produces={ "application/json" })
  public ResponseEntity<FeatureMeta> apiFeatureMetasNameGet( @PathVariable("name")
    String name) {
    return this.impl.apiFeatureMetasNameGet(    name);
  }

  @Override
  @RequestMapping(value="/api/features",method=RequestMethod.GET , produces={ "application/json" })
  public ResponseEntity<List<Feature>> apiFeaturesGet( @RequestParam(value="names", required=false)
    List<String> names,
     @RequestParam(value="offset", required=false)
    Integer offset,
     @RequestParam(value="limit", required=false)
    Integer limit) {
    return this.impl.apiFeaturesGet(    names,
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

  @Override
  @RequestMapping(value="/api/resolved-features",method=RequestMethod.GET , produces={ "application/json" })
  public ResponseEntity<List<Feature>> apiResolvedFeaturesGet( @RequestParam(value="names")
    List<String> names) {
    return this.impl.apiResolvedFeaturesGet(    names);
  }

}

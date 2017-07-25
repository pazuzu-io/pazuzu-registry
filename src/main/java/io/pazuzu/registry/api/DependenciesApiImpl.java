package io.pazuzu.registry.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.pazuzu.registry.model.DependenciesList;

import java.util.List;

//
// GENERATED CLASS, DO NOT EDIT
//
// RestController implementation. No need to modify or extend. Just use it.
//

@RestController
@RequestMapping(value="/api")
public final class DependenciesApiImpl implements DependenciesApi {
  private DependenciesServiceImpl impl;

  @Autowired
  public DependenciesApiImpl(DependenciesServiceImpl impl) {
    this.impl = impl;
  }

  @Override
  @RequestMapping(value="/dependencies",method=RequestMethod.GET , produces={ "application/json" })
  public ResponseEntity<DependenciesList> dependenciesGet( @RequestParam(value="names")
    List<String> names) {
    return this.impl.dependenciesGet(    names);
  }

}

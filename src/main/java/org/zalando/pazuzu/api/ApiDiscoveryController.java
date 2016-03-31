package org.zalando.pazuzu.api;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@CrossOrigin
public class ApiDiscoveryController {

    @RequestMapping(value = "/api/swagger.yaml")
    @ResponseBody
    public ResponseEntity<InputStreamResource> serveSwaggerApiDefinition(HttpServletResponse response) {
        response.setContentType("text/x-yaml");
        return ResponseEntity.ok(new InputStreamResource(this.getClass().getResourceAsStream("/api/swagger.yaml")));
    }
}

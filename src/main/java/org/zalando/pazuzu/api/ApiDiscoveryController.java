package org.zalando.pazuzu.api;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;

@Controller
@CrossOrigin
public class ApiDiscoveryController {

    @RequestMapping(value = "/api/swagger.yaml")
    @ResponseBody
    public String serveSwaggerApiDefinition(HttpServletResponse response) throws IOException, URISyntaxException {
        response.setContentType("text/x-yaml");
        return IOUtils.toString(this.getClass().getResourceAsStream("/api/swagger.yaml"), "UTF-8");
    }
}

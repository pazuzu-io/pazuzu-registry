package org.zalando.pazuzu;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@CrossOrigin
public class ApiDiscoveryController {

    @RequestMapping(value = "/api/swagger.yaml")
    @ResponseBody
    public String serveSwaggerApiDefinition(HttpServletResponse response) throws IOException {
        response.setContentType("text/x-yaml");
        return FileUtils.readFileToString(ResourceUtils.getFile("classpath:api/swagger.yaml"), "UTF-8");
    }
}

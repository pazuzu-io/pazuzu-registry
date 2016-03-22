package org.zalando.pazuzu.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class WebController {

    @RequestMapping("/home")
    public String pazuzuHome(@RequestParam() String username, Map<String, Object> model) {
        model.put("username", username);
        return "home";
    }
}

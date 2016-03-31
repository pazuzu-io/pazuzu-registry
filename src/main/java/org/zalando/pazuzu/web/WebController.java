package org.zalando.pazuzu.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.zauth.api.ZAuth;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class WebController {

    private final ZAuth zAuth;

    @Autowired
    public WebController(ZAuth zAuth) {
        this.zAuth = zAuth;
    }

    @RequestMapping("/home")
    public String pazuzuHome(Map<String, Object> model) {
        return "home";
    }

    @RequestMapping("/signin")
    public String signin() {
        return "signin";
    }
}

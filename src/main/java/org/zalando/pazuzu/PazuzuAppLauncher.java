package org.zalando.pazuzu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@SpringBootApplication
public class PazuzuAppLauncher {

    @Controller
    public static class WebControler {

        @RequestMapping("/home")
        public String pazuzuHome(@RequestParam() String username, Map<String, Object> model) {
            model.put("username", username);
            return "home";
        }
    }

    public static void main(final String[] args) {
        SpringApplication.run(PazuzuAppLauncher.class, args);
    }
}

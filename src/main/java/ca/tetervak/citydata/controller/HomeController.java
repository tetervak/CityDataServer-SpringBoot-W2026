package ca.tetervak.citydata.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Hidden
@Controller
public class HomeController {

    @GetMapping("/secret-page")
    @ResponseBody
    public String getSecretPage(@AuthenticationPrincipal OAuth2User principal) {
        return "<h1>Hello, " + principal.getAttribute("sub") + "</h1>";
    }
}

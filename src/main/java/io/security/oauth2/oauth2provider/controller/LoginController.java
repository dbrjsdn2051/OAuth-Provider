package io.security.oauth2.oauth2provider.controller;

import io.security.oauth2.oauth2provider.service.OAuthLoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private final OAuthLoginService oAuthLoginService;

    @GetMapping("/login/{provider}")
    public void loginPage(@PathVariable String provider, HttpServletResponse response) throws IOException {
        String redirectUrl = oAuthLoginService.retrieveUrlFromProvider(provider);
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/login/oauth2/code/{provider}")
    public String accessTokenInfo(@RequestParam("code") String code, @PathVariable String provider){
        log.info("code = {}", code);
        return oAuthLoginService.exchangeCodeForToken(code, provider);
    }
}

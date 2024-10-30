package io.security.oauth2.oauth2provider.service.google;

import io.security.oauth2.oauth2provider.service.LoginService;
import io.security.oauth2.oauth2provider.service.RestClientService;
import io.security.oauth2.oauth2provider.util.QueryParamBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import java.util.LinkedHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleLoginService implements LoginService {

    @Value("${oauth2.google.client-id}")
    public String clientId;

    @Value("${oauth2.google.client-secret}")
    public String clientSecret;

    @Value("${oauth2.google.authorization-uri}")
    public String authorizationUri;

    @Value("${oauth2.google.token-uri}")
    public String tokenUri;

    @Value("${oauth2.google.redirect-uri}")
    public String redirectUri;

    @Value("${oauth2.google.response-type}")
    public String responseType;

    @Value("${oauth2.google.grant-type}")
    public String grantType;

    private final QueryParamBuilder queryParamBuilder;
    private final RestClientService restClientService;

    @Override
    public String loginPage() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("client_id", clientId);
        params.put("redirect_uri", redirectUri);
        params.put("response_type", responseType);
        params.put("scope", "email");

        return queryParamBuilder.createUrl(authorizationUri, params);
    }

    @Override
    public String getToken(String code) {
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", grantType);

        return restClientService.exchangeCodeForToken(tokenUri, body);
    }
}

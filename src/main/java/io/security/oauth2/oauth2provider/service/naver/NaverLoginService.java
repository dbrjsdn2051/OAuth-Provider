package io.security.oauth2.oauth2provider.service.naver;

import io.security.oauth2.oauth2provider.service.LoginService;
import io.security.oauth2.oauth2provider.service.RestClientService;
import io.security.oauth2.oauth2provider.util.QueryParamBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import java.util.HashMap;


@Slf4j
@Service
@RequiredArgsConstructor
public class NaverLoginService implements LoginService {

    @Value("${oauth2.naver.client-id}")
    private String clientId;

    @Value("${oauth2.naver.client-secret}")
    private String clientSecret;

    @Value("${oauth2.naver.authorization-uri}")
    private String authorizationUri;

    @Value("${oauth2.naver.token-uri}")
    private String tokenUri;

    @Value("${oauth2.naver.redirect-uri}")
    private String redirectUri;

    @Value("${oauth2.naver.response-type}")
    private String responseType;

    @Value("${oauth2.naver.grant-type}")
    private String grantType;

    private final QueryParamBuilder queryParamBuilder;
    private final RestClientService restClientService;

    @Override
    public String loginPage() {
        HashMap<String, String> params = new HashMap<>();
        params.put("client_id", clientId);
        params.put("redirect_uri", redirectUri);
        params.put("response_type", responseType);

        return queryParamBuilder.createUrl(authorizationUri, params);
    }

    @Override
    public String getToken(String code) {
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", grantType);
        body.add("code", code);

        return restClientService.exchangeCodeForToken(tokenUri, body);
    }
}

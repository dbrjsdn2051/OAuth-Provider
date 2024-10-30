package io.security.oauth2.oauth2provider.service.kakao;

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
public class KakaoLoginService implements LoginService {

    @Value("${oauth2.kakao.client-id}")
    public String clientId;

    @Value("${oauth2.kakao.authorization-uri}")
    public String authorizationUri;

    @Value("${oauth2.kakao.token-uri}")
    public String tokenUri;

    @Value("${oauth2.kakao.redirect-uri}")
    public String redirectUri;

    @Value("${oauth2.kakao.response-type}")
    public String responseType;

    @Value("${oauth2.kakao.code}")
    public String code;

    @Value("${oauth2.kakao.grant-type}")
    public String grantType;

    private final QueryParamBuilder queryParamBuilder;
    private final RestClientService restClientService;

    @Override
    public String loginPage() {
        HashMap<String, String> params = new HashMap<>();
        params.put("client_id", clientId);
        params.put("redirect_uri", redirectUri);
        params.put("response_type", responseType);
        params.put("code", code);

        return queryParamBuilder.createUrl(authorizationUri, params);
    }

    @Override
    public String getToken(String code) {
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", grantType);
        body.add("code", code);

        return restClientService.exchangeCodeForToken(tokenUri, body);
    }


}
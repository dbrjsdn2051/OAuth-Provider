package io.security.oauth2.oauth2provider.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final OAuthLoginFactory oAuthLoginFactory;

    public String retrieveUrlFromProvider(String provider) {
        return oAuthLoginFactory.getLoginService(provider).loginPage();
    }

    public String exchangeCodeForToken(String code, String provider) {
        return oAuthLoginFactory.getLoginService(provider).getToken(code);
    }


}

package io.security.oauth2.oauth2provider.service;

public interface LoginService {

    String loginPage();

    String getToken(String code);
}

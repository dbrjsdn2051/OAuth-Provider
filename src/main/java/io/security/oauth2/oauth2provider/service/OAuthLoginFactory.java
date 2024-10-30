package io.security.oauth2.oauth2provider.service;

import io.security.oauth2.oauth2provider.service.google.GoogleLoginService;
import io.security.oauth2.oauth2provider.service.kakao.KakaoLoginService;
import io.security.oauth2.oauth2provider.service.naver.NaverLoginService;
import org.springframework.stereotype.Component;

@Component
public class OAuthLoginFactory {

    private final NaverLoginService naverLoginService;
    private final GoogleLoginService googleLoginService;
    private final KakaoLoginService kakaoLoginService;

    public OAuthLoginFactory(NaverLoginService naverLoginService, GoogleLoginService googleLoginService, KakaoLoginService kakaoLoginService) {
        this.naverLoginService = naverLoginService;
        this.googleLoginService = googleLoginService;
        this.kakaoLoginService = kakaoLoginService;
    }

    public LoginService getLoginService(String loginType) {
        return switch (loginType.toLowerCase()) {
            case "naver" -> naverLoginService;
            case "google" -> googleLoginService;
            case "kakao" -> kakaoLoginService;
            default -> throw new IllegalArgumentException("정확한 로그인 서비스를 입력해주세요.");
        };
    }
}

package io.security.oauth2.oauth2provider.util;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class QueryParamBuilder {

    public String createUrl(String authorizationUrl, Map<String, String> params){
        String paramStr = params.entrySet().stream()
                .map(param -> param.getKey() + "=" + param.getValue())
                .collect(Collectors.joining("&"));

        return authorizationUrl + "?" + paramStr;
    }
}

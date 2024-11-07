package io.security.oauth2.oauth2provider.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestClientService {

    public String exchangeCodeForToken(String tokenUri, LinkedMultiValueMap<String, String> body) {
        Map<String, Object> response = RestClient.create()
                .post()
                .uri(URI.create(tokenUri))
                .headers(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
                .body(body)
                .accept(MediaType.APPLICATION_JSON)
                .exchange((clientRequest, clientResponse) -> {
                    if (!clientResponse.getStatusCode().is4xxClientError()) {
                        return new ObjectMapper()
                                .readValue(clientResponse.getBody(),
                                        new TypeReference<Map<String, Object>>() {});
                    }
                    throw new HttpClientErrorException(clientResponse.getStatusCode());
                });

        log.info("Response = {}", response);
        return (String) response.get("access_token");
    }

}

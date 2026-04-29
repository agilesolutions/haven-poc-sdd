package com.example.servicea.client;

import com.example.servicea.dto.InfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class InfoClient {

    private final WebClient webClient;
    private final OAuth2AuthorizedClientManager clientManager;
    private final String serviceBBase;

    public InfoClient(WebClient.Builder builder, OAuth2AuthorizedClientManager clientManager, @Value("${serviceb.base-url:http://localhost:8081}") String serviceBBase) {
        this.webClient = builder.baseUrl(serviceBBase).build();
        this.clientManager = clientManager;
        this.serviceBBase = serviceBBase;
    }

    public InfoDto getInfo() {
        // Obtain OAuth2 client credentials token using the authorized client manager
        OAuth2AuthorizeRequest authRequest = OAuth2AuthorizeRequest.withClientRegistrationId("service-a-client").principal("service-a").build();
        OAuth2AuthorizedClient authorizedClient = clientManager.authorize(authRequest);
        String token = authorizedClient.getAccessToken().getTokenValue();

        return this.webClient.get()
                .uri("/info")
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(InfoDto.class)
                .block();
    }
}

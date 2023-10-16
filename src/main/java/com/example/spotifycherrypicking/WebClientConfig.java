package com.example.spotifycherrypicking;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
        // more infos at https://docs.spring.io/spring-security/site/docs/5.1.5.RELEASE/reference/html/servlet-webclient.html
    WebClient webClient(ClientRegistrationRepository clientRegistrations,
                        OAuth2AuthorizedClientRepository authorizedClients) {
        var oauth = new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations, authorizedClients);
        // (optional) explicitly opt into using the oauth2Login to provide an access token implicitly
        // oauth.setDefaultOAuth2AuthorizedClient(true);
        // (optional) set a default ClientRegistration.registrationId
        // oauth.setDefaultClientRegistrationId("client-registration-id");
        return WebClient.builder()
                .apply(oauth.oauth2Configuration())
                .build();
    }
}

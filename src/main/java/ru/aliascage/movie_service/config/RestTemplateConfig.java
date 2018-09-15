package ru.aliascage.movie_service.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.Size;
import java.net.URI;

@Configuration
@Validated
public class RestTemplateConfig {

    private static final String API_KEY = "api_key";

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder,
                                     @Value("${movie.db.apiKey}") @Size(min = 1) String apiKey,
                                     @Value("${movie.db.url}") @Size(min = 1) String url) {
        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        RestTemplate template = builder.rootUri(url)
                .interceptors(LoggingRequest.of("/3/movie/", "/3/search/person", "/3/genre/movie/list"),
                        (request, body, execution) -> {
                            String uri = UriComponentsBuilder.fromHttpRequest(request)
                                    .queryParam(API_KEY, apiKey)
                                    .build().toUriString();
                            ClientHttpRequest clientHttpRequest = factory.createRequest(URI.create(uri), request.getMethod());
                            return execution.execute(clientHttpRequest, body);
                        })
                .build();
        template.setRequestFactory(factory);
        return template;
    }

}

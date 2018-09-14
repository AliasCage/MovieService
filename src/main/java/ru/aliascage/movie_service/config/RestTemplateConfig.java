package ru.aliascage.movie_service.config;


import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        RestTemplate template = builder.interceptors(LoggingRequest.of("/3/movie/", "/3/search/person", "/3/genre/movie/list"))
                .build();
        template.setRequestFactory(factory);
        return template;
    }

}

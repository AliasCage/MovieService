package ru.aliascage.movie_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "movie.db")
public class MovieConfig {
    private String apiKey;
    private String url;
    @Value("#{'${available.sort.value}'.split(',')}")
    private List<String> availableSortValue;
    @Value("#{'${available.filter.value}'.split(',')}")
    private List<String> availableFilterValue;
}

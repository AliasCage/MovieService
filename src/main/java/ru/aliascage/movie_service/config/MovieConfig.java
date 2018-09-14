package ru.aliascage.movie_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "movie.db")
public class MovieConfig {
    @Size(min = 1)
    private String apiKey;
    @Size(min = 1)
    private String url;
    @Value("#{'${available.sort.value}'.split(',')}")
    private List<String> availableSortValue;
    @Value("#{'${available.filter.value}'.split(',')}")
    private List<String> availableFilterValue;
}

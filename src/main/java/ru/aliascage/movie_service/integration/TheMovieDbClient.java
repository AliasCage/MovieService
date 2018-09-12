package ru.aliascage.movie_service.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.aliascage.movie_service.config.MovieConfig;
import ru.aliascage.movie_service.model.*;

import java.net.URI;

import static org.springframework.util.StringUtils.isEmpty;

@Slf4j
@Component
public class TheMovieDbClient {

    private static final String MOVIE_PATH = "/movie/";
    private static final String MOVIES_PATH = "/discover/movie";
    private static final String SEARCH_PERSON_PATH = "/search/person";
    private static final String GENRES_PATH = "/genre/movie/list";
    private static final String API_KEY = "api_key";
    private static final String PAGE = "page";
    private static final String SORT_BY = "sort_by";
    private static final String QUERY = "query";

    @Autowired
    private CacheManager manager;

    @Autowired
    private MovieConfig config;

    @Autowired
    private RestTemplate restTemplate;

    public MovieDetails getMovieDetails(Integer movieId) {
        URI uri = buildUri(config.getUrl() + MOVIE_PATH + movieId);
        HttpEntity<MovieDetails> httpEntity = new HttpEntity<>(new MovieDetails());
        ResponseEntity<MovieDetails> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, MovieDetails.class);
        return responseEntity.getBody();
    }

    @Cacheable(value = "genres")
    public GenreList getGenres() {
        URI uri = buildUri(config.getUrl() + GENRES_PATH);
        HttpEntity<GenreList> httpEntity = new HttpEntity<>(new GenreList());
        ResponseEntity<GenreList> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, GenreList.class);
        return responseEntity.getBody();
    }

    public MovieList getMovieList(MovieListRequest request) {
        URI uri = UriComponentsBuilder.fromUriString(config.getUrl() + MOVIES_PATH)
                .queryParam(API_KEY, config.getApiKey())
                .queryParams(buildParamsMap(request))
                .build().toUri();
        HttpEntity<MovieList> httpEntity = new HttpEntity<>(new MovieList());
        ResponseEntity<MovieList> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, MovieList.class);
        return responseEntity.getBody();
    }

    public PersonList findPerson(String name) {
        URI uri = UriComponentsBuilder.fromUriString(config.getUrl() + SEARCH_PERSON_PATH)
                .queryParam(API_KEY, config.getApiKey())
                .queryParam(QUERY, name)
                .build().toUri();
        HttpEntity<PersonList> httpEntity = new HttpEntity<>(new PersonList());
        ResponseEntity<PersonList> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, PersonList.class);
        return responseEntity.getBody();
    }

    private MultiValueMap<String, String> buildParamsMap(MovieListRequest request) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>(3);

        map.add(PAGE, request.getPage().toString());
        if (!isEmpty(request.getSort())) {
            map.add(SORT_BY, request.getPage().toString());
        }
        String filter = request.getFilter();
        if (!isEmpty(filter)) {
            String[] params = filter.split("=");
            map.add(params[0], params[1]);
        }
        return map;
    }

    private URI buildUri(String urlParts) {
        return UriComponentsBuilder.fromUriString(urlParts)
                .queryParam(API_KEY, config.getApiKey())
                .build().toUri();
    }
}

package ru.aliascage.movie_service.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.isEmpty;

@Slf4j
@Component
public class TheMovieDbClient {

    private static final String MOVIE_PATH = "/movie/";
    private static final String MOVIES_PATH = "/discover/movie";
    private static final String SEARCH_PERSON_PATH = "/search/person";
    private static final String GENRES_PATH = "/genre/movie/list";
    private static final String API_KEY = "api_key";

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

    private PersonShort findPerson(String name) {
        URI uri = UriComponentsBuilder.fromUriString(config.getUrl() + SEARCH_PERSON_PATH)
                .queryParam(API_KEY, config.getApiKey())
                .queryParam("query", name)
                .build().toUri();
        HttpEntity<PersonList> httpEntity = new HttpEntity<>(new PersonList());
        ResponseEntity<PersonList> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, PersonList.class);

        List<PersonShort> personShortList = Optional.ofNullable(responseEntity.getBody())
                .map(PersonList::getResults)
                .orElse(Collections.emptyList());
        if (personShortList.size() == 1) {
            return personShortList.get(0);
        }
        String msg = String.format("Found %s persons with name: %s", personShortList.size(), name);
        throw new IllegalStateException(msg);
    }

    private MultiValueMap<String, String> buildParamsMap(MovieListRequest request) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>(3);
        map.add("page", request.getPage().toString());
        if (!isEmpty(request.getSort())) {
            map.add("sort_by", request.getPage().toString());
        }
        String filter = request.getFilter();
        if (!isEmpty(filter)) {
            String[] params = filter.split("=");
            String field = params[0];
            String value = params[1];
            switch (field) {
                case "with_actor":
                    addActor(map, value);
                    break;
                case "with_genres":
                    addGenres(map, value);
                    break;
                default:
                    map.add(field, value);
            }
        }
        return map;
    }

    private void addGenres(MultiValueMap<String, String> map, String value) {
        List<Genre> genres = getGenres().getGenres();
        String[] needGenres = value.split(",");
        List<Integer> genresIds = new ArrayList<>(needGenres.length);
        for (String genreName : needGenres) {
            Integer id = genres.stream()
                    .filter(genre -> genre.getName().equalsIgnoreCase(genreName))
                    .findFirst()
                    .map(Genre::getId)
                    .orElseThrow(() -> new RuntimeException(String.format("Not found genre: %s", genreName)));
            genresIds.add(id);
        }
        String result = genresIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        map.add("with_genres", result);
    }

    private void addActor(MultiValueMap<String, String> map, String value) {
        Integer id = findPerson(value).getId();
        map.add("with_cast", String.valueOf(id));
    }

    private URI buildUri(String urlParts) {
        return UriComponentsBuilder.fromUriString(urlParts)
                .queryParam(API_KEY, config.getApiKey())
                .build().toUri();
    }
}

package ru.aliascage.movie_service.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.aliascage.movie_service.exception.NotFoundGenreException;
import ru.aliascage.movie_service.model.*;

import java.util.List;

import static org.springframework.util.StringUtils.isEmpty;

@Component
public class TheMovieDbClientImpl implements TheMovieDbClient {

    private static final String MOVIE_PATH = "/movie/";
    private static final String MOVIES_PATH = "/discover/movie";
    private static final String SEARCH_PERSON_PATH = "/search/person";
    private static final String GENRES_PATH = "/genre/movie/list";
    private static final String PAGE = "page";
    private static final String SORT_BY = "sort_by";
    private static final String QUERY = "query";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public MovieDetails getMovieDetails(Integer movieId) {
        ResponseEntity<MovieDetails> responseEntity = restTemplate.getForEntity(MOVIE_PATH + movieId, MovieDetails.class);
        return responseEntity.getBody();
    }

    @Override
    @Cacheable(value = "dataCache")
    public GenreList getGenres() {
        ResponseEntity<GenreList> responseEntity = restTemplate.getForEntity(GENRES_PATH, GenreList.class);
        return responseEntity.getBody();
    }

    @Override
    public MovieList getMovieList(MovieListRequest request) {
        String uri = UriComponentsBuilder.fromPath(MOVIES_PATH)
                .queryParams(buildParamsMap(request))
                .build().toUriString();
        ResponseEntity<MovieList> responseEntity = restTemplate.getForEntity(uri, MovieList.class);
        return responseEntity.getBody();
    }

    @Override
    public PersonList findPerson(String name) {
        String uri = UriComponentsBuilder.fromPath(SEARCH_PERSON_PATH)
                .queryParam(QUERY, name)
                .build().toUriString();
        ResponseEntity<PersonList> responseEntity = restTemplate.getForEntity(uri, PersonList.class);
        return responseEntity.getBody();
    }

    @Override
    public Integer getGenreIdByName(String genreName) {
        List<Genre> genres = getGenres().getGenres();
        return genres.stream()
                .filter(genre -> genreName.equalsIgnoreCase(genre.getName()))
                .map(Genre::getId)
                .findFirst().orElseThrow(() -> new NotFoundGenreException(genreName));
    }

    private MultiValueMap<String, String> buildParamsMap(MovieListRequest request) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>(3);

        map.add(PAGE, request.getPage().toString());
        if (!isEmpty(request.getSort())) {
            map.add(SORT_BY, request.getSort());
        }
        String filter = request.getFilter();
        if (!isEmpty(filter)) {
            String[] params = filter.split("=");
            map.add(params[0], params[1]);
        }
        return map;
    }

}

package ru.aliascage.movie_service.service;

import com.hazelcast.core.IMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.aliascage.movie_service.exception.IllegalPersonCountException;
import ru.aliascage.movie_service.integration.TheMovieDbClient;
import ru.aliascage.movie_service.model.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.springframework.util.StringUtils.isEmpty;
import static ru.aliascage.movie_service.model.VoteAverageStatus.FINISH;

@Slf4j
@Component
public class MovieServiceImpl implements MovieService {

    private static final String ACTOR_REQUEST_FIELD = "with_actor";
    private static final String GENRES_REQUEST_FIELD = "with_genres";
    private static final String GENRE_SPLITTER = ",";
    private static final String FILTER_SPLITTER = "=";
    private static final String GENRES_FORMAT_STRING = "with_genres=%s";
    private static final String ACTOR_FORMAT_STRING = "with_cast=%s";

    @Autowired
    private IMap<String, VoteAverageResponse> map;

    @Autowired
    private TheMovieDbClient client;

    @Autowired
    private VoteAverageService averageService;

    @Override
    public MovieDetails getMovie(Integer id) {
        return client.getMovieDetails(id);
    }

    @Override
    public MovieList getMovieList(MovieListRequest request) {
        convertFilterField(request);
        return client.getMovieList(request);
    }

    @Override
    public GenreList getGenres() {
        return client.getGenres();
    }

    @Override
    public VoteAverageResponse getVoteAverageByGenre(String genreName) {
        VoteAverageResponse response = map.putIfAbsent(genreName, new VoteAverageResponse());
        if (response == null || FINISH.equals(response.getStatus()) && !response.isActual()) {
            averageService.runAsync(genreName);
        }
        return map.get(genreName);
    }

    private void convertFilterField(MovieListRequest request) {
        String filter = request.getFilter();
        if (!isEmpty(filter)) {
            String[] params = filter.split(FILTER_SPLITTER);
            String field = params[0];
            String value = params[1];

            if (ACTOR_REQUEST_FIELD.equals(field)) {
                request.setFilter(addActor(value));
            }
            if (GENRES_REQUEST_FIELD.equals(field)) {
                request.setFilter(addGenres(value));
            }
        }
    }

    private PersonShort findPerson(String name) {
        PersonList personList = client.findPerson(name);
        List<PersonShort> personShortList = Optional.ofNullable(personList)
                .map(PersonList::getResults)
                .orElse(Collections.emptyList());
        if (personShortList.size() == 1) {
            return personShortList.get(0);
        }
        throw new IllegalPersonCountException(name, personShortList.size());
    }

    private String addGenres(String value) {
        String result = Stream.of(value.split(GENRE_SPLITTER))
                .map(client::getGenreIdByName)
                .map(String::valueOf)
                .collect(Collectors.joining(GENRE_SPLITTER));
        return format(GENRES_FORMAT_STRING, result);
    }

    private String addActor(String value) {
        Integer personId = findPerson(value).getId();
        return format(ACTOR_FORMAT_STRING, personId);
    }

}

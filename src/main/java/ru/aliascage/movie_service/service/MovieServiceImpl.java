package ru.aliascage.movie_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.aliascage.movie_service.integration.TheMovieDbClient;
import ru.aliascage.movie_service.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.springframework.util.StringUtils.isEmpty;

@Component
public class MovieServiceImpl implements MovieService {

    private static final String ACTOR_REQUEST_FIELD = "with_actor";
    private static final String GENRES_REQUEST_FIELD = "with_genres";
    private static final String GENRE_SPLITTER = ",";
    private static final String FILTER_SPLITTER = "=";
    private static final String GENRES_FORMAT_STRING = "with_genres=%s";
    private static final String ACTOR_FORMAT_STRING = "with_cast=%s";

    @Autowired
    private TheMovieDbClient client;

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
    public GenreList getGenres(String language) {
        return client.getGenres();
    }

    private void convertFilterField(MovieListRequest request) {
        String filter = request.getFilter();
        if (!isEmpty(filter)) {
            String[] params = filter.split(FILTER_SPLITTER);
            String field = params[0];
            String value = params[1];

            switch (field) {
                case ACTOR_REQUEST_FIELD:
                    request.setFilter(addActor(value));
                    break;
                case GENRES_REQUEST_FIELD:
                    request.setFilter(addGenres(value));
                    break;
                default:
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
        String msg = format("Found %s persons with name: %s", personShortList.size(), name);
        throw new IllegalStateException(msg);
    }

    private String addGenres(String value) {
        GenreList genreList = client.getGenres();

        String[] needGenres = value.split(GENRE_SPLITTER);
        List<Integer> genresIds = new ArrayList<>(needGenres.length);
        for (String genreName : needGenres) {
            Genre genre = getGenreByName(genreList, genreName);
            genresIds.add(genre.getId());
        }
        String result = genresIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return format(GENRES_FORMAT_STRING, result);
    }

    private String addActor(String value) {
        Integer personId = findPerson(value).getId();
        return format(ACTOR_FORMAT_STRING, personId);
    }

    public Genre getGenreByName(GenreList genres, String genreName) {
        return genres.getGenres().stream()
                .filter(genre -> genreName.equals(genre.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(format("Not found genre: %s", genreName)));
    }
}

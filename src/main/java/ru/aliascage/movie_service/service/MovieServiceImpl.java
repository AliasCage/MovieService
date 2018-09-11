package ru.aliascage.movie_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.aliascage.movie_service.integration.TheMovieDbClient;
import ru.aliascage.movie_service.model.*;

@Component
public class MovieServiceImpl implements MovieService {

    @Autowired
    private TheMovieDbClient client;

    @Override
    public MovieDetails getMovie(Integer id) {
        return client.getMovieDetails(id);
    }

    @Override
    public MovieList getMovieList(MovieListRequest request) {
        return client.getMovieList(request);
    }

    @Override
    public GenreList getGenres(String language) {
        return client.getGenres();
    }

    public Genre getGenreByName(String genreName) {
        GenreList genres = client.getGenres();
        return genres.getGenres().stream()
                .filter(genre -> genreName.equals(genre.getName()))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}

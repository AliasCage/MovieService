package ru.aliascage.movie_service.service;

import ru.aliascage.movie_service.model.*;

import java.util.List;

public interface MovieService {
    MovieDetails getMovie(Integer id);

    MovieList getMovieList(MovieListRequest request);

    GenreList getGenres(String language);
}

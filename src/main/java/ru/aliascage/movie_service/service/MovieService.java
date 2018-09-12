package ru.aliascage.movie_service.service;

import ru.aliascage.movie_service.model.GenreList;
import ru.aliascage.movie_service.model.MovieDetails;
import ru.aliascage.movie_service.model.MovieList;
import ru.aliascage.movie_service.model.MovieListRequest;

public interface MovieService {
    MovieDetails getMovie(Integer id);

    MovieList getMovieList(MovieListRequest request);

    GenreList getGenres();
}

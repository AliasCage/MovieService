package ru.aliascage.movie_service.integration;

import ru.aliascage.movie_service.model.*;

public interface TheMovieDbClient {

    MovieDetails getMovieDetails(Integer movieId);

    GenreList getGenres();

    MovieList getMovieList(MovieListRequest request);

    PersonList findPerson(String name);

    Integer getGenreIdByName(String genreName);

}

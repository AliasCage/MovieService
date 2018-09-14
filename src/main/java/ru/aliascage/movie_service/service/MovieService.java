package ru.aliascage.movie_service.service;

import ru.aliascage.movie_service.model.*;

public interface MovieService {
    MovieDetails getMovie(Integer id);

    MovieList getMovieList(MovieListRequest request);

    GenreList getGenres();

    VoteAverageResponse getVoteAverageByGenre(String genreName);

}

package ru.aliascage.movie_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aliascage.movie_service.model.*;
import ru.aliascage.movie_service.service.MovieService;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@RestController
@RequestMapping(value = "/movie", produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/{id}")
    public MovieDetails getMovieDetails(@PathVariable Integer id) {
        return movieService.getMovie(id);
    }

    @GetMapping()
    public MovieList getMovieList(@Valid MovieListRequest request) {
        return movieService.getMovieList(request);
    }

    @GetMapping("/genres")
    public GenreList getMovieList() {
        return movieService.getGenres();
    }

    @GetMapping("/vote-average/{genreName}")
    public VoteAverageResponse getVoteAverageByGenre(@PathVariable String genreName) {
        return movieService.getVoteAverageByGenre(genreName);
    }

}
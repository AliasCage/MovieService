package ru.aliascage.movie_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aliascage.movie_service.model.MovieDetails;
import ru.aliascage.movie_service.model.MovieList;
import ru.aliascage.movie_service.model.MovieListRequest;
import ru.aliascage.movie_service.model.VoteAverageResponse;
import ru.aliascage.movie_service.service.MovieService;

import javax.validation.constraints.Pattern;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@RestController
@RequestMapping(value = "/movie", produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
@Validated
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/{id}")
    public MovieDetails getMovieDetails(@PathVariable Integer id) {
        return movieService.getMovie(id);
    }

    @GetMapping()
    public MovieList getMovieList(MovieListRequest request) {
        return movieService.getMovieList(request);
    }

    @GetMapping("/vote-average/{genreName}")
    public VoteAverageResponse getVoteAverageByGenre(@PathVariable @Pattern(regexp = "[A-Za-z]+", message = "Should be one correct genre") String genreName) {
        return movieService.getVoteAverageByGenre(genreName.toUpperCase());
    }

}
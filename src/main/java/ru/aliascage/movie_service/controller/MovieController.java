package ru.aliascage.movie_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aliascage.movie_service.model.GenreList;
import ru.aliascage.movie_service.model.MovieDetails;
import ru.aliascage.movie_service.model.MovieList;
import ru.aliascage.movie_service.model.MovieListRequest;
import ru.aliascage.movie_service.service.MovieService;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@RestController
@RequestMapping("/movie")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping(value = "/{id}", produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
    public MovieDetails getMovieDetails(@PathVariable Integer id) {
        return movieService.getMovie(id);
    }

    @GetMapping(produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
    public MovieList getMovieList(@Valid MovieListRequest request) {
        return movieService.getMovieList(request);
    }

    @GetMapping(value = "/genres", produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
    public GenreList getMovieList() {
        return movieService.getGenres();
    }

}
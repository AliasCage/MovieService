package ru.aliascage.movie_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.aliascage.movie_service.model.GenreList;
import ru.aliascage.movie_service.model.MovieDetails;
import ru.aliascage.movie_service.model.MovieList;
import ru.aliascage.movie_service.model.MovieListRequest;
import ru.aliascage.movie_service.service.MovieService;

import javax.validation.Valid;

@RestController
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping(value = "/movie/{id}", produces = {"application/json", "application/xml"})
    public ResponseEntity<MovieDetails> getMovieDetails(@PathVariable Integer id) {
        MovieDetails movie = movieService.getMovie(id);
        return ResponseEntity.ok(movie);
    }

    @GetMapping(value = "/movie", produces = {"application/json", "application/xml"})
    public ResponseEntity<MovieList> getMovieList(@Valid MovieListRequest request) {
        MovieList movie = movieService.getMovieList(request);
        return ResponseEntity.ok(movie);
    }


    @GetMapping(value = "/genres", produces = {"application/json", "application/xml"})
    public ResponseEntity<GenreList> getMovieList(@RequestParam(required = false, defaultValue = "ru-RU") String language) {
        GenreList genres = movieService.getGenres(language);
        return ResponseEntity.ok(genres);
    }


}
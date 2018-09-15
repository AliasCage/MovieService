package ru.aliascage.movie_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aliascage.movie_service.model.GenreList;
import ru.aliascage.movie_service.service.MovieService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@RestController
@RequestMapping(value = "/system", produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
public class SystemController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/available-sort-value")
    public List<String> getAvailableSortValue(@Value("#{'${available.sort.value}'.split(',')}") List<String> availableSortValue) {
        return availableSortValue;
    }

    @GetMapping("/available-filter-value")
    public List<String> getAvailableFilter(@Value("#{'${available.filter.value}'.split(',')}") List<String> availableFilterValue) {
        return availableFilterValue;
    }

    @GetMapping("/genres")
    public GenreList getGenres() {
        return movieService.getGenres();
    }

}

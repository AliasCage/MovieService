package ru.aliascage.movie_service.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MovieList extends PagedList {
    private List<ResultMovie> results;
}

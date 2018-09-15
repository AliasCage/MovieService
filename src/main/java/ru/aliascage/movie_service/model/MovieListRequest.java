package ru.aliascage.movie_service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;
import ru.aliascage.movie_service.validation.Available;
import ru.aliascage.movie_service.validation.Params;

@Getter
@Setter
@Accessors(chain = true)
public class MovieListRequest {
    @Range(min = 1, max = 1000)
    private Integer page = 1;
    @Available(value = Params.SORT, message = "Not allowed sort value")
    private String sort;
    @Available(value = Params.FILTER, message = "Not allowed filter value")
    private String filter;
}
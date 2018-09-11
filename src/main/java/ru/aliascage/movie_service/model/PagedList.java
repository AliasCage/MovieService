package ru.aliascage.movie_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PagedList {
    private Integer page;
    @JsonProperty("total_pages")
    protected Integer totalPages;
    @JsonProperty("total_results")
    protected Integer totalResults;
}

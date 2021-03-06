package ru.aliascage.movie_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MovieDetails {
    private Integer id;
    private Boolean adult;
    @JsonProperty("backdrop_path")
    private String backdropPath;
    @JsonProperty("belongs_to_collection")
    private BelongToCollection belongsToCollection;
    private Integer budget;
    private List<Genre> genres;
    private String homepage;
    @JsonProperty("imdb_id")
    private String imdbId;
    @JsonProperty("original_language")
    private String originalLanguage;
    @JsonProperty("original_title")
    private String originalTitle;
    private String overview;
    private Float popularity;
    @JsonProperty("poster_path")
    private String posterPath;
    @JsonProperty("production_companies")
    private List<ProductionCompany> productionCompanies;
    @JsonProperty("production_countries")
    private List<ProductionCountries> productionCountries;
    @JsonProperty("release_date")
    private String releaseDate;
    private Integer revenue;
    private Integer runtime;
    @JsonProperty("spoken_languages")
    private List<SpokenLanguage> spokenLanguages;
    //Rumored, Planned, In Production, Post Production, Released, Canceled
    private String status;
    private String tagline;
    private String title;
    private Boolean video;
    @JsonProperty("vote_average")
    private Float voteAverage;
    @JsonProperty("vote_count")
    private Integer voteCount;
}

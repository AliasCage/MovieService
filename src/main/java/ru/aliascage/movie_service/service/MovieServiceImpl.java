package ru.aliascage.movie_service.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.aliascage.movie_service.integration.TheMovieDbClient;
import ru.aliascage.movie_service.model.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.springframework.util.StringUtils.isEmpty;
import static ru.aliascage.movie_service.model.VoteAverageStatus.FINISH;

@Slf4j
@Component
public class MovieServiceImpl implements MovieService {

    private static final String ACTOR_REQUEST_FIELD = "with_actor";
    private static final String GENRES_REQUEST_FIELD = "with_genres";
    private static final String GENRE_SPLITTER = ",";
    private static final String FILTER_SPLITTER = "=";
    private static final String GENRES_FORMAT_STRING = "with_genres=%s";
    private static final String ACTOR_FORMAT_STRING = "with_cast=%s";

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    private TheMovieDbClient client;

    @Autowired
    private VoteAverageService averageService;

    @Override
    public MovieDetails getMovie(Integer id) {
        return client.getMovieDetails(id);
    }

    @Override
    public MovieList getMovieList(MovieListRequest request) {
        convertFilterField(request);
        return client.getMovieList(request);
    }

    @Override
    public GenreList getGenres() {
        return client.getGenres();
    }

    @Override
    public VoteAverageResponse getVoteAverageByGenre(String genreName) {
        IMap<String, VoteAverageResponse> map = hazelcastInstance.getMap("voteAverage");
        String genre = genreName.toUpperCase();
        VoteAverageResponse response = map.get(genre);
        if (response == null) {
            response = new VoteAverageResponse();
            map.put(genre, response);
            averageService.run(genre);
        } else if (FINISH.equals(response.getStatus()) && !response.isActual()) {
            averageService.run(genre);
        }
        return response;
    }

    @Override
    public Integer getGenreIdByName(String genreName) {
        List<Genre> genres = client.getGenres().getGenres();
        return genres.stream()
                .filter(genre -> genreName.equalsIgnoreCase(genre.getName()))
                .map(Genre::getId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(format("Not found genre: %s", genreName)));
    }

//    @Async
//    public CompletableFuture<Void> runCalculateVoteAverage(String genreName) {
//        log.info("Started calculation vote average for genre: {}", genreName);
//        Integer genreId = getGenreIdByName(genreName);
//        MovieListRequest request = new MovieListRequest().setFilter(format(GENRES_FORMAT_STRING, genreId));
//        MovieList movieList = client.getMovieList(request);
//        double sum = movieList.getResults().stream()
//                .mapToDouble(ResultMovie::getVoteAverage)
//                .sum();
//        IMap<String, VoteAverageResponse> map = hazelcastInstance.getMap("voteAverage");
//        VoteAverageResponse response = map.get(genreName);
//        Integer totalPages = movieList.getTotalPages();
//        for (int i = 2; i < totalPages; i++) {
//            request.setPage(i);
//            movieList = client.getMovieList(request);
//            sum += movieList.getResults().stream()
//                    .mapToDouble(ResultMovie::getVoteAverage)
//                    .sum();
//            response.setPercent((i / totalPages) * 100);
//            map.put(genreName, response);
//        }
//        response.setLastUpdate(LocalDateTime.now());
//        response.setVoteAverage((float) (sum / movieList.getTotalResults()));
//        map.put(genreName, response);
//        log.info("Calculation finished for: {}, total count: {}, vote average: {}",
//                genreName,
//                movieList.getTotalResults(),
//                response.getVoteAverage());
//        return CompletableFuture.completedFuture(null);
//    }

    private void convertFilterField(MovieListRequest request) {
        String filter = request.getFilter();
        if (!isEmpty(filter)) {
            String[] params = filter.split(FILTER_SPLITTER);
            String field = params[0];
            String value = params[1];

            switch (field) {
                case ACTOR_REQUEST_FIELD:
                    request.setFilter(addActor(value));
                    break;
                case GENRES_REQUEST_FIELD:
                    request.setFilter(addGenres(value));
                    break;
                default:
            }
        }
    }

    private PersonShort findPerson(String name) {
        PersonList personList = client.findPerson(name);
        List<PersonShort> personShortList = Optional.ofNullable(personList)
                .map(PersonList::getResults)
                .orElse(Collections.emptyList());
        if (personShortList.size() == 1) {
            return personShortList.get(0);
        }
        String msg = format("Found %s persons with name: %s", personShortList.size(), name);
        throw new IllegalStateException(msg);
    }

    private String addGenres(String value) {
        String result = Stream.of(value.split(GENRE_SPLITTER))
                .map(this::getGenreIdByName)
                .map(String::valueOf)
                .collect(Collectors.joining(GENRE_SPLITTER));
        return format(GENRES_FORMAT_STRING, result);
    }

    private String addActor(String value) {
        Integer personId = findPerson(value).getId();
        return format(ACTOR_FORMAT_STRING, personId);
    }

}

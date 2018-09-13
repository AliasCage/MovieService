package ru.aliascage.movie_service.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.aliascage.movie_service.integration.TheMovieDbClient;
import ru.aliascage.movie_service.model.MovieList;
import ru.aliascage.movie_service.model.MovieListRequest;
import ru.aliascage.movie_service.model.ResultMovie;
import ru.aliascage.movie_service.model.VoteAverageResponse;

import java.time.LocalDateTime;

import static java.lang.String.format;
import static ru.aliascage.movie_service.model.VoteAverageStatus.FINISH;
import static ru.aliascage.movie_service.model.VoteAverageStatus.RUNNING;

@Slf4j
@Component
public class VoteAverageServiceImpl implements VoteAverageService {

    private static final String GENRES_FORMAT_STRING = "with_genres=%s";

    @Autowired
    private TheMovieDbClient client;

    @Autowired
    private MovieService service;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Async
    public void run(String genreName) {
        log.info("Started calculation vote average for genre: {}", genreName);
        Integer genreId = service.getGenreIdByName(genreName);
        MovieListRequest request = new MovieListRequest().setFilter(format(GENRES_FORMAT_STRING, genreId));
        MovieList movieList = client.getMovieList(request);
        double sum = movieList.getResults().stream()
                .mapToDouble(ResultMovie::getVoteAverage)
                .sum();
        IMap<String, VoteAverageResponse> map = hazelcastInstance.getMap("voteAverage");
        VoteAverageResponse response = map.get(genreName);
        response.setStatus(RUNNING);
        Integer totalPages = movieList.getTotalPages();
        for (int i = 2; i < totalPages; i++) {
            request.setPage(i);
            movieList = client.getMovieList(request);
            sum += movieList.getResults().stream()
                    .mapToDouble(ResultMovie::getVoteAverage)
                    .sum();
            response.setPercent(((float) i / totalPages) * 100);
            map.put(genreName, response);
        }
        response.setLastUpdate(LocalDateTime.now());
        response.setStatus(FINISH);
        response.setVoteAverage((float) (sum / movieList.getTotalResults()));
        map.put(genreName, response);
        log.info("Calculation finished for: {}, total count: {}, vote average: {}",
                genreName,
                movieList.getTotalResults(),
                response.getVoteAverage());
    }
}

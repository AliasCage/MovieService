package ru.aliascage.movie_service.service;

import com.hazelcast.core.IMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import ru.aliascage.movie_service.integration.TheMovieDbClient;
import ru.aliascage.movie_service.model.MovieList;
import ru.aliascage.movie_service.model.MovieListRequest;
import ru.aliascage.movie_service.model.ResultMovie;
import ru.aliascage.movie_service.model.VoteAverageResponse;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static ru.aliascage.movie_service.model.VoteAverageStatus.FINISH;
import static ru.aliascage.movie_service.model.VoteAverageStatus.RUNNING;

@Slf4j
@Component
public class VoteAverageServiceImpl implements VoteAverageService {

    private static final String FINISH_CALC_MSG = "Calculation finished for: {}, vote average: {}";
    private static final String START_CALC_MSG = "Started calculation vote average for genre: {}";
    private static final String TOO_MANY_REQ_MSG = "Too many requests. {} {} seconds sleep";

    private static final String GENRES_FORMAT_STRING = "with_genres=%s";
    private static final int PAGE_MAX_SIZE = 1000;

    @Value("#{'${request.limit.delay.milis}'}")
    private int REQUEST_LIMIT_DELAY;

    @Autowired
    private TheMovieDbClient client;

    @Autowired
    private IMap<String, VoteAverageResponse> averageMap;

    @Async
    public void runAsync(String genreName) {
        log.info(START_CALC_MSG, genreName);
        Integer genreId = client.getGenreIdByName(genreName);
        MovieListRequest request = new MovieListRequest().setFilter(format(GENRES_FORMAT_STRING, genreId));
        MovieList movieList = client.getMovieList(request);
        List<ResultMovie> results = movieList.getResults();

        VoteAverageResponse response = averageMap.get(genreName);
        response.setStatus(RUNNING);
        //В условии выхода лучше использовать значение movieList.getTotalPages(), но максимальное значение page, но
        // максимально е значение которое может принимать TNDB равно 1000
        int totalPages = movieList.getTotalPages() <= PAGE_MAX_SIZE ? movieList.getTotalPages() : PAGE_MAX_SIZE;
        int i = 2;
        while (i < totalPages) {
            request.setPage(i);
            try {
                movieList = client.getMovieList(request);
            } catch (HttpClientErrorException e) {
                if (!TOO_MANY_REQUESTS.equals(e.getStatusCode())) {
                    throw e;
                }
                log.debug(TOO_MANY_REQ_MSG, genreName, REQUEST_LIMIT_DELAY / 1000);
                sleep();
                continue;
            }
            i++;
            results.addAll(movieList.getResults());
            response.setPercent(((float) i / totalPages) * 100);
            averageMap.put(genreName, response);
            log.debug("{} : {}%", genreName, response.getPercent());
        }
        setResultInResponse(results, response);
        averageMap.put(genreName, response);
        log.info(FINISH_CALC_MSG, genreName, response.getVoteAverage());
    }

    private void sleep() {
        try {
            Thread.sleep(REQUEST_LIMIT_DELAY);
        } catch (InterruptedException e) {
            log.error("", e);
        }
    }

    private void setResultInResponse(List<ResultMovie> results, VoteAverageResponse response) {
        Double average = results.stream()
                .mapToDouble(ResultMovie::getVoteAverage)
                .average()
                .orElseThrow(RuntimeException::new);
        response.setVoteAverage(average.floatValue());
        response.setLastUpdate(LocalDateTime.now());
        response.setStatus(FINISH);
    }
}

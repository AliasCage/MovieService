import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.IMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.aliascage.movie_service.App;
import ru.aliascage.movie_service.integration.TheMovieDbClient;
import ru.aliascage.movie_service.model.MovieList;
import ru.aliascage.movie_service.model.MovieListRequest;
import ru.aliascage.movie_service.model.VoteAverageResponse;
import ru.aliascage.movie_service.model.VoteAverageStatus;
import ru.aliascage.movie_service.service.VoteAverageService;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@EnableAsync
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {App.class})
public class VoteAverageServiceTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VoteAverageService service;

    @Autowired
    private IMap<String, VoteAverageResponse> averageMap;

    @MockBean
    private TheMovieDbClient client;

    @Test
    public void runAsyncVoteAverageCalculationTest() throws IOException {
        String genreName = "ACTION";
        MovieList movieList = getObjectFromJson("MovieList.json", MovieList.class);
        MovieListRequest request = new MovieListRequest().setFilter("with_genres=28");

        averageMap.putIfAbsent(genreName, new VoteAverageResponse());

        when(client.getGenreIdByName(eq("ACTION"))).thenReturn(28);
        when(client.getMovieList(any(MovieListRequest.class))).thenReturn(movieList);

        request.setPage(3);
        when(client.getMovieList(eq(request))).thenAnswer((Answer<MovieList>) invocation -> {
            VoteAverageResponse voteAverageResponse = averageMap.get(genreName);
            assertThat(voteAverageResponse, notNullValue());
            assertThat(voteAverageResponse.getStatus(), equalTo(VoteAverageStatus.RUNNING));
            assertThat(voteAverageResponse.getPercent(), equalTo(66.66667f));
            assertThat(voteAverageResponse.getLastUpdate(), nullValue());
            assertThat(voteAverageResponse.isActual(), equalTo(false));
            return movieList;
        });

        service.runAsync(genreName).join();

        VoteAverageResponse voteAverageResponse = averageMap.get(genreName);
        assertThat(voteAverageResponse, notNullValue());
        assertThat(voteAverageResponse.getStatus(), equalTo(VoteAverageStatus.FINISH));
        assertThat(voteAverageResponse.getPercent(), equalTo(100.0f));
        assertThat(voteAverageResponse.getLastUpdate(), notNullValue());
        assertThat(voteAverageResponse.isActual(), equalTo(true));
    }

    private <T> T getObjectFromJson(String path, Class<T> type) throws IOException {
        File resourcesDirectory = new File("src/test/resources/" + path);
        return objectMapper.readValue(resourcesDirectory, type);
    }
}

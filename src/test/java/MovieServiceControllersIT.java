import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import ru.aliascage.movie_service.App;
import ru.aliascage.movie_service.model.MovieDetails;
import ru.aliascage.movie_service.model.MovieList;
import ru.aliascage.movie_service.model.VoteAverageResponse;
import ru.aliascage.movie_service.model.VoteAverageStatus;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpMethod.GET;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MovieServiceControllersIT {

    private static final String HOST = "http://localhost:";

    @LocalServerPort
    private int port;

    private static final TestRestTemplate restTemplate = new TestRestTemplate();
    private static HttpEntity<String> entity;

    @Before
    public void init() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        entity = new HttpEntity<>(null, headers);
    }

    @Test
    public void getMovieListTest() {
        ResponseEntity<MovieList> response = restTemplate.exchange(createURL("/movie/"), GET, entity, MovieList.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        MovieList body = response.getBody();
        assertThat(body, notNullValue());
        assertThat(body.getPage(), equalTo(1));
        assertThat(body.getTotalPages(), equalTo(18900));
        assertThat(body.getResults(), hasSize(20));
    }

    @Test
    public void getMovieDetailTest() {
        ResponseEntity<MovieDetails> response = restTemplate.exchange(createURL("/movie/76341"), GET, entity, MovieDetails.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        MovieDetails body = response.getBody();

        assertThat(body, notNullValue());
        assertThat(body.getTitle(), equalTo("Mad Max: Fury Road"));
        assertThat(body.getBudget(), equalTo(150000000));
        assertThat(body.getVoteAverage(), equalTo(7.4F));
        assertThat(body.getProductionCompanies(), hasSize(3));
    }

    @Test
    public void getVoteAverageByGenre() throws InterruptedException {

        String uri = "/movie/vote-average/action";
        ResponseEntity<VoteAverageResponse> response = restTemplate.exchange(createURL(uri), GET, entity, VoteAverageResponse.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        VoteAverageResponse body = response.getBody();

        assertThat(body, notNullValue());
        assertThat(body.getStatus(), equalTo(VoteAverageStatus.START));
        assertThat(body.isActual(), equalTo(false));


        Thread.sleep(10000);
        response = restTemplate.exchange(createURL(uri), GET, entity, VoteAverageResponse.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        body = response.getBody();

        assertThat(body, notNullValue());
        assertThat(body.getStatus(), equalTo(VoteAverageStatus.RUNNING));
        assertThat(body.getPercent(), notNullValue());
        assertThat(body.isActual(), equalTo(false));
    }

    private String createURL(String uri) {
        return HOST + port + uri;
    }

}
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.aliascage.movie_service.exception.IllegalPersonCountException;
import ru.aliascage.movie_service.integration.TheMovieDbClient;
import ru.aliascage.movie_service.model.*;
import ru.aliascage.movie_service.service.MovieService;
import ru.aliascage.movie_service.service.VoteAverageService;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MovieServiceTest extends AbstractBaseTest {

    @Autowired
    private MovieService service;

    @MockBean
    VoteAverageService averageService;
    @MockBean
    TheMovieDbClient client;

    @Test
    public void getVoteAverageTest() {
        String genreName = "action";
        when(averageService.runAsync(genreName)).thenReturn(CompletableFuture.completedFuture(null));
        VoteAverageResponse action = service.getVoteAverageByGenre(genreName);
        verify(averageService, times(1)).runAsync(anyString());
        assertThat(action.getStatus().toString(), notNullValue());
        assertThat(action.getStatus(), equalTo(VoteAverageStatus.START));

        service.getVoteAverageByGenre(genreName);
        verify(averageService, times(1)).runAsync(anyString());
    }

    @Test
    public void getMovieListFilterGenreConverterTest() {
        final String genres = "action,war";
        MovieListRequest request = new MovieListRequest().setFilter("with_genres=" + genres);
        when(client.getGenreIdByName(eq("action"))).thenReturn(28);
        when(client.getGenreIdByName(eq("war"))).thenReturn(10752);
        service.getMovieList(request);

        MovieListRequest filter = new MovieListRequest().setFilter("with_genres=28,10752");
        verify(client, times(1)).getMovieList(eq(filter));
    }

    @Test
    public void getMovieListFilterActorConverterTest() {
        final String actor = "Tom Cruise";
        MovieListRequest request = new MovieListRequest().setFilter("with_actor=" + actor);

        PersonShort person = new PersonShort().setId(15).setName(actor);
        PersonList personList = new PersonList();
        personList.setResults(Collections.singletonList(person));

        when(client.findPerson(eq(actor))).thenReturn(personList);

        service.getMovieList(request);

        MovieListRequest filter = new MovieListRequest().setFilter("with_cast=15");
        verify(client, times(1)).getMovieList(eq(filter));
    }

    @Test(expected = IllegalPersonCountException.class)
    public void getMovieListFilterActorEmptyConverterTest() {
        final String actor = "Tom Cruise";
        MovieListRequest request = new MovieListRequest().setFilter("with_actor=" + actor);

        PersonList personList = new PersonList();
        personList.setResults(Collections.emptyList());

        when(client.findPerson(eq(actor))).thenReturn(personList);
        service.getMovieList(request);
    }

    @Test(expected = IllegalPersonCountException.class)
    public void getMovieListFilterActorManyConverterTest() {
        final String actor = "Tom";
        MovieListRequest request = new MovieListRequest().setFilter("with_actor=" + actor);

        PersonShort person1 = new PersonShort().setId(15).setName("Tom Cruise");
        PersonShort person2 = new PersonShort().setId(18).setName("Tom Hanks");
        PersonList personList = new PersonList();
        personList.setResults(Arrays.asList(person1, person2));

        when(client.findPerson(eq(actor))).thenReturn(personList);
        service.getMovieList(request);
    }

}

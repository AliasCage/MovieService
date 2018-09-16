import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.aliascage.movie_service.integration.TheMovieDbClient;
import ru.aliascage.movie_service.model.*;

import java.io.IOException;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class TheMovieDdClientTest extends AbstractBaseTest {

    private static final String MOVIE_PATH = "/movie/";
    private static final String MOVIES_PATH = "/discover/movie";
    private static final String SEARCH_PERSON_PATH = "/search/person";
    private static final String GENRES_PATH = "/genre/movie/list";

    @Autowired
    private TheMovieDbClient client;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void getGenresFromCacheTest() throws IOException {
        GenreList list = fromJson("Genre.json", GenreList.class);
        when(restTemplate.getForEntity(GENRES_PATH, GenreList.class)).thenReturn(ResponseEntity.ok(list));

        GenreList genres = client.getGenres();

        assertThat(genres, notNullValue());
        assertThat(genres.getGenres(), notNullValue());
        assertThat(genres.getGenres(), hasSize(19));

        client.getGenres();
        verify(restTemplate, times(1)).getForEntity(GENRES_PATH, GenreList.class);
    }

    @Test
    public void getGenreIdByNameTest() throws IOException {
        GenreList list = fromJson("Genre.json", GenreList.class);
        when(restTemplate.getForEntity(GENRES_PATH, GenreList.class)).thenReturn(ResponseEntity.ok(list));

        Integer id = client.getGenreIdByName("Action");
        assertThat(id, equalTo(28));

        verify(restTemplate, times(1)).getForEntity(GENRES_PATH, GenreList.class);
    }

    @Test
    public void getMovieDetailsTest() throws IOException {
        MovieDetails details = fromJson("MovieDetails.json", MovieDetails.class);
        int movieId = 23;
        when(restTemplate.getForEntity(MOVIE_PATH + movieId, MovieDetails.class)).thenReturn(ResponseEntity.ok(details));

        MovieDetails movieDetails = client.getMovieDetails(movieId);

        assertThat(movieDetails, notNullValue());
        assertThat(movieDetails, equalTo(details));

        verify(restTemplate, times(1)).getForEntity(MOVIE_PATH + movieId, MovieDetails.class);
    }

    @Test
    public void getMovieListTest() throws IOException {
        MovieList list = fromJson("MovieList.json", MovieList.class);

        String url = MOVIES_PATH + "?page=2&sort_by=popularity.desc&with_cast=Tom Cruise";
        when(restTemplate.getForEntity(url, MovieList.class)).thenReturn(ResponseEntity.ok(list));

        MovieListRequest request = new MovieListRequest()
                .setFilter("with_cast=Tom Cruise")
                .setSort("popularity.desc")
                .setPage(2);

        MovieList movieList = client.getMovieList(request);

        assertThat(movieList, notNullValue());
        assertThat(movieList.getResults(), notNullValue());
        assertThat(movieList.getResults(), hasSize(20));

        verify(restTemplate, times(1)).getForEntity(url, MovieList.class);
    }

    @Test
    public void findPersonTest() {
        String name = "Tom Cruise";
        String url = SEARCH_PERSON_PATH + "?query=" + name;
        PersonList list = new PersonList();
        list.setResults(Collections.singletonList(new PersonShort().setName(name).setId(1518)));

        when(restTemplate.getForEntity(url, PersonList.class)).thenReturn(ResponseEntity.ok(list));

        PersonList personList = client.findPerson(name);

        assertThat(personList, notNullValue());
        assertThat(personList.getResults(), notNullValue());
        assertThat(personList.getResults(), hasSize(1));
        assertThat(personList.getResults().get(0).getName(), equalTo(name));

        verify(restTemplate, times(1)).getForEntity(url, PersonList.class);
    }

}

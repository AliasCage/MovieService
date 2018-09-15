import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.aliascage.movie_service.App;
import ru.aliascage.movie_service.model.MovieDetails;
import ru.aliascage.movie_service.model.MovieList;
import ru.aliascage.movie_service.service.MovieService;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {App.class})
@WebAppConfiguration
public class ControllerTest {


    private static final String BASE_PATH = "/movie/";
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MovieService service;

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .alwaysDo(print())
                .build();
    }

    @Test
    public void getMovieDetailTest() throws Exception {
        MovieDetails object = getObjectFromJson("MovieDetails.json", MovieDetails.class);
        Mockito.when(service.getMovie(anyInt())).thenReturn(object);
        mvc.perform(get(BASE_PATH + "399360")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(76341)))
                .andExpect(jsonPath("$.title", is("Mad Max: Fury Road")))
                .andExpect(jsonPath("$.popularity", is(29.053)))
                .andExpect(jsonPath("$.adult", is(false)))
                .andExpect(jsonPath("$.belongs_to_collection").exists())
                .andExpect(jsonPath("$.belongs_to_collection.id", is(8945)))
                .andExpect(jsonPath("$.belongs_to_collection.name", is("Mad Max Collection")))
                .andExpect(jsonPath("$.belongs_to_collection.poster_path", is("/jZowUf4okNYuSlgj5iURE7CDMho.jpg")))
                .andExpect(jsonPath("$.genres", hasSize(4)))
                .andExpect(jsonPath("$.genres[0].id", is(28)))
                .andExpect(jsonPath("$.genres[0].name", is("Action")));
        Mockito.verify(service, times(1)).getMovie(any());
    }

    @Test
    public void getMovieListTest() throws Exception {
        MovieList object = getObjectFromJson("MovieList.json", MovieList.class);
        Mockito.when(service.getMovieList(any())).thenReturn(object);
        mvc.perform(get(BASE_PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page", is(2)))
                .andExpect(jsonPath("$.results").exists())
                .andExpect(jsonPath("$.results", hasSize(20)))
                .andExpect(jsonPath("$.total_pages", is(3)))
                .andExpect(jsonPath("$.total_results", is(60)));
        Mockito.verify(service, times(1)).getMovieList(any());
    }

    @Test
    public void getMovieListTestBadRequest() throws Exception {

        mvc.perform(get(BASE_PATH).accept(MediaType.APPLICATION_JSON)
                //todo: check validation
                .param("page", "1024"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("unexpected.Exception")))
                .andExpect(jsonPath("$.results").exists())
                .andExpect(jsonPath("$.results", hasSize(20)))
                .andExpect(jsonPath("$.total_pages", is(3)))
                .andExpect(jsonPath("$.total_results", is(60)));
        Mockito.verify(service, never()).getMovieList(any());
    }

    private <T> T getObjectFromJson(String path, Class<T> type) throws IOException {
        File resourcesDirectory = new File("src/test/resources/" + path);
        return objectMapper.readValue(resourcesDirectory, type);
    }

}

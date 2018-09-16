import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.aliascage.movie_service.model.MovieDetails;
import ru.aliascage.movie_service.model.MovieList;
import ru.aliascage.movie_service.model.MovieListRequest;
import ru.aliascage.movie_service.model.VoteAverageResponse;
import ru.aliascage.movie_service.service.MovieService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
public class ControllerTest extends AbstractBaseTest {

    private static final String GET_MOVIE_DETAIL_XML_RESPONSE = "<MovieDetails><id>76341</id><adult>false</adult><budget>150000000</budget><genres><genres><id>28</id><name>Action</name></genres><genres><id>12</id><name>Adventure</name></genres><genres><id>878</id><name>Science Fiction</name></genres><genres><id>53</id><name>Thriller</name></genres></genres><homepage>http://www.madmaxmovie.com/</homepage><overview>An apocalyptic story set in the furthest reaches of our planet, in a stark desert landscape where humanity is broken, and most everyone is crazed fighting for the necessities of life. Within this world exist two rebels on the run who just might be able to restore order. There's Max, a man of action and a man of few words, who seeks peace of mind following the loss of his wife and child in the aftermath of the chaos. And Furiosa, a woman of action and a woman who believes her path to survival may be achieved if she can make it across the desert back to her childhood homeland.</overview><popularity>29.053</popularity><revenue>378858340</revenue><runtime>120</runtime><status>Released</status><tagline>What a Lovely Day.</tagline><title>Mad Max: Fury Road</title><video>false</video><backdrop_path>/phszHPFVhPHhMZgo0fWTKBDQsJA.jpg</backdrop_path><belongs_to_collection><id>8945</id><name>Mad Max Collection</name><poster_path>/jZowUf4okNYuSlgj5iURE7CDMho.jpg</poster_path><backdrop_path>/zI0q2ENcQOLECbe0gAEGlncVh2j.jpg</backdrop_path></belongs_to_collection><imdb_id>tt1392190</imdb_id><original_language>en</original_language><original_title>Mad Max: Fury Road</original_title><poster_path>/kqjL17yufvn9OVLyXYpvtyrFfak.jpg</poster_path><production_companies><production_companies><id>79</id><name>Village Roadshow Pictures</name><logo_path>/tpFpsqbleCzEE2p5EgvUq6ozfCA.png</logo_path><origin_country>US</origin_country></production_companies><production_companies><id>2537</id><name>Kennedy Miller Productions</name><logo_path/><origin_country>AU</origin_country></production_companies><production_companies><id>174</id><name>Warner Bros. Pictures</name><logo_path>/ky0xOc5OrhzkZ1N6KyUxacfQsCk.png</logo_path><origin_country>US</origin_country></production_companies></production_companies><production_countries><production_countries><iso_3166_1>AU</iso_3166_1><name>Australia</name></production_countries><production_countries><iso_3166_1>US</iso_3166_1><name>United States of America</name></production_countries></production_countries><release_date>2015-05-13</release_date><spoken_languages><spoken_languages><iso_639_1>en</iso_639_1><name>English</name></spoken_languages></spoken_languages><vote_average>7.4</vote_average><vote_count>12435</vote_count></MovieDetails>";
    private static final String GET_MOVIE_LIST_XML_RESPONSE = "<MovieList><page>2</page><results><results><adult>false</adult><overview>Cab driver Max picks up a man who offers him $600 to drive him around. But the promise of easy money sours when Max realizes his fare is an assassin.</overview><id>1538</id><title>Collateral</title><popularity>10.382</popularity><video>false</video><release_date>2004-08-04</release_date><genre_ids><genre_ids>18</genre_ids><genre_ids>80</genre_ids><genre_ids>53</genre_ids></genre_ids><original_title>Collateral</original_title><original_language>en</original_language><backdrop_path>/whhU7492W0fcZE73r1HNhfyc5uV.jpg</backdrop_path><vote_count>2083</vote_count><vote_average>7.1</vote_average></results><results><adult>false</adult><overview>An epic mosaic of many interrelated characters in search of happiness, forgiveness, and meaning in the San Fernando Valley.</overview><id>334</id><title>Magnolia</title><popularity>10.293</popularity><video>false</video><release_date>1999-12-10</release_date><genre_ids><genre_ids>18</genre_ids></genre_ids><original_title>Magnolia</original_title><original_language>en</original_language><backdrop_path>/9In3XGQSdr2yfgUoEdCpqxEGAOO.jpg</backdrop_path><vote_count>1255</vote_count><vote_average>7.6</vote_average></results><results><adult>false</adult><overview>When cocky military lawyer Lt. Daniel Kaffee and his co-counsel, Lt. Cmdr. JoAnne Galloway, are assigned to a murder case, they uncover a hazing ritual that could implicate high-ranking officials such as shady Col. Nathan Jessep.</overview><id>881</id><title>A Few Good Men</title><popularity>10.256</popularity><video>false</video><release_date>1992-12-11</release_date><genre_ids><genre_ids>18</genre_ids></genre_ids><original_title>A Few Good Men</original_title><original_language>en</original_language><backdrop_path>/ynNzKnXWQ2Gs1okPNAsBTUn1eLZ.jpg</backdrop_path><vote_count>1304</vote_count><vote_average>7.3</vote_average></results><results><adult>false</adult><overview>For Lieutenant Pete 'Maverick' Mitchell and his friend and Co-Pilot Nick 'Goose' Bradshaw being accepted into an elite training school for fighter pilots is a dream come true.  A tragedy, as well as personal demons, threaten Pete's dreams of becoming an Ace pilot.</overview><id>744</id><title>Top Gun</title><popularity>10.221</popularity><video>false</video><release_date>1986-05-16</release_date><genre_ids><genre_ids>28</genre_ids><genre_ids>10749</genre_ids><genre_ids>10752</genre_ids></genre_ids><original_title>Top Gun</original_title><original_language>en</original_language><backdrop_path>/9GyBSsMiGkPSk4OESIYZuedijBI.jpg</backdrop_path><vote_count>2552</vote_count><vote_average>6.8</vote_average></results><results><adult>false</adult><overview>Wounded in Africa during World War II, Nazi Col. Claus von Stauffenberg returns to his native Germany and joins the Resistance in a daring plan to create a shadow government and assassinate Adolf Hitler. When events unfold so that he becomes a central player, he finds himself tasked with both leading the coup and personally killing the Fahrer.</overview><id>2253</id><title>Valkyrie</title><popularity>9.814</popularity><video>false</video><release_date>2008-12-25</release_date><genre_ids><genre_ids>18</genre_ids><genre_ids>53</genre_ids><genre_ids>36</genre_ids><genre_ids>10752</genre_ids></genre_ids><original_title>Valkyrie</original_title><original_language>en</original_language><backdrop_path>/ptNcTh2bewqzcV2qVpjDgsh6pTs.jpg</backdrop_path><vote_count>1619</vote_count><vote_average>6.8</vote_average></results><results><adult>false</adult><overview>The true story of pilot Barry Seal, who transported contraband for the CIA and the Medellin cartel in the 1980s.</overview><id>337170</id><title>American Made</title><popularity>8.916</popularity><video>false</video><release_date>2017-08-17</release_date><genre_ids><genre_ids>28</genre_ids><genre_ids>35</genre_ids><genre_ids>80</genre_ids><genre_ids>18</genre_ids></genre_ids><original_title>American Made</original_title><original_language>en</original_language><backdrop_path>/jcKzxRGYiXuS7ctTHLdw9wH8d7V.jpg</backdrop_path><vote_count>1657</vote_count><vote_average>6.7</vote_average></results><results><adult>false</adult><overview>Mitch McDeere is a young man with a promising future in Law. About to sit his Bar exam, he is approached by 'The Firm' and made an offer he doesn't refuse. Seduced by the money and gifts showered on him, he is totally oblivious to the more sinister side of his company. Then, two Associates are murdered. The FBI contact him, asking him for information and suddenly his life is ruined. He has a choice - work with the FBI, or stay with the Firm. Either way he will lose his life as he knows it. Mitch figures the only way out is to follow his own plan...</overview><id>37233</id><title>The Firm</title><popularity>8.691</popularity><video>false</video><release_date>1993-06-30</release_date><genre_ids><genre_ids>18</genre_ids><genre_ids>9648</genre_ids><genre_ids>53</genre_ids></genre_ids><original_title>The Firm</original_title><original_language>en</original_language><backdrop_path>/fBOs1E9C2XeZo32Os9sAJm1xiAd.jpg</backdrop_path><vote_count>713</vote_count><vote_average>6.7</vote_average></results><results><adult>false</adult><overview>A vampire relates his epic life story of love, betrayal, loneliness, and dark hunger to an over-curious reporter.</overview><id>628</id><title>Interview with the Vampire</title><popularity>8.405</popularity><video>false</video><release_date>1994-11-11</release_date><genre_ids><genre_ids>27</genre_ids><genre_ids>10749</genre_ids></genre_ids><original_title>Interview with the Vampire</original_title><original_language>en</original_language><backdrop_path>/GRyynLqafMrLFMHqvfGdUweavA.jpg</backdrop_path><vote_count>2284</vote_count><vote_average>7.3</vote_average></results><results><adult>false</adult><overview>Talented but unproven stock car driver Cole Trickle gets a break and with the guidance of veteran Harry Hogge turns heads on the track. The young hotshot develops a rivalry with a fellow racer that threatens his career when the two smash their cars. But with the help of his doctor, Cole just might overcome his injuries-- and his fear.</overview><id>2119</id><title>Days of Thunder</title><popularity>8.367</popularity><video>false</video><release_date>1990-06-27</release_date><genre_ids><genre_ids>12</genre_ids><genre_ids>28</genre_ids><genre_ids>10749</genre_ids><genre_ids>18</genre_ids></genre_ids><original_title>Days of Thunder</original_title><original_language>en</original_language><backdrop_path>/hdfNe41FUS5VSgwgHDVZkrHDngp.jpg</backdrop_path><vote_count>505</vote_count><vote_average>6.1</vote_average></results><results><adult>false</adult><overview>A young man leaves Ireland with his landlord's daughter after some trouble with her father, and they dream of owning land at the big giveaway in Oklahoma ca. 1893. When they get to the new land, they find jobs and begin saving money. The man becomes a local barehands boxer, and rides in glory until he is beaten, then his employers steal all the couple's money and they must fight off starvation in the winter, and try to keep their dream of owning land alive. Meanwhile, the woman's parents find out where she has gone and have come to America to find her and take her back.</overview><id>11259</id><title>Far and Away</title><popularity>8.283</popularity><video>false</video><release_date>1992-05-22</release_date><genre_ids><genre_ids>12</genre_ids><genre_ids>18</genre_ids><genre_ids>10749</genre_ids><genre_ids>37</genre_ids></genre_ids><original_title>Far and Away</original_title><original_language>en</original_language><backdrop_path>/v40ut2UW4eHDkIhcOCIdBcEfKH4.jpg</backdrop_path><vote_count>425</vote_count><vote_average>6.4</vote_average></results><results><adult>false</adult><overview>When two poor greasers, Johnny, and Ponyboy are assaulted by a vicious gang, the socs, and Johnny kills one of the attackers, tension begins to mount between the two rival gangs, setting off a turbulent chain of events.</overview><id>227</id><title>The Outsiders</title><popularity>8.155</popularity><video>false</video><release_date>1983-03-25</release_date><genre_ids><genre_ids>80</genre_ids><genre_ids>18</genre_ids></genre_ids><original_title>The Outsiders</original_title><original_language>en</original_language><backdrop_path>/2Y9Zb61gL4w0eUgyla4IELWqzGh.jpg</backdrop_path><vote_count>445</vote_count><vote_average>7.2</vote_average></results><results><adult>false</adult><overview>A group of young gunmen, led by Billy the Kid, become deputies to avenge the murder of the rancher who became their benefactor. But when Billy takes their authority too far, they become the hunted.</overview><id>11967</id><title>Young Guns</title><popularity>8.099</popularity><video>false</video><release_date>1988-08-12</release_date><genre_ids><genre_ids>80</genre_ids><genre_ids>28</genre_ids><genre_ids>12</genre_ids><genre_ids>18</genre_ids><genre_ids>37</genre_ids></genre_ids><original_title>Young Guns</original_title><original_language>en</original_language><backdrop_path>/llVClV0i94ELiR9dO47fJkPwbq6.jpg</backdrop_path><vote_count>339</vote_count><vote_average>6.7</vote_average></results><results><adult>false</adult><overview>Jerry Maguire used to be a typical sports agent: willing to do just about anything he could to get the biggest possible contracts for his clients, plus a nice commission for himself. Then, one day, he suddenly has second thoughts about what he's really doing. When he voices these doubts, he ends up losing his job and all of his clients, save Rod Tidwell, an egomaniacal football player.</overview><id>9390</id><title>Jerry Maguire</title><popularity>7.895</popularity><video>false</video><release_date>1996-12-13</release_date><genre_ids><genre_ids>35</genre_ids><genre_ids>18</genre_ids><genre_ids>10749</genre_ids></genre_ids><original_title>Jerry Maguire</original_title><original_language>en</original_language><backdrop_path>/n1Gh3MJAVvXmkgSDf3SOLlPCjag.jpg</backdrop_path><vote_count>1320</vote_count><vote_average>6.8</vote_average></results><results><adult>false</adult><overview>Meet Joel Goodson, an industrious, college-bound 17-year-old and a responsible, trustworthy son. However, when his parents go away and leave him home alone in the wealthy Chicago suburbs with the Porsche at his disposal he quickly decides he has been good for too long and it is time to enjoy himself. After an unfortunate incident with the Porsche Joel must raise some cash, in a risky way.</overview><id>9346</id><title>Risky Business</title><popularity>7.878</popularity><video>false</video><release_date>1983-08-05</release_date><genre_ids><genre_ids>35</genre_ids><genre_ids>10749</genre_ids><genre_ids>18</genre_ids></genre_ids><original_title>Risky Business</original_title><original_language>en</original_language><backdrop_path>/xYcXdO8wUQD8qfNATzdU1h5ARFa.jpg</backdrop_path><vote_count>469</vote_count><vote_average>6.5</vote_average></results><results><adult>false</adult><overview>The biography of Ron Kovic. Paralyzed in the Vietnam war, he becomes an anti-war and pro-human rights political activist after feeling betrayed by the country he fought for.</overview><id>2604</id><title>Born on the Fourth of July</title><popularity>7.77</popularity><video>false</video><release_date>1989-12-20</release_date><genre_ids><genre_ids>18</genre_ids><genre_ids>10752</genre_ids></genre_ids><original_title>Born on the Fourth of July</original_title><original_language>en</original_language><backdrop_path>/t9MrNY0xM9U5JSe5vHN9z08UYty.jpg</backdrop_path><vote_count>591</vote_count><vote_average>6.9</vote_average></results><results><adult>false</adult><overview>David Aames has it all: wealth, good looks and gorgeous women on his arm. But just as he begins falling for the warmhearted Sofia, his face is horribly disfigured in a car accident. That's just the beginning of his troubles as the lines between illusion and reality, between life and death, are blurred.</overview><id>1903</id><title>Vanilla Sky</title><popularity>7.696</popularity><video>false</video><release_date>2001-12-10</release_date><genre_ids><genre_ids>10749</genre_ids><genre_ids>878</genre_ids><genre_ids>14</genre_ids><genre_ids>18</genre_ids><genre_ids>53</genre_ids></genre_ids><original_title>Vanilla Sky</original_title><original_language>en</original_language><backdrop_path>/6XwX3HzKhlBUbTy2qZx0THQOu2T.jpg</backdrop_path><vote_count>1624</vote_count><vote_average>6.7</vote_average></results><results><adult>false</adult><overview>Former pool hustler \"Fast Eddie\" Felson decides he wants to return to the game by taking a pupil. He meets talented but green Vincent Lauria and proposes a partnership. As they tour pool halls, Eddie teaches Vincent the tricks of scamming, but he eventually grows frustrated with Vincent's showboat antics, leading to an argument and a falling-out. Eddie takes up playing again and soon crosses paths with Vincent as an opponent.</overview><id>11873</id><title>The Color of Money</title><popularity>7.036</popularity><video>false</video><release_date>1986-10-07</release_date><genre_ids><genre_ids>18</genre_ids></genre_ids><original_title>The Color of Money</original_title><original_language>en</original_language><backdrop_path>/jAitDcerFEgELU0195AyT4AWKG0.jpg</backdrop_path><vote_count>446</vote_count><vote_average>6.8</vote_average></results><results><adult>false</adult><overview>After being discharged from the Army, Brian Flanagan moves back to Queens and takes a job in a bar run by Doug Coughlin, who teaches Brian the fine art of bar-tending. Brian quickly becomes a patron favorite with his flashy drink-mixing style. Brian adopts his mentor's cynical philosophy on life and goes for the money. He leaves his artist girlfriend Jordan Mooney for Bonnie, a wealthy, high-powered executive. Brian soon must chose between the two, as he evaluates his options.</overview><id>7520</id><title>Cocktail</title><popularity>7.02</popularity><video>false</video><release_date>1988-07-29</release_date><genre_ids><genre_ids>18</genre_ids><genre_ids>10749</genre_ids><genre_ids>35</genre_ids></genre_ids><original_title>Cocktail</original_title><original_language>en</original_language><backdrop_path>/xwT7LGaJCpq0lbVgUlxNLPCu3eA.jpg</backdrop_path><vote_count>495</vote_count><vote_average>5.9</vote_average></results><results><adult>false</adult><overview>Going Clear intimately profiles eight former members of the Church of Scientology, shining a light on how they attract true believers and the things they do in the name of religion.</overview><id>318224</id><title>Going Clear: Scientology and the Prison of Belief</title><popularity>6.748</popularity><video>false</video><release_date>2015-01-25</release_date><genre_ids><genre_ids>99</genre_ids></genre_ids><original_title>Going Clear: Scientology and the Prison of Belief</original_title><original_language>en</original_language><backdrop_path>/29VoISagzOfJcuuhFzKQg9ZmEUU.jpg</backdrop_path><vote_count>408</vote_count><vote_average>7.8</vote_average></results><results><adult>false</adult><overview>Set in a timeless mythical forest inhabited by fairies, goblins, unicorns and mortals, this fantastic story follows a mystical forest dweller, chosen by fate, to undertake a heroic quest. He must save the beautiful Princess Lily and defeat the demonic Lord of Darkness, or the world will be plunged into a never-ending ice age.</overview><id>11976</id><title>Legend</title><popularity>6.128</popularity><video>false</video><release_date>1985-07-19</release_date><genre_ids><genre_ids>12</genre_ids><genre_ids>14</genre_ids></genre_ids><original_title>Legend</original_title><original_language>en</original_language><backdrop_path>/kiTjtGWfQNuwz8YMyzicn4wDkET.jpg</backdrop_path><vote_count>403</vote_count><vote_average>6.2</vote_average></results></results><total_pages>3</total_pages><total_results>60</total_results></MovieList>";
    private static final String GET_VOTE_AVERAGE_XML_RESPONSE = "<VoteAverageResponse><percent>15.55</percent><status>START</status><actual>false</actual></VoteAverageResponse>";
    private static final String BASE_PATH = "/movie/";

    @Autowired
    private WebApplicationContext context;

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
        MovieDetails object = fromJson("MovieDetails.json", MovieDetails.class);
        when(service.getMovie(anyInt())).thenReturn(object);
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
        verify(service, times(1)).getMovie(any());
    }

    @Test
    public void getMovieDetailXMLTest() throws Exception {
        MovieDetails object = fromJson("MovieDetails.json", MovieDetails.class);
        when(service.getMovie(anyInt())).thenReturn(object);
        MvcResult mvcResult = mvc.perform(get(BASE_PATH + "399360")
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        String response = new String(mvcResult.getResponse().getContentAsByteArray());
        assertThat(response, equalTo(GET_MOVIE_DETAIL_XML_RESPONSE));
        verify(service, times(1)).getMovie(any());
    }

    @Test
    public void getVoteAverageTest() throws Exception {
        VoteAverageResponse voteAverage = new VoteAverageResponse();
        voteAverage.setPercent(15.55f);
        when(service.getVoteAverageByGenre(anyString())).thenReturn(voteAverage);
        mvc.perform(get(BASE_PATH + "/vote-average/action")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.percent", is(15.55)))
                .andExpect(jsonPath("$.status", is("START")))
                .andExpect(jsonPath("$.actual", is(false)));
        verify(service, times(1)).getVoteAverageByGenre(eq("ACTION"));
    }

    @Test
    public void getVoteAverageXmlTest() throws Exception {
        VoteAverageResponse voteAverage = new VoteAverageResponse();
        voteAverage.setPercent(15.55f);
        when(service.getVoteAverageByGenre(anyString())).thenReturn(voteAverage);
        MvcResult mvcResult = mvc.perform(get(BASE_PATH + "/vote-average/action")
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        String response = new String(mvcResult.getResponse().getContentAsByteArray());
        assertThat(response, equalTo(GET_VOTE_AVERAGE_XML_RESPONSE));
        verify(service, times(1)).getVoteAverageByGenre(eq("ACTION"));
    }

    @Test
    public void getMovieListTest() throws Exception {
        MovieList object = fromJson("MovieList.json", MovieList.class);
        when(service.getMovieList(any())).thenReturn(object);
        MvcResult mvcResult = mvc.perform(get(BASE_PATH).accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        String response = new String(mvcResult.getResponse().getContentAsByteArray());
        assertThat(response, equalTo(GET_MOVIE_LIST_XML_RESPONSE));
        verify(service, times(1)).getMovieList(any());
    }

    @Test
    public void getMovieListXMLTest() throws Exception {
        MovieList object = fromJson("MovieList.json", MovieList.class);
        when(service.getMovieList(any())).thenReturn(object);
        mvc.perform(get(BASE_PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page", is(2)))
                .andExpect(jsonPath("$.results").exists())
                .andExpect(jsonPath("$.results", hasSize(20)))
                .andExpect(jsonPath("$.total_pages", is(3)))
                .andExpect(jsonPath("$.total_results", is(60)));
        verify(service, times(1)).getMovieList(any());
    }

    @Test
    public void getMovieListValuesTest() throws Exception {
        MovieListRequest request = new MovieListRequest()
                .setPage(100)
                .setFilter("with_actor=Tom Cruise")
                .setSort("release_date.asc");
        mvc.perform(get(BASE_PATH).accept(MediaType.APPLICATION_JSON)
                .param("page", "100")
                .param("filter", "with_actor=Tom Cruise")
                .param("sort", "release_date.asc"))
                .andExpect(status().isOk());
        verify(service, times(1)).getMovieList(eq(request));
    }

    @Test
    public void getMovieListBadRequestPageTest() throws Exception {
        mvc.perform(get(BASE_PATH).accept(MediaType.APPLICATION_JSON)
                .param("page", "1024"))
                .andExpect(status().isBadRequest());
        verify(service, never()).getMovieList(any());
    }

    @Test
    public void getMovieListBadRequestSortTest() throws Exception {
        mvc.perform(get(BASE_PATH).accept(MediaType.APPLICATION_JSON)
                .param("sort", "anything"))
                .andExpect(status().isBadRequest());
        verify(service, never()).getMovieList(any());
    }

    @Test
    public void getMovieListBadRequestFilterTest() throws Exception {
        mvc.perform(get(BASE_PATH).accept(MediaType.APPLICATION_JSON)
                .param("filter", "anything"))
                .andExpect(status().isBadRequest());
        verify(service, never()).getMovieList(any());
    }

    @Test
    public void getMovieListBadRequestFiltersTest() throws Exception {
        mvc.perform(get(BASE_PATH).accept(MediaType.APPLICATION_JSON)
                .param("filter", "with_actor=Tom Cruise,realise_date=2012"))
                .andExpect(status().isBadRequest());
        verify(service, never()).getMovieList(any());
    }

}

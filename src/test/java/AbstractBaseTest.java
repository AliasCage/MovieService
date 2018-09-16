import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.aliascage.movie_service.App;

import java.io.File;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {App.class})
public abstract class AbstractBaseTest {
    @Autowired
    private ObjectMapper objectMapper;

    protected <T> T fromJson(String path, Class<T> type) throws IOException {
        File resourcesDirectory = new File("src/test/resources/" + path);
        return objectMapper.readValue(resourcesDirectory, type);
    }
}

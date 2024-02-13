package cucumber;

import com.sopromadze.blogapi.BlogApiApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ActiveProfiles({"local", "test"})
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestContainerSupport.class)
@ContextConfiguration(classes = BlogApiApplication.class)
@Slf4j
public class CucumberSpringConfiguration {

  @LocalServerPort
  private int port;

  @PostConstruct
  public void setup() {
    System.setProperty("port", String.valueOf(port));
  }

}
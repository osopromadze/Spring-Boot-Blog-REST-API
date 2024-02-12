package cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

//@ActiveProfiles("dev")
@CucumberContextConfiguration
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = TestConfig.class
)
public class CucumberSpringConfiguration {

  @LocalServerPort
  private int port;

  @PostConstruct
  public void setup() {
    System.setProperty("port", String.valueOf(port));
  }

}
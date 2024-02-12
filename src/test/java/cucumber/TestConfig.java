package cucumber;

import com.sopromadze.blogapi.BlogApiApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.sopromadze.blogapi")
@EnableAutoConfiguration
@EntityScan(basePackageClasses = {BlogApiApplication.class})
public class TestConfig {

}

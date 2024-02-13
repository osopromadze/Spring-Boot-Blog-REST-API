package cucumber;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.org.yaml.snakeyaml.Yaml;

@TestConfiguration
public class TestContainerSupport {

  public static final DockerComposeContainer<?> compose = new DockerComposeContainer<>(
      new File(ClassLoader.getSystemResource("compose/docker-compose.yml").getPath()));

  private static final List<Map<String, Object>> services = new ArrayList<>();

  static {
    //    Boolean localContainers = Boolean.parseBoolean(System.getenv("LOCAL_CONTAINERS"));
    //
    //    if (!localContainers) {
    //      startCompose();
    //    }

    startCompose();
  }

  public static void startCompose() {
    prepareCompose();

    services.forEach(service -> {
      String serviceName = (String) service.get("serviceName");
      int[] ports = ((List<Integer>) service.get("ports")).stream().mapToInt(i -> i).toArray();

      compose.waitingFor(serviceName, Wait.forListeningPorts(ports).withStartupTimeout(Duration.ofSeconds(30)));
    });

    compose.start();
  }

  @SuppressWarnings("unchecked")
  private static void prepareCompose() {
    Map<String, Object> composeContent = new Yaml().load(ClassLoader.getSystemResourceAsStream("compose/docker-compose.yml"));
    Map<String, Object> composeServices = (Map<String, Object>) composeContent.get("services");

    for (Entry<String, Object> composeServicesEntry : composeServices.entrySet()) {
      String composeServiceName = composeServicesEntry.getKey();

      Map<String, Object> composeService = (Map<String, Object>) composeServicesEntry.getValue();

      List<String> composeServicePorts = (List<String>) composeService.get("ports");

      List<Integer> servicePorts = new ArrayList<>();

      for (String composeServicePort : composeServicePorts) {
        int servicePort = Integer.parseInt(composeServicePort.split(":")[1]);
        servicePorts.add(servicePort);
        compose.withExposedService(composeServiceName, servicePort, Wait.defaultWaitStrategy().withStartupTimeout(Duration.ofSeconds(30)));
      }

      services.add(Map.of(
          "serviceName", composeServiceName,
          "ports", servicePorts
      ));
    }
  }
}

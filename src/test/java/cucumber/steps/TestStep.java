package cucumber.steps;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class TestStep {

  private String test;

  @Given("make request")
  public void makeRequest() {
    test = "test";
  }

  @Then("response is")
  public void responseIs() {
    assertThat(test).isEqualTo("test");
  }
}

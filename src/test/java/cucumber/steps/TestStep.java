package cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import static org.assertj.core.api.Assertions.assertThat;

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

package com.sopromadze.blogapi;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BlogApiApplication.class)
@WebAppConfiguration
public abstract class AbstractTest {
	protected MockMvc mvc;

	@Autowired
	protected WebApplicationContext webApplicationContext;

	@Autowired
	protected Filter springSecurityFilterChain;

	@PostConstruct
	protected void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity(springSecurityFilterChain))
				.build();
	}

}

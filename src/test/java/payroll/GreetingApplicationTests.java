package payroll;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PayrollApplication.class)
@SpringBootTest
public class GreetingApplicationTests {

	private MockMvc mockMvc;
	private final String empDetailsJson = "{\"_embedded\":{\"employees\":[{\"id\":1,\"name\":\"Bilbo Baggins\",\"role\":\"burglar\",\"age\":0,\"_links\":{\"self\":{\"href\":\"http://localhost/employees/1\"},\"employees\":{\"href\":\"http://localhost/employees\"}}},{\"id\":2,\"name\":\"Frodo Baggins\",\"role\":\"thief\",\"age\":0,\"_links\":{\"self\":{\"href\":\"http://localhost/employees/2\"},\"employees\":{\"href\":\"http://localhost/employees\"}}}]},\"_links\":{\"self\":{\"href\":\"http://localhost/employees\"}}}";
	private final String validPath="/employees/";
	private final String invalidPath="/emp/employee/";
	private final int validEmpId=1;
	private final int invalidEmpId=6;
	private final String validEmpJson = "{\"id\":1,\"name\":\"Bilbo Baggins\",\"role\":\"burglar\",\"age\":0,\"_links\":{\"self\":{\"href\":\"http://localhost/employees/1\"},\"employees\":{\"href\":\"http://localhost/employees\"}}}";
	private final String invalidEmpIdJson="{\"errorCode\":400,\"message\":\"Could not find employee 6\"}";

	@Autowired
	private WebApplicationContext wac;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}
	
	@Test
	public void getEmpDetailsJson() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(validPath).accept(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		String expected = empDetailsJson;
		System.out.println("Result get emp details is: " + result.getResponse().getContentAsString());
		System.out.println("Expected: " + empDetailsJson);
		JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
	}

	@Test
	public void getAnEmpDetails() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(validPath + validEmpId).accept(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		String expected = validEmpJson;

		JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
	}
	
	@Test
	public void getNoEmpDetails() throws Exception   {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(validPath+invalidEmpId).accept(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		String expected = invalidEmpIdJson;
	
		JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
	}

	@Test
	public void notFoundURI() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(invalidPath);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		int resultStatus=result.getResponse().getStatus();
		int actualStatus=HttpStatus.NOT_FOUND.value();
		
		assertThat(resultStatus, is(actualStatus));	
	}
}
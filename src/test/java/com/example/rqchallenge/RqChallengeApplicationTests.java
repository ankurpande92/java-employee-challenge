package com.example.rqchallenge;

import com.example.rqchallenge.model.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
class RqChallengeApplicationTests {

    @LocalServerPort
    int randomServerPort;

    private static RestTemplate restTemplate;
    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void setup() {
        restTemplate = new RestTemplate();
        objectMapper = new ObjectMapper();
    }

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("Invalid API URL test")
    public void testInvalidUrl() throws URISyntaxException {
        //prepare
        URI uri = new URI(getCompleteBaseUrl().append("/employees").toString());

        //execute and assert
        assertThrows(HttpClientErrorException.class,
                ()-> restTemplate.getForEntity(uri, String.class));
    }

    @Test
    @DisplayName("Get all employees list API URL test")
    public void testGetAllEmployeesAPI() throws URISyntaxException, JsonProcessingException {
        //prepare
        URI uri = new URI(getCompleteBaseUrl().toString());
        ObjectReader objectReader = objectMapper.reader().forType(new TypeReference<List<Employee>>(){});

        //execute
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);

        //assertions
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        List<Employee> employeeList = objectReader.readValue(result.getBody());
        Assertions.assertNotNull(employeeList);
        Assertions.assertEquals(24, employeeList.size());
    }

    @Test
    @DisplayName("Search employee by search string API test")
    public void testSearchEmployeeBySearchString() throws URISyntaxException, JsonProcessingException {
        //prepare
        URI uri = new URI(getCompleteBaseUrl().append("/search/tt").toString());
        ObjectReader objectReader = objectMapper.reader().forType(new TypeReference<List<Employee>>(){});

        //execute
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);

        //assertions
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        List<Employee> employeeList = objectReader.readValue(result.getBody());
        Assertions.assertNotNull(employeeList);
        Assertions.assertEquals(3, employeeList.size());
        employeeList.forEach(employee -> Assertions.assertTrue(employee.getEmployeeName().contains("tt")));
    }

    @Test
    @DisplayName("Get employee details for valid Id API test")
    public void testGetEmployeeDetailsAPIGivenValidEmployeeId() throws URISyntaxException, JsonProcessingException {
        //prepare
        URI uri = new URI(getCompleteBaseUrl().append("/2").toString());
        ObjectReader objectReader = objectMapper.reader().forType(new TypeReference<Employee>(){});
        Employee expectedEmployee = new Employee(2L, "Garrett Winters", 170750, 63, "");

        //execute
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);

        //assertions
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Employee actualEmployee = objectReader.readValue(result.getBody());
        Assertions.assertNotNull(actualEmployee);
        Assertions.assertEquals(expectedEmployee, actualEmployee);
    }

    @Test
    @DisplayName("Get employee details for invalid Id API test")
    public void testGetEmployeeDetailsAPIGivenInvalidEmployeeId() throws URISyntaxException {
        //prepare
        URI uri = new URI(getCompleteBaseUrl().append("/560").toString());

        //execute and assert
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class,
                ()-> restTemplate.getForEntity(uri, String.class));
        Assertions.assertTrue(ex.getMessage().contains("Employee with id 560 not found."));
    }

    @Test
    @DisplayName("Highest salary of employee API test")
    public void testHighestSalaryOfEmployeesAPI() throws URISyntaxException {
        //prepare
        URI uri = new URI(getCompleteBaseUrl().append("/highestSalary").toString());

        //execute
        ResponseEntity<Integer> result = restTemplate.getForEntity(uri, Integer.class);

        //assertions
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Assertions.assertEquals(725000, result.getBody());
    }

    @Test
    @DisplayName("Top ten highest earning employee list API test")
    public void testTopTenHighestEarningEmployeeNames() throws URISyntaxException, JsonProcessingException {
        //prepare
        URI uri = new URI(getCompleteBaseUrl().append("/topTenHighestEarningEmployeeNames").toString());
        ObjectReader objectReader = objectMapper.reader().forType(new TypeReference<List<String>>(){});
        List<String> expectedNameList = Arrays.asList("Paul Byrd", "Yuri Berry", "Charde Marshall", "Cedric Kelly",
                "Tatyana Fitzpatrick", "Brielle Williamson", "Jenette Caldwell", "Quinn Flynn", "Rhona Davidson", "Tiger Nixon");

        //execute
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);

        //assertions
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        List<String> actualNameList = objectReader.readValue(result.getBody());
        Assertions.assertNotNull(actualNameList);
        Assertions.assertEquals(10, actualNameList.size());
        Assertions.assertEquals(expectedNameList, actualNameList);
    }

    @Test
    @DisplayName("Create employee API test")
    public void testCreateEmployee() throws URISyntaxException {
        //prepare
        URI uri = new URI(getCompleteBaseUrl().toString());

        Map<String, Object> inputRequestPayload = new HashMap<>();
        inputRequestPayload.put("name", "Harry Potter");
        inputRequestPayload.put("salary", 204500);
        inputRequestPayload.put("age", 26);

        //execute
        ResponseEntity<Employee> result = restTemplate.postForEntity(uri, inputRequestPayload, Employee.class);

        //assertions
        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Employee actualEmployee = result.getBody();
        Assertions.assertNotNull(actualEmployee);
        Assertions.assertNotNull(actualEmployee.getId());
    }

    @Test
    @DisplayName("Create employee name missing API test")
    public void testCreateEmployeeNameMissing() throws URISyntaxException {
        //prepare
        URI uri = new URI(getCompleteBaseUrl().toString());

        Map<String, Object> inputRequestPayload = new HashMap<>();
        inputRequestPayload.put("salary", 204500);
        inputRequestPayload.put("age", 26);

        //execute and assert
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class,
                ()-> restTemplate.postForEntity(uri, inputRequestPayload, Employee.class));

        //assertions
        Assertions.assertTrue(ex.getMessage().contains("name field is mandatory in create employee request."));
    }

    @Test
    @DisplayName("Create employee age missing API test")
    public void testCreateEmployeeAgeMissing() throws URISyntaxException {
        //prepare
        URI uri = new URI(getCompleteBaseUrl().toString());

        Map<String, Object> inputRequestPayload = new HashMap<>();
        inputRequestPayload.put("name", "Harry Potter");
        inputRequestPayload.put("salary", 204500);

        //execute and assert
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class,
                ()-> restTemplate.postForEntity(uri, inputRequestPayload, Employee.class));

        //assertions
        Assertions.assertTrue(ex.getMessage().contains("age field is mandatory in create employee request."));
    }

    @Test
    @DisplayName("Create employee salary missing API test")
    public void testCreateEmployeeSalaryMissing() throws URISyntaxException {
        //prepare
        URI uri = new URI(getCompleteBaseUrl().toString());

        Map<String, Object> inputRequestPayload = new HashMap<>();
        inputRequestPayload.put("name", "Harry Potter");
        inputRequestPayload.put("age", 26);

        //execute and assert
        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class,
                ()-> restTemplate.postForEntity(uri, inputRequestPayload, Employee.class));

        //assertions
        Assertions.assertTrue(ex.getMessage().contains("salary field is mandatory in create employee request."));
    }


    @Test
    @DisplayName("Delete employee by Id API test")
    public void testDeleteEmployeeById() throws URISyntaxException {
        //prepare
        URI uri = new URI(getCompleteBaseUrl().append("/23").toString());

        //execute
        restTemplate.delete(uri);
    }

    private StringBuilder getCompleteBaseUrl() {
        String BASE_URL = "http://localhost:";
        return new StringBuilder().append(BASE_URL).append(randomServerPort);
    }

}

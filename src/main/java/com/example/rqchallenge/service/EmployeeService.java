package com.example.rqchallenge.service;

import com.example.rqchallenge.client.RestExecutorServiceClient;
import com.example.rqchallenge.constants.Constants;
import com.example.rqchallenge.model.ApiResponse;
import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.model.EmployeeResponse;
import com.example.rqchallenge.model.EmployeeListResponse;
import com.example.rqchallenge.exceptionhandler.InvalidRequestException;
import com.example.rqchallenge.exceptionhandler.OperationFailedException;
import com.example.rqchallenge.exceptionhandler.ResourceNotFoundException;
import com.example.rqchallenge.util.HttpClientHelper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Employee operation's business logic
 */
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);
    private final RestExecutorServiceClient restExecutorServiceClient;


    /**
     *
     * This will return all the employees
     * @return
     */
    public ResponseEntity<List<Employee>> getAllEmployees() {
        try {

            ResponseEntity<EmployeeListResponse> empResponseEntity =  restExecutorServiceClient.execute(Constants.BASE_URL + Constants.GET_ALL_EMPLOYEES_ENDPOINT_URL,
                    HttpClientHelper.getHttpEntity(),
                    HttpMethod.GET,
                    EmployeeListResponse.class);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Objects.requireNonNull(empResponseEntity.getBody()).getData());

        } catch (Exception e) {
            log.error("Failed to get all employee list.", e);
            throw e;
        }
    }

    /**
     *
     * This will return all the matching employees name
     *
     * @param searchString
     * @return
     */
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(final String searchString) {
        ResponseEntity<List<Employee>> employees = getAllEmployees();

        if(Objects.isNull(employees.getBody())) {
            log.error("Failed to get employee list to get employees by name search.");
            throw new OperationFailedException(String.format(Constants.SEARCH_BY_NAME_OP_FAILED, searchString));
        }

        return new ResponseEntity<>(searchMatchingNameEmployeeList(employees.getBody(), searchString), HttpStatus.OK);
    }

    /**
     *
     * This will return employee by Id
     *
     * @param id
     * @return
     */
    public ResponseEntity<Employee> getEmployeeById(final String id) {
        try {
            ResponseEntity<EmployeeResponse> empResponseEntity =  restExecutorServiceClient.execute(Constants.BASE_URL + String.format(Constants.GET_EMPLOYEE_BY_ID_ENDPOINT_URL, id),
                   HttpClientHelper.getHttpEntity(),
                    HttpMethod.GET,
                    EmployeeResponse.class);

            if (Objects.nonNull(empResponseEntity.getBody()))
                return new ResponseEntity<>(empResponseEntity.getBody().getData(), HttpStatus.OK);
            else
                throw new ResourceNotFoundException(String.format(Constants.EMP_NOT_FOUND, id));
        } catch (Exception e) {
            log.error("Failed to get employee with id {}", id, e);
            throw e;
        }
    }

    /**
     * This will return Highest Salary of the Employees
     *
     * @return
     */
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        ResponseEntity<List<Employee>> employees = getAllEmployees();

        if(Objects.isNull(employees.getBody())) {
            log.error("Failed to get employee list to calculate highest salary of employee.");
            throw new OperationFailedException(Constants.CALC_HIGHEST_SALARY_OP_FAILED);
        }

        return new ResponseEntity<>(calculateMaxSalary(employees.getBody()), HttpStatus.OK);
    }

    /**
     *
     * This will return top 10 earning employees
     * @return
     */
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        ResponseEntity<List<Employee>> employees = getAllEmployees();

        if(Objects.isNull(employees.getBody())) {
            log.error("Failed to get employee list to calculate top ten highest earning employees.");
            throw new OperationFailedException(Constants.TOP_TEN_HIGHEST_EARNING_OP_FAILED);
        }

        return new ResponseEntity<>(calculateTopTenEarningEmployeeNames(employees.getBody(), Constants.HIGHEST_EARNING_LIMIT), HttpStatus.OK);
    }

    /**
     *
     * This will create the new employee in the system
     *
     * @param employeeInput
     * @return
     */
    public ResponseEntity<Employee> createEmployee(final Map<String, Object> employeeInput) {
        validateCreateEmployeeInputPayload(employeeInput);
        ResponseEntity<EmployeeResponse> employeeResponseEntity = restExecutorServiceClient.execute(Constants.BASE_URL + Constants.CREATE_EMPLOYEE_ENDPOINT_URL,
                HttpClientHelper.getHttpEntity(employeeInput),
                HttpMethod.POST,
                EmployeeResponse.class);

        if(Objects.isNull(employeeResponseEntity.getBody())) {
            log.error("Failed to create employee record with given details {}", employeeInput);
            throw new OperationFailedException(String.format(Constants.CREATE_EMPLOYEE_FAILED, employeeInput.get("name")));
        }
        return new ResponseEntity<>(employeeResponseEntity.getBody().getData(), HttpStatus.CREATED);
    }

    /**
     * This will delete the employee from system
     *
     * @param id
     * @return
     */
    public ResponseEntity<String> deleteEmployeeById(final String id) {
        //Before deleting should we check does employee exist with that id?
        //Because for invalid id also original API returning success where we do not have control
        ResponseEntity<ApiResponse> responseEntity = restExecutorServiceClient.execute(Constants.BASE_URL + String.format(Constants.DELETE_EMPLOYEE_ENDPOINT_URL, id),
                HttpClientHelper.getHttpEntity(),
                HttpMethod.DELETE,
                ApiResponse.class);

        if(responseEntity.getStatusCode().isError())
            throw new OperationFailedException(String.format(Constants.DELETE_EMPLOYEE_FAILED, id));

        return new ResponseEntity<>(Objects.requireNonNull(responseEntity.getBody()).getMessage(), HttpStatus.OK);
    }

    /**
     * @param employeeList
     * @return Max salary value
     */
    private Integer calculateMaxSalary(final List<Employee> employeeList) {

        return employeeList.stream().max(Comparator.
                comparing(Employee::getEmployeeSalary)).get().getEmployeeSalary();
    }

    /**
     * @param employeeList
     * @param searchString
     * @return List of employees having matching searchString
     */
    private List<Employee> searchMatchingNameEmployeeList(final List<Employee> employeeList, final String searchString) {
        return employeeList.stream()
                .filter(employee -> employee.getEmployeeName().contains(searchString))
                .collect(Collectors.toList());
    }

    /**
     * @param employeeList
     * @param limit
     * @return List of top 10 highest earning employee names
     */
    private List<String> calculateTopTenEarningEmployeeNames(final List<Employee> employeeList, final Integer limit) {

        List<String> resultNameList = new ArrayList<>();

        employeeList.sort(Comparator.comparingDouble(Employee::getEmployeeSalary).reversed());

        log.info("Top {} earning employee names are {}.", limit, resultNameList);
        // Print the top 10 highest salary employees
        //System.out.println("Top 10 highest salary employees:");
        for (int i = 0; i < Math.min(limit, employeeList.size()); i++) {
            Employee employee = employeeList.get(i);
            resultNameList.add(employee.getEmployeeName());
        }
        return resultNameList;
    }


    /**
     * This will validate create employee input
     * @param employeeInput
     */
    public void validateCreateEmployeeInputPayload(final Map<String, Object> employeeInput) {
        if(Objects.isNull(employeeInput.get("name"))) {
            throw new InvalidRequestException(Constants.NAME_FIELD_MANDATORY);
        }

        if(Objects.isNull(employeeInput.get("salary"))) {
            throw new InvalidRequestException(Constants.SALARY_FIELD_MANDATORY);
        }

        if(Objects.isNull(employeeInput.get("age"))) {
            throw new InvalidRequestException(Constants.AGE_FIELD_MANDATORY);
        }

    }

}

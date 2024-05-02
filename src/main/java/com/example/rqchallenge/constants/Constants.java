package com.example.rqchallenge.constants;

/**
 *
 * This is constants file
 */
public class Constants {

    public static final String BASE_URL = "https://dummy.restapiexample.com/api/v1";
    public static final String GET_ALL_EMPLOYEES_ENDPOINT_URL = "/employees";
    public static final String GET_EMPLOYEE_BY_ID_ENDPOINT_URL = "/employee/%s";
    public static final String CREATE_EMPLOYEE_ENDPOINT_URL = "/create";
    public static final String DELETE_EMPLOYEE_ENDPOINT_URL = "/delete/%s";

    //SUCCESS MESSAGES

    //ERROR MESSAGES
    public static final String GLOBAL_ERROR_MESSAGE = "Unexpected error occurred, please retry after sometime.";
    public static final String EMP_NOT_FOUND = "Employee with id %s not found.";
    public static final String CREATE_EMPLOYEE_FAILED = "Failed to create employee with name %s.";
    public static final String DELETE_EMPLOYEE_FAILED = "Failed to delete employee of Id %s.";
    public static final String CALC_HIGHEST_SALARY_OP_FAILED = "Failed to calculate highest salary of employee.";
    public static final String SEARCH_BY_NAME_OP_FAILED = "Failed to search employee list whose name contains or matches the string %s";
    public static final String TOP_TEN_HIGHEST_EARNING_OP_FAILED = "Failed to calculate top 10 highest earning employee names";
    public static final String NAME_FIELD_MANDATORY = "name field is mandatory in create employee request.";
    public static final String SALARY_FIELD_MANDATORY = "salary field is mandatory in create employee request.";
    public static final String AGE_FIELD_MANDATORY = "age field is mandatory in create employee request.";

    //CONSTANTS
    public static final Integer HIGHEST_EARNING_LIMIT = 10;

}

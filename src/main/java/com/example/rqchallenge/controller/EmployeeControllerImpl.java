package com.example.rqchallenge.controller;

import com.example.rqchallenge.model.Employee;
import com.example.rqchallenge.employees.IEmployeeController;
import com.example.rqchallenge.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * IEmployeeController interface CURD operation implementations
 */

@Controller
@ResponseBody
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EmployeeControllerImpl implements IEmployeeController {

    private final EmployeeService employeeService;

    /**
     * @return  this should return all employees
     */
    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    /**
     * @param searchString
     * @return  this should return all employees whose name contains or matches the string input provided
     */
    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(final String searchString) {
        return employeeService.getEmployeesByNameSearch(searchString);
    }

    /**
     * @param id
     * @return a single employee
     */
    @Override
    public ResponseEntity<Employee> getEmployeeById(final String id) {
        return employeeService.getEmployeeById(id);
    }

    /**
     * @return a single integer indicating the highest salary of all employees
     */
    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return employeeService.getHighestSalaryOfEmployees();
    }

    /**
     * @return a list of the top 10 employees based off of their salaries
     */
    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return employeeService.getTopTenHighestEarningEmployeeNames();
    }

    /**
     * @param employeeInput
     * @return the object of created employee
     */
    @Override
    public ResponseEntity<Employee> createEmployee(final Map<String, Object> employeeInput) {
        return employeeService.createEmployee(employeeInput);
    }

    /**
     * @param id
     * @return Meesage of operation executed successfully
     */
    @Override
    public ResponseEntity<String> deleteEmployeeById(final String id) {
        return employeeService.deleteEmployeeById(id);
    }
}

package com.example.rqchallenge.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class EmployeeListResponse extends ApiResponse {

    private List<Employee> data;

}

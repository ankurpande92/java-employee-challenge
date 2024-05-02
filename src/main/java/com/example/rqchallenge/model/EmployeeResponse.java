package com.example.rqchallenge.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EmployeeResponse extends ApiResponse {

    private Employee data;

}

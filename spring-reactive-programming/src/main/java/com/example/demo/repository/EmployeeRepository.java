package com.example.demo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.example.demo.model.Employee;

public interface EmployeeRepository  extends ReactiveMongoRepository<Employee, String>{

}
